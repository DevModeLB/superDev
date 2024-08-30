package com.devmode.superdev.Controllers;

import com.devmode.superdev.models.Product;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import com.devmode.superdev.models.Category;
import com.devmode.superdev.utils.DataFetcher;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;

import java.io.InputStream;
import java.util.List;

public class HomeController {
    @FXML
    private HBox categoriesContainer;

    @FXML
    private VBox productsContainer;

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
        priceLabel = new Label("0Lb");
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

    private void populateProductsContainer(String query) {
        List<Product> products = DataFetcher.fetchAllProducts(query);
        int column = 0;
        HBox row = new HBox(10);
        for (Product product : products) {
            AnchorPane productPane = createProductPane(product);
            row.getChildren().add(productPane);
            column++;

            if (column >= 3) { 
                productsContainer.getChildren().add(row);
                row = new HBox(10); 
                column = 0;
            }
        }

        if (!row.getChildren().isEmpty()) {
            productsContainer.getChildren().add(row);
        }
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
        productsContainer.getChildren().clear();
        String filter = "c.id = " + category.getId();
        populateProductsContainer(filter);
    }

    public void handleToggle(MouseEvent mouseEvent) {
        if (isOn) {
            background.setFill(Color.web("#E0E0E0")); // Off background color
            toggle.setTranslateX(0); // Move toggle knob to the left
            priceLabel.setText("880.0Lb");

        } else {
            background.setFill(Color.web("#00CBF9")); // On background color
            toggle.setTranslateX(25); // Move toggle knob to the right;
            priceLabel.setText("10$");
        }
        isOn = !isOn;
    }


}
