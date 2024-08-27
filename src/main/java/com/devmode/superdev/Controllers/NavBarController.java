package com.devmode.superdev.Controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.TextField;
import com.devmode.superdev.utils.SceneSwitcher;

import javafx.scene.input.MouseEvent;





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
    }

    @FXML
    private void handleSearchButtonAction(MouseEvent event) {
        String searchText = searchField.getText();
        System.out.println("Searching for: " + searchText);
    }

    @FXML
    private void handleProductsLinkAction(MouseEvent event) {
        new SceneSwitcher().switchScene(event, "/FXML/products/getProducts.fxml", "Products");
    }

    @FXML
    private void handleOrderLinkAction(MouseEvent event) {
        new SceneSwitcher().switchScene(event, "/FXML/orders.fxml", "Order");
    }

    @FXML
    private void handleCategoryLinkAction(MouseEvent event) {
        new SceneSwitcher().switchScene(event, "/FXML/categories/Category.fxml", "Categories");
    }
}
