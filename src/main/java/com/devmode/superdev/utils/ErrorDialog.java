package com.devmode.superdev.utils;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

public class ErrorDialog {

    public void showErrorDialog(String message, String title) {
        AlertType type = AlertType.ERROR;
        if(title.equals("success")){
            type = AlertType.INFORMATION;
        }

        Alert  alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

}
