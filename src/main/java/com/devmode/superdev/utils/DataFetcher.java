package com.devmode.superdev.utils;

import java.sql.*;
import java.util.Objects;

import com.devmode.superdev.models.Product;
import com.devmode.superdev.models.User;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import com.devmode.superdev.DatabaseManager;
import com.devmode.superdev.models.Category;
import com.devmode.superdev.models.Supplier;



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
                    "AND p.deleted = 0";
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

}
