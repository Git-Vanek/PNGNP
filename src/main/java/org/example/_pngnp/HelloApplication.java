package org.example._pngnp;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class HelloApplication extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {
        // Настройка окна
        FXMLLoader loader = new FXMLLoader(getClass().getResource("hello-view.fxml"));
        Parent root = loader.load();
        Scene scene = new Scene(root, 1200, 800);
        primaryStage.setTitle("PNGNP");
        primaryStage.setScene(scene);
        // Установка минимальных размеров окна
        primaryStage.setMinWidth(1200);
        primaryStage.setMinHeight(800);
        // Отображение окна
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}