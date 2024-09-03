package com.devmode.superdev.Controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.devmode.superdev.DatabaseManager;

public class CustomerInfoController {

    @FXML
    private TextField phoneTextField;

    @FXML
    private Label pointsLabel;

    @FXML
    private Button checkButton;

    @FXML
    public void handleCheckPoints() {
        String phone = phoneTextField.getText().trim();

        if (phone.isEmpty()) {
            pointsLabel.setText("Please enter a phone number.");
            return;
        }

        try {
            int points = getPointsFromDatabase(phone);
            pointsLabel.setText("Points: " + points);
        } catch (SQLException | ClassNotFoundException e) {
            pointsLabel.setText("Error retrieving points.");
            e.printStackTrace();
        }
    }

    private int getPointsFromDatabase(String phone) throws SQLException, ClassNotFoundException {
        int points = 0;

        String query = "SELECT p.points " +
                       "FROM pointstransaction p " +
                       "JOIN customer c ON p.customerID = c.id " +
                       "WHERE c.phone = ?";

        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            
            statement.setString(1, phone);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    points = resultSet.getInt("points");
                }
            }
        }

        return points;
    }
}
