module com.devmode.superdev {
    requires javafx.controls;
    requires javafx.fxml;
    requires transitive javafx.graphics;
    requires java.desktop;
    requires java.compiler;
    requires com.google.gson;
    requires java.sql;
    requires java.net.http;
    requires jdk.httpserver;
    requires okhttp3;
    requires twilio;
    requires dotenv.java;

    // Open the package to Gson for reflection
    opens com.devmode.superdev.models to com.google.gson, javafx.base;

    opens com.devmode.superdev to javafx.fxml;
    exports com.devmode.superdev;

    exports com.devmode.superdev.Controllers;
    opens com.devmode.superdev.Controllers to javafx.fxml;


}
