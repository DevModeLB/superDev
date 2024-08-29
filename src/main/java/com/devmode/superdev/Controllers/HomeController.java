package com.devmode.superdev.Controllers;

import com.devmode.superdev.models.Product;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import com.devmode.superdev.models.Category;
import com.devmode.superdev.utils.DataFetcher;

import java.io.InputStream;
import java.util.List;

public class HomeController {
    @FXML
    private HBox categoriesContainer;

    @FXML
    private VBox productsContainer;

    @FXML
    public void initialize() {
        
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

        populateProductsContainer();
    }

    private void populateProductsContainer() {
        List<Product> products = DataFetcher.fetchAllProducts();

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
        textPane.setPrefSize(210, 115);

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
        System.out.println(category.getId());
    }
}
