package org.example._pngnp;

import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.Slider;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class MainController {
    private ImageModel model;
    private Stage primaryStage;

    @FXML
    private ImageView imageView;

    @FXML
    private Canvas canvas;

    @FXML
    private Slider brushSizeSlider;

    @FXML
    private ColorPicker brushColorPicker;

    private GraphicsContext gc;
    private double lastX, lastY;

    public void initialize(ImageModel model, Stage primaryStage) {
        this.model = model;
        this.primaryStage = primaryStage;

        gc = canvas.getGraphicsContext2D();
        gc.setStroke(Color.RED);
        gc.setLineWidth(brushSizeSlider.getValue());

        canvas.setOnMousePressed(this::onMousePressed);
        canvas.setOnMouseDragged(this::onMouseDragged);
        canvas.setOnMouseReleased(this::onMouseReleased);

        brushSizeSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
            gc.setLineWidth(newValue.doubleValue());
        });

        brushColorPicker.valueProperty().addListener((observable, oldValue, newValue) -> {
            gc.setStroke(newValue);
        });
    }

    @FXML
    private void loadImage() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Image File");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.gif", "*.bmp")
        );
        java.io.File file = fileChooser.showOpenDialog(primaryStage);
        if (file != null) {
            String originalPath = file.getAbsolutePath();
            model.loadImage(originalPath);
            imageView.setImage(model.getImage());
        }
    }

    @FXML
    private void saveImage() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save Image File");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("PNG Files", "*.png"),
                new FileChooser.ExtensionFilter("JPEG Files", "*.jpg"),
                new FileChooser.ExtensionFilter("BMP Files", "*.bmp")
        );
        File file = fileChooser.showSaveDialog(primaryStage);
        if (file != null) {
            try {
                javafx.scene.image.Image image = model.getImage();
                if (image != null) {
                    BufferedImage bufferedImage = SwingFXUtils.fromFXImage(image, null);
                    String format = getFileExtension(file);
                    ImageIO.write(bufferedImage, format, file);
                } else {
                    System.out.println("No image to save.");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private String getFileExtension(File file) {
        String name = file.getName();
        int lastIndexOf = name.lastIndexOf(".");
        if (lastIndexOf == -1) {
            return "png"; // default format
        }
        return name.substring(lastIndexOf + 1);
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

    private void onMousePressed(MouseEvent event) {
        lastX = event.getX();
        lastY = event.getY();
    }

    private void onMouseDragged(MouseEvent event) {
        gc.strokeLine(lastX, lastY, event.getX(), event.getY());
        lastX = event.getX();
        lastY = event.getY();
    }

    private void onMouseReleased(MouseEvent event) {
        // Обработка завершения рисования, если необходимо
    }
}