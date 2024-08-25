package com.devmode.superdev.Controllers;


import com.devmode.superdev.utils.SceneSwitcher;
import javafx.scene.input.MouseEvent;

public class UserController {

    public void handleGetSuppliers(MouseEvent event) {
        new SceneSwitcher().switchScene(event, "/FXML/UsersAndSuppliers/supplier.fxml", "Suppliers");
    }

    public void handleGetAddUser(MouseEvent event) {
        new SceneSwitcher().switchScene(event, "/FXML/UsersAndSuppliers/addUser.fxml", "Add User");
    }
}
