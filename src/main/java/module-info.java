module com.devmode.superdev {
    requires javafx.controls;
    requires javafx.fxml;
    requires transitive javafx.graphics;


    opens com.devmode.superdev to javafx.fxml;
    exports com.devmode.superdev;
}