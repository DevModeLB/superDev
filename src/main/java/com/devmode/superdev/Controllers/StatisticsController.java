package com.devmode.superdev.Controllers;

import com.devmode.superdev.models.DailyOrderStatistics;
import com.devmode.superdev.models.ProductSales;
import com.devmode.superdev.utils.DataFetcher;
import com.devmode.superdev.utils.SceneSwitcher;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class StatisticsController {

    public Label totalAmount;
    public Label date;
    @FXML
    private LineChart<String, Number> lineChart;

    @FXML
    private TableView<ProductSales> productTable;

    @FXML
    private TableColumn<ProductSales, String> productNameColumn;

    @FXML
    private TableColumn<ProductSales, Integer> unitsSoldColumn;

    @FXML
    private TableView<DailyOrderStatistics> orderTableView;
    @FXML
    private TableColumn<DailyOrderStatistics, Integer> orderProductIdColumn;
    @FXML
    private TableColumn<DailyOrderStatistics, String> orderProductNameColumn;
    @FXML
    private TableColumn<DailyOrderStatistics, Double> totalPriceSold;


    private final ObservableList<DailyOrderStatistics> orderData = FXCollections.observableArrayList();


    @FXML
    public void initialize() {
        if(lineChart != null){
            populateOrderStatistics(lineChart, "2024");
            productNameColumn.setCellValueFactory(new PropertyValueFactory<>("productName"));
            unitsSoldColumn.setCellValueFactory(new PropertyValueFactory<>("unitsSold"));
            populateProductTable();
        }
        if(orderTableView != null){
            orderProductIdColumn.setCellValueFactory(new PropertyValueFactory<>("productId"));
            orderProductNameColumn.setCellValueFactory(new PropertyValueFactory<>("productName"));
            totalPriceSold.setCellValueFactory(new PropertyValueFactory<>("totalPrice"));

            // Load data for today's statistics
            loadDailyStatistics();
        }
    }

    public void loadDailyStatistics() {
        // Fetch data from DataFetcher
        List<DailyOrderStatistics> dailyStatistics = DataFetcher.fetchDailyStatistics();

        // Update TableView
        orderData.clear();
        orderData.addAll(dailyStatistics);
        orderTableView.setItems(orderData);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        String formattedDate = LocalDate.now().format(formatter);
        date.setText("Date: " + formattedDate);
        date.setStyle("-fx-font-size: 16px;"); // Set font size for date label

        // Calculate the total amount for all orders
        double totalOrderAmount = dailyStatistics.stream()
                .mapToDouble(DailyOrderStatistics::getTotalOrderAmount)
                .sum();

        // Update the totalAmount label with the calculated total and decrease font size
        totalAmount.setText("Total: " +totalOrderAmount + "L.L");
        totalAmount.setStyle("-fx-font-size: 16px;"); // Set font size for total amount label
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


    public void handleGetDailyStat(MouseEvent event) {
        SceneSwitcher switcher = new SceneSwitcher();
        switcher.switchScene(event, "/FXML/statistics/daily.fxml", "Statistics");
    }
}
