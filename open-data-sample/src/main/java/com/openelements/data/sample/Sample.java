package com.openelements.data.sample;

import com.openelements.data.runtime.h2.H2Dialect;
import com.openelements.data.runtime.sql.ConnectionProvider;
import com.openelements.data.runtime.sql.PooledConnectionProvider;
import com.openelements.data.runtime.sql.SqlConnection;
import com.openelements.data.server.DataServer;
import java.sql.Connection;
import java.sql.DriverManager;

public class Sample {

    private static Connection createConnection() {
        try {
            Class.forName("org.h2.Driver");
            return DriverManager.getConnection("jdbc:h2:mem:open-data-sample", "sa", "");
        } catch (Exception e) {
            throw new RuntimeException("Error creating JDBC connection", e);
        }
    }

    private static ConnectionProvider createConnectionProvider() {
        return new PooledConnectionProvider(() -> createConnection(), 8);
    }

    private static SqlConnection createSqlConnection() {
        return new SqlConnection(createConnectionProvider(), new H2Dialect());
    }


    public static void main(String[] args) throws Exception {
        DataServer dataServer = new DataServer(8080, createSqlConnection());
        dataServer.start();
    }
}
