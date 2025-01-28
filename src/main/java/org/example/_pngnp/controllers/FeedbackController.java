// Объявление пакета, к которому принадлежит класс
package org.example._pngnp.controllers;

// Импорт необходимых классов из библиотеки JavaFX для работы с графическим интерфейсом

import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example._pngnp.classes.Notification;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;
import java.util.regex.Pattern;

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

    // Метод для кнопки отправить
    @FXML
    private void onSendButtonClick() {
        // Получение введенных данных
        String email = emailField.getText();
        String feedback = feedbackTextArea.getText();

        // Проверка данных
        if (email.isEmpty() || feedback.isEmpty()) {
            showNotification("All fields are required.");
            logger.warn("Feedback submission failed: All fields are required.");
            return;
        }

        if (!isValidEmail(email)) {
            showNotification("Invalid email address.");
            logger.warn("Feedback submission failed: Invalid email address.");
            return;
        }

        // Отправка обратной связи по электронной почте
        sendEmail(email, feedback);

        // Закрытие диалогового окна
        dialogStage.close();
    }

    // Метод для кнопки отмены
    @FXML
    private void onCancelButtonClick() {
        // Закрытие диалогового окна без отправки обратной связи
        dialogStage.close();
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
            showNotification("Failed to send email. Please try again later.");
        }
    }

    // Метод для проверки валидности адреса электронной почты
    private boolean isValidEmail(String email) {
        String emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";
        Pattern pattern = Pattern.compile(emailRegex);
        return pattern.matcher(email).matches();
    }

    // Метод для создания и отображения уведомления
    private void showNotification(String message) {
        Notification notification = new Notification("Error", message);
        notification.show();
    }
}