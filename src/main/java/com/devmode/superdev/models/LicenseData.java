package com.devmode.superdev.models;

public class LicenseData {
    private String _id;
    private String liscence;
    private String assignedTo;
    private String status;
    private String expiresAt;
    private String createdAt;
    private boolean isUsed;

    // Getters
    public String getId() {
        return _id;
    }

    public String getLiscence() {
        return liscence;
    }

    public String getAssignedTo() {
        return assignedTo;
    }

    public String getStatus() {
        return status;
    }

    public String getExpiresAt() {
        return expiresAt;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public boolean getIsUsed() {
        return isUsed;
    }
}
