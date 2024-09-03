package com.devmode.superdev.Controllers;



import com.devmode.superdev.Main;
import com.devmode.superdev.utils.ErrorDialog;
import com.devmode.superdev.utils.SceneSwitcher;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
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
import javafx.stage.WindowEvent;

import java.io.IOException;
import java.io.InputStream;
import java.text.NumberFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;



public class HomeController {
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
    // Map to track products and their quantities
    private final Map<Product, Integer> productQuantities = new HashMap<>();

    @FXML
    public void initialize() {
        // Initialize the label once and add it to the payment container
        priceLabel = new Label(totalPrice + " L.L");
        priceLabel.setPrefSize(134, 52); // Set preferred width and height
        priceLabel.setLayoutX(95); // Set X position
        priceLabel.setLayoutY(182); // Set Y position
        priceLabel.setStyle("-fx-font-size: 18;"); // Set font size
        priceLabel.setTextFill(Color.web("#0d134b")); // Set text color
        priceLabel.setAlignment(Pos.CENTER); // Center alignment
        priceLabel.setTextAlignment(javafx.scene.text.TextAlignment.CENTER); // Center text alignment
        priceLabel.setFont(Font.font("Britannic Bold", 15));
        paymanetcontainer.getChildren().add(priceLabel);
        ObservableList<Category> categories = DataFetcher.fetchCategories();
        categoriesContainer.getChildren().clear();
        for (Category category : categories) {
            Hyperlink categoryLink = new Hyperlink(category.getName());
            categoryLink.setStyle("-fx-font-size: 16; ");
            categoryLink.setPadding(new Insets(10));
            categoryLink.setPrefHeight(50.0);
            categoryLink.setPrefWidth(150.0);
            categoryLink.setAlignment(Pos.CENTER);
            categoryLink.getStyleClass().add("categorisation");
            categoryLink.setOnAction(event -> handleClickCategory(category));

            categoriesContainer.getChildren().add(categoryLink);
        }
        populateProductsContainer("none");
        if(isOn){
           updatePriceLabel("!");
        }else{
            NumberFormat numberFormat = NumberFormat.getNumberInstance(Locale.US);
            String formattedAmount = numberFormat.format(totalPrice);
            updatePriceLabel(formattedAmount);
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

    private AnchorPane createProductPane(Product product, boolean isInvoice) {
        AnchorPane pane = new AnchorPane();

        if (isInvoice) {
            // Style for products in the invoice
            pane.setPrefSize(278, 105);
            pane.setStyle("-fx-background-color: white; -fx-background-radius: 8; -fx-border-radius: 8;");
            DropShadow dropShadow = new DropShadow();
            dropShadow.setOffsetX(10);
            dropShadow.setOffsetY(10);
            dropShadow.setColor(Color.GRAY);
            dropShadow.setRadius(15);

            // Apply the shadow effect to the AnchorPane
            pane.setEffect(dropShadow);
        } else {
            // Style for products on the main page
            pane.setPrefSize(120, 150);
            pane.setStyle("-fx-background-color: white; -fx-background-radius: 12; -fx-border-radius: 12; -fx-padding: 10;");
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
        textPane.setLayoutX(isInvoice ? 100 :131);
        textPane.setLayoutY(isInvoice? 10 :17);
        if (isInvoice) {
            // Style for products in the invoice
            textPane.setPrefSize(145,85);
        } else {
            // Style for products on the main page
            textPane.setPrefSize(190, 115);
        }


        Label nameLabel = new Label(product.getName());
        nameLabel.setStyle(isInvoice ? "-fx-font-size: 21;" : "-fx-font-size: 32; -fx-wrap-text: true;");
        nameLabel.setWrapText(true);

        Label descriptionLabel = new Label(product.getDescription());
        descriptionLabel.setLayoutY(isInvoice?0 : 45);
        descriptionLabel.setStyle(isInvoice ? "-fx-opacity:0" : "-fx-font-size: 10; -fx-text-fill: #B5B7C0;-fx-wrap-text: true;");
        descriptionLabel.setPadding(new Insets(10, 0, 0, 0));

        Label priceLabel = new Label(product.getPrice() + "$");
        priceLabel.setLayoutY(isInvoice? 45 :87);
        priceLabel.setStyle(isInvoice ? "-fx-font-size: 14; -fx-background-color: #0D134B; -fx-text-fill: white; -fx-background-radius: 5;" : "-fx-font-size: 14; -fx-background-color: #0D134B; -fx-text-fill: white; -fx-background-radius:5;");
        priceLabel.setPadding(new Insets(3, 3, 3, 3));

        textPane.getChildren().addAll(nameLabel, descriptionLabel, priceLabel);
        pane.getChildren().addAll(imageView, textPane);

        pane.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> addToInvoice(product, pane));
        return pane;
    }

    private void updatePriceLabel(String formatedPrice) {
        if(formatedPrice == "!"){
            priceLabel.setText(String.format(isOn ? "%.2f $" : "%.2f L.L", totalPrice));
        }else{
            priceLabel.setText(formatedPrice + (isOn ? " $" : " L.L" ));
        }

    }

    private void addToInvoice(Product product, AnchorPane originalPane) {
        if (productQuantities.containsKey(product)) {
            // Product is already in the list; update quantity
            productQuantities.put(product, productQuantities.get(product) + 1);
        } else {
            // Create a copy of the originalPane for the invoice
            AnchorPane copiedPane = createProductPane(product, true); // Pass true for invoice styling
            productQuantities.put(product, 1);
            productsininvoice.getChildren().add(copiedPane);
        }
        updateInvoice();
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
                        Label newLabel = new Label(" x " + quantity);
                        newLabel.setStyle("-fx-font-size: 14; -fx-text-fill: #0D134B; -fx-padding: 5;");
                        newLabel.setLayoutY(42);
                        newLabel.setLayoutX(30);
                        textPane.getChildren().add(newLabel);
                        return newLabel;
                    });
            quantityLabel.setText(" x " + quantity);

            Label priceLabel = (Label) textPane.getChildren().get(2);
            double price = Double.parseDouble(priceLabel.getText().replace("$", "").trim());
            totalPrice += isOn ? price * quantity : (price * 89000) * quantity;

            productsininvoice.getChildren().add(pane);
        }

        priceLabel.setText(String.format(isOn ? "%.2f $" : "%.2f L.L", totalPrice));
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
            totalPrice /= 89000;
        }
        else{
            NumberFormat numberFormat = NumberFormat.getNumberInstance(Locale.US);
            totalPrice *= 89000;
            formattedAmount = numberFormat.format(totalPrice);
        }
        background.setFill(isOn ? Color.web("#00CBF9") : Color.web("#E0E0E0"));
        toggle.setTranslateX(isOn ? 25 : 0);
        updatePriceLabel(formattedAmount);
    }

    public void handleOpenCustomerInfo() {
        if(totalPrice == 0.0){
            new ErrorDialog().showErrorDialog("No products in the invoice", "Error");
        }else{
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/FXML/costumer.fxml"));
                Parent root = loader.load();

                CustomerInfoController controller = loader.getController();


                controller.setTotalAmount(totalPrice);
                controller.setProductQuantities(productQuantities);
                if(isOn){
                    controller.setIsDollar(true);
                }
                else{
                    controller.setIsDollar(false);
                }
                Stage stage = new Stage();
                stage.setTitle("Customer Info");
                stage.initModality(Modality.APPLICATION_MODAL);
                stage.setScene(new Scene(root));
                controller.setStage(stage);
                Stage stage1 = Main.getPrimaryStage();
                stage.setOnHidden(e -> refreshHomePage(stage1));
                stage.show();

            } catch (Exception e) {
                e.printStackTrace();
            }
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
