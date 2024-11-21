package org.example._pngnp;

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
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class HelloController {

    private static final Logger logger = LogManager.getLogger(HelloController.class);
    @FXML
    private ImageView logoImageView;

    @FXML
    public void initialize() {
        // Загрузка логотипа из папки resources
        Image logoImage = new Image(getClass().getResourceAsStream("/images/logo.png"));
        logoImageView.setImage(logoImage);
        // Запись в лог
        logger.info("Logo loaded");
        // Анимация появления логотипа
        FadeTransition fadeTransition = new FadeTransition(Duration.seconds(2), logoImageView);
        fadeTransition.setFromValue(0.0);
        fadeTransition.setToValue(1.0);
        fadeTransition.play();
    }

    @FXML
    protected void onStartButtonClick(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("main.fxml"));
            Parent root = loader.load();
            MainController controller = loader.getController();

            // Передача модели и основного окна в контроллер
            ImageModel model = new ImageModel();
            Stage primaryStage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
            controller.initialize(model, primaryStage);

            Scene scene = new Scene(root, 800, 600);
            primaryStage.setScene(scene);
            primaryStage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
