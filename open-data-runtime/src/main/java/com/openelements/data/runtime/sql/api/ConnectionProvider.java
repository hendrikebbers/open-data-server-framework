package com.openelements.data.runtime.sql.api;

import com.openelements.data.runtime.sql.connection.SimpleConnectionProvider;
import java.sql.Connection;
import java.sql.SQLException;
import org.jspecify.annotations.NonNull;

public interface ConnectionProvider {

    Connection getConnection() throws SQLException;

    static ConnectionProvider of(@NonNull final String driverClassName, @NonNull final String jdbcUrl,
            @NonNull final String username, @NonNull final String password) {
        return new SimpleConnectionProvider(driverClassName, jdbcUrl, username, password);
    }
}
