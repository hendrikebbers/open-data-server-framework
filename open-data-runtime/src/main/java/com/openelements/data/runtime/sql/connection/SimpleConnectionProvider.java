package com.openelements.data.runtime.sql.connection;

import com.openelements.data.runtime.sql.api.ConnectionProvider;
import com.openelements.data.runtime.sql.statement.LoggableConnection;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Objects;
import org.jspecify.annotations.NonNull;

public class SimpleConnectionProvider implements ConnectionProvider {

    private final String jdbcUrl;

    private final String username;

    private final String password;

    public SimpleConnectionProvider(@NonNull final String driverClassName, @NonNull final String jdbcUrl,
            @NonNull final String username, @NonNull final String password) {
        this.jdbcUrl = Objects.requireNonNull(jdbcUrl, "jdbcUrl must not be null");
        this.username = Objects.requireNonNull(username, "username must not be null");
        this.password = Objects.requireNonNull(password, "password must not be null");
        Objects.requireNonNull(driverClassName, "driverClassName must not be null");
        try {
            Class.forName(driverClassName);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Can not load JDBC driver " + driverClassName, e);
        }
    }

    @NonNull
    @Override
    public Connection getConnection() throws SQLException {
        final Connection innerConnection = DriverManager.getConnection(jdbcUrl, username, password);
        return new LoggableConnection(innerConnection);
    }
}
