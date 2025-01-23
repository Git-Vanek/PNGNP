// Объявление пакета, к которому принадлежит класс
package org.example._pngnp.controllers;

// Импорт необходимых классов из библиотеки JavaFX для работы с графическим интерфейсом
import javafx.animation.FadeTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import javafx.util.Duration;

// Импорт классов для логирования
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

// Импорт модели изображения
import org.example._pngnp.models.ImageModel;

// Импорт утилит для работы с объектами
import java.util.Objects;

// Объявление класса контроллера
public class HelloController {

    // Логгер для записи логов
    private static final Logger logger = LogManager.getLogger(HelloController.class);

    // Аннотация FXML для связывания с элементом интерфейса
    @FXML
    private ImageView logoImageView;

    // Метод инициализации контроллера
    @FXML
    public void initialize() {
        // Загрузка логотипа из папки resources
        Image logoImage = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/org/example/_pngnp/images/logo.png")));

        // Установка логотипа в ImageView
        logoImageView.setImage(logoImage);

        // Запись в лог о загрузке логотипа
        logger.info("Logo loaded");

        // Создание анимации появления логотипа
        FadeTransition fadeTransition = new FadeTransition(Duration.seconds(2), logoImageView);
        fadeTransition.setFromValue(0.0);
        fadeTransition.setToValue(1.0);
        fadeTransition.play();
    }

    // Метод обработки нажатия на кнопку "Start"
    @FXML
    protected void onStartButtonClick(ActionEvent event) {
        try {
            // Загрузка нового FXML файла для основного интерфейса
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/_pngnp/views/main.fxml"));
            Parent root = loader.load();

            // Получение контроллера основного интерфейса
            Stage primaryStage = getStage(event, loader);

            // Создание новой сцены и установка её в основное окно
            Scene scene = new Scene(root, 1200, 800);
            primaryStage.setScene(scene);
            primaryStage.show();
        } catch (Exception e) {
            // Обработка исключений и вывод стека вызовов
            //noinspection CallToPrintStackTrace
            e.printStackTrace();
        }
    }

    // Метод получения контроллера основного интерфейса
    private static Stage getStage(ActionEvent event, FXMLLoader loader) {
        MainController controller = loader.getController();
        ImageModel model = new ImageModel();
        Stage primaryStage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
        controller.initialize();
        controller.setModel(model);
        controller.setPrimaryStage(primaryStage);
        return primaryStage;
    }
}