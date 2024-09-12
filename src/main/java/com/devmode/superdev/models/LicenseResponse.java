package com.devmode.superdev.models;

public class LicenseResponse {
    private String status;
    private String message;
    private LicenseData data;
    private boolean isActive;


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

    public boolean isActive(){
        return isActive;
    }

    public void setIsActive(boolean isActive){
        this.isActive = isActive;
    }
}
