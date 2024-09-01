package com.devmode.superdev.Controllers;

import com.devmode.superdev.utils.ErrorDialog;
import com.devmode.superdev.utils.SceneSwitcher;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import com.devmode.superdev.DatabaseManager;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Objects;

public class EditSupplierController {

    @FXML
    private TextField nameField;

    @FXML
    private TextField phoneField;

    @FXML
    private Button updateButton;

    private String supplierId;

    @FXML
    public void initialize() {
        Platform.runLater(() -> {
            Stage stage = (Stage) nameField.getScene().getWindow();
            String userId = stage.getTitle();
            if (userId != null) {
                supplierId = userId;
                loadSupplierDetails(userId);
            }
        });
    }

    @FXML
    public void handleAddSupplier(MouseEvent event) {
        String newName = nameField.getText();
        String newPhoneNumber = phoneField.getText();

        // Validate inputs
        if (newName == null || newName.trim().isEmpty()) {
            new ErrorDialog().showErrorDialog("Name cannot be empty", "Error");
            return;
        }
        if (newPhoneNumber == null || newPhoneNumber.trim().isEmpty()) {
            new ErrorDialog().showErrorDialog("Phone number cannot be empty", "Error");
            return;
        }
        if (!newPhoneNumber.matches("^\\+961\\d{8}$")) {
            new ErrorDialog().showErrorDialog("Invalid phone number format", "Error");
            return;
        }


        updateSupplierDetails(supplierId, newName, newPhoneNumber);
        new SceneSwitcher().switchScene(event, "/FXML/UsersAndSuppliers/supplier.fxml", "Suppliers");
    }

    private void updateSupplierDetails(String supplierId, String name, String phoneNumber) {
        String updateQuery = "UPDATE Supplier SET name = ?, phone_nb = ? WHERE id = ?";
        if(Objects.equals(nameField.getText(), name) && Objects.equals(phoneField.getText(), phoneNumber)){
            new ErrorDialog().showErrorDialog("Nothing to update", "Error");
            return;
        }
        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(updateQuery)) {

            preparedStatement.setString(1, name);
            preparedStatement.setString(2, phoneNumber);
            preparedStatement.setInt(3, Integer.parseInt(supplierId));

            int rowsAffected = preparedStatement.executeUpdate();
            if (rowsAffected > 0) {
                new ErrorDialog().showErrorDialog("Supplier updated successfully", "success");

            } else {
                new ErrorDialog().showErrorDialog("Supplier not found", "Error");
            }

        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
            new ErrorDialog().showErrorDialog("An error occurred while updating supplier details", "Error");
        }
    }

    // Method to load supplier details for editing
    public void loadSupplierDetails(String supplierId) {
        this.supplierId = supplierId;

        // Load supplier details from the database and populate the fields
        String query = "SELECT * FROM Supplier WHERE id = ?";

        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setInt(1, Integer.parseInt(supplierId));
            var resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                String name = resultSet.getString("name");
                String phoneNumber = resultSet.getString("phone_nb");
                nameField.setText(name);
                phoneField.setText(phoneNumber);
            } else {
                new ErrorDialog().showErrorDialog("Supplier not found", "Error");
            }

        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
            new ErrorDialog().showErrorDialog("An error occurred while loading supplier details", "Error");
        }
    }
}
