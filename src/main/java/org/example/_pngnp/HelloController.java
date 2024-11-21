package org.example._pngnp;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

public class HelloController {

    @FXML
    private ImageView logoImageView;

    @FXML
    public void initialize() {
        // Загрузка логотипа из папки res
        Image logoImage = new Image(getClass().getResourceAsStream("/images/logo.png"));
        logoImageView.setImage(logoImage);
    }

    @FXML
    protected void onHelloButtonClick(ActionEvent event) {
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
