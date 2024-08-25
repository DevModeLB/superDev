package com.devmode.superdev.models;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

public class Product {
    private final SimpleIntegerProperty productId;
    private final SimpleStringProperty name;
    private final SimpleDoubleProperty price;
    private final SimpleIntegerProperty stock;
    private final SimpleStringProperty barcode;
    private final SimpleStringProperty category;
    private final SimpleStringProperty supplier;

    public Product(int productId, String name, double price, int stock, String barcode, String category, String supplier) {
        this.productId = new SimpleIntegerProperty(productId);
        this.name = new SimpleStringProperty(name);
        this.price = new SimpleDoubleProperty(price);
        this.stock = new SimpleIntegerProperty(stock);
        this.barcode = new SimpleStringProperty(barcode);
        this.category = new SimpleStringProperty(category);
        this.supplier = new SimpleStringProperty(supplier);
    }

    public int getProductId() { return productId.get(); }
    public String getName() { return name.get(); }
    public double getPrice() { return price.get(); }
    public int getStock() { return stock.get(); }
    public String getBarcode() { return barcode.get(); }
    public String getCategory() { return category.get(); }
    public String getSupplierId() { return supplier.get(); }
}
