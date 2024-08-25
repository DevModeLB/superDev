module com.devmode.superdev {
    requires javafx.controls;
    requires javafx.fxml;
    requires transitive javafx.graphics;
    requires java.sql;



    opens com.devmode.superdev to javafx.fxml;
    exports com.devmode.superdev;


    exports com.devmode.superdev.Controllers;
    opens com.devmode.superdev.Controllers to javafx.fxml;

    opens com.devmode.superdev.models to javafx.base;
    exports com.devmode.superdev.models;
}