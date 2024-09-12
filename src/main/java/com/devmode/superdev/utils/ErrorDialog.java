package com.devmode.superdev.utils;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

public class ErrorDialog {

    private Runnable onOkAction;

    public void setOnOkAction(Runnable onOkAction) {
        this.onOkAction = onOkAction;
    }

    public void showErrorDialog(String message, String title) {
        AlertType type = AlertType.ERROR;
        if(title.equals("success")){
            type = AlertType.INFORMATION;
        }

        Alert  alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);

        alert.setOnCloseRequest(event -> {
            if (onOkAction != null) {
                onOkAction.run();
            }
        });

        alert.showAndWait();
    }

}
