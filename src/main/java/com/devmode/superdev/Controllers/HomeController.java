package com.devmode.superdev.Controllers;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Hyperlink;
import javafx.scene.layout.HBox;
import com.devmode.superdev.models.Category;
import com.devmode.superdev.utils.DataFetcher;
import javafx.scene.text.Font;

public class HomeController {
    @FXML
    private HBox categoriesContainer;

    @FXML
    public void initialize() {
        // Fetch categories from the database
        ObservableList<Category> categories = DataFetcher.fetchCategories();

        // Clear the container before adding new content (optional, depending on your use case)
        categoriesContainer.getChildren().clear();

        // Dynamically create Hyperlink nodes for each category
        for (Category category : categories) {
            Hyperlink categoryLink = new Hyperlink(category.getName());
            categoryLink.setStyle("-fx-font-size: 24; -fx-text-fill: #0d134b;");
            categoryLink.setPrefHeight(85.0);
            categoryLink.setPrefWidth(230.0);
            categoryLink.getStyleClass().add("categorisation");

            // You can set an action on the hyperlink if needed
            categoryLink.setOnAction(event -> {
                // Handle the category click event
                System.out.println("Category clicked: " + category.getName());
            });

            // Add the hyperlink to the container
            categoriesContainer.getChildren().add(categoryLink);
        }
    }
}
