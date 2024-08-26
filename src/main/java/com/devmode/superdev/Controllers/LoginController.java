package com.devmode.superdev.Controllers;

import com.devmode.superdev.SessionManager;
import com.devmode.superdev.DatabaseConnector;
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

public class LoginController {

    public LoginController(){
        System.out.println("Login controller");
        System.out.println(SessionManager.getInstance().getUsername());
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

        if (isValidCredentials(username, password)) {
            System.out.println("Login successful!");
            SessionManager.getInstance().setUsername(username);
            switchView();
        } else {
            showErrorDialog("Invalid username or password!");
        }
    }

    private boolean isValidCredentials(String username, String password) {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            connection = DatabaseConnector.getConnection();
            String query = "SELECT * FROM user WHERE username = ? AND password = ?";
            preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, username);
            preparedStatement.setString(2, password);
            resultSet = preparedStatement.executeQuery();

            return resultSet.next(); // Returns true if user is found

        } catch (SQLException e) {
            e.printStackTrace();
            showErrorDialog("An error occurred while connecting to the database.");
            return false;
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

    private void switchView() {
        try {
            // Load the Category FXML file
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/FXML/home.fxml"));
            Parent categoryView = loader.load();
            // Get the current stage
            Stage stage = (Stage) loginButton.getScene().getWindow();
            // Set the new scene
            stage.setScene(new Scene(categoryView));
            stage.setFullScreen(true);
            System.out.println("Switched view");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showErrorDialog("An error occurred while loading the category view.");
        }
    }
}
