package com.devmode.superdev.Controllers;

import com.devmode.superdev.DatabaseManager;
import com.devmode.superdev.models.Category;
import com.devmode.superdev.utils.ErrorDialog;
import com.devmode.superdev.utils.SceneSwitcher;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Objects;
import java.util.ResourceBundle;

public class EditCategoryController implements Initializable {

    @FXML
    private AnchorPane anchorPane;

    @FXML
    private TextField nameTextField;

    @FXML
    private Button addButton;

    private Category category;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        Platform.runLater(() -> {
            Stage stage = (Stage) nameTextField.getScene().getWindow();
            String categoryId = stage.getTitle();
            if (categoryId != null) {
                loadCategoryDetails(categoryId);
            }
        });
    }

    private void loadCategoryDetails(String categoryId) {
        String query = "SELECT id, name FROM Category WHERE id = ?";
        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setInt(1, Integer.parseInt(categoryId));
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                int id = resultSet.getInt("id");
                String name = resultSet.getString("name");
                this.category = new Category(id, name);
                nameTextField.setText(name);
            } else {
                new ErrorDialog().showErrorDialog("Not found", "Error");
            }

        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
            // Handle the exception (e.g., show an error message to the user)
        }
    }


    public void handleEditCategory(MouseEvent event) {
        if (category == null) {

            new ErrorDialog().showErrorDialog("No category to update", "Error");
            return;
        }
        String newName = nameTextField.getText();
        if(Objects.equals(category.getName(), newName)){
            new ErrorDialog().showErrorDialog("Nothing to update", "Error");
            return;
        }
        if (newName == null || newName.trim().isEmpty()) {
            // Handle invalid input
            new ErrorDialog().showErrorDialog("Name cannot be empty", "Error");
            return;
        }
        String query = "UPDATE Category SET name = ? WHERE id = ?";
        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setString(1, newName);
            preparedStatement.setInt(2, category.getId());

            int rowsAffected = preparedStatement.executeUpdate();
            if (rowsAffected > 0) {
                new ErrorDialog().showErrorDialog("Category updated successfully", "success");
                new SceneSwitcher().switchScene(event, "/FXML/categories/Category.fxml", "Categories");
            } else {
                new ErrorDialog().showErrorDialog("Something went wrong", "Error");
            }

        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
            new ErrorDialog().showErrorDialog("Something went wrong", "Error");
        }
    }
}
