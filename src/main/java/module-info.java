module com.devmode.superdev {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.devmode.superdev to javafx.fxml;
    exports com.devmode.superdev;
}