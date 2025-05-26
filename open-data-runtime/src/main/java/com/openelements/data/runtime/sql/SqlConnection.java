package com.openelements.data.runtime.sql;

import com.openelements.data.runtime.sql.statement.LoggablePreparedStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Optional;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SqlConnection {

    private final static Logger log = LoggerFactory.getLogger(SqlConnection.class);

    private final ConnectionProvider connectionProvider;

    private final SqlDialect sqlDialect;

    private final ThreadLocal<Connection> connectionThreadLocal = ThreadLocal.withInitial(() -> null);

    public SqlConnection(ConnectionProvider connectionProvider, SqlDialect sqlDialect) {
        this.connectionProvider = connectionProvider;
        this.sqlDialect = sqlDialect;
    }

    public PreparedStatement prepareStatement(String sql) throws SQLException {
        final Connection connection = Optional.ofNullable(connectionThreadLocal.get())
                .orElseGet(() -> {
                    try {
                        return connectionProvider.getConnection();
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                });
        final PreparedStatement internalPreparedStatement = connection.prepareStatement(sql);
        return new LoggablePreparedStatement(internalPreparedStatement);
    }

    public SqlDialect getSqlDialect() {
        return sqlDialect;
    }

    public <T> T runInTransaction(sqlTransactionCallable<T> callable) throws SQLException {
        if (connectionThreadLocal.get() != null) {
            throw new SQLException("A transaction is already in progress for this thread");
        }
        final Connection connection = connectionProvider.getConnection();
        connectionThreadLocal.set(connection);
        final UUID transactionId = UUID.randomUUID();
        try {
            log.info("Transaction '{}' started", transactionId);
            connection.setAutoCommit(false);
            final T result = callable.call();
            connection.commit();
            log.info("Transaction '{}' committed", transactionId);
            return result;
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
            connection.setAutoCommit(true);
            connectionThreadLocal.remove();
        }
    }

    public interface sqlTransactionCallable<T> {
        T call() throws SQLException;
    }

    public interface sqlTransactionRunnable {
        void run() throws SQLException;
    }

    public void runInTransaction(sqlTransactionRunnable runnable) throws SQLException {
        runInTransaction(() -> {
            runnable.run();
            return null;
        });
    }
}
