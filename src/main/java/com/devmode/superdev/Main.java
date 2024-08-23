package com.devmode.superdev;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import com.devmode.superdev.utils.AuthUtils;
import com.devmode.superdev.SessionManager;

public class Main extends Application {

    private static Stage primaryStage;

    @Override
    public void start(Stage primaryStage) throws Exception {
        Main.primaryStage = primaryStage;

        // Load the FXML file
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/FXML/Category.fxml"));
        Parent root = loader.load();
        // Set up the scene and stage
        Scene scene = new Scene(root);
        scene.getStylesheets().add(getClass().getResource("/CSS/style.css").toExternalForm());
        primaryStage.setFullScreen(true);
        if(SessionManager.getInstance().getUsername() != null){
            primaryStage.setScene(scene);
        }
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }

    public static Stage getPrimaryStage() {
        return primaryStage;
    }
}
