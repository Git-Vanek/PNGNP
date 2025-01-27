// Объявление пакета, к которому принадлежит класс
package org.example._pngnp.controllers;

// Импорт необходимых классов из библиотеки JavaFX для работы с графическим интерфейсом
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import javax.mail.*;
import javax.mail.internet.*;
import java.util.Properties;
import java.util.regex.Pattern;

// Импорт классов для логирования
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example._pngnp.classes.Notification;

public class FeedbackController {

    // Логгер для записи логов
    private static final Logger logger = LogManager.getLogger(FeedbackController.class);

    @FXML
    private TextField emailField;

    @FXML
    private TextArea feedbackTextArea;

    private Stage dialogStage;

    // Метод для установки сцены диалога
    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }

    // Метод для обработки нажатия кнопки "Send"
    @FXML
    private void onSendButtonClick(ActionEvent event) {
        // Получение введенных данных
        String email = emailField.getText();
        String feedback = feedbackTextArea.getText();

        // Проверка данных
        if (email.isEmpty() || feedback.isEmpty()) {
            showNotification("Error", "All fields are required.");
            logger.warn("Feedback submission failed: All fields are required.");
            return;
        }

        if (!isValidEmail(email)) {
            showNotification("Error", "Invalid email address.");
            logger.warn("Feedback submission failed: Invalid email address.");
            return;
        }

        // Отправка обратной связи по электронной почте
        sendEmail(email, feedback);

        // Закрытие диалогового окна
        dialogStage.close();
    }

    // Метод для обработки нажатия кнопки "Cancel"
    @FXML
    private void onCancelButtonClick(ActionEvent event) {
        // Закрытие диалогового окна без отправки обратной связи
        dialogStage.close();
    }

    // Метод для отправки электронной почты
    private void sendEmail(String userEmail, String feedback) {
        String to = "ki16082005@gmail.com";
        String from = "your-email@example.com";
        String host = "smtp.example.com";

        // Настройки для SMTP сервера
        Properties properties = new Properties();
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.starttls.enable", "true");
        properties.put("mail.smtp.host", host);
        properties.put("mail.smtp.port", "587");

        // Сессия для отправки электронной почты
        Session session = Session.getInstance(properties, new javax.mail.Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication("your-email@example.com", "your-email-password");
            }
        });

        try {
            // Создание сообщения
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(from));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
            message.setSubject("Feedback from PNGNP");
            message.setText("From: " + userEmail + "\n\n" + feedback);

            // Отправка сообщения
            Transport.send(message);

            logger.info("Email sent successfully to " + to);
        } catch (MessagingException mex) {
            logger.warn("Failed to send email", mex);
            showNotification("Error", "Failed to send email. Please try again later.");
        }
    }

    // Метод для проверки валидности адреса электронной почты
    private boolean isValidEmail(String email) {
        String emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";
        Pattern pattern = Pattern.compile(emailRegex);
        return pattern.matcher(email).matches();
    }

    // Метод для создания и отображения уведомления
    private void showNotification(String title, String message) {
        Notification notification = new Notification(title, message);
        notification.show();
    }
}