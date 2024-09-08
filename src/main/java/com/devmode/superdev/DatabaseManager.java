package com.devmode.superdev;
import java.sql.*;
import java.time.LocalDate;

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

    public static int createOrder(double totalAmount, String status, int userID, int invoiceID, int customerID) throws SQLException {
        String query = "INSERT INTO orders (orderDate, totalAmount, status, userID, invoiceID, customerID) VALUES (datetime('now'), ?, ?, ?, ?, ?)";
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {

            statement.setDouble(1, totalAmount);
            statement.setString(2, status);
            statement.setInt(3, userID);
            statement.setInt(4, invoiceID);
            statement.setInt(5, customerID);

            int affectedRows = statement.executeUpdate();

            if (affectedRows > 0) {
                try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        int orderID = generatedKeys.getInt(1);
                        System.out.println("Order created with ID: " + orderID);
                        return orderID;
                    } else {
                        throw new SQLException("Creating order failed, no ID obtained.");
                    }
                }
            }
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        return -1;
    }


    public static int createCustomer(String phone) throws SQLException, ClassNotFoundException {
        String query = "INSERT INTO customer (phone) VALUES (?)";
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, phone);
            statement.executeUpdate();
            try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    return generatedKeys.getInt(1);
                } else {
                    throw new SQLException("Creating customer failed, no ID obtained.");
                }
            }
        }
    }

    public static void createPointTransaction(int customerID, double points, int orderID) throws SQLException {
        String query = "INSERT INTO pointstransaction (points, transactionDate, transactionType, orderID, customerID, synced, deleted) " +
                "VALUES (?, CURRENT_TIMESTAMP, 'EARN', ?, ?, false, false)";
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setDouble(1, points);
            statement.setInt(2, orderID);
            statement.setInt(3, customerID);
            statement.executeUpdate();
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }


    public static void createOrderItem(int orderID, int productID, double subtotal) throws SQLException, ClassNotFoundException {
        String query = "INSERT INTO orderitem (subtotal, orderID, productID, synced, deleted) " +
                "VALUES (?, ?, ?, 0, 0)";

        try (Connection connection = getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            // Set the parameters for the prepared statement

            preparedStatement.setDouble(1, subtotal);
            preparedStatement.setInt(2, orderID);
            preparedStatement.setInt(3, productID);

            // Execute the insert operation
            preparedStatement.executeUpdate();
        }
    }

    public static void updateProductQuantity(int productId, int quantityChange) throws SQLException {
        String query = "UPDATE product SET stockQuantity = stockQuantity + ? WHERE id = ?";
        try (Connection connection = getConnection();
             PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setInt(1, quantityChange);
            pstmt.setInt(2, productId);
            pstmt.executeUpdate();
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
    public static void updateCustomerPoints(int customerID, int points) throws SQLException {
        String query = "UPDATE customer SET points = points + ? WHERE id = ?";
        try (Connection connection = getConnection();
             PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setDouble(1, points); // Points should be negative for deduction
            pstmt.setInt(2, customerID);
            int rowsAffected = pstmt.executeUpdate();

            if (rowsAffected == 0) {
                System.out.println("No rows updated. Customer ID might be incorrect.");
            } else {
                System.out.println("Customer points updated successfully.");
            }
        } catch (SQLException e) {
            System.out.println("SQL Exception: " + e.getMessage());
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            System.out.println("Class Not Found Exception: " + e.getMessage());
            e.printStackTrace();
        }
    }


    public static void createPointTransaction(int customerID, double points, int orderID, String type) throws SQLException {
        Connection connection = null;
        try {
            connection = getConnection();
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

        String query = "INSERT INTO pointstransaction (points, transactionDate, transactionType, orderID, customerID, synced, deleted) VALUES (?, datetime('now'), ?, ?, ?, 0, 0)";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setDouble(1, points);
            pstmt.setString(2, type);
            pstmt.setInt(3, orderID);
            pstmt.setInt(4, customerID);
            pstmt.executeUpdate();
        } finally {
            if (connection != null) {
                connection.close();
            }
        }
    }



    public static Integer createInvoice(double totalAmount) throws SQLException, ClassNotFoundException {
        String query = "INSERT INTO invoice (date, totalAmount) VALUES (?, ?)";
        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {

            statement.setString(1, LocalDate.now().toString()); // Use current date
            statement.setDouble(2, totalAmount);
            statement.executeUpdate();

            try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    return generatedKeys.getInt(1);
                } else {
                    throw new SQLException("Creating invoice failed, no ID obtained.");
                }
            }
        }
    }


    public static Connection getConnection() throws SQLException, ClassNotFoundException {
        return databaseConn.getConnection();
    }
}
