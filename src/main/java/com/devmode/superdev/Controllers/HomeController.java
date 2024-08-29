package com.devmode.superdev.Controllers;

import com.devmode.superdev.models.Product;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import com.devmode.superdev.models.Category;
import com.devmode.superdev.utils.DataFetcher;
import java.io.InputStream;
import java.util.List;


public class HomeController {
    @FXML
    private HBox categoriesContainer;

    @FXML
    private GridPane gridPane;


    @FXML
    public void initialize() {
        // Fetch categories from the database
        ObservableList<Category> categories = DataFetcher.fetchCategories();

        // Clear the container before adding new content (optional, depending on your use case)
        categoriesContainer.getChildren().clear();

        // Dynamically create Hyperlink nodes for each category
        for (Category category : categories) {
            Hyperlink categoryLink = new Hyperlink(category.getName());
            categoryLink.setStyle("-fx-font-size: 24; ");
            categoryLink.setPrefHeight(85.0);
            categoryLink.setPrefWidth(230.0);
            categoryLink.getStyleClass().add("categorisation");

            // You can set an action on the hyperlink if needed
            categoryLink.setOnAction(event -> {
                handleClickCategory(category);

            });

            // Add the hyperlink to the container
            categoriesContainer.getChildren().add(categoryLink);

        }
        populateGridPane();
    }
    private void populateGridPane() {
        List<Product> products = DataFetcher.fetchAllProducts();

        int column = 0;
        int row = 0;
        for (Product product : products) {
            AnchorPane productPane = createProductPane(product);

            gridPane.add(productPane, column, row);

            column++;
            if (column > 2) { // Number of columns in your GridPane
                column = 0;
                row++;
            }
        }
    }


    private AnchorPane createProductPane(Product product) {
        AnchorPane pane = new AnchorPane();
        pane.setPrefSize(200, 200);
        pane.setStyle("-fx-background-color: white; -fx-background-radius: 12; -fx-border-radius: 12;");
        String imagePath = "/uploadedImages/" + product.getImagePath();
        InputStream imageStream = getClass().getResourceAsStream(imagePath);
        if(imageStream == null){
            imageStream = getClass().getResourceAsStream("/images/placeholder.jpg");
        }
        ImageView imageView = new ImageView(new Image(imageStream));
        imageView.setFitHeight(115);
        imageView.setFitWidth(102);
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
        priceLabel.setStyle("-fx-font-size: 14; -fx-background-color: #0D134B; -fx-text-fill: white; -fx-background-radius: 5;");
        priceLabel.setPadding(new javafx.geometry.Insets(3, 3, 3, 3));

        textPane.getChildren().addAll(nameLabel, descriptionLabel, priceLabel);
        pane.getChildren().addAll(imageView, textPane);

        return pane;
    }


    public void handleClickCategory(Category category){
        System.out.println(category.getId());
    }
}
