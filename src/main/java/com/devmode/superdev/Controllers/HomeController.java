package com.devmode.superdev.Controllers;

import com.devmode.superdev.models.Product;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import com.devmode.superdev.models.Category;
import com.devmode.superdev.utils.DataFetcher;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import java.text.NumberFormat;
import java.util.Locale;
import java.io.InputStream;
import java.util.List;

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
    private AnchorPane paymanetcontainer;

    private boolean isOn = false;
    private Label priceLabel;

    @FXML
    public void initialize() {


        // Initialize the label once and add it to the payment container
        priceLabel = new Label("890,000");
        priceLabel.setPrefSize(134, 52); // Set preferred width and height
        priceLabel.setLayoutX(156); // Set X position
        priceLabel.setLayoutY(176); // Set Y position
        priceLabel.setStyle("-fx-font-size: 22;"); // Set font size
        priceLabel.setTextFill(Color.web("#0d134b")); // Set text color
        priceLabel.setAlignment(Pos.CENTER); // Center alignment
        priceLabel.setTextAlignment(javafx.scene.text.TextAlignment.CENTER); // Center text alignment
        priceLabel.setFont(Font.font("Britannic Bold", 15));
        paymanetcontainer.getChildren().add(priceLabel);



        ObservableList<Category> categories = DataFetcher.fetchCategories();

       
        categoriesContainer.getChildren().clear();

        
        for (Category category : categories) {
            Hyperlink categoryLink = new Hyperlink(category.getName());
            categoryLink.setStyle("-fx-font-size: 24;");
            categoryLink.setPrefHeight(85.0);
            categoryLink.setPrefWidth(230.0);
            categoryLink.getStyleClass().add("categorisation");
            categoryLink.setOnAction(event -> handleClickCategory(category));

           
            categoriesContainer.getChildren().add(categoryLink);
        }
        populateProductsContainer("none");
    }

    private void populateProductsContainer(String category) {
        // Create a new VBox to hold all product rows
        VBox productList = new VBox(10); // 10 is the spacing between rows
        productList.setPadding(new Insets(10));
        productList.setAlignment(Pos.TOP_CENTER);

        // Fetch products based on the category
        List<Product> products = DataFetcher.fetchAllProducts(category);

        int column = 0;
        HBox row = new HBox(10); // Spacing between products in a row

        for (Product product : products) {
            AnchorPane productPane = createProductPane(product); // Create product pane
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

        // Ensure productList has its height calculated
        productList.layout(); // Force layout computation
        productsContainer.setContent(productList);
        productsContainer.setFitToWidth(true); // Ensure content fits to the width of the ScrollPane
        productsContainer.setVbarPolicy(ScrollPane.ScrollBarPolicy.ALWAYS); // Show vertical scrollbar if needed
        productsContainer.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER); // Hide horizontal scrollbar if not needed
        productsContainer.requestLayout(); // Request layout update
        productsContainer.setPrefHeight(600);
        productsContainer.setVvalue(0);

    }







    private AnchorPane createProductPane(Product product) {
        AnchorPane pane = new AnchorPane();
        pane.setPrefSize(120, 150);
        pane.setStyle("-fx-background-color: white; -fx-background-radius: 12; -fx-border-radius: 12; -fx-padding: 10;");
        
        String imagePath = "/uploadedImages/" + product.getImagePath();
        InputStream imageStream = getClass().getResourceAsStream(imagePath);
        if (imageStream == null) {
            imageStream = getClass().getResourceAsStream("/images/placeholder.jpg");
        }

        ImageView imageView = new ImageView(new Image(imageStream));
        imageView.setFitHeight(115);
        imageView.setFitWidth(100);
        imageView.setLayoutX(14);
        imageView.setLayoutY(17);
        AnchorPane textPane = new AnchorPane();
        textPane.setLayoutX(131);
        textPane.setLayoutY(17);
        textPane.setPrefSize(190, 115);
        Label nameLabel = new Label(product.getName());
        nameLabel.setLayoutY(2);
        nameLabel.setStyle("-fx-font-size: 32; -fx-wrap-text: true;");
        nameLabel.setWrapText(true);
        Label descriptionLabel = new Label(product.getDescription());
        descriptionLabel.setLayoutY(46);
        descriptionLabel.setStyle("-fx-font-size: 10; -fx-text-fill: #B5B7C0;-fx-wrap-text: true;");
        descriptionLabel.setPadding(new javafx.geometry.Insets(10, 0, 0, 0));

        Label priceLabel = new Label(product.getPrice() + "$");
        priceLabel.setLayoutY(90);
        priceLabel.setStyle("-fx-font-size: 14; -fx-background-color: #0D134B; -fx-text-fill: white; -fx-background-radius:5;");
        priceLabel.setPadding(new javafx.geometry.Insets(3, 3, 3, 3));

        textPane.getChildren().addAll(nameLabel, descriptionLabel, priceLabel);
        pane.getChildren().addAll(imageView, textPane);

        return pane;
    }

    public void handleClickCategory(Category category) {
        productsContainer.setContent(null);
        String filter = "c.id = " + category.getId();
        populateProductsContainer(filter);
    }

    public void handleToggle(MouseEvent mouseEvent) {
        if (isOn) {
            background.setFill(Color.web("#E0E0E0"));
            toggle.setTranslateX(0);
            double priceValue = 10 * 89000;
            NumberFormat numberFormat = NumberFormat.getNumberInstance(Locale.US);
            String price = numberFormat.format(priceValue);
            System.out.println(price);
            priceLabel.setText(price);
        } else {
            background.setFill(Color.web("#00CBF9"));
            toggle.setTranslateX(25);
            priceLabel.setText("10$");
        }
        isOn = !isOn;
    }


}
