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
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;

// Импорт классов для логирования
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

// Импорт модели изображения
import org.example._pngnp.models.ImageModel;

// Импорт утилит для работы с объектами
import java.io.IOException;
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
        logger.info("Initializing HelloController");
        try {
            // Загрузка логотипа из папки resources
            Image logoImage = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/org/example/_pngnp/images/logo.png")));
            logger.info("Logo image loaded successfully");

            // Установка логотипа в ImageView
            logoImageView.setImage(logoImage);
            logger.info("Logo image set in ImageView");

            // Создание анимации появления логотипа
            FadeTransition fadeTransition = new FadeTransition(Duration.seconds(2), logoImageView);
            fadeTransition.setFromValue(0.0);
            fadeTransition.setToValue(1.0);
            fadeTransition.play();
            logger.info("Fade transition started");
        } catch (Exception e) {
            logger.error("Error occurred during initialization", e);
        }
    }

    // Метод обработки нажатия на кнопку "Start"
    @FXML
    protected void onStartButtonClick(ActionEvent event) {
        try {
            // Загрузка нового FXML файла для основного интерфейса
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/_pngnp/views/main.fxml"));
            Parent root = loader.load();
            logger.info("Main FXML file loaded successfully");

            // Получение контроллера основного интерфейса
            Stage primaryStage = getStage(event, loader);
            logger.info("Primary stage obtained");

            // Сохранение текущих размеров окна
            assert primaryStage != null;
            double currentWidth = primaryStage.getWidth();
            double currentHeight = primaryStage.getHeight();
            boolean isMaximized = primaryStage.isMaximized();

            // Создание новой сцены и установка её в основное окно
            Scene scene = new Scene(root, currentWidth - 50, currentHeight - 50);
            logger.info("Scene created");

            primaryStage.setScene(scene);

            // Восстановление размеров окна
            primaryStage.setWidth(currentWidth);
            primaryStage.setHeight(currentHeight);
            primaryStage.setMaximized(isMaximized);

            primaryStage.show();
            logger.info("Main scene displayed");
        } catch (Exception e) {
            logger.error("Error occurred during start button click", e);
        }
    }

    // Метод получения контроллера основного интерфейса
    private static Stage getStage(ActionEvent event, FXMLLoader loader) {
        try {
            MainController controller = loader.getController();
            ImageModel model = new ImageModel();
            Stage primaryStage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
            controller.setModel(model);
            controller.setPrimaryStage(primaryStage);
            logger.info("Main controller initialized and primary stage set");
            return primaryStage;
        } catch (Exception e) {
            logger.error("Error occurred while getting the main controller", e);
            return null;
        }
    }

    public void onSettingsButtonClick() {
        showDialog("settings");
    }

    public void onFeedbackButtonClick() {
        showDialog("feedback");
    }

    private void showDialog(String title) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/_pngnp/views/" + title + "_dialog.fxml"));
            Parent root = loader.load();
            if (Objects.equals(title, "settings")) {
                SettingsController controller = loader.getController();
                Stage dialogStage = new Stage();
                dialogStage.initModality(Modality.APPLICATION_MODAL);
                dialogStage.setTitle(title);
                dialogStage.setScene(new Scene(root));

                controller.setDialogStage(dialogStage);

                dialogStage.showAndWait();
            } else  {
                FeedbackController controller = loader.getController();
                Stage dialogStage = new Stage();
                dialogStage.initModality(Modality.APPLICATION_MODAL);
                dialogStage.setTitle(title);
                dialogStage.setScene(new Scene(root));

                controller.setDialogStage(dialogStage);

                dialogStage.showAndWait();
            }
        } catch (IOException e) {
            logger.error("Error occurred during " + title + " button click", e);
        }
    }
}