package com.devmode.superdev;

import com.devmode.superdev.Controllers.HomeController;
import com.devmode.superdev.Controllers.LicenseValidator;
import com.devmode.superdev.Controllers.LoginController;
import com.devmode.superdev.Controllers.LicenseController;
import com.devmode.superdev.Controllers.SyncController;
import com.devmode.superdev.utils.NetworkUtils;
import javafx.application.Application;
import javafx.application.Platform;
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
        SQLiteConnector connector = new SQLiteConnector();
        connector.initializeDatabase();
        Main.primaryStage = primaryStage;

        // Load the appropriate FXML file based on license validity
        FXMLLoader loader;
        if (LicenseValidator.isLicenseValid()) {
            loader = new FXMLLoader(getClass().getResource("/FXML/Login.fxml"));
            primaryStage.setFullScreen(true);
        } else {
            System.out.println("LISCENCE STILL VALID");
            loader = new FXMLLoader(getClass().getResource("/FXML/license.fxml"));
            primaryStage.setMinWidth(200.0);
            primaryStage.setMinHeight(400.0);
            primaryStage.setFullScreen(false);
        }

        // Load the UI and set up the scene
        Parent root = loader.load();
        Object controller = loader.getController();
        Scene scene = new Scene(root);
        scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/CSS/style.css")).toExternalForm());



        // Handle controller setup
        if (controller != null) {
            if (controller instanceof LoginController) {
                // Show login page if license is valid
                primaryStage.setScene(scene);
            } else if (controller instanceof LicenseController) {
                // Show license validation page if license is invalid
                primaryStage.setScene(scene);
            }
        }

        // Show the primary stage
        primaryStage.show();

        // Setup the network monitor to sync data
        setupInternetMonitor();
    }

    @Override
    public void stop() {
        if (scheduler != null && !scheduler.isShutdown()) {
            scheduler.shutdown();
        }
    }

    // Periodically check for internet availability and sync data
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
                } else {
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
