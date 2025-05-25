package com.openelements.data.runtime.sql;

import com.openelements.data.runtime.sql.statement.LoggablePreparedStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class SqlConnection {

    private final ConnectionProvider connectionProvider;

    public SqlConnection(ConnectionProvider connectionProvider) {this.connectionProvider = connectionProvider;}

    public PreparedStatement prepareStatement(String sql) throws SQLException {
        final Connection connection = connectionProvider.getConnection();
        final PreparedStatement internalPreparedStatement = connection.prepareStatement(sql);
        return new LoggablePreparedStatement(internalPreparedStatement);

    }
}
