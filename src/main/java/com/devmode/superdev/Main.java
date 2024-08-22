package com.devmode.superdev;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        // Load the FXML file
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/FXML/login.fxml"));
        Parent root = loader.load();
        
        // Set the scene with the loaded UI
        Scene scene = new Scene(root);
        
        // Optionally, apply an external CSS stylesheet
        scene.getStylesheets().add(getClass().getResource("/CSS/style.css").toExternalForm());
        
        // Set the stage title and scene
        primaryStage.setTitle("Category");
        primaryStage.setFullScreen(true);
        primaryStage.setScene(scene);
        
        // Show the stage
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }

}
