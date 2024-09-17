package com.devmode.superdev.Controllers;

import com.devmode.superdev.SessionManager;
import com.devmode.superdev.models.PointsSettings;
import com.devmode.superdev.models.Product;
import com.devmode.superdev.utils.*;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.NumberFormat;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import com.devmode.superdev.DatabaseManager;
import javafx.stage.Stage;

public class CustomerInfoController {

    @FXML
    private TextField phoneTextField;

    @FXML
    private Label pointsLabel;

    @FXML
    private Label usePointsLabel;

    @FXML
    private TextField usePointsTextField;

    @FXML
    private Button usePointsButton;

    @FXML
    private Button checkButton;

    @FXML
    private Button checkoutButton;

    @FXML
    private Label totalAmountLabel;

    private boolean isDollar = false; // true for USD, false for LBP
    private Map<Product, Integer> productQuantities = new HashMap<>();
    private double totalAmount;
    private int availablePoints;
    private int pointsUsed = 0;

    private double LBP_RATE = 89000.0;
    double thresholdUSD = 10.0;
    double thresholdLBP = thresholdUSD * LBP_RATE;
    int pointsPerThreshold = 2;
    private Stage stage;
    private double POINT_VALUE_USD;
    private double POINT_VALUE_LBP;

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    @FXML
    public void initialize() {
        try {
            AuthUtils auth = new AuthUtils();
            auth.checkAuthentication();

            // Fetch and parse currency rate
            String currencyRateStr = null;
            try {
                currencyRateStr = DataFetcher.fetchCurrencyRate();

            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            try {
                LBP_RATE = Double.parseDouble(currencyRateStr);
            } catch (NumberFormatException e) {
                throw new RuntimeException(e);
            }

            // Fetch and parse points settings
            PointsSettings settings = DataFetcher.fetchPointsSettings();
            if (settings == null) {
                new ErrorDialog().showErrorDialog("Update the point settings", "Error");
                return;
            }

            String pointsStepStr = settings.getPointsStep();
            thresholdUSD = Double.parseDouble(pointsStepStr);
            thresholdLBP = thresholdUSD * LBP_RATE;

            String pointAmountStr = settings.getPointAmount();
            POINT_VALUE_USD = Double.parseDouble(pointAmountStr);

            // Recalculate POINT_VALUE_LBP after POINT_VALUE_USD is set
            POINT_VALUE_LBP = POINT_VALUE_USD * LBP_RATE;

            String stepPointsStr = settings.getStepPoints();
            pointsPerThreshold = Integer.parseInt(stepPointsStr);

            // Debug statements
            System.out.println("LBP_RATE: " + LBP_RATE);
            System.out.println("thresholdUSD: " + thresholdUSD);
            System.out.println("POINT_VALUE_USD: " + POINT_VALUE_USD);
            System.out.println("POINT_VALUE_LBP: " + POINT_VALUE_LBP);
            System.out.println("pointsPerThreshold: " + pointsPerThreshold);

        } catch (NumberFormatException e) {
            e.printStackTrace();
            new ErrorDialog().showErrorDialog("Invalid number format in settings.", "Error");
        } catch (IllegalStateException e) {
            e.printStackTrace();
            new ErrorDialog().showErrorDialog("Failed to fetch points settings.", "Error");
        } catch (Exception e) {
            e.printStackTrace();
            new ErrorDialog().showErrorDialog("An unexpected error occurred during initialization.", "Error");
        }
    }

    public void setTotalAmount(double amount) {
        this.totalAmount = amount;
        updateTotalAmountLabel();
    }

    public void setIsDollar(boolean isDollar) {
        this.isDollar = isDollar;
        updateTotalAmountLabel();
    }

    public void setProductQuantities(Map<Product, Integer> productQuantities) {
        this.productQuantities = productQuantities;
    }

    // Update total amount label based on the currency
    private void updateTotalAmountLabel() {
        String currencySymbol = isDollar ? "$" : "L.L";

        // Create a NumberFormat instance for currency formatting
        NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(Locale.US);
        if (!isDollar) {
            // For L.L, we may need to format the number without the currency symbol and with custom locale
            currencyFormat = NumberFormat.getNumberInstance(Locale.getDefault()); // Use default locale or create a custom one
            currencyFormat.setGroupingUsed(true);
            currencyFormat.setMaximumFractionDigits(2);
        }
        // Format the total amount
        String formattedAmount = currencyFormat.format(totalAmount);

        // Set the formatted amount to the label
        totalAmountLabel.setText("Total amount: " + formattedAmount + " " + currencySymbol);
    }

    @FXML
    public void handleCheckPoints(MouseEvent event) {
        String phone = phoneTextField.getText().trim();
        if (phone.isEmpty()) {
            pointsLabel.setText("Please enter a phone number.");
            return;
        }

        try {
            availablePoints = getPointsFromDatabase(phone);
            if (availablePoints == -1) {
                pointsLabel.setText("No customer found with this phone number.");
                checkoutButton.setVisible(true);
            } else {
                // Calculate points amount
                BigDecimal pointsAmount = (isDollar) ?
                        BigDecimal.valueOf(availablePoints).multiply(BigDecimal.valueOf(POINT_VALUE_USD)) :
                        BigDecimal.valueOf(availablePoints).multiply(BigDecimal.valueOf(POINT_VALUE_LBP));

                // Round to 3 decimal places
                pointsAmount = pointsAmount.setScale(3, RoundingMode.HALF_UP);

                // Format pointsAmount with commas for thousands separators
                NumberFormat numberFormat = NumberFormat.getNumberInstance(Locale.US); // Adjust locale as needed
                numberFormat.setGroupingUsed(true);
                numberFormat.setMaximumFractionDigits(3); // Set to 3 decimal places

                // Format the amount
                String formattedPointsAmount = numberFormat.format(pointsAmount);

                // Update the pointsLabel text
                pointsLabel.setText(availablePoints + " = " + formattedPointsAmount);
                usePointsLabel.setVisible(true);
                usePointsTextField.setVisible(true);
                usePointsButton.setVisible(true);
                checkoutButton.setVisible(true);
            }

        } catch (SQLException | ClassNotFoundException e) {
            pointsLabel.setText("Error retrieving points.");
            e.printStackTrace();
        }
    }

    @FXML
    public void handleUsePoints(MouseEvent event) {
        try {
            // Retrieve and validate the points to use
            String pointsText = usePointsTextField.getText().trim();
            int pointsToUse = Integer.parseInt(pointsText);

            if (pointsToUse <= 0) {
                pointsLabel.setText("Please enter a positive number of points.");
                return;
            }

            if (pointsToUse > availablePoints) {
                pointsLabel.setText("You cannot use more points than available.");
                return;
            }

            // Calculate the discount
            double discount;
            if (isDollar) {
                discount = pointsToUse * POINT_VALUE_USD;
            } else {
                discount = pointsToUse * POINT_VALUE_LBP;
            }

            System.out.println("Discount: " + (isDollar ? "$" : "L.L") + discount);
            double remainingAmount = totalAmount - discount;

            if (remainingAmount < 0) {
                new ErrorDialog().showErrorDialog("Negative total amount", "error");
                return;
            }

            // Update total amount
            totalAmount = remainingAmount;
            updateTotalAmountLabel();

            // Update the points label to show the applied discount
            pointsLabel.setText("Discount applied: " + String.format("%.2f", discount));

            // Update available points
            availablePoints -= pointsToUse;
            this.pointsUsed = pointsToUse;

            // Reset points input field
            usePointsTextField.setText("0");

        } catch (NumberFormatException e) {
            pointsLabel.setText("Please enter a valid number of points.");
        }
    }

    @FXML
    public void handleCheckout(MouseEvent event) {
        try {
            int userID = SessionManager.getInstance().getId();
            int invoiceID = DatabaseManager.createInvoice(totalAmount);

            String phone = phoneTextField.getText().trim();
            int points = getPointsFromDatabase(phone);
            int customerID;

            if (points == -1) {
                customerID = DatabaseManager.createCustomer(phone);
            } else {
                customerID = getCustomerIdFromPhone(phone);
            }

            int orderID = DatabaseManager.createOrder(totalAmount, "paid", userID, invoiceID, customerID);

            for (Map.Entry<Product, Integer> entry : productQuantities.entrySet()) {
                Product product = entry.getKey();
                int quantity = entry.getValue();
                double subtotal = product.getPrice() * quantity;
                DatabaseManager.createOrderItem(orderID, product.getProductId(), subtotal);

                // update product quantities:
                DatabaseManager.updateProductQuantity(product.getProductId(), -quantity);
            }

            int pointsToAdd = calculatePoints(totalAmount);

            if (pointsUsed > 0) {
                System.out.println(pointsUsed);
                DatabaseManager.updateCustomerPoints(customerID, -pointsUsed);
                DatabaseManager.createPointTransaction(customerID, pointsUsed, orderID, "USED");
                System.out.println("Points deducted: " + pointsUsed);
                System.out.println("Remaining points for customer ID " + customerID + ": " + getPointsFromDatabase(phone));
            }else{
                DatabaseManager.updateCustomerPoints(customerID, pointsToAdd);
                DatabaseManager.createPointTransaction(customerID, pointsToAdd, orderID, "EARNED");
            }


            if (stage != null) {
                stage.close();
            }
            int remaining = getPointsFromDatabase(phone);
            if(remaining < 0){
                remaining = 0;
            }
            String currency = isDollar ? " $" : " L.L";
            NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(Locale.US);
            if (!isDollar) {
                // For L.L, we may need to format the number without the currency symbol and with custom locale
                currencyFormat = NumberFormat.getNumberInstance(Locale.getDefault()); // Use default locale or create a custom one
                currencyFormat.setGroupingUsed(true);
                currencyFormat.setMaximumFractionDigits(2);
            }
            // Format the total amount
            String formattedAmount = currencyFormat.format(totalAmount);
            String body = "Your order completed successfully\nTotal: " + formattedAmount + currency + "\nRemaining Points: " + remaining;
            SmsSender.sendSms(phone, body);
        } catch (SQLException | ClassNotFoundException e) {
            pointsLabel.setText("Error during checkout.");
            e.printStackTrace();
        }
    }

    private int getPointsFromDatabase(String phone) throws SQLException, ClassNotFoundException {
        int points = -1;
        String query = "SELECT points from customer where phone = ?";
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

    private int calculatePoints(double totalAmount) {
        System.out.println(isDollar);
        if (isDollar) {
            int points = (int) (totalAmount / thresholdUSD) * pointsPerThreshold;
            System.out.println(points);
            return points;
        } else {
            // Calculate points for LBP
            int points = (int) (totalAmount / thresholdLBP) * pointsPerThreshold;
            return points;
        }
    }


    public int getCustomerIdFromPhone(String phone) throws SQLException, ClassNotFoundException {
        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement("SELECT id FROM customer WHERE phone = ?")) {
            preparedStatement.setString(1, phone);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getInt("id");
                } else {
                    return -1;
                }
            }
        }
    }
}





