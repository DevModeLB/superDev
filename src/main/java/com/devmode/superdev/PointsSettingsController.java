package com.devmode.superdev;

import com.devmode.superdev.models.PointsSettings;
import com.devmode.superdev.utils.DataFetcher;
import com.devmode.superdev.utils.ErrorDialog;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;

import java.sql.Connection;
import java.sql.PreparedStatement;

public class PointsSettingsController {

    @FXML
    private Circle Pointstoggle;

    @FXML
    private Rectangle background;

    @FXML
    private TextField pointsStepField; // TextField for the "Points Step" (e.g., 10$)

    @FXML
    private TextField stepPointsField; // TextField for "Step Points" (e.g., 2 points)

    @FXML
    private TextField pointAmountField; // TextField for "1 Point Amount" (e.g., 1$)

    @FXML
    private Button applyButton; // Button to apply changes

    private boolean isOn = true; // Track the toggle state

    @FXML
    private void initialize() {
        PointsSettings settings = DataFetcher.fetchPointsSettings();
        if (settings != null) {
            pointsStepField.setText(settings.getPointsStep());
            stepPointsField.setText(settings.getStepPoints());
            pointAmountField.setText(settings.getPointAmount());
            isOn = settings.isActive();
            System.out.println("IS ACTIVE: ");
            System.out.println(settings.isActive());
            updateToggle();
        }

        // Add listeners to TextFields
        pointsStepField.textProperty().addListener((observable, oldValue, newValue) -> checkForChanges());
        stepPointsField.textProperty().addListener((observable, oldValue, newValue) -> checkForChanges());
        pointAmountField.textProperty().addListener((observable, oldValue, newValue) -> checkForChanges());

        // Initialize Apply button state
        checkForChanges();
    }

    @FXML
    public void handlePointsToggle(MouseEvent event) {
        isOn = !isOn;
        updateToggle();
        checkForChanges(); // Check for changes when toggle is switched
    }

    private void updateToggle(){
        background.setFill(isOn ? Color.web("#00CBF9") : Color.web("#E0E0E0")); // Change background color
        Pointstoggle.setTranslateX(isOn ? 25 : 0); // Move the toggle (circle)
    }

    private void checkForChanges() {
        boolean hasChanges = !pointsStepField.getText().equals(DataFetcher.fetchPointsSettings().getPointsStep()) ||
                !stepPointsField.getText().equals(DataFetcher.fetchPointsSettings().getStepPoints()) ||
                !pointAmountField.getText().equals(DataFetcher.fetchPointsSettings().getPointAmount()) ||
                isOn != DataFetcher.fetchPointsSettings().isActive();

        applyButton.setVisible(hasChanges); // Show or hide the Apply button based on changes
    }


    @FXML
    public void handleApply(MouseEvent event) {
        // Validate input
        String pointsStep = pointsStepField.getText();
        String stepPoints = stepPointsField.getText();
        String pointAmount = pointAmountField.getText();

        if (pointsStep.isEmpty() || stepPoints.isEmpty() || pointAmount.isEmpty()) {
            new ErrorDialog().showErrorDialog("Please fill in all fields.", "Error");
            return;
        }

        try {
            Double.parseDouble(pointsStep); // Validate pointsStep as number
            Double.parseDouble(stepPoints); // Validate stepPoints as number
            Double.parseDouble(pointAmount); // Validate pointAmount as number
        } catch (NumberFormatException e) {
            new ErrorDialog().showErrorDialog("Please enter valid numerical values.", "Error");
            return;
        }

        // Save the settings to the database
        savePointsSettingsToDB(pointsStep, stepPoints, pointAmount, isOn);

        // Display success message
        new ErrorDialog().showErrorDialog("Points settings applied successfully.", "success");

        // Hide the Apply button after successful save
        applyButton.setVisible(false);
    }

    private void savePointsSettingsToDB(String pointsStep, String stepPoints, String pointAmount, boolean isActive) {
        String activationStatus = isActive ? "active" : "inactive";

        try (Connection conn = DatabaseManager.getConnection()) {
            String updateQuery = "UPDATE settings SET setting_value = ? WHERE setting_name = ?";
            try (PreparedStatement stmt = conn.prepareStatement(updateQuery)) {
                stmt.setString(1, pointsStep);
                stmt.setString(2, "points_step");
                int rowsUpdated = stmt.executeUpdate();
                System.out.println("Rows updated for points_step: " + rowsUpdated);

                stmt.setString(1, stepPoints);
                stmt.setString(2, "step_points");
                rowsUpdated = stmt.executeUpdate();
                System.out.println("Rows updated for step_points: " + rowsUpdated);

                stmt.setString(1, pointAmount);
                stmt.setString(2, "point_amount");
                rowsUpdated = stmt.executeUpdate();
                System.out.println("Rows updated for point_amount: " + rowsUpdated);

                stmt.setString(1, activationStatus);
                stmt.setString(2, "points_activation");
                rowsUpdated = stmt.executeUpdate();
                System.out.println("Rows updated for points_activation: " + rowsUpdated);

                // Check if any rows were updated
                if (rowsUpdated == 0) {
                    System.out.println("Inserting");
                    // If no rows were updated, insert the setting
                    String insertQuery = "INSERT INTO settings (setting_name, setting_value) VALUES (?, ?)";
                    try (PreparedStatement insertStmt = conn.prepareStatement(insertQuery)) {
                        insertStmt.setString(1, "points_step");
                        insertStmt.setString(2, pointsStep);
                        insertStmt.executeUpdate();

                        insertStmt.setString(1, "step_points");
                        insertStmt.setString(2, stepPoints);
                        insertStmt.executeUpdate();

                        insertStmt.setString(1, "point_amount");
                        insertStmt.setString(2, pointAmount);
                        insertStmt.executeUpdate();

                        insertStmt.setString(1, "points_activation");
                        insertStmt.setString(2, activationStatus);
                        insertStmt.executeUpdate();
                    }
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
            new ErrorDialog().showErrorDialog("Something went wrong while saving settings.", "Error");
        }
    }
}
