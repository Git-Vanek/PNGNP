package org.example._pngnp.classes;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;

public class Notification {
    private final String title;
    private final String message;

    public Notification(String title, String message) {
        this.title = title;
        this.message = message;
    }

    public void show() {
        Stage notificationStage = new Stage();
        notificationStage.initStyle(StageStyle.UTILITY);

        Label titleLabel = new Label(title);
        titleLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        Label messageLabel = new Label(message);
        messageLabel.setStyle("-fx-font-size: 14px;");

        VBox layout = new VBox(10, titleLabel, messageLabel);
        layout.setStyle("-fx-padding: 10px; -fx-background-color: #f0f0f0; -fx-border-color: #ccc; -fx-border-width: 1px;");

        Scene scene = new Scene(layout, 300, 100);
        notificationStage.setScene(scene);
        notificationStage.show();

        Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(3), event -> notificationStage.close()));
        timeline.play();
    }
}
