// Объявление пакета, к которому принадлежит класс
package org.example._pngnp.controllers;

// Импорт необходимых классов из библиотеки JavaFX для работы с графическим интерфейсом
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

// Импорт классов для логирования
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class SettingsController {

    // Логгер для записи логов
    private static final Logger logger = LogManager.getLogger(FeedbackController.class);

    @FXML
    private TextField setting1Field;

    @FXML
    private TextField setting2Field;

    private Stage dialogStage;

    // Метод для установки сцены диалога
    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }

    // Метод для обработки нажатия кнопки "Save"
    @FXML
    private void onSaveButtonClick(ActionEvent event) {
        String setting1 = setting1Field.getText();
        String setting2 = setting2Field.getText();

        logger.info("Save button clicked. Setting1: {}, Setting2: {}", setting1, setting2);
        // Закрытие диалогового окна
        dialogStage.close();
    }

    // Метод для обработки нажатия кнопки "Cancel"
    @FXML
    private void onCancelButtonClick(ActionEvent event) {
        logger.info("Cancel button clicked. No settings were saved.");
        // Закрытие диалогового окна без сохранения настроек
        dialogStage.close();
    }
}