package com.devmode.superdev.Controllers;

import com.devmode.superdev.models.LicenseResponse;
import com.devmode.superdev.utils.ErrorDialog;
import com.devmode.superdev.utils.SceneSwitcher;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

public class LicenseController {

    public TextField licenseKey;

    // This method will be triggered when the user clicks the validate button
    public void handleValidateKey(MouseEvent event) {
        String key = licenseKey.getText();

        // Check if the license key field is empty
        if (key == null || key.isEmpty()) {
            new ErrorDialog().showErrorDialog("Please fill all fields", "Error");
            return;
        }

        // Call the API to validate the license key
        LicenseResponse response = LicenseValidator.validateLicense(key);

        if (response != null && "success".equalsIgnoreCase(response.getStatus())) {
            // If the license is valid, store it in the database
            LicenseValidator.storeLicense(response.getData().getLiscence(), response.getData().getExpiresAt());
            // Show success message and close the license validation window
            new ErrorDialog().showErrorDialog("License validated successfully!", "success");
            new SceneSwitcher().switchScene(event, "/FXML/Login.fxml", "Login");
        } else {
            // Show error if validation failed
            String message = response != null ? response.getMessage() : "License validation failed.";
            new ErrorDialog().showErrorDialog(message, "Error");

        }
    }




}
