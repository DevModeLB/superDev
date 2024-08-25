package com.devmode.superdev.Controllers;

import javafx.fxml.FXML;

import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import com.devmode.superdev.utils.ErrorDialog;

import com.devmode.superdev.utils.AuthUtils;
import com.devmode.superdev.DatabaseConnector;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class CategoryController {

    @FXML
    private TextField nameTextField;

    @FXML
    private Button addButton;

    @FXML
    private void handleAddCategory(){
        String categoryName = nameTextField.getText().trim();

        if (categoryName.isEmpty()) {
            ErrorDialog error = new ErrorDialog();
            error.showErrorDialog("Name Cannot be empty", "Error");
            return;
        }

        if(!isCategoryNameUnique(categoryName)){
            new ErrorDialog().showErrorDialog("Category already exists", "Error");
            return;
        }
        try (Connection conn = DatabaseConnector.getConnection()) {
            String sql = "INSERT INTO category (name) VALUES (?)";
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, categoryName);
                int rowsAffected = pstmt.executeUpdate();
                if (rowsAffected > 0) {
                    new ErrorDialog().showErrorDialog("Category added Succesfully", "success");
                } else {
                    new ErrorDialog().showErrorDialog("Failed to add category. Please try again.", "Database Error");
                }
            }
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
            new ErrorDialog().showErrorDialog("An error occurred while adding the category.", "Database Error");
        }
    }

    @FXML
    public void initialize() {
        AuthUtils authUtils = new AuthUtils();
        authUtils.checkAuthentication();
    }

    public boolean isCategoryNameUnique(String categoryName) {
        String validationSql = "SELECT * FROM category WHERE name = ?";
        try (Connection connection = DatabaseConnector.getConnection();
             PreparedStatement statement = connection.prepareStatement(validationSql)) {

            statement.setString(1, categoryName);

            try (ResultSet resultSet = statement.executeQuery()) {
                return !resultSet.next();
            }
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
            return false;
        }
    }
}
