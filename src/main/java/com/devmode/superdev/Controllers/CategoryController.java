package com.devmode.superdev.Controllers;

import com.devmode.superdev.models.Category;
import com.devmode.superdev.utils.DataFetcher;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;

import javafx.geometry.Pos;
import javafx.scene.control.*;
import com.devmode.superdev.utils.ErrorDialog;

import com.devmode.superdev.utils.AuthUtils;
import com.devmode.superdev.DatabaseConnector;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import com.devmode.superdev.utils.SceneSwitcher;
import javafx.scene.layout.HBox;
import javafx.util.Callback;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class CategoryController {



    @FXML
    private TextField nameTextField;

    @FXML
    private Button addButton;

    @FXML
    private TableView<Category> categoryTable;
    @FXML
    private TableColumn<Category, Integer> categoryId;
    @FXML
    private TableColumn<Category, String> name;
    @FXML
    private TableColumn<Category, Void> actionColumn;

    @FXML
    private HBox categorisCOntainer;


    @FXML
    public void initialize() {
        AuthUtils authUtils = new AuthUtils();
        authUtils.checkAuthentication();


        if(categoryTable != null){

            // Initialize columns
            // Initialize columns
            categoryId.setCellValueFactory(new PropertyValueFactory<>("id"));
            name.setCellValueFactory(new PropertyValueFactory<>("name"));

            // Set up action column with buttons
            actionColumn.setCellFactory(new Callback<TableColumn<Category, Void>, TableCell<Category, Void>>() {
                @Override
                public TableCell<Category, Void> call(TableColumn<Category, Void> param) {
                    return new TableCell<>() {
                        private final Button updateButton = new Button("");
                        private final Button deleteButton = new Button("");

                        {
                            updateButton.setOnAction(e -> handleUpdateButton(getTableRow().getItem()));
                            deleteButton.setOnAction(e -> handleDeleteButton(getTableRow().getItem()));

                            // Style buttons if needed
                            updateButton.getStyleClass().add("updateButton");
                            deleteButton.getStyleClass().add("deleteButton");

                            HBox hbox = new HBox(10, updateButton, deleteButton);

                            setGraphic(hbox);
                        }

                        @Override
                        protected void updateItem(Void item, boolean empty) {
                            super.updateItem(item, empty);
                            setAlignment(Pos.BASELINE_CENTER);
                            if (empty) {
                                setGraphic(null);
                            } else {
                                setGraphic(new HBox(10, updateButton, deleteButton));
                            }
                        }
                    };
                }
            });

            // Fetch categories and populate the table
            ObservableList<Category> categories = DataFetcher.fetchCategories();
            categoryTable.setItems(categories);
        }
    }


    @FXML
    private void handleAddCategory(){
        String categoryName = nameTextField.getText().trim();

        if (categoryName.isEmpty()) {
            ErrorDialog error = new ErrorDialog();
            error.showErrorDialog("Name Cannot be empty", "Error");
            return;
        }

        if(!isCategoryNameUnique(categoryName)){
            new ErrorDialog().showErrorDialog("Category already exists", "Error");
            return;
        }
        try (Connection conn = DatabaseConnector.getConnection()) {
            String sql = "INSERT INTO category (name) VALUES (?)";
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, categoryName);
                int rowsAffected = pstmt.executeUpdate();
                if (rowsAffected > 0) {
                    new ErrorDialog().showErrorDialog("Category added Succesfully", "success");
                } else {
                    new ErrorDialog().showErrorDialog("Failed to add category. Please try again.", "Database Error");
                }
            }
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
            new ErrorDialog().showErrorDialog("An error occurred while adding the category.", "Database Error");
        }
    }

    public boolean isCategoryNameUnique(String categoryName) {
        String validationSql = "SELECT * FROM category WHERE name = ?";
        try (Connection connection = DatabaseConnector.getConnection();
             PreparedStatement statement = connection.prepareStatement(validationSql)) {

            statement.setString(1, categoryName);

            try (ResultSet resultSet = statement.executeQuery()) {
                return !resultSet.next();
            }
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
            return false;
        }
    }
    public void handleGetAddCategory(MouseEvent event){
        new SceneSwitcher().switchScene(event, "/FXML/categories/addCategory.fxml", "Add Category");
    }

    public void handleUpdateButton(Category category){
        System.out.println("Update");
    }

    public void handleDeleteButton(Category category){
        System.out.println("delete");
    }

}
