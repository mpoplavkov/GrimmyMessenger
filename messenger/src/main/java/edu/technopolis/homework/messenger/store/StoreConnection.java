package edu.technopolis.homework.messenger.store;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class StoreConnection {
    private static Connection connection;

    public static Connection getConnection() {
        if (connection == null) {
            connect();
        }
        return connection;
    }

    public static void disconnect() {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            System.out.println("Disconnection successful");
        }
    }

    private static void connect() {
        String url = "jdbc:postgresql://localhost:5432/messenger";
        String name = "admin";
        String pass = "admin";
        try {
            Class.forName("org.postgresql.Driver");
            System.out.println("JDBC driver connected");
            connection = DriverManager.getConnection(url, name, pass);
            System.out.println("Connection successful");

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
