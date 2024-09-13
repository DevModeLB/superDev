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

    public static PointsSettings fetchPointsSettings() {
        String query = "SELECT setting_name, setting_value FROM settings WHERE setting_name IN ('points_step', 'step_points', 'point_amount', 'points_activation')";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            ResultSet rs = stmt.executeQuery();
            if (!rs.isBeforeFirst()) { // check if no rows
                System.out.println("No results found for the query.");
            }
            String pointsStep = null;
            String stepPoints = null;
            String pointAmount = null;
            boolean isActive = false;

            while (rs.next()) {
                String settingName = rs.getString("setting_name");
                String settingValue = rs.getString("setting_value");


                switch (settingName) {
                    case "points_step":
                        pointsStep = settingValue;
                        break;
                    case "step_points":
                        stepPoints = settingValue;
                        break;
                    case "point_amount":
                        pointAmount = settingValue;
                        break;
                    case "points_activation":
                        isActive = "active".equalsIgnoreCase(settingValue);
                        break;
                }
            }

            return new PointsSettings(pointsStep, stepPoints, pointAmount, isActive);

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    public static String fetchCurrencyRate() {
        String currencyRate = null;
        String query = "SELECT setting_value FROM settings WHERE setting_name = 'currency_rate'";

        try (Connection conn = DatabaseManager.getConnection()) {
            PreparedStatement stmt = conn.prepareStatement(query);
            ResultSet resultSet = stmt.executeQuery();
            if (resultSet.next()) {
                currencyRate = resultSet.getString("setting_value");
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        } ;
        return currencyRate;
    }

    public static int fetchDefaultCustomerId() throws SQLException, ClassNotFoundException {
        int customerId = -1;
        String query = "SELECT id FROM customer WHERE phone = '0000000'";
        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(query);
             ResultSet resultSet = statement.executeQuery()) {
            if (resultSet.next()) {
                customerId = resultSet.getInt("id");
            }
        }
        return customerId;
    }

    public static String expireDate() {
        String sql = "SELECT expiresAt FROM license LIMIT 1";

        try (Connection conn = DatabaseManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            if (rs.next()) {
                String expiresAt = rs.getString("expiresAt");
                String datePart = expiresAt.substring(0, 10); // Extract "yyyy-MM-dd"
                LocalDate expiryDate = LocalDate.parse(datePart, DateTimeFormatter.ISO_DATE);
                return datePart;
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        new ErrorDialog().showErrorDialog("Expired Licence", "Error");
        System.exit(1);
        return "";
    }

    public static String fetchLicenseKey() {
        String sql = "SELECT licenseKey FROM license LIMIT 1";
        try (Connection conn = DatabaseManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                return rs.getString("licenseKey");
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        new ErrorDialog().showErrorDialog("Expired Licence", "Error");
        System.exit(1);
        return "";
    }

    public static Map<String, Double> getOrderStatisticsForYear(String year) {
        Map<String, Double> ordersTotalPerMonth = new HashMap<>();

        String query = "SELECT strftime('%m', orderDate) AS month, SUM(totalAmount) AS total " +
                "FROM orders WHERE strftime('%Y', orderDate) = ? " +
                "GROUP BY strftime('%m', orderDate);";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, year);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                String month = rs.getString("month");
                Double total = rs.getDouble("total");
                ordersTotalPerMonth.put(month, total);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

        return ordersTotalPerMonth;
    }

    public static Map<String, Double> getBestSellingProducts() {
        String query = "SELECT p.name, SUM(oi.subtotal) AS units_sold " +
                "FROM orderitem oi " +
                "JOIN product p ON oi.productID = p.id " +
                "GROUP BY oi.productID " +
                "ORDER BY units_sold DESC " +
                "LIMIT 10;";

        Map<String, Double> bestSellingProducts = new HashMap<>();

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                String productName = rs.getString("name");
                Double unitsSold = rs.getDouble("units_sold");
                bestSellingProducts.put(productName, unitsSold);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

        return bestSellingProducts;
    }


}
