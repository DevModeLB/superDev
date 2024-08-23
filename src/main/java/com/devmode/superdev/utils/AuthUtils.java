package com.devmode.superdev.utils;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Stage;
import com.devmode.superdev.Main;
import com.devmode.superdev.SessionManager;

import java.io.IOException;

public class AuthUtils {

    public void checkAuthentication() {
        if (SessionManager.getInstance().getUsername() == null) {
            System.out.println("You are not logged in!");
            redirectToLogin();
        } else {
            System.out.println("Welcome: " + SessionManager.getInstance().getUsername());
        }
    }

    private void redirectToLogin() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/FXML/Login.fxml"));
            Parent loginView = loader.load();
            Stage stage = Main.getPrimaryStage();
            if (stage != null) {
                stage.setScene(new Scene(loginView));
                stage.show();
            } else {
                System.out.println("Primary stage is not initialized.");
            }
        } catch (IOException e) {
            e.printStackTrace();
            showErrorDialog("An error occurred while loading the login view.");
        }
    }

    protected void showErrorDialog(String message) {
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
