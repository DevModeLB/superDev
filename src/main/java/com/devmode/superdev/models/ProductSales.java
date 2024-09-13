package com.devmode.superdev.models;

public class ProductSales {
    private String productName;
    private Double unitsSold;

    public ProductSales(String productName, Double unitsSold) {
        this.productName = productName;
        this.unitsSold = unitsSold;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public Double getUnitsSold() {
        return unitsSold;
    }

    public void setUnitsSold(Double unitsSold) {
        this.unitsSold = unitsSold;
    }
}
