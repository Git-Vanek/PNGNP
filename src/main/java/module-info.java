module org.example._pngnp {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;
    requires javafx.swing;
    requires java.desktop;
    requires org.apache.logging.log4j;

    opens org.example._pngnp to javafx.fxml;
    exports org.example._pngnp;
    exports org.example._pngnp.controllers;
    opens org.example._pngnp.controllers to javafx.fxml;
    exports org.example._pngnp.models;
    opens org.example._pngnp.models to javafx.fxml;
    exports org.example._pngnp.classes;
    opens org.example._pngnp.classes to javafx.fxml;
}