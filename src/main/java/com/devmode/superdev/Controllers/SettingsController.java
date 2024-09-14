package com.devmode.superdev.Controllers;

import com.devmode.superdev.DatabaseManager;
import com.devmode.superdev.utils.DataFetcher;
import com.devmode.superdev.utils.ErrorDialog;
import com.devmode.superdev.utils.SceneSwitcher;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;

import java.sql.Connection;
import java.sql.PreparedStatement;

public class SettingsController {

    public Label expiryDate;
    public Label key;
    @FXML
    private TextField rateTextField;

    @FXML
    private Button applyButton;

    @FXML
    private void initialize() {
        String currency_value = DataFetcher.fetchCurrencyRate();
        if (currency_value != null) {
            rateTextField.setText(currency_value);
        }

        String expiryDateStr = DataFetcher.expireDate();
        String keyStr = DataFetcher.fetchLicenseKey();
        expiryDate.setText("License Expiry Date: " + expiryDateStr);
        key.setText("License Key: " + keyStr);

        // Initially hide the Apply button
        applyButton.setVisible(false);

        // Add a listener to the TextField to detect changes
        rateTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null && !newValue.equals(currency_value)) {
                applyButton.setVisible(true); // Show the Apply button when the value changes
            } else {
                applyButton.setVisible(false); // Hide it if the value is the same
            }
        });
    }
    public void handlePoints(MouseEvent event) {
        SceneSwitcher switcher = new SceneSwitcher();
        switcher.switchScene(event, "/FXML/settings/pointssettings.fxml", "Points settings");
    }

    public void handleAboutUs(MouseEvent event) {
        SceneSwitcher switcher = new SceneSwitcher();
        switcher.switchScene(event, "/FXML/settings/aboutUs.fxml", "About US");
    }

    public void handleApplyRate(MouseEvent event) {
        String rateValue = rateTextField.getText();
        // Validate input (check if it's a number)
        if (rateValue == null || rateValue.isEmpty()) {
            new ErrorDialog().showErrorDialog("Please enter a valid rate.", "Error");
            return;
        }

        try {
            Double.parseDouble(rateValue); // Will throw NumberFormatException if not a valid number
        } catch (NumberFormatException e) {
            new ErrorDialog().showErrorDialog("Please enter a numerical value.", "Error");
            return;
        }

        // Update the settings table
        updateCurrencyRateInDB(rateValue);

        // Show success message
        new ErrorDialog().showErrorDialog("Settings Applied.", "success");

        rateTextField.setText(""); // Clear the text field after apply
        applyButton.setVisible(false); // Hide the button after applying the change
    }

    private void updateCurrencyRateInDB(String newRate) {
        String query = "UPDATE settings SET setting_value = ? WHERE setting_name = 'currency_rate'";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, newRate);
            int rowsUpdated = stmt.executeUpdate();
            if (rowsUpdated == 0) {
                String insertQuery = "INSERT INTO settings (setting_name, setting_value) VALUES ('currency_rate', ?)";
                try (PreparedStatement insertStmt = conn.prepareStatement(insertQuery)) {
                    insertStmt.setString(1, newRate);
                    insertStmt.executeUpdate();
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            new ErrorDialog().showErrorDialog("Something went wrong, try again.", "Error");
        }
    }
}
