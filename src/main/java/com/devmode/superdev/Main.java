package com.devmode.superdev;

import com.devmode.superdev.Controllers.LoginController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    private static Stage primaryStage;

    @Override
    public void start(Stage primaryStage) throws Exception {
        Main.primaryStage = primaryStage;

        // Load the FXML file
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/FXML/Login.fxml"));
        Parent root = loader.load();

        Object controller = loader.getController();


        // Set up the scene and stage
        Scene scene = new Scene(root);
        scene.getStylesheets().add(getClass().getResource("/CSS/style.css").toExternalForm());
        primaryStage.setFullScreen(true);
        if(controller != null) {
            if (controller instanceof LoginController) {
                primaryStage.setScene(scene);
            }
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
