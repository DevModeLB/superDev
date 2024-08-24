package com.devmode.superdev.Controllers;
import com.devmode.superdev.models.Category;
import com.devmode.superdev.models.Supplier;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import com.devmode.superdev.utils.DataFetcher;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;


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
        categoryComboBox.setItems(categories);
        supplierComboBox.setItems(suppliers);
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
}
