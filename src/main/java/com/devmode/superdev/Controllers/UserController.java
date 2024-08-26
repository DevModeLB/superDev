package com.devmode.superdev.Controllers;

import com.devmode.superdev.DatabaseConnector;
import com.devmode.superdev.models.User;
import com.devmode.superdev.utils.DataFetcher;
import com.devmode.superdev.utils.ErrorDialog;
import com.devmode.superdev.utils.SceneSwitcher;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.input.MouseEvent;
import javafx.util.Callback;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class UserController {

    @FXML
    private TableView<User> usersTable;

    @FXML
    private TableColumn<User, Integer> userId;
    @FXML
    private TableColumn<User, String> username;
    @FXML
    private TableColumn<User, String> role;
    @FXML
    private TableColumn<User, Void> actionsColumn;
    @FXML
    private TextField nameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private CheckBox adminCheckBox;

    @FXML
    private CheckBox cashierCheckBox;

    @FXML
    private void initialize() {
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

        if(usersTable != null){
            // Initialize columns
            userId.setCellValueFactory(new PropertyValueFactory<>("userId"));
            username.setCellValueFactory(new PropertyValueFactory<>("username"));
            role.setCellValueFactory(new PropertyValueFactory<>("role"));

            // Set up the actions column with buttons
            actionsColumn.setCellFactory(new Callback<TableColumn<User, Void>, TableCell<User, Void>>() {
                @Override
                public TableCell<User, Void> call(TableColumn<User, Void> param) {
                    return new TableCell<User, Void>() {
                        private final Button updateButton = new Button("");
                        private final Button deleteButton = new Button("");

                        {
                            updateButton.getStyleClass().add("updateButton");
                            deleteButton.getStyleClass().add("deleteButton");

                            updateButton.setOnAction(e -> handleUpdateAction(getTableRow().getItem()));
                            deleteButton.setOnAction(e -> handleDeleteAction(getTableRow().getItem()));
                        }

                        @Override
                        protected void updateItem(Void item, boolean empty) {
                            super.updateItem(item, empty);

                            if (empty || getTableRow() == null || getTableRow().getItem() == null) {
                                setGraphic(null);
                                return;
                            }

                            HBox hbox = new HBox(10, updateButton, deleteButton);
                            setGraphic(hbox);
                            setAlignment(javafx.geometry.Pos.CENTER); // Center the buttons
                        }
                    };
                }
            });

            // Load users
            loadUsers();
        }
    }

    private void loadUsers() {
        ObservableList<User> users = DataFetcher.fetchUsers();
        usersTable.setItems(users);
    }

    @FXML
    private void handleGetAddUser(MouseEvent event) {
        new SceneSwitcher().switchScene(event, "/FXML/UsersAndSuppliers/addUser.fxml", "Add User");
    }

    @FXML
    private void handleAddUser() {
        String username = nameField.getText();
        String password = passwordField.getText();
        boolean isAdmin = adminCheckBox.isSelected();
        boolean isCashier = cashierCheckBox.isSelected();

        if (username.isEmpty() || password.isEmpty()) {
            new ErrorDialog().showErrorDialog("Username and Password fields cannot be empty.", "Error");
            return;
        }

        if (!isAdmin && !isCashier) {
            new ErrorDialog().showErrorDialog("Please select a role", "Error");
            return;
        }

        // Insert user into database
        String roles = isAdmin ? "admin" : "cashier";

        try (Connection connection = DatabaseConnector.getConnection()) {
            String query = "INSERT INTO user (username, password, role) VALUES (?, ?, ?)";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, username);
            preparedStatement.setString(2, password);
            preparedStatement.setString(3, roles);

            int result = preparedStatement.executeUpdate();

            if (result > 0) {
                new ErrorDialog().showErrorDialog("User added succesfully", "success");
                clearFields();

            } else {
                new ErrorDialog().showErrorDialog("Something wen wrong, try again", "Error");
            }

        } catch (SQLException e) {
            e.printStackTrace();
            new ErrorDialog().showErrorDialog("Connection to database failed", "Error");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

    }




        private void clearFields() {
            nameField.clear();
            passwordField.clear();
            adminCheckBox.setSelected(false);
            cashierCheckBox.setSelected(false);
        }


    private void handleUpdateAction(User user) {
        System.out.println("Update user: " + user.getUserId());
        // Implement update logic here
    }

    private void handleDeleteAction(User user) {
        System.out.println("Delete user: " + user.getUserId());
        // Implement delete logic here
    }

    @FXML
    private void handleGetSuppliers(MouseEvent event){
        new SceneSwitcher().switchScene(event, "/FXML/UsersAndSuppliers/supplier.fxml", "Suppliers");
    }
}
