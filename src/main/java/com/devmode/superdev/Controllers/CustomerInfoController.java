package com.devmode.superdev.Controllers;

import com.devmode.superdev.SessionManager;
import com.devmode.superdev.models.Product;
import com.devmode.superdev.utils.AuthUtils;
import com.devmode.superdev.utils.ErrorDialog;
import com.devmode.superdev.utils.SceneSwitcher;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
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
    private boolean isDollar = true;
    @FXML
    private Button checkoutButton;

    private Map<Product, Integer> productQuantities = new HashMap<>();
    @FXML
    private Label totalAmountLabel;
    private double totalAmount;
    private int availablePoints;
    private int pointsUsed = 0;

    private Stage stage; // Add a Stage variable

    public void setStage(Stage stage) {
        this.stage = stage;
    }


    @FXML
    public void initialize() {
        AuthUtils auth = new AuthUtils();
        auth.checkAuthentication();
    }
    public void setTotalAmount(double amount){
        this.totalAmount = amount;
        totalAmountLabel.setText("Total amount: " + totalAmount);
    }
    public void setIsDollar(boolean isDollar){
        this.isDollar = isDollar;
    }

    public void setProductQuantities(Map<Product, Integer> productQuantities) {
        this.productQuantities = productQuantities;
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
                pointsLabel.setText("" + availablePoints);
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
            double discount = (isDollar) ? pointsToUse : (pointsToUse * 89000.0);
            System.out.println("Discount: $" + discount);
            double temo = totalAmount - discount;
            if(temo < 0){
                new ErrorDialog().showErrorDialog("Amount im negativ", "error");
                return;
            }
            totalAmount -= discount;
            // Update the points label to show the applied discount
            pointsLabel.setText("Discount applied: " + String.format("%.2f", discount));
            // Update available points
            availablePoints -= pointsToUse;
            // Store the points used
            this.pointsUsed = pointsToUse;
            // Reset the points text field
            usePointsTextField.setText("0");

            // Update the total amount label
            totalAmountLabel.setText("Total Amount: " + String.format("%.2f", totalAmount));

        } catch (NumberFormatException e) {
            pointsLabel.setText("Please enter a valid number of points.");
        }
    }


    @FXML
    public void handleCheckout(MouseEvent event) {
        try {
            totalAmountLabel.setText(String.valueOf(totalAmount));
            int userID = SessionManager.getInstance().getId();
            // Create an invoice and get its ID
            int invoiceID = DatabaseManager.createInvoice(totalAmount);

            // Retrieve phone number from the text field
            String phone = phoneTextField.getText().trim();

            // Get points from database based on phone number
            int points = getPointsFromDatabase(phone);
            int customerID;

            // Check if the customer exists
            if (points == -1) {
                // Create a new customer if not found
                customerID = DatabaseManager.createCustomer(phone);
            } else {
                // Use existing customer ID
                customerID = getCustomerIdFromPhone(phone);
            }

            // Create the order and get its ID
            int orderID = DatabaseManager.createOrder(totalAmount, "paid", userID, invoiceID, customerID);

            // Create order items based on productQuantities
            for (Map.Entry<Product, Integer> entry : productQuantities.entrySet()) {
                Product product = entry.getKey();
                int quantity = entry.getValue();
                double subtotal = product.getPrice() * quantity;
                System.out.println("Adding to order: " + product + " with quantity: " + quantity);
                DatabaseManager.createOrderItem(orderID, product.getProductId(), subtotal);
            }

            // Handle points and point transactions
            if (points == -1) {
                // If it's a new customer, calculate and add points
                double pointsToAdd = calculatePoints(totalAmount);
                DatabaseManager.createPointTransaction(customerID, pointsToAdd, orderID, "EARNED");
                DatabaseManager.updateCustomerPoints(customerID, pointsToAdd);
                pointsLabel.setText("New customer created with " + pointsToAdd + " points.");
            } else {
                // If it's an existing customer, add points
                double pointsToAdd = calculatePoints(totalAmount);
                DatabaseManager.updateCustomerPoints(customerID, pointsToAdd);
                DatabaseManager.createPointTransaction(customerID, pointsToAdd, orderID, "EARNED");
                pointsLabel.setText("Checkout completed with total amount: $" + totalAmount + ". Points added: " + pointsToAdd);
            }

            System.out.println("Points used: " + pointsUsed);
            if (pointsUsed > 0) {
                DatabaseManager.updateCustomerPoints(customerID, -pointsUsed);
                DatabaseManager.createPointTransaction(customerID, pointsUsed, orderID, "USED");
                pointsLabel.setText("Checkout completed with total amount: $" + totalAmount + ". Points used: " + pointsUsed);
            }
            System.out.println("Order created with ID: " + orderID);

            if (stage != null) {
                stage.close();
            }

        } catch (SQLException e) {
            pointsLabel.setText("Error during checkout.");
            e.printStackTrace(); // Handle exception appropriately
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
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
        double percentage = 0.0;
        if (totalAmount >= 1 && totalAmount <= 150) {
            percentage = 0.5;
        } else if (totalAmount > 150 && totalAmount <= 300) {
            percentage = 0.8;
        } else if (totalAmount > 300) {
            percentage = 0.15;
        }
        // Calculate points
        double points = totalAmount * percentage / 0.5;
        System.out.println(points);
        // Round to nearest integer
        return (int) Math.round(points);
    }


    public int getCustomerIdFromPhone(String phone) throws SQLException, ClassNotFoundException {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        try {
            // Get a database connection
            connection = DatabaseManager.getConnection();

            // Define the SQL query to find the customer by phone number
            String query = "SELECT id FROM customer WHERE phone = ?";

            // Create a PreparedStatement to execute the query
            preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, phone);

            // Execute the query and get the result
            resultSet = preparedStatement.executeQuery();

            // Check if a result was found
            if (resultSet.next()) {
                // Return the customer ID
                return resultSet.getInt("id");
            } else {
                // No customer found, return -1 or handle as needed
                return -1;
            }

        } finally {
            // Ensure resources are closed properly
            if (resultSet != null) resultSet.close();
            if (preparedStatement != null) preparedStatement.close();
            if (connection != null) connection.close();
        }
    }

}
