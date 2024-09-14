package com.devmode.superdev.models;

public class DailyOrderStatistics {
    private int productId;
    private String productName;
    private double totalPrice;
    private double totalOrderAmount;

    public DailyOrderStatistics(int productId, String productName, double totalPrice, double totalOrderAmount) {
        this.productId = productId;
        this.productName = productName;
        this.totalPrice = totalPrice;
        this.totalOrderAmount = totalOrderAmount;
    }

    public double getTotalOrderAmount(){
        return this.totalOrderAmount;
    }

    // Getters and Setters
    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(double totalPrice) {
        this.totalPrice = totalPrice;
    }


}
