package org.example._pngnp;

import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.scene.control.ScrollPane;
import javafx.scene.SnapshotParameters;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

// Основной контроллер для управления интерфейсом и логикой приложения
public class MainController {
    private ImageModel model;
    private Stage primaryStage;

    @FXML
    private ScrollPane scrollPane;

    @FXML
    private ImageView imageView;

    @FXML
    private Canvas canvas;

    @FXML
    private Slider brushSizeSlider;

    @FXML
    private ColorPicker brushColorPicker;

    @FXML
    private TextField zoomTextField;

    private double zoomLevel = 1.0;
    private double mouseX, mouseY;
    private boolean dragging = false;

    private GraphicsContext gc;
    private double lastX, lastY;

    // Инициализация компонентов и обработчиков событий
    @FXML
    public void initialize() {
        if (canvas != null) {
            gc = canvas.getGraphicsContext2D();
            gc.setStroke(Color.RED);
            gc.setLineWidth(brushSizeSlider.getValue());

            canvas.setOnMousePressed(this::onMousePressed);
            canvas.setOnMouseDragged(this::onMouseDragged);
            canvas.setOnMouseReleased(this::onMouseReleased);
        }

        if (brushSizeSlider != null) {
            brushSizeSlider.valueProperty().addListener((observable, oldValue, newValue) -> gc.setLineWidth(newValue.doubleValue()));
        }

        if (brushColorPicker != null) {
            brushColorPicker.valueProperty().addListener((observable, oldValue, newValue) -> gc.setStroke(newValue));
        }

        if (zoomTextField != null) {
            zoomTextField.textProperty().addListener((observable, oldValue, newValue) -> {
                try {
                    double newZoomLevel = Double.parseDouble(newValue.replace("%", "")) / 100.0;
                    if (newZoomLevel > 0) {
                        zoomLevel = newZoomLevel;
                        updateZoom();
                    }
                } catch (NumberFormatException e) {
                    // Игнорирование некорректного ввода
                }
            });
        }

        if (scrollPane != null) {
            // Обработчики событий мыши для перемещения изображения
            scrollPane.addEventFilter(MouseEvent.MOUSE_PRESSED, event -> {
                mouseX = event.getSceneX();
                mouseY = event.getSceneY();
                dragging = true;
            });

            scrollPane.addEventFilter(MouseEvent.MOUSE_DRAGGED, event -> {
                if (dragging) {
                    double deltaX = event.getSceneX() - mouseX;
                    double deltaY = event.getSceneY() - mouseY;
                    scrollPane.setHvalue(scrollPane.getHvalue() - deltaX / scrollPane.getContent().getBoundsInLocal().getWidth());
                    scrollPane.setVvalue(scrollPane.getVvalue() - deltaY / scrollPane.getContent().getBoundsInLocal().getHeight());
                    mouseX = event.getSceneX();
                    mouseY = event.getSceneY();
                }
            });

            scrollPane.addEventFilter(MouseEvent.MOUSE_RELEASED, event -> dragging = false);
        }
    }

    // Установка модели изображения
    public void setModel(ImageModel model) {
        this.model = model;
    }

    // Установка основного окна приложения
    public void setPrimaryStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    // Загрузка изображения
    @FXML
    private void loadImage() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Image File");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.gif", "*.bmp")
        );
        File file = fileChooser.showOpenDialog(primaryStage);
        if (file != null) {
            String originalPath = file.getAbsolutePath();
            model.loadImage(originalPath);
            imageView.setImage(model.getImage());
            updateScrollPane();
        }
    }

    // Сохранение изображения
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
                    // Объединение изображения с содержимым холста
                    WritableImage combinedImage = combineImageWithCanvas(image);
                    BufferedImage bufferedImage = SwingFXUtils.fromFXImage(combinedImage, null);
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

    // Объединение изображения с содержимым холста
    private WritableImage combineImageWithCanvas(javafx.scene.image.Image image) {
        int width = (int) image.getWidth();
        int height = (int) image.getHeight();
        WritableImage combinedImage = new WritableImage(width, height);
        PixelWriter writer = combinedImage.getPixelWriter();

        // Рисование изображения на объединенное изображение
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                writer.setColor(x, y, image.getPixelReader().getColor(x, y));
            }
        }

        // Рисование содержимого холста на объединенное изображение
        SnapshotParameters params = new SnapshotParameters();
        params.setFill(Color.TRANSPARENT);
        WritableImage canvasImage = canvas.snapshot(params, null);
        int canvasWidth = (int) canvas.getWidth();
        int canvasHeight = (int) canvas.getHeight();

        for (int y = 0; y < Math.min(height, canvasHeight); y++) {
            for (int x = 0; x < Math.min(width, canvasWidth); x++) {
                Color canvasColor = canvasImage.getPixelReader().getColor(x, y);
                if (canvasColor.getOpacity() > 0) {
                    writer.setColor(x, y, canvasColor);
                }
            }
        }

        return combinedImage;
    }

    // Получение расширения файла
    private String getFileExtension(File file) {
        String name = file.getName();
        int lastIndexOf = name.lastIndexOf(".");
        if (lastIndexOf == -1) {
            return "png"; // формат по умолчанию
        }
        return name.substring(lastIndexOf + 1);
    }

    // Применение фильтра серого цвета
    @FXML
    private void applyGrayscaleFilter() {
        model.applyGrayscaleFilter();
        imageView.setImage(model.getImage());
    }

    // Применение медианного фильтра
    @FXML
    private void applyMedianFilter() {
        model.applyMedianFilter(3); // Пример размера
        imageView.setImage(model.getImage());
    }

    // Применение порогового фильтра
    @FXML
    private void applyThresholdFilter() {
        model.applyThresholdFilter(0.5); // Пример порога
        imageView.setImage(model.getImage());
    }

    // Применение фильтра Собеля
    @FXML
    private void applySobelFilter() {
        model.applySobelFilter();
        imageView.setImage(model.getImage());
    }

    // Обработка нажатия мыши
    private void onMousePressed(MouseEvent event) {
        lastX = event.getX();
        lastY = event.getY();
    }

    // Обработка перетаскивания мыши
    private void onMouseDragged(MouseEvent event) {
        gc.strokeLine(lastX, lastY, event.getX(), event.getY());
        lastX = event.getX();
        lastY = event.getY();
    }

    // Обработка отпускания мыши
    private void onMouseReleased(MouseEvent event) {
        // Обработка завершения рисования, если необходимо
    }

    // Увеличение масштаба
    @FXML
    private void increaseZoom() {
        zoomLevel += 0.1;
        updateZoom();
    }

    // Уменьшение масштаба
    @FXML
    private void decreaseZoom() {
        zoomLevel -= 0.1;
        if (zoomLevel < 0.1) {
            zoomLevel = 0.1;
        }
        updateZoom();
    }

    // Обновление масштаба
    private void updateZoom() {
        imageView.setScaleX(zoomLevel);
        imageView.setScaleY(zoomLevel);
        zoomTextField.setText(String.format("%.0f%%", zoomLevel * 100));
        updateScrollPane();
    }

    // Обновление размеров ScrollPane
    private void updateScrollPane() {
        if (scrollPane != null) {
            scrollPane.layout();
        }
    }
}