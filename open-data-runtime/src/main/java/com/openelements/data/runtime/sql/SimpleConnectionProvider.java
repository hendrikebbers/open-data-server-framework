package com.openelements.data.runtime.sql;

import com.openelements.data.runtime.sql.statement.LoggableConnection;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class SimpleConnectionProvider implements ConnectionProvider {
    
    private final String jdbcUrl;

    private final String username;

    private final String password;

    public SimpleConnectionProvider(String driverClassName, String jdbcUrl, String username, String password) {
        this.jdbcUrl = jdbcUrl;
        this.username = username;
        this.password = password;
        try {
            Class.forName(driverClassName);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Can not load JDBC driver " + driverClassName, e);
        }
    }

    @Override
    public Connection getConnection() throws SQLException {
        Connection innerConnection = DriverManager.getConnection(jdbcUrl, username, password);
        return new LoggableConnection(innerConnection);
    }
}
