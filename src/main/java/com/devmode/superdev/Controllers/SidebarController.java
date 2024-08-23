package com.devmode.superdev.Controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Hyperlink;
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

    private void redirectToLogin() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/FXML/Login.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setTitle("Login");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();

        }
    }
}
