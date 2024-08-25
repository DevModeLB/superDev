package com.devmode.superdev.Controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.scene.input.MouseEvent;


import java.io.IOException;

public class NavBarController {

    @FXML
    private TextField searchField;

    @FXML
    private Button searchButton;

    @FXML
    private Hyperlink productsLink;

    @FXML
    private Hyperlink orderLink;

    @FXML
    private Hyperlink categoryLink;

    @FXML
    public void initialize() {
        // Initialization code, if needed
    }

    @FXML
    private void handleSearchButtonAction(MouseEvent event) {
        String searchText = searchField.getText();
        System.out.println("Searching for: " + searchText);
        // Implement your search functionality here
    }

    @FXML
    private void handleProductsLinkAction(MouseEvent event) {
        switchScene(event, "/FXML/products/getProducts.fxml", "Products");
    }

    @FXML
    private void handleOrderLinkAction(MouseEvent event) {
        switchScene(event, "/FXML/order/addOrder.fxml", "Order"); // Update path as needed
    }

    @FXML
    private void handleCategoryLinkAction(MouseEvent event) {
        switchScene(event, "/FXML/categories/addCategory.fxml", "Categories");
    }

    private void switchScene(MouseEvent event, String path, String title) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(path));
            Parent root = loader.load();

            // Ensure the scene is attached before getting the window
            Scene currentScene = ((Node) event.getSource()).getScene();
            Stage stage = (Stage) currentScene.getWindow();

            stage.setTitle(title);
            stage.setScene(new Scene(root));
            stage.setFullScreen(true);
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Error while switching");
        }
    }

}
