// Объявление пакета, к которому принадлежит класс
package org.example._pngnp.controllers;

// Импорт необходимых классов из библиотеки JavaFX для работы с графическим интерфейсом
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.scene.control.ScrollPane;
import javafx.scene.SnapshotParameters;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

// Импорт классов для логирования
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

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

    // Логгер для записи логов
    private static final Logger logger = LogManager.getLogger(MainController.class);

    // Модель изображения
    private ImageModel model;

    // Основное окно приложения
    private Stage primaryStage;

    // Аннотации FXML для связывания с элементами интерфейса
    @FXML
    private Button button_toggle_mode;

    @FXML
    private Button button_draw_mode;

    @FXML
    private Button button_crop_mode;

    @FXML
    private Button button_text_mode;

    @FXML
    private Button button_stickers_mode;

    @FXML
    private Button button_filters_mode;

    @FXML
    private Button button_layers_mode;

    @FXML
    private Button button_brightness_and_contrast_mode;

    @FXML
    private ScrollPane scrollPane;

    @FXML
    private ImageView imageView;

    @FXML
    private Canvas canvas;

    @FXML
    private TextField zoomTextField;

    // Текущий уровень масштабирования
    private double zoomLevel = 1.0;

    // Координаты мыши для перетаскивания
    private double mouseX, mouseY;

    // Флаг перетаскивания
    private boolean dragging = false;

    // Инициализация компонентов и обработчиков событий
    @FXML
    public void initialize() {
        logger.info("Initializing MainController");
        // Установка иконок для кнопок
        setButtonImage(button_toggle_mode, "/org/example/_pngnp/images/toggle.png");
        setButtonImage(button_draw_mode, "/org/example/_pngnp/images/draw.png");
        setButtonImage(button_crop_mode, "/org/example/_pngnp/images/crop.png");
        setButtonImage(button_text_mode, "/org/example/_pngnp/images/text.png");
        setButtonImage(button_stickers_mode, "/org/example/_pngnp/images/stickers.png");
        setButtonImage(button_filters_mode, "/org/example/_pngnp/images/filters.png");
        setButtonImage(button_layers_mode, "/org/example/_pngnp/images/layers.png");
        setButtonImage(button_brightness_and_contrast_mode, "/org/example/_pngnp/images/brightness_and_contrast.png");

        // Установка обработчиков для поля ввода масштаба
        if (zoomTextField != null) {
            zoomTextField.textProperty().addListener((observable, oldValue, newValue) -> {
                try {
                    double newZoomLevel = Double.parseDouble(newValue.replace("%", "")) / 100.0;
                    if (newZoomLevel > 0) {
                        zoomLevel = newZoomLevel;
                        updateZoom();
                        logger.info("ZoomTextField - Zoom level changed to: " + zoomLevel);
                    }
                } catch (NumberFormatException e) {
                    logger.warn("ZoomTextField - Invalid zoom level input: " + newValue);
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

            scrollPane.addEventFilter(MouseEvent.MOUSE_RELEASED, event -> {
                dragging = false;
                logger.info("Mouse released");
            });
        }
    }

    // Метод установки иконки для кнопки
    private void setButtonImage(Button button, String imagePath) {
        Image image = new Image(Objects.requireNonNull(getClass().getResourceAsStream(imagePath)));
        ImageView imageView = new ImageView(image);
        imageView.setFitWidth(30);
        imageView.setFitHeight(30);
        button.setGraphic(imageView);
        logger.info("Button image set for: " + button.getId());
    }

    // Метод установки модели изображения
    public void setModel(ImageModel model) {
        this.model = model;
        logger.info("Image model set");
    }

    // Метод установки основного окна приложения
    public void setPrimaryStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
        logger.info("Primary stage set");
    }

    // Метод для кнопки переключения на режим перемещения
    @FXML
    private void toggleMode() {

    }

    // Метод для кнопки переключения на режим рисования
    @FXML
    private void drawMode() {

    }

    // Метод для кнопки переключения на режим редактирования
    @FXML
    private void cropMode() {

    }

    // Метод для кнопки переключения на режим работы с текстом
    @FXML
    private void textMode() {

    }

    // Метод для кнопки переключения на режим работы со стикерами
    @FXML
    private void stickersMode() {

    }

    // Метод для кнопки переключения на режим работы с фильтрами
    @FXML
    private void filtersMode() {

    }

    // Метод для кнопки переключения на режим работы со слоями
    @FXML
    private void layersMode() {

    }

    // Метод для кнопки переключения на режим
    @FXML
    private void brightnessAndContrastMode() {

    }

    // Метод для кнопки загрузка изображения
    @FXML
    private void loadImage() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Image File");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.bmp")
        );
        File file = fileChooser.showOpenDialog(primaryStage);
        if (file != null) {
            String originalPath = file.getAbsolutePath();
            model.loadImage(originalPath);
            imageView.setImage(model.getImage());
            updateScrollPane();
            logger.info("Image loaded from: " + originalPath);
        } else {
            logger.warn("No image file selected");
        }
    }

    // Метод для кнопки сохранения изображения
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
                    logger.info("Image saved to: " + file.getAbsolutePath());
                } else {
                    logger.warn("No image to save");
                }
            } catch (IOException e) {
                logger.error("Error saving image", e);
            }
        } else {
            logger.warn("No file selected for saving image");
        }
    }

    // Метод объединения изображения с содержимым холста
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

    // Метод для кнопки отмены действия
    @FXML
    private void undo() {

    }

    // Метод для кнопки возврата отмены действя
    @FXML
    private void redo() {

    }

    // Метод для кнопки увеличения масштаба
    @FXML
    private void increaseZoom() {
        zoomLevel += 0.1;
        updateZoom();
        logger.info("ButtonIncreaseZoom - Zoom level increased to: " + zoomLevel);
    }

    // Метод для кнопки уменьшения масштаба
    @FXML
    private void decreaseZoom() {
        zoomLevel -= 0.1;
        if (zoomLevel < 0.1) {
            zoomLevel = 0.1;
        }
        updateZoom();
        logger.info("ButtonDecreaseZoom - Zoom level decreased to: " + zoomLevel);
    }

    // Метод обновления масштабов imageView и canvas
    private void updateZoom() {
        double newWidth = imageView.getImage().getWidth() * zoomLevel;
        double newHeight = imageView.getImage().getHeight() * zoomLevel;

        imageView.setFitWidth(newWidth);
        imageView.setFitHeight(newHeight);

        canvas.setWidth(newWidth);
        canvas.setHeight(newHeight);

        zoomTextField.setText(String.format("%.0f%%", zoomLevel * 100));

        // Обновление размеров ScrollPane
        updateScrollPane();

        logger.info("Zoom level updated to: " + zoomLevel);
    }

    // Метод обновления размеров ScrollPane
    private void updateScrollPane() {
        if (scrollPane != null) {
            scrollPane.layout();
            // Сброс содержимого для корректного обновления
            scrollPane.setContent(null);
            // Установка нового содержимого
            scrollPane.setContent(new StackPane(imageView, canvas));
            scrollPane.setFitToWidth(true);
            scrollPane.setFitToHeight(true);
            logger.info("ScrollPane layout updated");
        }
    }
}