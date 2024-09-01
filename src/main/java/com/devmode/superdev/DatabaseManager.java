package com.devmode.superdev;
import java.sql.Connection;
import java.sql.SQLException;
import com.devmode.superdev.utils.NetworkUtils;

public class DatabaseManager {

    private static final DatabaseConn databaseConn;
    static {
        System.out.println(NetworkUtils.isInternetAvailable());
        if (NetworkUtils.isInternetAvailable()) {
            databaseConn = new SQLiteConnector();
            System.out.println("Internet found");
        } else {
            System.out.println("No internet connection");
            databaseConn = new SQLiteConnector();
        }
    }
    public static Connection getConnection() throws SQLException, ClassNotFoundException {
        return databaseConn.getConnection();
    }
}
