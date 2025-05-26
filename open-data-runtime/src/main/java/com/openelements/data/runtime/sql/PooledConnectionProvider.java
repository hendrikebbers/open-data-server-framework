package com.openelements.data.runtime.sql;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Random;
import java.util.concurrent.CopyOnWriteArrayList;

public class PooledConnectionProvider implements ConnectionProvider {

    private final ConnectionProvider internalProvider;

    private CopyOnWriteArrayList<Connection> connections = new CopyOnWriteArrayList<>();

    private final Random random = new Random(System.currentTimeMillis());

    public PooledConnectionProvider(ConnectionProvider internalProvider, int poolSize) {
        this.internalProvider = internalProvider;
        for (int i = 0; i < poolSize; i++) {
            try {
                connections.add(internalProvider.getConnection());
            } catch (SQLException e) {
                throw new RuntimeException("Failed to initialize connection pool", e);
            }
        }
    }

    @Override
    public Connection getConnection() throws SQLException {
        Connection connection = connections.get(random.nextInt(connections.size()));
        connection.rollback();
        if (connection.isClosed()) {
            connections.remove(connection);
            connection = internalProvider.getConnection();
            connections.add(connection);
        }
        connection.setAutoCommit(true);
        return connection;
    }
}
