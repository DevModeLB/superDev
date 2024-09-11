package com.devmode.superdev.models;

public class LicenseResponse {
    private String status;
    private String message;
    private LicenseData data;

    // Getters
    public String getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }

    public LicenseData getData() {
        return data;
    }
}
