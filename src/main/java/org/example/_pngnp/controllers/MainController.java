// Объявление пакета, к которому принадлежит класс
package org.example._pngnp.controllers;

// Импорт необходимых классов из библиотеки JavaFX для работы с графическим интерфейсом
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.scene.control.ScrollPane;
import javafx.scene.SnapshotParameters;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Button;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

// Импорт модели изображения
import org.example._pngnp.models.ImageModel;

// Импорт классов для работы с изображениями и файлами
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Objects;

// Основной контроллер для управления интерфейсом и логикой приложения
public class MainController {
    // Модель изображения
    private ImageModel model;

    // Основное окно приложения
    private Stage primaryStage;

    // Аннотации FXML для связывания с элементами интерфейса
    @FXML
    private Button button_draw;

    @FXML
    private Button button_crop;

    @FXML
    private Button button_stickers;

    @FXML
    private Button button_filters;

    @FXML
    private Button button_layers;

    @FXML
    private Button button_brightness_and_contrast;

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

    // Текущий уровень масштабирования
    private double zoomLevel = 1.0;

    // Координаты мыши для перетаскивания
    private double mouseX, mouseY;

    // Флаг перетаскивания
    private boolean dragging = false;

    // Контекст графики для рисования на холсте
    public GraphicsContext gc;

    // Инициализация компонентов и обработчиков событий
    @FXML
    public void initialize() {
        // Установка иконок для кнопок
        setButtonImage(button_draw, "/org/example/_pngnp/images/draw.png");
        setButtonImage(button_crop, "/org/example/_pngnp/images/crop.png");
        setButtonImage(button_stickers, "/org/example/_pngnp/images/stickers.png");
        setButtonImage(button_filters, "/org/example/_pngnp/images/filters.png");
        setButtonImage(button_layers, "/org/example/_pngnp/images/layers.png");
        setButtonImage(button_brightness_and_contrast, "/org/example/_pngnp/images/brightness_and_contrast.png");

        // Установка обработчиков для слайдера размера кисти
        if (brushSizeSlider != null) {
            brushSizeSlider.valueProperty().addListener((observable, oldValue, newValue) -> gc.setLineWidth(newValue.doubleValue()));
        }

        // Установка обработчиков для выбора цвета кисти
        if (brushColorPicker != null) {
            brushColorPicker.valueProperty().addListener((observable, oldValue, newValue) -> gc.setStroke(newValue));
        }

        // Установка обработчиков для поля ввода масштаба
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

        // Установка обработчиков событий мыши для перемещения изображения
        if (scrollPane != null) {
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

    // Метод установки иконки для кнопки
    private void setButtonImage(Button button, String imagePath) {
        Image image = new Image(Objects.requireNonNull(getClass().getResourceAsStream(imagePath)));
        ImageView imageView = new ImageView(image);
        imageView.setFitWidth(30);
        imageView.setFitHeight(30);
        button.setGraphic(imageView);
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
                //noinspection CallToPrintStackTrace
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

    // Применение фильтра
    @FXML
    private void applySobelFilter() {
        model.apply1Filter();
        imageView.setImage(model.getImage());
    }
}