package com.devmode.superdev.Controllers;

import com.devmode.superdev.models.ProductSales;
import com.devmode.superdev.utils.DataFetcher;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import java.util.Map;

public class StatisticsController {

    @FXML
    private LineChart<String, Number> lineChart;

    @FXML
    private TableView<ProductSales> productTable;

    @FXML
    private TableColumn<ProductSales, String> productNameColumn;

    @FXML
    private TableColumn<ProductSales, Integer> unitsSoldColumn;

    @FXML
    public void initialize() {
        populateOrderStatistics(lineChart, "2024");

        productNameColumn.setCellValueFactory(new PropertyValueFactory<>("productName"));
        unitsSoldColumn.setCellValueFactory(new PropertyValueFactory<>("unitsSold"));

        // Populate the table with data
        populateProductTable();
    }

    public void populateOrderStatistics(LineChart<String, Number> lineChart, String year) {
        Map<String, Double> orderStatistics = DataFetcher.getOrderStatisticsForYear(year);
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Total Orders");
        // Loop through the months (Jan to Dec) and add data points
        for (int i = 1; i <= 12; i++) {
            String month = String.format("%02d", i); // Ensure two-digit format (e.g., 01 for January)
            Double total = orderStatistics.getOrDefault(month, 0.0);
            series.getData().add(new XYChart.Data<>(month, total));
        }

        lineChart.getData().add(series);
    }

    public void populateProductTable() {
        ObservableList<ProductSales> productSalesList = FXCollections.observableArrayList();

        // Fetch data from the database
        Map<String, Double> productSales = DataFetcher.getBestSellingProducts();

        for (Map.Entry<String, Double> entry : productSales.entrySet()) {
            productSalesList.add(new ProductSales(entry.getKey(), entry.getValue()));
        }

        // Set the data to the table
        productTable.setItems(productSalesList);
    }



}
