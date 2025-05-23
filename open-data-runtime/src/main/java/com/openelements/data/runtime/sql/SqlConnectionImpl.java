package com.openelements.data.runtime.sql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class SqlConnectionImpl implements SqlConnection {

    private final ConnectionProvider connectionProvider;

    public SqlConnectionImpl(ConnectionProvider connectionProvider) {this.connectionProvider = connectionProvider;}

    @Override
    public PreparedStatement prepareStatement(String sql) throws SQLException {
        final Connection connection = connectionProvider.getConnection();
        final PreparedStatement internalPreparedStatement = connection.prepareStatement(sql);
        return new LoggablePreparedStatement(internalPreparedStatement);

    }
}
