package com.devmode.superdev.Controllers;

import com.devmode.superdev.models.User;
import com.devmode.superdev.utils.DataFetcher;
import com.devmode.superdev.utils.SceneSwitcher;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.input.MouseEvent;
import javafx.util.Callback;

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
    private void initialize() {
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

    private void loadUsers() {
        ObservableList<User> users = DataFetcher.fetchUsers();
        usersTable.setItems(users);
    }

    @FXML
    private void handleGetAddUser(MouseEvent event) {
        new SceneSwitcher().switchScene(event, "/FXML/users/addUser.fxml", "Add User");
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
