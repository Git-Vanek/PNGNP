module org.example._pngnp {
    requires javafx.controls;
    requires javafx.fxml;
    requires org.apache.logging.log4j;


    opens org.example._pngnp to javafx.fxml;
    exports org.example._pngnp;
}