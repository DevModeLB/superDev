package com.devmode.superdev.Controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Hyperlink;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import java.io.IOException;
import com.devmode.superdev.SessionManager;

public class SidebarController {

    @FXML
    private Hyperlink logoutLink;

    @FXML
    private void initialize() {

    }

    @FXML
    private void handleLogout() {
        Stage stage = (Stage) logoutLink.getScene().getWindow();
        stage.close();
        SessionManager.getInstance().setUsername(null);
        redirectToLogin();
    }

    @FXML
    private void handleSuppliers(MouseEvent event){
        switchScene(event, "/FXML/UsersAndSuppliers/addSupplier.fxml", "Suppliers");
    }

    private void redirectToLogin() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/FXML/Login.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle("Login");
            stage.setScene(new Scene(root));
            stage.setFullScreen(true);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();

        }
    }
    private void switchScene(MouseEvent event, String path, String title) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(path));
            Parent root = loader.load();

            // Ensure the scene is attached before getting the window
            Scene currentScene = ((Node) event.getSource()).getScene();
            Stage stage = (Stage) currentScene.getWindow();

            stage.setTitle(title);
            stage.setScene(new Scene(root));
            stage.setFullScreen(true);
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Error while switching");
        }
    }
}
