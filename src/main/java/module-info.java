module org.example._pngnp {
    requires javafx.controls;
    requires javafx.fxml;


    opens org.example._pngnp to javafx.fxml;
    exports org.example._pngnp;
}