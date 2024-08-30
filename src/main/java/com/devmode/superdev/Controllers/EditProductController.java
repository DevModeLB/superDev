package com.devmode.superdev.Controllers;

import com.devmode.superdev.DatabaseConnector;
import com.devmode.superdev.models.Category;
import com.devmode.superdev.models.Supplier;
import com.devmode.superdev.utils.DataFetcher;
import com.devmode.superdev.utils.ErrorDialog;
import com.devmode.superdev.utils.RandomStringGenerator;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;
import com.devmode.superdev.models.Product;


public class EditProductController implements Initializable {

    @FXML
    private TextField productName;
    @FXML
    private TextField productPrice;
    @FXML
    private TextField productQuantity;
    @FXML
    private TextField productBarCode;
    @FXML
    private ComboBox<Category> categoryComboBox;
    @FXML
    private ComboBox<Supplier> supplierComboBox;
    @FXML
    private ImageView productImage;
    @FXML
    private TextField productDescription;

    private DatabaseConnector databaseConnector;
    private File selectedImageFile;
    private String productID;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        databaseConnector = new DatabaseConnector();

        Platform.runLater(() -> {
            Stage stage = (Stage) productName.getScene().getWindow();
            String productId = stage.getTitle();
            if (productId != null) {
                productID = stage.getTitle();
                loadProductDetails(productId);
            }
        });

        ObservableList<Category> categories = DataFetcher.fetchCategories();
        ObservableList<Supplier> suppliers = DataFetcher.fetchSuppliers();
        categoryComboBox.setItems(categories);
        supplierComboBox.setItems(suppliers);
    }

    private void loadProductDetails(String productId) {
        Product product = fetchProductFromDatabase(Integer.parseInt(productId));
        if (product != null) {
            productName.setText(product.getName());
            productPrice.setText(String.valueOf(product.getPrice()));
            productQuantity.setText(String.valueOf(product.getStock()));
            productBarCode.setText(product.getBarcode());
            productDescription.setText(product.getDescription());

            if (product.getImagePath() != null) {
                String path = "file:src/main/resources/uploadedImages/"+ product.getImagePath();
                System.out.println(path);
                Image image = new Image(path);
                productImage.setImage(image);
                productImage.setY(400);
                productImage.setX(540);
            }
            for (Category category : categoryComboBox.getItems()) {
                if (Integer.parseInt(product.getCategory()) == category.getId()) {
                    categoryComboBox.setPromptText(category.getName());
                    categoryComboBox.setValue(category);
                    break;
                }
            }

            for (Supplier supplier : supplierComboBox.getItems()) {
                if (supplier.getId() == Integer.parseInt(product.getSupplierId())) {
                    supplierComboBox.setPromptText(supplier.getName());
                    supplierComboBox.setValue(supplier);
                    break;
                }
            }
        }
    }

    private Product fetchProductFromDatabase(int productId) {
        String query = "SELECT p.*, c.name as categoryName, s.name as supplierName FROM product p " +
                "JOIN category c ON p.categoryID = c.id " +
                "JOIN supplier s ON p.supplierID = s.id " +
                "WHERE p.id = ?";
        try (Connection connection = DatabaseConnector.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setInt(1, productId);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                return new Product(
                        resultSet.getInt("id"),
                        resultSet.getString("name"),
                        resultSet.getDouble("price"),
                        resultSet.getInt("stockQuantity"),
                        resultSet.getString("barCode"),
                        resultSet.getString("categoryID"),
                        resultSet.getString("supplierID"),
                        resultSet.getString("image"),
                        resultSet.getString("description")
                );
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

        return null;
    }

    @FXML
    private void handleSelectImage() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.gif"));
        File file = fileChooser.showOpenDialog(productImage.getScene().getWindow());
        if (file != null) {
            try {
                Path resourcesDir = Paths.get("src/main/resources/uploadedImages");
                Files.createDirectories(resourcesDir);
                String randomPrefix = RandomStringGenerator.generateRandomLetters(4);
                String newFileName = randomPrefix + "_" + file.getName();
                Path targetPath = resourcesDir.resolve(newFileName);
                Files.copy(file.toPath(), targetPath, StandardCopyOption.REPLACE_EXISTING);


                Image image = new Image(targetPath.toUri().toString());
                productImage.setImage(image);
                // Position the image view
                productImage.setY(400);
                productImage.setX(540);

                // Store the selected image file reference
                selectedImageFile = targetPath.toFile();

            } catch (IOException e) {
                e.printStackTrace();
                // Handle the exception (e.g., show an error message to the user)
            }
        }
    }


    @FXML
    public void handleEditProduct(ActionEvent event) {

        // Fetch the existing product image path before updating
        String oldImagePath = null;
        Product existingProduct = fetchProductFromDatabase(Integer.parseInt(productID));
        if (existingProduct != null) {
            oldImagePath = existingProduct.getImagePath();
        }

        // Get existing values
        String oldName = existingProduct.getName();
        double oldPrice = existingProduct.getPrice();
        int oldStock = existingProduct.getStock();
        String oldBarCode = existingProduct.getBarcode();
        int oldCategoryId = Integer.parseInt(existingProduct.getCategory());
        int oldSupplierId = Integer.parseInt(existingProduct.getSupplierId());
        String oldImagePath1 = existingProduct.getImagePath();
        String oldDescription = existingProduct.getDescription();

        ErrorDialog errorDialog = new ErrorDialog();

        // Get current values from UI
        String newName = productName.getText();
        double newPrice;
        int newStock;
        String newBarCode = productBarCode.getText();
        int newCategoryId;
        int newSupplierId;
        String newImagePath = (selectedImageFile != null) ? selectedImageFile.getName() : oldImagePath;
        String newDescription = productDescription.getText();

        // Validation
        if (newName == null || newName.trim().isEmpty()) {
            errorDialog.showErrorDialog("Product name cannot be empty", "Error");
            return;
        }

        try {
            newPrice = Double.parseDouble(productPrice.getText());
        } catch (NumberFormatException e) {
            errorDialog.showErrorDialog("Invalid price format", "Error");
            return;
        }

        try {
            newStock = Integer.parseInt(productQuantity.getText());
        } catch (NumberFormatException e) {
            errorDialog.showErrorDialog("Invalid stock quantity format", "Error");
            return;
        }

        if (categoryComboBox.getValue() == null) {
            errorDialog.showErrorDialog("Please select a category", "Error");
            return;
        }
        newCategoryId = categoryComboBox.getValue().getId();

        if (supplierComboBox.getValue() == null) {
            errorDialog.showErrorDialog("Please select a supplier", "Error");
            return;
        }
        newSupplierId = supplierComboBox.getValue().getId();

        if (newBarCode == null || newBarCode.trim().isEmpty()) {
            errorDialog.showErrorDialog("Bar code cannot be empty", "Error");
            return;
        }

        if (newDescription == null || newDescription.trim().isEmpty()) {
            errorDialog.showErrorDialog("Description cannot be empty", "Error");
            return;
        }


        // Check if any value has changed
        boolean hasChanges = !oldName.equals(newName) ||
                oldPrice != newPrice ||
                oldStock != newStock ||
                !oldBarCode.equals(newBarCode) ||
                oldCategoryId != newCategoryId ||
                oldSupplierId != newSupplierId ||
                !oldImagePath1.equals(newImagePath) ||
                !oldDescription.equals(newDescription);

        if (!hasChanges) {
            new ErrorDialog().showErrorDialog("Nothing new to update", "Error");
            return;
        }


        String query = "UPDATE product SET name = ?, price = ?, stockQuantity = ?, barCode = ?, categoryID = ?, supplierID = ?, image = ?, description = ? WHERE id = ?";
        try (Connection connection = DatabaseConnector.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setString(1, productName.getText());
            preparedStatement.setDouble(2, Double.parseDouble(productPrice.getText()));
            preparedStatement.setInt(3, Integer.parseInt(productQuantity.getText()));
            preparedStatement.setString(4, productBarCode.getText());
            preparedStatement.setInt(5, categoryComboBox.getValue().getId());
            preparedStatement.setInt(6, supplierComboBox.getValue().getId());

            if (selectedImageFile != null) {
                preparedStatement.setString(7, selectedImageFile.getName());
            } else {
                preparedStatement.setString(7, oldImagePath); // Keep the old image if no new image is selected
            }
            System.out.println(oldImagePath);

            preparedStatement.setString(8, productDescription.getText());
            preparedStatement.setInt(9, Integer.parseInt(productID));

            int rowsAffected = preparedStatement.executeUpdate();

            // If a new image was selected, delete the old image file
            if(rowsAffected > 0){
                new ErrorDialog().showErrorDialog("Updated succesfully", "success");
                if (selectedImageFile != null && oldImagePath != null) {
                    File oldImageFile = new File("src/main/resources/uploadedImages/" + oldImagePath);
                    if (oldImageFile.exists() && !oldImageFile.getName().equals(selectedImageFile.getName())) {
                        if (!oldImageFile.delete()) {
                            System.err.println("Failed to delete old image file: " + oldImageFile.getAbsolutePath());
                        }
                    }
                }
            }

        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
            // Handle the exception (e.g., show an error message to the user)
        }
    }

}

