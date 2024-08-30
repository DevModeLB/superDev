package com.devmode.superdev.utils;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Alert.AlertType;
import java.util.Optional;

public class ConfirmationDialog {

    // Method to show a confirmation dialog
    public static boolean showConfirmation(String title, String headerText, String contentText) {
        Alert alert = new Alert(AlertType.CONFIRMATION);
        alert.setTitle(title);
        alert.setHeaderText(headerText);
        alert.setContentText(contentText);

        // Wait for the user's response
        Optional<ButtonType> result = alert.showAndWait();

        // Return true if the user confirmed, false otherwise
        return result.isPresent() && result.get() == ButtonType.OK;
    }
}
