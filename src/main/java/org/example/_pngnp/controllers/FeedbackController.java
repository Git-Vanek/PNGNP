// Объявление пакета, к которому принадлежит класс
package org.example._pngnp.controllers;

// Импорт необходимых классов из библиотеки JavaFX для работы с графическим интерфейсом
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example._pngnp.classes.Notification;
import org.example._pngnp.classes.Settings;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.IOException;
import java.util.Objects;
import java.util.Optional;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.regex.Pattern;

public class FeedbackController {

    // Логгер для записи логов
    private static final Logger logger = LogManager.getLogger(FeedbackController.class);

    private Stage dialogStage;
    private ResourceBundle resources;
    private boolean unsavedChanges = false;

    @FXML
    private TextField emailField;

    @FXML
    private TextArea feedbackTextArea;

    // Метод для установки значений
    public void setProperties(Stage dialogStage, ResourceBundle resources) {
        this.dialogStage = dialogStage;
        this.resources = resources;

        // Установка темы
        setTheme();

        // Установка обработчика закрытия диалогового окна
        dialogStage.setOnCloseRequest(windowEvent ->
                handleUnsavedChanges(windowEvent, dialogStage::close));
        logger.info("Properties set");
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

    // Инициализация компонентов и обработчиков событий
    public void initialize() {
        logger.info("Initializing FeedbackController");

        // Добавляем слушатели на изменение почты
        emailField.textProperty().addListener((observable,
                                               oldValue, newValue) -> unsavedChanges = true);

        // Добавляем слушатели на изменение письма
        feedbackTextArea.textProperty().addListener((observable,
                                                     oldValue, newValue) -> unsavedChanges = true);
    }

    private void handleUnsavedChanges(Event event, Runnable onNoUnsavedChanges) {
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
                    onSendButtonClick();
                    if (!unsavedChanges) {
                        // Закрытие диалогового окна
                        onNoUnsavedChanges.run();
                    } else {
                        // Отмена закрытия диалогового окна
                        event.consume();
                    }
                } else if (result.get() == dontSaveButton) {
                    logger.info("User chose not to save the changes");
                    // Закрытие диалогового окна
                    onNoUnsavedChanges.run();
                } else {
                    logger.info("User chose to cancel");
                    // Отмена закрытия диалогового окна
                    event.consume();
                }
            }
        } else {
            // Если нет несохраненных изменений, просто закрываем диалоговое окно
            onNoUnsavedChanges.run();
        }
    }

    // Метод для кнопки отправить
    @FXML
    private void onSendButtonClick() {
        // Получение введенных данных
        String email = emailField.getText();
        String feedback = feedbackTextArea.getText();

        // Проверка данных
        if (email.isEmpty() || feedback.isEmpty()) {
            showNotification(resources.getString("notification_all_fields_required"));
            return;
        }

        if (!isValidEmail(email)) {
            showNotification(resources.getString("notification_invalid_email"));
            return;
        }

        // Отправка обратной связи по электронной почте
        sendEmail(email, feedback);

        // Закрытие диалогового окна
        dialogStage.close();
    }

    // Метод для проверки валидности адреса электронной почты
    private boolean isValidEmail(String email) {
        String emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";
        Pattern pattern = Pattern.compile(emailRegex);
        return pattern.matcher(email).matches();
    }

    // Метод для отправки электронной почты
    private void sendEmail(String userEmail, String feedback) {
        try {
            Properties props = new Properties();
            props.put("mail.smtp.auth", "true");
            props.put("mail.smtp.starttls.enable", "true");
            props.put("mail.smtp.host", "smtp.gmail.com");
            props.put("mail.smtp.port", "587");
            props.put("mail.smtp.ssl.protocols", "TLSv1.2");
            props.put("mail.smtp.ssl.trust", "smtp.gmail.com");
            String userLogin = "oscarok2005@gmail.com";
            String userPassword = "ecua moel oizg vkyx";
            Session session = Session.getInstance(
                    props,
                    new javax.mail.Authenticator() {
                        protected PasswordAuthentication getPasswordAuthentication() {
                            return new PasswordAuthentication(userLogin, userPassword);
                        }
                    }
            );
            MimeMessage message = new MimeMessage(session);
            try {
                message.setSubject("feedback PNGNP");
                message.setText("From: " + userEmail + "\n\n" + feedback);
                message.setRecipients(
                        Message.RecipientType.TO,
                        InternetAddress.parse("test1pngnp@gmail.com")
                );
                session.getTransport("smtps");
                Transport.send(message);
                logger.info("Email sent successfully to test1pngnp@gmail.com");
            } catch (MessagingException e) {
                throw new RuntimeException(e);
            }
        } catch (Exception ex) {
            logger.warn("Failed to send email", ex);
            showNotification(resources.getString("notification_failed_to_send_email"));
        }
    }

    // Метод для создания и отображения уведомления
    private void showNotification(String message) {
        Notification notification = new Notification("Error", message);
        notification.show();
    }

    // Метод для кнопки отмены
    @FXML
    private void onCancelButtonClick(ActionEvent event) {
        // Закрытие диалогового окна без отправки обратной связи
        handleUnsavedChanges(event, dialogStage::close);
    }
}