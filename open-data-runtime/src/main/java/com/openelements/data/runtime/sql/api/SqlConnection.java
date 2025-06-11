package com.openelements.data.runtime.sql.api;

import com.openelements.data.runtime.sql.connection.SqlConnectionImpl;
import com.openelements.data.runtime.sql.statement.SqlStatementFactory;
import java.sql.Blob;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

public interface SqlConnection {

    @NonNull
    SqlDialect getSqlDialect();

    @NonNull
    SqlStatementFactory getSqlStatementFactory();

    PreparedStatement prepareStatement(@NonNull final String sql) throws SQLException;

    @Nullable
    <T> T runInTransaction(@NonNull final TransactionCallable<T> callable) throws SQLException;

    void runInTransaction(@NonNull final TransactionRunnable runnable) throws SQLException;

    interface TransactionCallable<T> {
        @Nullable
        T call() throws SQLException;
    }

    interface TransactionRunnable {
        void run() throws SQLException;
    }

    static SqlConnection create(@NonNull final ConnectionProvider connectionProvider,
            @NonNull final SqlDialect sqlDialect) {
        return new SqlConnectionImpl(connectionProvider, sqlDialect);
    }

    Blob createBlob() throws SQLException;
}
