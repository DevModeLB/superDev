module com.devmode.superdev {
    requires javafx.controls;
    requires javafx.fxml;
    requires transitive javafx.graphics;
    requires java.sql;


    opens com.devmode.superdev to javafx.fxml;
    exports com.devmode.superdev;

    // Export the package that contains the controllers
    exports com.devmode.superdev.Controllers;

    // Open the package to JavaFX for reflection
    opens com.devmode.superdev.Controllers to javafx.fxml;
}