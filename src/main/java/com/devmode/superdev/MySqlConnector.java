package com.devmode.superdev;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class MySqlConnector implements DatabaseConn {

    private static final String URL = "jdbc:mysql://localhost:3306/superdev";
    private static final String USER = "root";
    private static final String PASSWORD = "";

    @Override
    public Connection getConnection() throws SQLException, ClassNotFoundException {
        // Register the MySQL driver
        Class.forName("com.mysql.cj.jdbc.Driver");
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}
