package com.devmode.superdev.Controllers;

import com.devmode.superdev.models.Product;
import com.devmode.superdev.utils.DataFetcher;
import javafx.application.Platform;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.layout.AnchorPane;

public class ClipboardMonitorController {
    private HomeController homeController;
    public ClipboardMonitorController(HomeController homeController) {
        this.homeController = homeController;
    }

    public void startMonitoring() {
        new Thread(() -> {
            final String[] lastContent = {""};
            while (true) {
                Platform.runLater(() -> {
                    // This code will run on the JavaFX Application Thread
                    String currentContent = Clipboard.getSystemClipboard().getString();
                    if (currentContent != null && !currentContent.equals(lastContent[0])) {
                        lastContent[0] = currentContent;
                        if(currentContent.length() > 12){
                            return;
                        }
                        processBarcode(currentContent);
                        clearClipboard();
                    }
                });
                try {
                    Thread.sleep(500); // Check every half second
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void processBarcode(String barcode) {
        Product product = DataFetcher.findProductByBarcode(barcode);
        if (product != null) {
            Platform.runLater(() -> {
                // Since we are modifying the UI, wrap this in Platform.runLater
                AnchorPane pane = homeController.createProductPane(product, true);
                homeController.addToInvoice(product, pane);
            });
        } else {
            System.out.println("Not found");
        }
    }
    private void clearClipboard() {
        Platform.runLater(() -> {
            Clipboard clipboard = Clipboard.getSystemClipboard();
            ClipboardContent content = new ClipboardContent();
            content.putString(""); // Set the clipboard content to an empty string
            clipboard.setContent(content); // Clear the clipboard
        });
    }
}