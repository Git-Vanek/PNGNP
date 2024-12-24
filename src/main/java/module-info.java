module org.example._pngnp {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;
    requires javafx.swing;
    requires java.desktop;
    requires org.apache.logging.log4j;

    opens org.example._pngnp to javafx.fxml;
    exports org.example._pngnp;
}