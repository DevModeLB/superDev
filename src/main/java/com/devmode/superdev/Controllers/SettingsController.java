package com.devmode.superdev.Controllers;

import com.devmode.superdev.utils.SceneSwitcher;
import javafx.event.ActionEvent;
import javafx.scene.input.MouseEvent;

public class SettingsController {
    public void handleAboutUs(MouseEvent event) {
    }

    public void handlePoints(MouseEvent event) {
        SceneSwitcher switcher = new SceneSwitcher();
        switcher.switchScene(event, "/FXML/pointssettings.fxml", "Points settings");
    }

    public void handleApplyRate(ActionEvent event) {
    }
}
