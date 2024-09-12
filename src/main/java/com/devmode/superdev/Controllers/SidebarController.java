package com.devmode.superdev.Controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Hyperlink;
import javafx.scene.input.MouseEvent;
import com.devmode.superdev.SessionManager;
import com.devmode.superdev.utils.SceneSwitcher;

import java.util.Objects;

public class SidebarController {

    @FXML
    private Hyperlink logoutLink;

    @FXML
    private void initialize() {

    }

    @FXML
    private void handleLogout(MouseEvent event) {
        SessionManager.getInstance().setUsername(null);
        redirectToLogin(event);
    }
    @FXML
    private void handleGets(MouseEvent event) {
        new SceneSwitcher().switchScene(event, "/FXML/products/getProducts.fxml", "Products");
    }


    @FXML
    private void handleSuppliers(MouseEvent event){
        new SceneSwitcher().switchScene(event, "/FXML/UsersAndSuppliers/getUser.fxml", "Suppliers");
    }


    private void redirectToLogin(MouseEvent event) {
        new SceneSwitcher().switchScene(event ,"/FXML/login.fxml", "Login");
    }

    @FXML
    private void handleHomePage(MouseEvent event){
        String role = SessionManager.getInstance().getRole();
        SceneSwitcher switcher = new SceneSwitcher();
        if(Objects.equals(role, "admin")){
            switcher.switchScene(event, "/FXML/home.fxml", "Home");
        }
        else{
            switcher.switchScene(event, "/FXML/Cashierhome.fxml", "Home");
        }
    }

    public void handleSettings(MouseEvent event) {
        new SceneSwitcher().switchScene(event, "/FXML/settings.fxml", "Settings");
    }

    public void handleGetStatistics(MouseEvent event) {
        SceneSwitcher switcher = new SceneSwitcher();
        switcher.switchScene(event, "/FXML/statistics.fxml", "Statistics");
    }
}
