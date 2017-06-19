package edu.technopolis.homework.messenger.store;

import edu.technopolis.homework.messenger.config.Config;

import java.io.IOException;
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
            System.out.println("Store: disconnection successful");
        }
    }

    private static void connect() {
        try {
            Config config = new Config();

            String url = config.getURL();
            String name = config.getDbUserName();
            String pass = config.getDbUserPassword();

            Class.forName(config.getDriverClassName());
            System.out.println("JDBC driver connected");
            connection = DriverManager.getConnection(url, name, pass);
            System.out.println("Store: connection successful");

        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
