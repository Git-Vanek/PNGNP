// Объявление пакета, к которому принадлежит класс
package org.example._pngnp.controllers;

// Импорт необходимых классов из библиотеки JavaFX для работы с графическим интерфейсом
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import com.sendgrid.*;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Content;
import com.sendgrid.helpers.mail.objects.Email;
import java.io.IOException;
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
        Email from = new Email(userEmail);
        String subject = "Feedback from PNGNP";
        Email to = new Email("ki16082005@gmail.com");
        Content content = new Content("text/plain", "From: " + userEmail + "\n\n" + feedback);
        Mail mail = new Mail(from, subject, to, content);

        SendGrid sg = new SendGrid("YOUR_SENDGRID_API_KEY");
        Request request = new Request();
        try {
            request.setMethod(Method.POST);
            request.setEndpoint("mail/send");
            request.setBody(mail.build());
            Response response = sg.api(request);
            logger.info("Email sent successfully to " + to.getEmail());
        } catch (IOException ex) {
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