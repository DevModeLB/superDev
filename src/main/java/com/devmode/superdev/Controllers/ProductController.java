package com.devmode.superdev.Controllers;
import com.devmode.superdev.models.Category;
import com.devmode.superdev.models.Supplier;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import com.devmode.superdev.utils.DataFetcher;



public class ProductController {

    @FXML
    private ComboBox<Category> categoryComboBox;

    @FXML
    private ComboBox<Supplier> supplierComboBox;

    @FXML
    private void initialize() {
        categoryComboBox.setItems(DataFetcher.fetchCategories());
        supplierComboBox.setItems(DataFetcher.fetchSuppliers());
    }

    @FXML
    private void handleAddProduct() {
        Category selectedCategory = categoryComboBox.getValue();
        Supplier selectedSupplier = supplierComboBox.getValue();

        if (selectedCategory != null && selectedSupplier != null) {
            int categoryId = selectedCategory.getId();
            int supplierId = selectedSupplier.getId();

            // Save these IDs to the database as needed
            saveProduct(categoryId, supplierId);
        } else {
            // Handle error: show message to the user
        }
    }

    private void saveProduct(int categoryId, int supplierId) {
        // Implement saving product with categoryId and supplierId to the database
    }
}
