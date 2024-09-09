package com.devmode.superdev.utils;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

import com.devmode.superdev.models.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import com.devmode.superdev.DatabaseManager;


public class DataFetcher {
    public static ObservableList<Category> fetchCategories() {
        ObservableList<Category> categories = FXCollections.observableArrayList();
        String query = "SELECT id, name FROM category where deleted = 0";
        try (Connection conn = DatabaseManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                int id = rs.getInt("id");
                String name = rs.getString("name");
                categories.add(new Category(id, name));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        return categories;
    }

    public static Product findProductByBarcode(String barcode) {
        Product product = null;
        String sql = "SELECT p.id, p.name, p.price, p.stockQuantity AS stock, p.image, p.description, p.barCode AS barcode, " +
                "c.name AS category, s.name AS supplier " +
                "FROM Product p " +
                "JOIN Category c ON p.categoryID = c.id " +
                "JOIN Supplier s ON p.supplierID = s.id " +
                "WHERE p.barCode = ? AND p.deleted = 0";

        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement pstmt = connection.prepareStatement(sql)) {

            // Set the barcode parameter
            pstmt.setString(1, barcode);

            // Execute the query
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    System.out.println("Found");
                    // Get column values from result set
                    int id = rs.getInt("id");
                    String name = rs.getString("name");
                    double price = rs.getDouble("price");
                    int stock = rs.getInt("stock");  // Changed to match the alias 'stock'
                    String barCode = rs.getString("barcode");
                    String category = rs.getString("category");
                    String supplier = rs.getString("supplier");
                    String imagePath = rs.getString("image");
                    String description = rs.getString("description");

                    // Create a new Product object
                    product = new Product(id, name, price, stock, barCode, category, supplier, imagePath, description);
                } else {
                    System.out.println("No product found for barcode: " + barcode);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();  // Log SQL exception
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);  // Log class not found exception
        }

        return product;  // Return the found product or null
    }


    public static ObservableList<Supplier> fetchSuppliers() {
        ObservableList<Supplier> suppliers = FXCollections.observableArrayList();
        String query = "SELECT id, name, phone_nb FROM supplier where deleted = 0";
        try (Connection conn = DatabaseManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                int id = rs.getInt("id");
                String name = rs.getString("name");
                String phone_nb = rs.getString("phone_nb");
                suppliers.add(new Supplier(id, name, phone_nb));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        return suppliers;
    }
    public static ObservableList<Product> fetchAllProducts(String filter) {
        ObservableList<Product> products = FXCollections.observableArrayList();
        String query;
        if(!Objects.equals(filter, "none")){
            query = "SELECT p.id, p.name, p.price, p.stockQuantity AS stock,p.image , p.description ,p.barCode AS barcode, " +
                    "c.name AS category, s.name AS supplier " +
                    "FROM Product p " +
                    "JOIN Category c ON p.categoryID = c.id " +
                    "JOIN Supplier s ON p.supplierID = s.id " +
                    "WHERE " + filter +
                    " AND p.deleted = 0";
        }
        else{
            query = "SELECT p.id, p.name, p.price, p.stockQuantity AS stock,p.image , p.description ,p.barCode AS barcode, " +
                    "c.name AS category, s.name AS supplier " +
                    "FROM Product p " +
                    "JOIN Category c ON p.categoryID = c.id " +
                    "JOIN Supplier s ON p.supplierID = s.id " +
                    "WHERE p.deleted = 0";
        }

        try (Connection conn = DatabaseManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                int id = rs.getInt("id");
                String name = rs.getString("name");
                double price = rs.getDouble("price");
                int stock = rs.getInt("stock");
                String barcode = rs.getString("barcode");
                String category = rs.getString("category");
                String supplier = rs.getString("supplier");
                String imagePath = rs.getString("image");
                String description = rs.getString("description");
                products.add(new Product(id, name, price, stock, barcode, category, supplier, imagePath, description));

            }
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        return products;
    }

    public static ObservableList<User> fetchUsers() {
        ObservableList<User> users = FXCollections.observableArrayList();

        String query = "SELECT id, username, password,  role FROM user";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Integer userId = rs.getInt("id");
                String username = rs.getString("username");
                String password = rs.getString("password");
                String role = rs.getString("role");
                User user = new User(userId, username, password , role);
                users.add(user);
            }

        } catch (SQLException e) {
            e.printStackTrace(); // Handle exception properly
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

        return users;
    }

    public static List<Order> fetchOrders() {
        List<Order> orders = new ArrayList<>();
        String sql = "SELECT " +
                "o.id AS orderId, " +
                "o.orderDate, " +
                "o.totalAmount AS total, " +
                "o.status, " +
                "u.username AS user, " +
                "i.id AS invoiceId, " +
                "i.totalAmount AS invoiceTotal, " +
                "c.id AS customerId, " +
                "c.phone AS customerPhone, " +
                "c.points AS customerPoints, " +
                "oi.id AS itemId, " +
                "oi.productID AS productId, " +
                "p.name AS productName, " +
                "oi.subtotal AS itemSubtotal " +
                "FROM orders o " +
                "LEFT JOIN user u ON o.userID = u.id " +
                "LEFT JOIN invoice i ON o.invoiceID = i.id " +
                "LEFT JOIN customer c ON o.customerID = c.id " +
                "LEFT JOIN orderitem oi ON o.id = oi.orderID " +
                "LEFT JOIN product p ON oi.productID = p.id " +
                "WHERE o.deleted = 0 " +
                "ORDER BY o.orderDate DESC";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            Map<Integer, Order> orderMap = new HashMap<>();

            while (rs.next()) {
                int orderId = rs.getInt("orderId");
                Order order = orderMap.computeIfAbsent(orderId, id -> {
                    Order o = new Order();
                    o.setId(id);
                    try {
                        o.setOrderDate(LocalDateTime.parse(rs.getString("orderDate"), formatter));
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                    try {
                        o.setTotal(rs.getBigDecimal("total"));
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                    try {
                        o.setStatus(rs.getString("status"));
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                    try {
                        o.setUser(rs.getString("user"));
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                    try {
                        o.setInvoiceId(rs.getInt("invoiceId"));
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                    try {
                        o.setInvoiceTotal(rs.getBigDecimal("invoiceTotal"));
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                    try {
                        o.setCustomerId(rs.getInt("customerId"));
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                    try {
                        o.setCustomerPhone(rs.getString("customerPhone"));
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                    try {
                        o.setCustomerPoints(rs.getInt("customerPoints"));
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                    o.setOrderItems(new ArrayList<>()); // Initialize orderItems list
                    return o;
                });

                // Only add OrderItem if it exists
                int itemId = rs.getInt("itemId");
                if (itemId != 0) {
                    OrderItem orderItem = new OrderItem();
                    orderItem.setId(itemId);
                    orderItem.setProductId(rs.getInt("productId"));
                    orderItem.setProductName(rs.getString("productName"));
                    orderItem.setSubtotal(rs.getBigDecimal("itemSubtotal"));

                    // Add orderItem to the current order
                    order.getOrderItems().add(orderItem);
                }
            }

            orders.addAll(orderMap.values());
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        return orders;
    }




}
