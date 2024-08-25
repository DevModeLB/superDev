package com.devmode.superdev.Controllers;

import com.devmode.superdev.utils.ErrorDialog;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import com.devmode.superdev.utils.SceneSwitcher;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.control.Button;
import com.devmode.superdev.DatabaseConnector;
import javafx.scene.input.MouseEvent;

import java.sql.ResultSet;

public class SupplierController {

    @FXML
    private TextField nameField;

    @FXML
    private TextField phoneField;

    @FXML
    private Button addButton;

    @FXML
    public void initialize() {
        // Initialization code, if needed
    }

    @FXML
    public void handleAddSupplier(ActionEvent event) {
        String name = nameField.getText().trim();
        String phone = phoneField.getText().trim();

        if (name.isEmpty() || phone.isEmpty()) {
            showError("All fields are required", "Error");
            return;
        }

        if (!isValidPhoneNumber(phone)) {
            showError("The phone number should starts with +961 and 8 digits", "Error");
            return;
        }

        if(!isPhoneNumberUnique(phone)){
            showError("The phone number already exists", "Error");
            return;
        }

        try {
            addSupplierToDatabase(name, phone);
            showError("Supplier added successfully", "success");
            clearFields();
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
            showError("Error adding supplier: " + e.getMessage(), "Error");
        }
    }

    @FXML
    private boolean isValidPhoneNumber(String phone) {
        String regex = "^\\+961\\d{8}$";
        return phone.matches(regex);
    }

    @FXML
    public boolean isPhoneNumberUnique(String phoneNumber) {
        String sql = "SELECT COUNT(*) FROM supplier WHERE phone_nb = ?";
        try (Connection connection = DatabaseConnector.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, phoneNumber);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getInt(1) == 0;
            }
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }

        return false;
    }

    private void addSupplierToDatabase(String name, String phone) throws SQLException, ClassNotFoundException {
        String insertSql = "INSERT INTO supplier (name, phone_nb) VALUES (?, ?)";

        try (Connection connection = DatabaseConnector.getConnection();
             PreparedStatement statement = connection.prepareStatement(insertSql)) {
            statement.setString(1, name);
            statement.setString(2, phone);
            statement.executeUpdate();
        }
    }



    private void showError(String message, String title) {
        ErrorDialog errorDialog = new ErrorDialog();
        errorDialog.showErrorDialog(message, title);
    }

    private void clearFields() {
        nameField.clear();
        phoneField.clear();
    }

    public void handleGetAddSupplier(MouseEvent event) {
        new SceneSwitcher().switchScene(event, "/FXML/UsersAndSuppliers/addSupplier.fxml", "Add Supplier");
    }

    public void handleGetUsers(MouseEvent event) {
        new SceneSwitcher().switchScene(event, "/FXML/UsersAndSuppliers/getUser.fxml", "Users");

    }
}
