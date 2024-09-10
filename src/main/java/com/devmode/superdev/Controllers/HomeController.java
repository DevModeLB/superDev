package com.devmode.superdev.Controllers;



import com.devmode.superdev.DatabaseManager;
import com.devmode.superdev.Main;
import com.devmode.superdev.SessionManager;
import com.devmode.superdev.models.PointsSettings;
import com.devmode.superdev.utils.AuthUtils;
import com.devmode.superdev.utils.ErrorDialog;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.stage.Modality;
import javafx.stage.Stage;

import com.devmode.superdev.models.Category;
import com.devmode.superdev.models.Product;
import com.devmode.superdev.utils.DataFetcher;
import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.text.NumberFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;



public class HomeController {
    public AnchorPane anchorPane;
    @FXML
    private HBox categoriesContainer;

    @FXML
    private ScrollPane productsContainer;

    @FXML
    private Rectangle background;

    @FXML
    private Circle toggle;

    @FXML
    private VBox productsininvoice;

    @FXML
    private AnchorPane paymanetcontainer;
    

    private boolean isOn = false;
    private double totalPrice = 0.0;
    private Label priceLabel;
    private double currencyRate = 89000.0;
    // Map to track products and their quantities
    private final Map<Product, Integer> productQuantities = new HashMap<>();

    @FXML
    public void initialize() {
        // Fetch the currency rate
        try {
            AuthUtils auth = new AuthUtils();
            auth.checkAuthentication();

            // Fetch the currency rate as a string
            String currencyRateStr = DataFetcher.fetchCurrencyRate();
            if (currencyRateStr == null || currencyRateStr.trim().isEmpty()) {
                new ErrorDialog().showErrorDialog("Currency is missing in the settings", "Error");
                return;
            }

            // Parse the currency rate
            try {
                currencyRate = Double.parseDouble(currencyRateStr);
            } catch (NumberFormatException e) {
                e.printStackTrace();
                new ErrorDialog().showErrorDialog("Invalid currency rate format.", "Error");
                return; // Exit the method or handle appropriately
            }


            // Initialize the label once and add it to the payment container
            priceLabel = new Label(totalPrice + " L.L");
            priceLabel.setPrefSize(155, 52); // Set preferred width and height
            priceLabel.setLayoutX(135); // Set X position
            priceLabel.setLayoutY(182); // Set Y position
            priceLabel.setStyle("-fx-font-size: 19;"); // Set font size
            priceLabel.setTextFill(Color.web("#0d134b")); // Set text color
            priceLabel.setAlignment(Pos.CENTER); // Center alignment
            priceLabel.setTextAlignment(javafx.scene.text.TextAlignment.CENTER); // Center text alignment
            priceLabel.setFont(Font.font("Britannic Bold", 15));
            paymanetcontainer.getChildren().add(priceLabel);
            ObservableList<Category> categories = DataFetcher.fetchCategories();
            categoriesContainer.getChildren().clear();
            for (Category category : categories) {
                Hyperlink categoryLink = new Hyperlink(category.getName());
                categoryLink.setStyle("-fx-font-size: 17; ");
                categoryLink.setPadding(new Insets(10));
                categoryLink.setPrefHeight(50.0);
                categoryLink.setPrefWidth(170.0);
                categoryLink.setAlignment(Pos.CENTER);
                categoryLink.getStyleClass().add("categorisation");
                categoryLink.setOnAction(event -> handleClickCategory(category));

                categoriesContainer.getChildren().add(categoryLink);
            }
            populateProductsContainer("none");
            if (isOn) {
                updatePriceLabel("!");
            } else {
                NumberFormat numberFormat = NumberFormat.getNumberInstance(Locale.US);
                String formattedAmount = numberFormat.format(totalPrice);
                updatePriceLabel(formattedAmount);
            }


            ClipboardMonitorController clipboardMonitor = new ClipboardMonitorController(this);
            clipboardMonitor.startMonitoring();
        }
        catch (Exception e) {
            e.printStackTrace();
            new ErrorDialog().showErrorDialog("An unexpected error occurred during initialization.", "Error");
    }


}



    private void populateProductsContainer(String category) {
        VBox productList = new VBox(10); // 10 is the spacing between rows
        productList.setPadding(new Insets(10));
        productList.setAlignment(Pos.TOP_CENTER);
        List<Product> products = DataFetcher.fetchAllProducts(category);
        int column = 0;
        HBox row = new HBox(10); // Spacing between products in a row

        for (Product product : products) {
            AnchorPane productPane = createProductPane(product, false); // Create product pane with main display styling
            row.getChildren().add(productPane); // Add product pane to row
            column++;
            if (column >= 3) { // If row has 3 products, add to VBox
                productList.getChildren().add(row);
                row = new HBox(10); // Start a new row
                column = 0;
            }
        }
        if (!row.getChildren().isEmpty()) { // Add any remaining products in the last row
            productList.getChildren().add(row);
        }

        productList.layout(); // Force layout computation
        productsContainer.setContent(productList);
        productsContainer.setFitToWidth(true);
        productsContainer.setVbarPolicy(ScrollPane.ScrollBarPolicy.ALWAYS);
        productsContainer.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        productsContainer.requestLayout();
        productsContainer.setPrefHeight(600);
        productsContainer.setVvalue(0);
    }





    public AnchorPane createProductPane(Product product, boolean isInvoice) {
        AnchorPane pane = new AnchorPane();

        if (isInvoice) {
            pane.setPrefSize(278, 105);
            pane.setStyle("-fx-background-color: white; -fx-background-radius: 8; -fx-border-radius: 8;");
            DropShadow dropShadow = new DropShadow();
            dropShadow.setOffsetX(10);
            dropShadow.setOffsetY(10);
            dropShadow.setColor(Color.GRAY);
            dropShadow.setRadius(15);
            pane.setEffect(dropShadow);
        } else {
            pane.setPrefSize(120, 150);
            pane.setStyle("-fx-background-color: white; -fx-background-radius: 12; -fx-border-radius: 12; -fx-padding: 10;-fx-cursor:Hand");
        }

        String imagePath = "/uploadedImages/" + product.getImagePath();
        InputStream imageStream = getClass().getResourceAsStream(imagePath);
        if (imageStream == null) {
            imageStream = getClass().getResourceAsStream("/images/placeholder.jpg");
        }

        ImageView imageView = new ImageView(new Image(imageStream));
        imageView.setFitHeight(isInvoice ? 85 : 115);
        imageView.setFitWidth(isInvoice ? 70 : 100);
        imageView.setLayoutX(isInvoice ? 5 : 14);
        imageView.setLayoutY(isInvoice ? 10 : 17);

        AnchorPane textPane = new AnchorPane();
        textPane.setLayoutX(isInvoice ? 100 : 131);
        textPane.setLayoutY(isInvoice ? 10 : 17);
        textPane.setPrefSize(isInvoice ? 145 : 190, isInvoice ? 85 : 115);

        Label nameLabel = new Label(product.getName());
        nameLabel.setStyle(isInvoice ? "-fx-font-size: 21;" : "-fx-font-size: 32; -fx-wrap-text: true;");
        nameLabel.setWrapText(true);

        Label descriptionLabel = new Label(product.getDescription());
        descriptionLabel.setLayoutY(isInvoice ? 0 : 45);
        descriptionLabel.setStyle(isInvoice ? "-fx-opacity:0" : "-fx-font-size: 10; -fx-text-fill: #B5B7C0;-fx-wrap-text: true;");
        descriptionLabel.setPadding(new Insets(10, 0, 0, 0));

        Label priceLabel = new Label(product.getPrice() + "$");
        priceLabel.setLayoutY(isInvoice ? 45 : 87);
        priceLabel.setStyle(isInvoice ? "-fx-font-size: 14; -fx-background-color: #0D134B; -fx-text-fill: white; -fx-background-radius: 5;" : "-fx-font-size: 14; -fx-background-color: #0D134B; -fx-text-fill: white; -fx-background-radius:5;");
        priceLabel.setPadding(new Insets(3, 3, 3, 3));

        textPane.getChildren().addAll(nameLabel, descriptionLabel, priceLabel);

        pane.getChildren().addAll(imageView, textPane);

        if (isInvoice) {
            // Add remove button to remove the product
            Label removeButton = new Label("x");
            removeButton.setStyle("-fx-font-size: 18; -fx-text-fill: #0D134B;");
            removeButton.setLayoutX(250);
            removeButton.setLayoutY(10);
            removeButton.setOnMouseClicked(event -> removeFromInvoice(product));
            pane.getChildren().add(removeButton);

            // Add decrease quantity button
            Label decreaseButton = new Label("-");
            decreaseButton.setStyle("-fx-font-size: 22; -fx-text-fill: #0D134B; ");
            decreaseButton.setLayoutX(150);
            decreaseButton.setLayoutY(55);
            decreaseButton.setOnMouseClicked(event -> decreaseQuantity(product));
            pane.getChildren().add(decreaseButton);

            // Add increase quantity button (optional)
            Label increaseButton = new Label("+");
            increaseButton.setStyle("-fx-font-size: 20; -fx-text-fill: #0D134B; ");
            increaseButton.setLayoutX(190);
            increaseButton.setLayoutY(55);
            increaseButton.setOnMouseClicked(event -> addToInvoice(product, pane));
            pane.getChildren().add(increaseButton);
        } else {
            pane.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> addToInvoice(product, pane));
        }

        return pane;
    }

    public void addToInvoice(Product product, AnchorPane pane) {
        int productId = product.getProductId();

        // Check if the product is already in the map by product ID
        boolean productExists = productQuantities.keySet().stream()
                .anyMatch(p -> p.getProductId() == productId);

        System.out.println("Product to add: " + product);
        System.out.println("Product exists in map: " + productExists);

        if (productExists) {
            // Find the product with the matching ID
            Product existingProduct = productQuantities.keySet().stream()
                    .filter(p -> p.getProductId() == productId)
                    .findFirst()
                    .orElse(null);

            if (existingProduct != null) {
                Integer currentQuantity = productQuantities.get(existingProduct);
                System.out.println("Current quantity: " + currentQuantity);

                if (currentQuantity != null) {
                    // Update quantity
                    productQuantities.put(existingProduct, currentQuantity + 1);
                } else {
                    System.err.println("Unexpected null quantity for product: " + existingProduct);
                    productQuantities.put(existingProduct, 1);
                }
            }
        } else {
            // Add new product with initial quantity of 1
            productQuantities.put(product, 1);
        }

        // Call updateInvoice to reflect changes
        updateInvoice();
    }



    private void removeFromInvoice(Product product) {
        productQuantities.remove(product);
        updateInvoice();
    }

    private void decreaseQuantity(Product product) {
        if (productQuantities.containsKey(product)) {
            int currentQuantity = productQuantities.get(product);
            if (currentQuantity > 1) {
                productQuantities.put(product, currentQuantity - 1);
            } else {
                productQuantities.remove(product);
            }
            updateInvoice();
        }
    }

    private void updatePriceLabel(String formatedPrice) {
        if(formatedPrice == "!"){
            priceLabel.setText(String.format(isOn ? "%.2f $" : "%.2f L.L", totalPrice));
        }else{
            priceLabel.setText(formatedPrice + (isOn ? " $" : " L.L" ));
        }

    }

    private void updateInvoice() {
        productsininvoice.getChildren().clear(); // Clear existing content
        totalPrice = 0.0;

        for (Map.Entry<Product, Integer> entry : productQuantities.entrySet()) {
            Product product = entry.getKey();
            int quantity = entry.getValue();
            AnchorPane pane = createProductPane(product, true);
            AnchorPane textPane = (AnchorPane) pane.getChildren().get(1);
            Label quantityLabel = (Label) textPane.getChildren().stream()
                    .filter(node -> node instanceof Label && ((Label) node).getStyle().contains("quantity"))
                    .findFirst()
                    .orElseGet(() -> {
                        Label newLabel = new Label("" + quantity);
                        newLabel.setStyle("-fx-font-size: 14; -fx-text-fill: #0D134B; -fx-padding: 5;");
                        newLabel.setLayoutY(45);
                        newLabel.setLayoutX(65);
                        textPane.getChildren().add(newLabel);
                        return newLabel;
                    });
            quantityLabel.setText("" + quantity);
            System.out.println(quantityLabel.getTranslateX());


            Label priceLabel = (Label) textPane.getChildren().get(2);
            double price = Double.parseDouble(priceLabel.getText().replace("$", "").trim());
            totalPrice += isOn ? price * quantity : (price * currencyRate) * quantity;

            productsininvoice.getChildren().add(pane);
        }
        NumberFormat numberFormat = NumberFormat.getNumberInstance(Locale.US);
        String formattedAmount = numberFormat.format(totalPrice);
        priceLabel.setText(String.format(isOn ?  formattedAmount + " $" : formattedAmount + "L.L"));
    }



    public void handleClickCategory(Category category) {
        productsContainer.setContent(null);
        String filter = "c.id = " + category.getId();
        populateProductsContainer(filter);
    }

    public void handleToggle(MouseEvent mouseEvent) {
        String formattedAmount = "!";
        isOn = !isOn;
        if(isOn){
            totalPrice /= currencyRate;
        }
        else{
            NumberFormat numberFormat = NumberFormat.getNumberInstance(Locale.US);
            totalPrice *= currencyRate;
            formattedAmount = numberFormat.format(totalPrice);
        }
        background.setFill(isOn ? Color.web("#00CBF9") : Color.web("#E0E0E0"));
        toggle.setTranslateX(isOn ? 25 : 0);
        updatePriceLabel(formattedAmount);
    }

    public void handleOpenCustomerInfo() {
        if (totalPrice == 0.0) {
            new ErrorDialog().showErrorDialog("No products in the invoice", "Error");
            return;
        }

        try {
            // Fetch points settings to check if activation is on
            PointsSettings settings = DataFetcher.fetchPointsSettings();
            if (settings == null) {
                new ErrorDialog().showErrorDialog("Failed to fetch points settings.", "Error");
                return;
            }

            boolean isActive = settings.isActive(); // Assuming isActivationOn() exists in PointsSettings
            if (!isActive) {
                // If activation is off, proceed with checkout without customer info
                checkoutWithoutCustomerInfo();
            } else {
                // Load the customer info window
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/FXML/costumer.fxml"));
                Parent root = loader.load();
                CustomerInfoController controller = loader.getController();
                controller.setTotalAmount(totalPrice);
                controller.setProductQuantities(productQuantities);
                controller.setIsDollar(isOn);
                Stage stage = new Stage();
                stage.setTitle("Customer Info");
                stage.initModality(Modality.APPLICATION_MODAL);
                stage.setScene(new Scene(root));
                controller.setStage(stage);
                Stage primaryStage = Main.getPrimaryStage();
                stage.setOnHidden(e -> refreshHomePage(primaryStage));
                stage.show();
            }

        } catch (Exception e) {
            e.printStackTrace();
            new ErrorDialog().showErrorDialog("An error occurred while processing.", "Error");
        }
    }

    private void checkoutWithoutCustomerInfo() {
        try {
            int userID = SessionManager.getInstance().getId();
            int invoiceID = DatabaseManager.createInvoice(totalPrice);
            int defaultCustomerID = DataFetcher.fetchDefaultCustomerId();
            int orderID = DatabaseManager.createOrder(totalPrice, "paid", userID, invoiceID, defaultCustomerID);
            for (Map.Entry<Product, Integer> entry : productQuantities.entrySet()) {
                Product product = entry.getKey();
                int quantity = entry.getValue();
                double subtotal = product.getPrice() * quantity;
                DatabaseManager.createOrderItem(orderID, product.getProductId(), subtotal);

                // Update product quantities
                DatabaseManager.updateProductQuantity(product.getProductId(), -quantity);
            }

            Stage primaryStage = Main.getPrimaryStage();
            refreshHomePage(primaryStage);

        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
            new ErrorDialog().showErrorDialog("Error during checkout.", "Error");
        }
    }


    public void refreshHomePage(Stage stage) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/FXML/home.fxml"));
            Parent root = loader.load();

            // Update the stage with the new scene
            Scene newScene = new Scene(root);
            stage.setScene(newScene);
            stage.setTitle("Home");
            stage.setFullScreen(true);
        } catch (IOException e) {
            e.printStackTrace();
            // Show an alert for better user feedback
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Error loading Home page");
            alert.setContentText("An error occurred while loading the Home page.");
            alert.showAndWait();
        }
    }





}
