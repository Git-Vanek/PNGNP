// Объявление пакета, к которому принадлежит класс
package org.example._pngnp.controllers;

// Импорт необходимых классов из библиотеки JavaFX для работы с графическим интерфейсом
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.stage.Stage;

// Импорт классов для логирования
import javafx.stage.WindowEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

// Импорт классов для работы с настройками
import org.example._pngnp.classes.Settings;
import java.io.IOException;
import java.util.Objects;
import java.util.Optional;

public class SettingsController {

    // Логгер для записи логов
    private static final Logger logger = LogManager.getLogger(SettingsController.class);

    private Stage dialogStage;

    // Переменная для отслеживания наличия несохраненных изменений
    private boolean unsavedChanges = false;

    @FXML
    private ComboBox<String> languageComboBox;

    @FXML
    private ComboBox<String> themeComboBox;

    // Метод для установки сцены диалога и темы
    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
        logger.info("Dialog stage set");

        // Установка темы
        setTheme();
    }

    // Метод для установки темы
    private void setTheme() {
        // Загрузка настроек
        try {
            Settings settings = Settings.loadSettings("settings.json");
            String themePath = settings.getThemePath();
            Scene scene = dialogStage.getScene();
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

    // Метод для кнопки сохранить
    @FXML
    private void onSaveButtonClick() {
        // Сохранение настроек
        saveSettings();
    }

    // Метод сохранения настроек
    private void saveSettings() {
        logger.info("Save button clicked");
        // Получение параметров
        String language = languageComboBox.getValue();
        String theme = themeComboBox.getValue();

        // Создание объекта настроек
        Settings settings = new Settings();
        settings.setLanguage(language);
        // Определение темы
        if (theme.equalsIgnoreCase("dark")) {
            settings.setThemePath("/org/example/_pngnp/styles/dark-theme.css");
        } else {
            settings.setThemePath("/org/example/_pngnp/styles/light-theme.css");
        }

        // Сохранение настроек в файл
        try {
            Settings.saveSettings(settings, "settings.json");
            logger.info("Settings saved");
            unsavedChanges = false;
            // Закрытие диалогового окна
            dialogStage.close();
        } catch (IOException e) {
            logger.error("Error saving settings: ", e);
        }
    }

    // Метод для кнопки отмены
    @FXML
    private void onCancelButtonClick() {
        logger.info("Cancel button clicked");
        // Закрытие диалогового окна без сохранения настроек
        dialogStage.close();
    }

    public void initialize() {
        logger.info("Initializing SettingsController");

        // Загрузка настроек при инициализации
        try {
            Settings settings = Settings.loadSettings("settings.json");
            languageComboBox.setValue(settings.getLanguage());
            if (settings.getThemePath().equalsIgnoreCase("/org/example/_pngnp/styles/dark-theme.css")) {
                themeComboBox.setValue("dark");
            } else {
                themeComboBox.setValue("light");
            }
            logger.info("Settings are loaded");
        } catch (IOException e) {
            logger.error("Error loading settings: ", e);
        }

        // Слушатель изменений языка
        languageComboBox.valueProperty().addListener((observable, oldValue, newValue) -> unsavedChanges = true);

        // Слушатель изменений темы
        themeComboBox.valueProperty().addListener((observable, oldValue, newValue) -> unsavedChanges = true);

        // Установка обработчика закрытия диалогового окна
        if (dialogStage != null) {
            dialogStage.setOnCloseRequest(this::handleCloseRequest);
        }
    }

    private void handleCloseRequest(WindowEvent windowEvent) {
        if (unsavedChanges) {
            logger.info("Displaying unsaved changes alert");
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Unsaved Changes");
            alert.setHeaderText("You have unsaved changes in the settings.");
            alert.setContentText("Do you want to save the changes?");

            ButtonType saveButton = new ButtonType("Save");
            ButtonType dontSaveButton = new ButtonType("Don't Save");
            ButtonType cancelButton = new ButtonType("Cancel");

            alert.getButtonTypes().setAll(saveButton, dontSaveButton, cancelButton);

            Optional<ButtonType> result = alert.showAndWait();
            if (result.isPresent()) {
                if (result.get() == saveButton) {
                    logger.info("User chose to save the changes");
                    // Сохранение изменений
                    saveSettings();
                    if (!unsavedChanges) {
                        // Закрытие диалогового окна
                        dialogStage.close();
                    } else {
                        // Отмена закрытия диалогового окна
                        windowEvent.consume();
                    }
                } else if (result.get() == dontSaveButton) {
                    logger.info("User chose not to save the changes");
                    // Закрытие диалогового окна
                    dialogStage.close();
                } else {
                    logger.info("User chose to cancel");
                    // Отмена закрытия диалогового окна
                    windowEvent.consume();
                }
            }
        } else {
            // Если нет несохраненных изменений, просто закрываем диалоговое окно
            dialogStage.close();
        }
    }
}