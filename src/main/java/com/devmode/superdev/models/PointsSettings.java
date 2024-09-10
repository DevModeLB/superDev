package com.devmode.superdev.models;

public class PointsSettings {
    private String pointsStep;
    private String stepPoints;
    private String pointAmount;
    private boolean isActive;

    // Constructor
    public PointsSettings(String pointsStep, String stepPoints, String pointAmount, boolean isActive) {
        this.pointsStep = pointsStep;
        this.stepPoints = stepPoints;
        this.pointAmount = pointAmount;
        this.isActive = isActive;
    }

    // Getters and Setters
    public String getPointsStep() {
        return pointsStep;
    }

    public void setPointsStep(String pointsStep) {
        this.pointsStep = pointsStep;
    }

    public String getStepPoints() {
        return stepPoints;
    }

    public void setStepPoints(String stepPoints) {
        this.stepPoints = stepPoints;
    }

    public String getPointAmount() {
        return pointAmount;
    }

    public void setPointAmount(String pointAmount) {
        this.pointAmount = pointAmount;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    @Override
    public String toString() {
        return "PointsSettings{" +
                "pointsStep='" + pointsStep + '\'' +
                ", stepPoints='" + stepPoints + '\'' +
                ", pointAmount='" + pointAmount + '\'' +
                ", isActive=" + isActive +
                '}';
    }
}
