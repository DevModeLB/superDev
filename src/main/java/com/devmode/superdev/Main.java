package com.devmode.superdev;

import com.devmode.superdev.Controllers.LoginController;
import com.devmode.superdev.Controllers.SyncController;
import com.devmode.superdev.utils.NetworkUtils;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.util.Objects;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Main extends Application {

    private static Stage primaryStage;
    private ScheduledExecutorService scheduler;

    @Override
    public void start(Stage primaryStage) throws Exception {
        Main.primaryStage = primaryStage;

        // Load the FXML file
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/FXML/Login.fxml"));
        Parent root = loader.load();
        Object controller = loader.getController();
        // Set up the scene and stage
        Scene scene = new Scene(root);
        scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/CSS/style.css")).toExternalForm());
        primaryStage.setFullScreen(true);
        if(controller != null) {

            if (controller instanceof LoginController) {
                primaryStage.setScene(scene);
            }
        }
        SQLiteConnector connector = new SQLiteConnector();
        connector.initializeDatabase();
        setupInternetMonitor();
        primaryStage.show();
    }
    @Override
    public void stop() {
        if (scheduler != null && !scheduler.isShutdown()) {
            scheduler.shutdown();
        }
    }
    private void setupInternetMonitor() {
        scheduler = Executors.newScheduledThreadPool(1);
        Runnable checkInternetTask = new Runnable() {
            @Override
            public void run() {
                if (NetworkUtils.isInternetAvailable()) {
                    System.out.println("Internet found");
                    System.out.println("Trying to sync");
                    SQLiteConnector sqliteConnector = new SQLiteConnector();
                    MySqlConnector mysqlConnector = new MySqlConnector();
                    SyncController syncController = new SyncController(sqliteConnector, mysqlConnector);
                    syncController.syncAllTables();
                }
                else{
                    System.out.println("No internet");
                }
            }
        };
        // Schedule the task to run every 1 hour
        scheduler.scheduleAtFixedRate(checkInternetTask, 0, 1, TimeUnit.HOURS);
    }
    public static void main(String[] args) {
        launch(args);
    }
    public static Stage getPrimaryStage() {
        return primaryStage;
    }
}
