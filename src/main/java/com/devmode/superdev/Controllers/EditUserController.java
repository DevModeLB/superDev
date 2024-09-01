package com.devmode.superdev.Controllers;

import com.devmode.superdev.models.User;
import com.devmode.superdev.DatabaseManager;
import com.devmode.superdev.utils.ErrorDialog;
import com.devmode.superdev.utils.SceneSwitcher;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Objects;
import java.util.ResourceBundle;

public class EditUserController implements Initializable {

    @FXML
    private AnchorPane anchorPane;

    @FXML
    private TextField nameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private CheckBox adminCheckBox;

    @FXML
    private CheckBox cashierCheckBox;

    @FXML
    private Button updateButton;

    private User user;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        if(adminCheckBox != null && cashierCheckBox != null){
            adminCheckBox.selectedProperty().addListener((observable, oldValue, newValue) -> {
                if (newValue) {
                    cashierCheckBox.setSelected(false);
                }
            });

            cashierCheckBox.selectedProperty().addListener((observable, oldValue, newValue) -> {
                if (newValue) {
                    adminCheckBox.setSelected(false);
                }
            });
        }

        Platform.runLater(() -> {
            Stage stage = (Stage) nameField.getScene().getWindow();
            String userId = stage.getTitle();
            if (userId != null) {
                loadUserDetails(userId);
            }
        });
    }

    public void setUser(User user) {
        this.user = user;
        if (user != null) {
            nameField.setText(user.getUsername());
            boolean admin = Objects.equals(user.getRole(), "admin");
            adminCheckBox.setSelected(admin);
            cashierCheckBox.setSelected(!admin);
        }
    }

    private void loadUserDetails(String userId) {
        String query = "SELECT * FROM User WHERE id = ?";
        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setInt(1, Integer.parseInt(userId));

            var resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                // Assuming the table has columns 'username', 'password', 'isAdmin', 'isCashier'
                String username = resultSet.getString("username");
                String password = resultSet.getString("password");
                String role = resultSet.getString("role");
                User user = new User(Integer.parseInt(userId), username, password, role);
                setUser(user);
            } else {
                // Handle case where no user is found
                new ErrorDialog().showErrorDialog("User not found", "Error");
            }

        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
            new ErrorDialog().showErrorDialog("An error occurred while loading user details", "Error");
        }
    }


    @FXML
    public void handleAddUser(MouseEvent event) {
        if (user == null) {
            new ErrorDialog().showErrorDialog("No user to update", "Error");
            return;
        }
        String newUsername = nameField.getText();
        String newPassword = passwordField.getText();
        boolean isAdmin = adminCheckBox.isSelected();
        String role = (isAdmin) ? "admin" : "cashier";
        ErrorDialog errorDialog = new ErrorDialog();

        // Validation
        if (newUsername == null || newUsername.trim().isEmpty()) {
            errorDialog.showErrorDialog("Username cannot be empty", "Error");
            return;
        }

        if (newUsername.equals(user.getUsername()) &&
                role.equals(user.getRole()) &&
                newPassword.trim().isEmpty())
        {
            errorDialog.showErrorDialog("Nothing to update", "Error");
            return;
        }

        if(newPassword.trim().isEmpty()){
            String query = "UPDATE User SET username = ?, role = ? WHERE id = ?";
            try (Connection connection = DatabaseManager.getConnection();
                 PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                preparedStatement.setString(1, newUsername);
                preparedStatement.setString(2, role );
                preparedStatement.setInt(3, user.getUserId());
                int rowsAffected = preparedStatement.executeUpdate();
                if (rowsAffected > 0) {
                    errorDialog.showErrorDialog("User updated successfully", "success");
                    new SceneSwitcher().switchScene(event, "/FXML/UsersAndSuppliers/getUser.fxml", "Users");
                } else {
                    errorDialog.showErrorDialog("Failed to update user", "Error");
                }

            } catch (SQLException | ClassNotFoundException e) {
                e.printStackTrace();
                errorDialog.showErrorDialog("An error occurred while updating the user", "Error");
            }
        }
        else{
            String query = "UPDATE User SET username = ?, password = ?, role = ? WHERE id = ?";
            try (Connection connection = DatabaseManager.getConnection();
                 PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                preparedStatement.setString(1, newUsername);
                preparedStatement.setString(2, newPassword);
                preparedStatement.setString(3, role );
                preparedStatement.setInt(4, user.getUserId());

                int rowsAffected = preparedStatement.executeUpdate();
                if (rowsAffected > 0) {
                    errorDialog.showErrorDialog("User updated successfully", "success");
                    new SceneSwitcher().switchScene(event, "/FXML/UsersAndSuppliers/getUser.fxml", "Users");
                } else {
                    errorDialog.showErrorDialog("Failed to update user", "Error");
                }

            } catch (SQLException | ClassNotFoundException e) {
                e.printStackTrace();
                errorDialog.showErrorDialog("An error occurred while updating the user", "Error");
            }
        }

    }
}
