package org.example._pngnp;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class MainController {
    private ImageModel model;
    private Stage primaryStage;

    @FXML
    private ImageView imageView;

    @FXML
    private Button loadButton;

    @FXML
    private Button grayscaleButton;

    @FXML
    private Button medianButton;

    @FXML
    private Button thresholdButton;

    @FXML
    private Button sobelButton;

    public void initialize(ImageModel model, Stage primaryStage) {
        this.model = model;
        this.primaryStage = primaryStage;
    }

    @FXML
    private void loadImage() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Image File");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.gif")
        );
        String filePath = fileChooser.showOpenDialog(primaryStage).getAbsolutePath();
        model.loadImage(filePath);
        imageView.setImage(model.getImage());
    }

    @FXML
    private void applyGrayscaleFilter() {
        model.applyGrayscaleFilter();
        imageView.setImage(model.getImage());
    }

    @FXML
    private void applyMedianFilter() {
        model.applyMedianFilter(3); // Example size
        imageView.setImage(model.getImage());
    }

    @FXML
    private void applyThresholdFilter() {
        model.applyThresholdFilter(0.5); // Example threshold
        imageView.setImage(model.getImage());
    }

    @FXML
    private void applySobelFilter() {
        model.applySobelFilter();
        imageView.setImage(model.getImage());
    }
}