package com.devmode.superdev;

import javafx.stage.Stage;

public class StageManager {
    private static Stage primaryStage;

    private StageManager() {
        // Private constructor to prevent instantiation
    }

    public static void setPrimaryStage(Stage stage) {
        primaryStage = stage;
    }

    public static Stage getPrimaryStage() {
        return primaryStage;
    }
}
