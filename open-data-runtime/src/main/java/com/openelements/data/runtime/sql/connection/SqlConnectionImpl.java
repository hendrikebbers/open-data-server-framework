package com.openelements.data.runtime.sql.connection;

import com.openelements.data.runtime.sql.api.ConnectionProvider;
import com.openelements.data.runtime.sql.api.SqlConnection;
import com.openelements.data.runtime.sql.api.SqlDialect;
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

public class SqlConnectionImpl implements SqlConnection {

    private final static Logger log = LoggerFactory.getLogger(SqlConnectionImpl.class);

    private final ConnectionProvider connectionProvider;

    private final SqlDialect sqlDialect;

    private final ThreadLocal<Connection> connectionThreadLocal = ThreadLocal.withInitial(() -> null);

    private final ThreadLocal<UUID> transactionThreadLocal = ThreadLocal.withInitial(() -> null);

    public SqlConnectionImpl(@NonNull final ConnectionProvider connectionProvider,
            @NonNull final SqlDialect sqlDialect) {
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
            if (!connection.isValid(5)) {
                try {
                    if (!connection.isClosed()) {
                        connection.close();
                    }
                } catch (SQLException e) {
                    log.warn("Failed to close invalid connection", e);
                }
                log.info("Connection in thread-local is not valid anymore, acquiring a new connection");
                final Connection newConnection = connectionProvider.getConnection();
                connectionThreadLocal.set(newConnection);
                return newConnection;
            }
            return connection;
        }
        log.debug("No connection found in thread-local, acquiring a new connection");
        final Connection newConnection = connectionProvider.getConnection();
        connectionThreadLocal.set(newConnection);
        return newConnection;
    }

    @Nullable
    public <T> T runInTransaction(@NonNull final TransactionCallable<T> callable) throws SQLException {
        Objects.requireNonNull(callable, "Callable must not be null");
        if (transactionThreadLocal.get() != null) {
            log.debug("A transaction is already in progress for this thread");
            return callable.call();
        }
        final Connection connection = getOrCreateConnection();
        final UUID transactionId = UUID.randomUUID();
        transactionThreadLocal.set(transactionId);
        try {
            connection.setAutoCommit(false);
            try {
                log.debug("Transaction '{}' started", transactionId);
                try {
                    final T result = callable.call();
                    connection.commit();
                    log.debug("Transaction '{}' committed", transactionId);
                    return result;
                } catch (Exception e) {
                    log.error("Transaction '{}' failed. Will try to roll back", transactionId, e);
                    try {
                        connection.rollback();
                        log.error("Transaction '" + transactionId + "' rolled back", e);
                    } catch (SQLException rollbackEx) {
                        log.error("Transaction '" + transactionId + "' failed to roll back", rollbackEx);
                        throw new SQLException("Transaction failed and was not able to be rolled back", e);
                    }
                    throw new SQLException("Transaction failed and rolled back", e);
                }
            } finally {
                connection.setAutoCommit(true);
            }
        } finally {
            transactionThreadLocal.remove();
        }
    }

    public void runInTransaction(@NonNull final TransactionRunnable runnable) throws SQLException {
        Objects.requireNonNull(runnable, "Runnable must not be null");
        runInTransaction(() -> {
            runnable.run();
            return null;
        });
    }
}
