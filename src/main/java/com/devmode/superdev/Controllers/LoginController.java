package com.devmode.superdev.Controllers;

import com.devmode.superdev.DatabaseManager;
import com.devmode.superdev.SessionManager;
import javafx.fxml.FXMLLoader;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Objects;

public class LoginController {

    public LoginController(){
    }

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Button loginButton;

    @FXML
    private void handleLoginButtonAction() {
        String username = usernameField.getText();
        String password = passwordField.getText();
        if (username.isEmpty() || password.isEmpty()) {
            showErrorDialog("Username or Password cannot be empty.");
            return;
        }
        Integer userId = getUserId(username, password);
        if (userId != null) {
            String role = getUserRole(username, password);
            SessionManager.getInstance().setUsername(username);
            SessionManager.getInstance().setId(userId);
            SessionManager.getInstance().setRole(role);
            if(Objects.equals(role, "admin")) {
                switchView("/FXML/home/home.fxml");
            }else{
                switchView("/FXML/home/CashierHome.fxml");
            }
        } else {
            showErrorDialog("Invalid username or password!");
        }
    }

    private Integer getUserId(String username, String password) {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        try {
            connection = DatabaseManager.getConnection();
            String query = "SELECT id FROM user WHERE username = ? AND password = ?";
            preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, username);
            preparedStatement.setString(2, password);
            resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getInt("id");
            } else {
                return null;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showErrorDialog("An error occurred while connecting to the database.");
            return null;
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        } finally {
            try {
                if (resultSet != null) resultSet.close();
                if (preparedStatement != null) preparedStatement.close();
                if (connection != null) connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
                showErrorDialog("An error occurred while closing the database connection.");
            }
        }
    }

    private String getUserRole(String username, String password) {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        try {
            connection = DatabaseManager.getConnection();
            String query = "SELECT role FROM user WHERE username = ? AND password = ?";
            preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, username);
            preparedStatement.setString(2, password);
            resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getString("role");
            } else {
                return null;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showErrorDialog("An error occurred while connecting to the database.");
            return null;
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        } finally {
            try {
                if (resultSet != null) resultSet.close();
                if (preparedStatement != null) preparedStatement.close();
                if (connection != null) connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
                showErrorDialog("An error occurred while closing the database connection.");
            }
        }
    }


    private void showErrorDialog(String message) {
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle("Login Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void switchView(String path) {
        try {
            // Load the Category FXML file
            FXMLLoader loader = new FXMLLoader(getClass().getResource(path));
            Parent categoryView = loader.load();
            // Get the current stage
            Stage stage = (Stage) loginButton.getScene().getWindow();
            // Set the new scene
            stage.setScene(new Scene(categoryView));
            stage.setFullScreen(true);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showErrorDialog("An error occurred while loading the category view.");
        }
    }
}
