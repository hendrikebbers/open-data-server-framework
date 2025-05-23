package com.openelements.data.sample;

import com.openelements.data.server.DataServer;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Sample {

    private static Connection createConnection() {
        try {
            Class.forName("org.h2.Driver");
            return DriverManager.getConnection("jdbc:h2:~/test", "sa", "");
        } catch (ClassNotFoundException | SQLException e) {
            throw new RuntimeException("Error creating JDBC connection", e);
        }
    }


    public static void main(String[] args) throws Exception {
        DataServer dataServer = new DataServer(8080, Sample::createConnection);
        dataServer.start();
    }
}
