// Объявление пакета, к которому принадлежит класс
package org.example._pngnp.controllers;

// Импорт необходимых классов из библиотеки JavaFX для работы с графическим интерфейсом
import javafx.animation.FadeTransition;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example._pngnp.classes.Settings;
import org.example._pngnp.models.ImageModel;

import java.io.IOException;
import java.util.Locale;
import java.util.Objects;
import java.util.ResourceBundle;

// Объявление класса контроллера
public class HelloController {

    // Логгер для записи логов
    private static final Logger logger = LogManager.getLogger(HelloController.class);

    private Stage primaryStage;
    private Settings settings;
    private ResourceBundle resources;

    // Аннотация FXML для связывания с элементом интерфейса
    @FXML
    private ImageView logoImageView;

    @FXML
    private Label descriptionLabel;

    @FXML
    private Button startButton;

    @FXML
    private Button settingsButton;

    @FXML
    private Button feedbackButton;

    // Метод установки значений
    public void setProperties(Stage primaryStage) {
        // Установка основного окна приложения
        this.primaryStage = primaryStage;

        // Установка темы
        setTheme();
        logger.info("Properties set");
    }

    // Метод для установки темы
    private void setTheme() {
        // Загрузка настроек
        try {
            settings = Settings.loadSettings("settings.json");
            String themePath = settings.getThemePath();
            Scene scene = primaryStage.getScene();
            scene.getStylesheets().clear();
            String cssPath = Objects.requireNonNull(getClass().getResource(themePath)).toExternalForm();
            if (cssPath != null) {
                scene.getStylesheets().add(cssPath);
                logger.info("The theme is fixed");
            } else {
                logger.error("CSS file not found: {}", themePath);
            }
        } catch (IOException e) {
            logger.error("Error occurred during setting theme", e);
        }
    }

    // Метод инициализации контроллера
    @FXML
    public void initialize() {
        logger.info("Initializing HelloController");
        try {
            // Обновление локализации
            updateLocalization();

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

    // Метод для кнопки начать
    @FXML
    protected void onStartButtonClick() {
        mainStart();
    }

    private void mainStart() {
        try {
            // Загрузка нового FXML файла для основного интерфейса
            FXMLLoader loader = new FXMLLoader(getClass().
                    getResource("/org/example/_pngnp/views/main.fxml"),
                    getResourceBundle(settings.getLanguage()));
            Parent root = loader.load();
            logger.info("Main FXML file loaded successfully");

            // Сохранение текущих размеров окна
            double currentWidth = primaryStage.getWidth();
            double currentHeight = primaryStage.getHeight();
            boolean isMaximized = primaryStage.isMaximized();

            // Создание новой сцены и установка её в основное окно
            Scene scene = new Scene(root, currentWidth - 50, currentHeight - 50);
            logger.info("Scene created");

            primaryStage.setScene(scene);

            MainController controller = loader.getController();
            // Передача ссылки на главное окно в контроллер
            controller.setPrimaryStage(primaryStage);
            ImageModel model = new ImageModel();
            // Передача модели в контроллер
            controller.setModel(model);
            logger.info("Main controller initialized and primary stage set");

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

    // Метод для кнопки настроек
    public void onSettingsButtonClick() {
        showDialog("settings");
    }

    // Метод для кнопки отзыва
    public void onFeedbackButtonClick() {
        showDialog("feedback");
    }

    // Метод создания и отображения модального окна
    private void showDialog(String title) {
        try {
            // Создание загрузчика FXML для загрузки диалогового окна
            FXMLLoader loader = new FXMLLoader(getClass().
                    getResource("/org/example/_pngnp/views/" + title + "_dialog.fxml"),
                    getResourceBundle(settings.getLanguage()));
            Parent root = loader.load();
            logger.info("{} FXML file loaded successfully", title);

            // Проверка типа диалогового окна и настройка соответствующего контроллера
            if (Objects.equals(title, "settings")) {
                // Получение контроллера для диалогового окна настроек
                SettingsController controller = loader.getController();
                // Создание нового и настройка окна для диалога
                Stage dialogStage = new Stage();
                dialogStage.initModality(Modality.APPLICATION_MODAL);
                dialogStage.setTitle(title);
                dialogStage.setScene(new Scene(root, 400, 600));
                // Запрет изменения размера модального окна
                dialogStage.setResizable(false);

                // Передача ссылки на диалоговое окно в контроллер
                controller.setProperties(dialogStage);
                // Отображение диалогового окна и ожидание его закрытия
                dialogStage.showAndWait();

                // Установка темы
                setTheme();
                // Установка локализации
                updateLocalization();
            } else {
                // Получение контроллера для диалогового окна обратной связи
                FeedbackController controller = loader.getController();
                // Создание нового и настройка окна для диалога
                Stage dialogStage = new Stage();
                dialogStage.initModality(Modality.APPLICATION_MODAL);
                dialogStage.setTitle(title);
                dialogStage.setScene(new Scene(root, 400, 600));
                // Запрет изменения размера модального окна
                dialogStage.setResizable(false);

                // Передача ссылки на диалоговое окно в контроллер
                controller.setProperties(dialogStage, resources);
                // Отображение диалогового окна и ожидание его закрытия
                dialogStage.showAndWait();
            }
            logger.info("{} dialog stage are closed", title);
        } catch (IOException e) {
            logger.error("Error occurred during {} button click", title, e);
        }
    }

    // Метод для получения ResourceBundle в зависимости от языка
    private ResourceBundle getResourceBundle(String language) {
        if (language.equalsIgnoreCase("russian")) {
            logger.info("Russian localisation is fixed");
            Locale.setDefault(new Locale.Builder().setLanguage("ru").setRegion("RU").build());
            return ResourceBundle.getBundle("org.example._pngnp.messages", new Locale.Builder().
                    setLanguage("ru").setRegion("RU").build());
        }
        logger.info("English localisation is fixed");
        Locale.setDefault(Locale.ENGLISH);
        return ResourceBundle.getBundle("org.example._pngnp.messages", Locale.ENGLISH);
    }

    // Метод обновления локализации
    private void updateLocalization() {
        try {
            settings = Settings.loadSettings("settings.json");
            resources = getResourceBundle(settings.getLanguage());
            descriptionLabel.setText(resources.getString("description"));
            startButton.setText(resources.getString("start"));
            settingsButton.setText(resources.getString("settings"));
            feedbackButton.setText(resources.getString("feedback"));
        } catch (IOException e) {
            logger.error("Error occurred during updateLocalization", e);
        }
    }
}