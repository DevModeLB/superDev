package com.devmode.superdev.Controllers;

import com.devmode.superdev.models.OrderItem;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import com.devmode.superdev.utils.DataFetcher;
import com.devmode.superdev.models.Order;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

public class OrderController {

    @FXML
    private TableView<Order> ordersTable;
    @FXML
    private TableColumn<Order, Integer> orderId;
    @FXML
    private TableColumn<Order, String> customer;
    @FXML
    private TableColumn<Order, String> orderItems;
    @FXML
    private TableColumn<Order, Double> total;
    @FXML
    private TableColumn<Order, String> orderDate;
    @FXML
    private TableColumn<Order, String> user;
    @FXML
    private TableColumn<Order, String> status;
    @FXML
    private TableColumn<Order, String> actionColumn;

    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @FXML
    public void initialize() {
        // Set up columns
        orderId.setCellValueFactory(new PropertyValueFactory<>("id"));
        customer.setCellValueFactory(new PropertyValueFactory<>("user")); // Update if needed
        orderItems.setCellValueFactory(cellData -> {
            List<OrderItem> items = cellData.getValue().getOrderItems();
            return new SimpleStringProperty(items.stream()
                    .map(item -> "" + item.getProductName())
                    .collect(Collectors.joining("\n")));
        });
        total.setCellValueFactory(new PropertyValueFactory<>("total"));
        orderDate.setCellValueFactory(new PropertyValueFactory<>("orderDate"));
        user.setCellValueFactory(new PropertyValueFactory<>("user"));
        status.setCellValueFactory(new PropertyValueFactory<>("status"));
        actionColumn.setCellValueFactory(new PropertyValueFactory<>("action"));

        // Load data into the TableView
        List<Order> orders = DataFetcher.fetchOrders();
        ObservableList<Order> observableOrders = FXCollections.observableArrayList(orders);
        ordersTable.setItems(observableOrders);
    }
}
