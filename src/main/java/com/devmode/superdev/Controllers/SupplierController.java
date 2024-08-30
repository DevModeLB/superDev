package com.devmode.superdev.Controllers;

import com.devmode.superdev.models.Supplier;
import com.devmode.superdev.utils.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import javafx.scene.control.TableCell;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.Button;
import com.devmode.superdev.DatabaseConnector;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;

import java.sql.ResultSet;

public class SupplierController {

    @FXML
    private TextField nameField;

    @FXML
    private TextField phoneField;

    @FXML
    private Button addButton;

    @FXML
    private TableView<Supplier> supplierTable;

    @FXML
    private TableColumn<Supplier, Integer> id;

    @FXML
    private TableColumn<Supplier, String> name;

    @FXML
    private TableColumn<Supplier, Void> actionColumn;

    @FXML
    private AnchorPane anchorPane;

    @FXML
    public void initialize() {
        if(id != null){
            id.setCellValueFactory(new PropertyValueFactory<>("id"));
            name.setCellValueFactory(new PropertyValueFactory<>("name"));


            addButtonToTable();

            // Fetch and load suppliers
            loadSuppliers();
        }
    }

    private void loadSuppliers() {
        supplierTable.getItems().setAll(DataFetcher.fetchSuppliers());
    }

    private void addButtonToTable() {
        actionColumn.setCellFactory(param -> new TableCell<Supplier, Void>() {
            private final Button updateButton = new Button("");
            private final Button deleteButton = new Button("");

            {
                updateButton.getStyleClass().add("updateButton");
                deleteButton.getStyleClass().add("deleteButton");

                updateButton.setOnMouseClicked(event -> {
                    Supplier supplier = getTableView().getItems().get(getIndex());
                    handleUpdate(event ,supplier);
                });

                deleteButton.setOnMouseClicked(event -> {
                    Supplier supplier = getTableView().getItems().get(getIndex());
                    handleDelete(event ,supplier);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    HBox hbox = new HBox(updateButton, deleteButton);
                    hbox.setSpacing(10); // Set spacing between buttons
                    setGraphic(hbox);
                }
            }
        });
    }

    private void handleUpdate(MouseEvent event, Supplier supplier) {

    }

    private void handleDelete(MouseEvent event, Supplier supplier) {
        boolean confirmed = ConfirmationDialog.showConfirmation(
                "Delete confirmation",
                "Are u sure u want to delete this supplier?",
                "Supplier: " + supplier.getName()
        );
        if(confirmed){
            DeleteFromDatabase.deleteFromDatabase("supplier", supplier.getId() );
            new SceneSwitcher().switchScene(event, "/FXML/UsersAndSuppliers/supplier.fxml", "Products");
        }
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
