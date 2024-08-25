package com.devmode.superdev.Controllers;
import com.devmode.superdev.models.Category;
import com.devmode.superdev.models.Supplier;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import com.devmode.superdev.utils.DataFetcher;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

import java.io.IOException;


public class ProductController {

    public TextField productName;
    public TextField productPrice;
    public TextField productQuantity;
    public TextField productBarCode;
    public ImageView productImage;
    public TextField productDescription;

    @FXML
    private ComboBox<Category> categoryComboBox;

    @FXML
    private ComboBox<Supplier> supplierComboBox;

    @FXML
    private void initialize() {
        ObservableList<Category> categories = DataFetcher.fetchCategories();
        ObservableList<Supplier> suppliers = DataFetcher.fetchSuppliers();
        if(categoryComboBox != null && supplierComboBox != null){
            categoryComboBox.setItems(categories);
            supplierComboBox.setItems(suppliers);
        }
    }

    @FXML
    private void handleAddProduct() {
        try {
            // Retrieve values from the UI components
            String name = productName.getText();
            double price = Double.parseDouble(productPrice.getText());
            int quantity = Integer.parseInt(productQuantity.getText());
            String barCode = productBarCode.getText();
            Category selectedCategory = categoryComboBox.getValue();
            Supplier selectedSupplier = supplierComboBox.getValue();

            // Print the values
            System.out.println("Name: " + name);
            System.out.println("Price: " + price);
            System.out.println("Quantity: " + quantity);
            System.out.println("BarCode: " + barCode);
            System.out.println("Selected Supplier: " + (selectedSupplier != null ? selectedSupplier.getId() : "None"));
            System.out.println("Selected Category: " + (selectedCategory != null ? selectedCategory.getId() : "None"));

        } catch (NumberFormatException e) {
            System.err.println("Invalid number format. Please enter valid values.");
            // Optionally, show an error message to the user
        }
    }


    private void saveProduct(int categoryId, int supplierId) {
        // Implement saving product with categoryId and supplierId to the database
    }

    public void handleGetAddProduct(ActionEvent event){
        switchScene(event, "/FXML/products/addProduct.fxml", "Add product");
    }
    @FXML
    private void switchScene(ActionEvent event, String path, String title) {
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
