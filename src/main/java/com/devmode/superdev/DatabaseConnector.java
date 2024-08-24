package com.devmode.superdev;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnector {

    private static final String URL = "jdbc:mysql://localhost:3306/superdev";
    private static final String USER = "root";
    private static final String PASSWORD = "";

    @SuppressWarnings("exports")
    public static Connection getConnection() throws SQLException, ClassNotFoundException {
        // Register the driver
        Class.forName("com.mysql.cj.jdbc.Driver");
        return DriverManager.getConnection(
                URL, USER, PASSWORD
        );
    }

}
