package com.devmode.superdev.utils;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.SQLException;

import com.devmode.superdev.models.Product;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import com.devmode.superdev.DatabaseConnector;
import com.devmode.superdev.models.Category;
import com.devmode.superdev.models.Supplier;



public class DataFetcher {

    public static ObservableList<Category> fetchCategories() {
        ObservableList<Category> categories = FXCollections.observableArrayList();
        String query = "SELECT id, name FROM category";
        try (Connection conn = DatabaseConnector.getConnection();
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
        String query = "SELECT id, name FROM supplier";
        try (Connection conn = DatabaseConnector.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                int id = rs.getInt("id");
                String name = rs.getString("name");
                suppliers.add(new Supplier(id, name));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        return suppliers;
    }
    public static ObservableList<Product> fetchAllProducts() {
        ObservableList<Product> products = FXCollections.observableArrayList();
        String query = "SELECT p.id, p.name, p.price, p.stockQuantity AS stock, p.barCode AS barcode, " +
                "c.name AS category, s.name AS supplier " +
                "FROM Product p " +
                "JOIN Category c ON p.categoryID = c.id " +
                "JOIN Supplier s ON p.supplierID = s.id";

        try (Connection conn = DatabaseConnector.getConnection();
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
                products.add(new Product(id, name, price, stock, barcode, category, supplier));

            }
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        return products;
    }

}
