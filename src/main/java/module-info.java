module space.ifel.directory {
    requires space.ifel.core;
    requires space.ifel.config;
    requires javafx.base;
    requires javafx.fxml;
    requires javafx.controls;
    requires java.desktop;

    opens space.ifel.directory to javafx.fxml;

    exports space.ifel.directory;
}