package com.devmode.superdev.Controllers;

import javafx.scene.input.MouseEvent;
import java.awt.Desktop;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public class AboutController {

    public void handleInstagram(MouseEvent event) {
        openURL("https://www.instagram.com/devmodee");
    }

    public void handleTiktok(MouseEvent event) {
        openURL("https://www.tiktok.com/@devmodee");
    }

    public void handleWebsite(MouseEvent event) {
        openURL("https://www.devmode.me");
    }

    private void openURL(String url) {
        if(Desktop.isDesktopSupported()) {
            try {
                Desktop.getDesktop().browse(new URI(url));
            } catch (IOException | URISyntaxException e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("Desktop is not supported");
        }
    }
}
