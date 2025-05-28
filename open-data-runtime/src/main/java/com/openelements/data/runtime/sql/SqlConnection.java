package com.openelements.data.runtime.sql;

import com.openelements.data.runtime.sql.statement.SqlStatementFactory;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SqlConnection {

    private final static Logger log = LoggerFactory.getLogger(SqlConnection.class);

    private final ConnectionProvider connectionProvider;

    private final SqlDialect sqlDialect;

    private final ThreadLocal<Connection> connectionThreadLocal = ThreadLocal.withInitial(() -> null);

    private final ThreadLocal<UUID> transactionThreadLocal = ThreadLocal.withInitial(() -> null);

    public SqlConnection(ConnectionProvider connectionProvider, SqlDialect sqlDialect) {
        this.connectionProvider = connectionProvider;
        this.sqlDialect = sqlDialect;
    }

    public PreparedStatement prepareStatement(String sql) throws SQLException {
        final Connection connection = getOrCreateConnection();
        return connection.prepareStatement(sql);
    }

    public SqlDialect getSqlDialect() {
        return sqlDialect;
    }

    public SqlStatementFactory getSqlStatementFactory() {
        return sqlDialect.getSqlStatementFactory();
    }

    private Connection getOrCreateConnection() throws SQLException {
        final Connection connection = connectionThreadLocal.get();
        if (connection != null) {
            return connection;
        }
        log.debug("No connection found in thread-local, acquiring a new connection");
        final Connection newConnection = connectionProvider.getConnection();
        connectionThreadLocal.set(newConnection);
        return newConnection;
    }

    public <T> T runInTransaction(sqlTransactionCallable<T> callable) throws SQLException {
        if (transactionThreadLocal.get() != null) {
            log.debug("A transaction is already in progress for this thread");
            return callable.call();
        }
        final Connection connection = getOrCreateConnection();
        final UUID transactionId = UUID.randomUUID();
        transactionThreadLocal.set(transactionId);
        try {
            log.info("Transaction '{}' started", transactionId);
            connection.setAutoCommit(false);
            try {
                final T result = callable.call();
                connection.commit();
                log.info("Transaction '{}' committed", transactionId);
                return result;
            } finally {
                connection.setAutoCommit(true);
            }
        } catch (Exception e) {
            try {
                connection.rollback();
                log.error("Transaction '" + transactionId + "' rolled back", e);
            } catch (SQLException rollbackEx) {
                log.error("Transaction '" + transactionId + "' failed to roll back", rollbackEx);
                throw new SQLException("Transaction failed and was not able to be rolled back", e);
            }
            throw new SQLException("Transaction failed and rolled back", e);
        } finally {
            transactionThreadLocal.remove();
        }
    }

    public void runInTransaction(sqlTransactionRunnable runnable) throws SQLException {
        runInTransaction(() -> {
            runnable.run();
            return null;
        });
    }

    public interface sqlTransactionCallable<T> {
        T call() throws SQLException;
    }

    public interface sqlTransactionRunnable {
        void run() throws SQLException;
    }

}
