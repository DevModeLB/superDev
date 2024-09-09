package com.devmode.superdev.models;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

import java.util.Objects;

public class Product {
    private final SimpleIntegerProperty productId;
    private final SimpleStringProperty name;
    private final SimpleDoubleProperty price;
    private final SimpleIntegerProperty stock;
    private final SimpleStringProperty barcode;
    private final SimpleStringProperty category;
    private final SimpleStringProperty supplier;
    private final SimpleStringProperty imagePath;
    private final String description;
    public Product(int productId, String name, double price, int stock, String barcode, String category, String supplier, String imagePath, String description) {
        this.productId = new SimpleIntegerProperty(productId);
        this.name = new SimpleStringProperty(name);
        this.price = new SimpleDoubleProperty(price);
        this.stock = new SimpleIntegerProperty(stock);
        this.barcode = new SimpleStringProperty(barcode);
        this.category = new SimpleStringProperty(category);
        this.supplier = new SimpleStringProperty(supplier);
        this.imagePath = new SimpleStringProperty(imagePath);
        this.description = description;
    }

    public int getProductId() { return productId.get(); }
    public String getName() { return name.get(); }
    public double getPrice() { return price.get(); }
    public int getStock() { return stock.get(); }
    public String getBarcode() { return barcode.get(); }
    public String getCategory() { return category.get(); }
    public String getSupplierId() { return supplier.get(); }
    public String getImagePath() { return  imagePath.get();}

    public String getDescription() {
        return this.description;
    }
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Product product = (Product) o;
        return getProductId() == product.getProductId();
    }

    @Override
    public int hashCode() {
        return Objects.hash(getProductId()); // Ensure consistent hashing based on product id
    }
    @Override
    public String toString() {
        return "Product{id=" + getProductId() + ", name='" + getName() + "', price=" + getPrice() + "}";
    }

}
