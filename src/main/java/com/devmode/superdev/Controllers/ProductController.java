package com.devmode.superdev.Controllers;

import com.devmode.superdev.models.Category;
import com.devmode.superdev.models.Product;
import com.devmode.superdev.models.Supplier;
import com.devmode.superdev.utils.DataFetcher;
import com.devmode.superdev.DatabaseConnector;
import com.devmode.superdev.utils.ErrorDialog;
import com.devmode.superdev.utils.SceneSwitcher;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.stage.FileChooser;
import javafx.util.Callback;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

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
    private TableView<Product> productTable;

    @FXML
    private TableColumn<Product, Integer> productIdColumn;
    @FXML
    private TableColumn<Product, String> productNameColumn;
    @FXML
    private TableColumn<Product, Double> priceColumn;
    @FXML
    private TableColumn<Product, Integer> stockColumn;
    @FXML
    private TableColumn<Product, String> barcodeColumn;
    @FXML
    private TableColumn<Product, String> categoryColumn;
    @FXML
    private TableColumn<Product, Integer> supplierIdColumn;
    @FXML
    private TableColumn<Product, Void> actionColumn;

    private File selectedImageFile;

    @FXML
    private void initialize() {
        if(categoryComboBox != null && supplierComboBox != null){
            ObservableList<Category> categories = DataFetcher.fetchCategories();
            ObservableList<Supplier> suppliers = DataFetcher.fetchSuppliers();
            categoryComboBox.setItems(categories);
            supplierComboBox.setItems(suppliers);
        }else{
            productIdColumn.setCellValueFactory(new PropertyValueFactory<>("productId"));
            productNameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
            priceColumn.setCellValueFactory(new PropertyValueFactory<>("price"));
            stockColumn.setCellValueFactory(new PropertyValueFactory<>("stock"));
            barcodeColumn.setCellValueFactory(new PropertyValueFactory<>("barcode"));
            categoryColumn.setCellValueFactory(new PropertyValueFactory<>("category"));
            supplierIdColumn.setCellValueFactory(new PropertyValueFactory<>("supplierId"));


            actionColumn.setCellFactory(new Callback<TableColumn<Product, Void>, TableCell<Product, Void>>() {
                @Override
                public TableCell<Product, Void> call(TableColumn<Product, Void> param) {
                    return new TableCell<Product, Void>() {
                        private final Button updateButton = new Button("");
                        private final Button deleteButton = new Button("");

                        {

                            updateButton.getStyleClass().add("updateButton");
                            deleteButton.getStyleClass().add("deleteButton");

                            updateButton.setOnAction(e -> handleUpdateAction(getTableRow().getItem()));
                            deleteButton.setOnAction(e -> handleDeleteAction(getTableRow().getItem()));
                        }

                        @Override
                        protected void updateItem(Void item, boolean empty) {
                            super.updateItem(item, empty);

                            setAlignment(javafx.geometry.Pos.CENTER);

                            if (empty || getTableRow() == null || getTableRow().getItem() == null) {
                                setGraphic(null);
                                return;
                            }
                            HBox hbox = new HBox(10, updateButton, deleteButton);
                            setGraphic(hbox);
                        }
                    };
                }
            });

            loadProducts();
        }
    }

    private void loadProducts() {
        ObservableList<Product> products = DataFetcher.fetchAllProducts();
        productTable.setItems(products);
    }

    @FXML
    private void handleAddProduct() {
        try {
            // Retrieve values from the UI components
            String name = productName.getText();
            String priceText = productPrice.getText();
            String quantityText = productQuantity.getText();
            String barCode = productBarCode.getText();
            String description = productDescription.getText();
            Category selectedCategory = categoryComboBox.getValue();
            Supplier selectedSupplier = supplierComboBox.getValue();

            if(name.isEmpty() || priceText.isEmpty() || quantityText.isEmpty() || barCode.isEmpty() || description.isEmpty() || selectedCategory == null || selectedSupplier == null){
                new ErrorDialog().showErrorDialog("All fields are required", "Error");
                return;
            }

            if (!isDouble(priceText)) {
                new ErrorDialog().showErrorDialog("Price must be a valid number.","Input Error");
                return;
            }

            if (!isInteger(quantityText)) {
                new ErrorDialog().showErrorDialog("Quantity must be a valid integer.","Input Error" );
                return;
            }

            double price = Double.parseDouble(priceText);
            int quantity = Integer.parseInt(quantityText);

            // Validate product details
            if (validateProductDetails(name, price, quantity, barCode, selectedCategory, selectedSupplier)) {
                // Save product to the database
                saveProduct(name, price, quantity, barCode, description, selectedCategory.getId(), selectedSupplier.getId());
            }

        } catch (NumberFormatException e) {
            new ErrorDialog().showErrorDialog("Invalid number format. Please enter valid values.", "Error");
        }
    }

    private boolean validateProductDetails(String name, double price, int quantity, String barCode, Category selectedCategory, Supplier selectedSupplier) {
        if (name == null || name.isEmpty()) {
            new ErrorDialog().showErrorDialog("Product name cannot be empty.", "Validation Error");
            return false;
        }
        if (price <= 0) {
            new ErrorDialog().showErrorDialog("Price must be a positive value.", "Validation Error");
            return false;
        }
        if (quantity < 0) {
            new ErrorDialog().showErrorDialog("Quantity cannot be negative.", "Validation Error");
            return false;
        }
        if (barCode == null || barCode.isEmpty()) {
            new ErrorDialog().showErrorDialog("BarCode cannot be empty.", "Validation Error");
            return false;
        }
        if (selectedCategory == null) {
            new ErrorDialog().showErrorDialog("You must select a category.", "Validation Error");
            return false;
        }
        if (selectedSupplier == null) {
            new ErrorDialog().showErrorDialog("You must select a supplier.", "Validation Error");
            return false;
        }
        return true;
    }

    private void saveProduct(String name, double price, int quantity, String barCode, String description, int categoryId, int supplierId) {
        String insertSQL = "INSERT INTO Product (name, price, stockQuantity, barCode, description, categoryID, supplierID, image) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection connection = DatabaseConnector.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(insertSQL)) {

            preparedStatement.setString(1, name);
            preparedStatement.setDouble(2, price);
            preparedStatement.setInt(3, quantity);
            preparedStatement.setString(4, barCode);
            preparedStatement.setString(5, description);
            preparedStatement.setInt(6, categoryId);
            preparedStatement.setInt(7, supplierId);

            if (selectedImageFile != null) {
                String imagePath = "product_images/" + selectedImageFile.getName();
                preparedStatement.setString(8, imagePath);
            } else {
                preparedStatement.setNull(8, java.sql.Types.VARCHAR);
            }

            int rowsAffected = preparedStatement.executeUpdate();
            if (rowsAffected > 0) {
                new ErrorDialog().showErrorDialog("Product added successfully", "success");
                clearInputs();
            } else {
                new ErrorDialog().showErrorDialog("Failed to insert product.","Database Error");
            }

        } catch (SQLException e) {
            new ErrorDialog().showErrorDialog("Error occurred while inserting product: " + e.getMessage(), "Database Error");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    // Helper method to check if a string can be parsed as a double
    private boolean isDouble(String value) {
        try {
            Double.parseDouble(value);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    // Helper method to check if a string can be parsed as an integer
    private boolean isInteger(String value) {
        try {
            Integer.parseInt(value);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private void clearInputs() {
        productName.clear();
        productPrice.clear();
        productQuantity.clear();
        productBarCode.clear();
        productDescription.clear();
        categoryComboBox.setValue(null);
        supplierComboBox.setValue(null);
        productImage.setImage(null);
        selectedImageFile = null; // Clear the image file reference
    }

    @FXML
    private void handleSelectImage() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.gif"));
        File file = fileChooser.showOpenDialog(productImage.getScene().getWindow());

        if (file != null) {
            selectedImageFile = file;
            Image image = new Image(file.toURI().toString());
            productImage.setImage(image);
            productImage.setY(400);
            productImage.setX(540);
        }
    }

    public void handleGetAddProduct(MouseEvent event) {
        new SceneSwitcher().switchScene(event, "/FXML/products/addProduct.fxml", "Add product");
    }

    public void handleUpdateAction(Product product){
        System.out.println("Update");
    }
    public void handleDeleteAction(Product product){
        System.out.println("Delete");
    }
}
