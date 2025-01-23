// Объявление пакета, к которому принадлежит класс
package org.example._pngnp;

// Импорт необходимых классов из библиотеки JavaFX для создания графического интерфейса
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

// Объявление основного класса приложения, наследующегося от Application
public class HelloApplication extends Application {
    // Переопределение метода start для настройки и отображения основного окна приложения
    @Override
    public void start(Stage primaryStage) throws Exception {
        // Загрузка FXML файла для создания графического интерфейса
        FXMLLoader loader = new FXMLLoader(getClass().getResource("views/hello-view.fxml"));
        Parent root = loader.load();

        // Создание сцены с загруженным интерфейсом
        Scene scene = new Scene(root, 1200, 800);

        // Установка заголовка окна
        primaryStage.setTitle("PNGNP");

        // Установка сцены в окно
        primaryStage.setScene(scene);

        // Установка минимальных размеров окна
        primaryStage.setMinWidth(1200);
        primaryStage.setMinHeight(800);

        // Отображение окна
        primaryStage.show();
    }

    // Точка входа в приложение
    public static void main(String[] args) {
        // Запуск JavaFX приложения
        launch(args);
    }
}