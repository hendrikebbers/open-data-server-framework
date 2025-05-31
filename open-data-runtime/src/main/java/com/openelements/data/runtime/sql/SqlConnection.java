package com.openelements.data.runtime.sql;

import com.openelements.data.runtime.sql.statement.SqlStatementFactory;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Objects;
import java.util.UUID;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SqlConnection {

    private final static Logger log = LoggerFactory.getLogger(SqlConnection.class);

    private final ConnectionProvider connectionProvider;

    private final SqlDialect sqlDialect;

    private final ThreadLocal<Connection> connectionThreadLocal = ThreadLocal.withInitial(() -> null);

    private final ThreadLocal<UUID> transactionThreadLocal = ThreadLocal.withInitial(() -> null);

    public SqlConnection(@NonNull final ConnectionProvider connectionProvider, @NonNull final SqlDialect sqlDialect) {
        this.connectionProvider = Objects.requireNonNull(connectionProvider, "connectionProvider must not be null");
        this.sqlDialect = Objects.requireNonNull(sqlDialect, "sqlDialect must not be null");
    }

    public PreparedStatement prepareStatement(@NonNull final String sql) throws SQLException {
        Objects.requireNonNull(sql, "SQL statement must not be null");
        if (sql.isBlank()) {
            throw new IllegalArgumentException("SQL statement must not be blank");
        }
        final Connection connection = getOrCreateConnection();
        return connection.prepareStatement(sql);
    }

    @NonNull
    public SqlDialect getSqlDialect() {
        return sqlDialect;
    }

    @NonNull
    public SqlStatementFactory getSqlStatementFactory() {
        return sqlDialect.getSqlStatementFactory(this);
    }

    @NonNull
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

    @Nullable
    public <T> T runInTransaction(@NonNull final sqlTransactionCallable<T> callable) throws SQLException {
        Objects.requireNonNull(callable, "Callable must not be null");
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

    public void runInTransaction(@NonNull final sqlTransactionRunnable runnable) throws SQLException {
        Objects.requireNonNull(runnable, "Runnable must not be null");
        runInTransaction(() -> {
            runnable.run();
            return null;
        });
    }

    public interface sqlTransactionCallable<T> {
        @Nullable
        T call() throws SQLException;
    }

    public interface sqlTransactionRunnable {
        void run() throws SQLException;
    }

}
