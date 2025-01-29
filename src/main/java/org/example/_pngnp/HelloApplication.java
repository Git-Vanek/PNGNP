// Объявление пакета, к которому принадлежит класс
package org.example._pngnp;

// Импорт необходимых классов из библиотеки JavaFX для создания графического интерфейса
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

// Импорт классов для логирования
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.example._pngnp.classes.Settings;

// Объявление основного класса приложения, наследующегося от Application
public class HelloApplication extends Application {

    // Логгер для записи логов
    private static final Logger logger = LogManager.getLogger(HelloApplication.class);

    // Переопределение метода start для настройки и отображения основного окна приложения
    @Override
    public void start(Stage primaryStage) {
        try {
            // Загрузка настроек
            Settings settings = Settings.loadSettings("settings.json");

            // Применение настроек
            applySettings(settings, primaryStage);

            // Загрузка FXML файла для создания графического интерфейса
            FXMLLoader loader = new FXMLLoader(getClass().getResource("views/hello-view.fxml"));
            Parent root = loader.load();
            logger.info("Hello FXML file loaded successfully");

            // Создание сцены с загруженным интерфейсом
            Scene scene = new Scene(root, 1200, 800);
            logger.info("Scene created");

            // Настройка и отображение окна
            primaryStage.setTitle("PNGNP");
            primaryStage.setScene(scene);
            primaryStage.setMinWidth(1200);
            primaryStage.setMinHeight(800);

            // Установка максимального размера окна
            primaryStage.setMaximized(true);

            primaryStage.show();
            logger.info("Hello scene displayed");
        } catch (Exception e) {
            logger.error("Error occurred during application startup", e);
        }
    }

    // Метод применения выгруженных настроек
    private void applySettings(Settings settings, Stage stage) {
        // Применение локализации
        if ("Russian".equalsIgnoreCase(settings.getLanguage())) {
            // Установка русского языка
        } else {
            // Установка английского языка
        }

        // Применение темы
        if ("Dark".equalsIgnoreCase(settings.getTheme())) {
            // stage.getScene().getStylesheets().add(getClass().getResource("/styles/dark-theme.css").toExternalForm());
        } else if ("Light".equalsIgnoreCase(settings.getTheme())) {
            // stage.getScene().getStylesheets().add(getClass().getResource("/styles/light-theme.css").toExternalForm());
        } else {
            // Установить системную тему
        }
    }

    // Переопределение метода stop для записи лога о завершении работы приложения
    @Override
    public void stop() {
        logger.info("Application is shutting down");
    }

    // Точка входа в приложение
    public static void main(String[] args) {
        logger.info("Launching the application");
        launch(args);
    }
}