// Объявление пакета, к которому принадлежит класс
package org.example._pngnp.controllers;

// Импорт необходимых классов из библиотеки JavaFX для работы с графическим интерфейсом
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.SnapshotParameters;
import javafx.scene.canvas.Canvas;
import javafx.scene.image.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.transform.Affine;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

// Импорт классов для логирования
import javafx.stage.WindowEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

// Импорт модели изображения
import org.example._pngnp.classes.Notification;
import org.example._pngnp.models.ImageModel;

// Импорт классов для работы с изображениями и файлами
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Objects;
import java.util.Optional;
import java.util.Stack;

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

    // Флаг перетаскивания
    private boolean dragging = false;

    // Координаты мыши для перетаскивания
    private double mouseX, mouseY;

    // Переменная для рисования
    private GraphicsContext gc;

    // Флаг рисования
    private boolean drawing = false;

    // Координаты мыши для рисования
    private double lastX, lastY;

    // Переменная для отслеживания текущего режима
    private String currentMode = "DRAG";

    // Стеки для отмены и возврата отмены действий
    private final Stack<ImageModel> undoStack = new Stack<>();
    private final Stack<ImageModel> redoStack = new Stack<>();

    // Переменная для отслеживания наличия несохраненных изменений
    private boolean unsavedChanges = false;

    // Инициализация компонентов и обработчиков событий
    @FXML
    public void initialize() {
        logger.info("Initializing MainController");

        // Установка иконок для кнопок
        setButtonImage(button_draw_mode, "/org/example/_pngnp/images/draw.png");
        setButtonImage(button_crop_mode, "/org/example/_pngnp/images/crop.png");
        setButtonImage(button_stickers_mode, "/org/example/_pngnp/images/stickers.png");
        setButtonImage(button_filters_mode, "/org/example/_pngnp/images/filters.png");
        setButtonImage(button_layers_mode, "/org/example/_pngnp/images/layers.png");
        setButtonImage(button_brightness_and_contrast_mode, "/org/example/_pngnp/images/brightness_and_contrast.png");
        setButtonImage(button_toggle_mode, "/org/example/_pngnp/images/toggle.png");
        setButtonImage(button_text_mode, "/org/example/_pngnp/images/text.png");

        // Установка обработчиков для поля ввода масштаба
        if (zoomTextField != null) {
            zoomTextField.textProperty().addListener((observable, oldValue, newValue) -> {
                try {
                    double newZoomLevel = Double.parseDouble(newValue.replace("%", "")) / 100.0;
                    if (newZoomLevel > 0) {
                        if (imageView.getImage() != null) {
                            // Ограничение минимального и максимального значения масштаба
                            if (newZoomLevel < 0.1) {
                                newZoomLevel = 0.1;
                            } else if (newZoomLevel > 5.0) {
                                newZoomLevel = 5.0;
                            }
                            zoomLevel = newZoomLevel;
                            updateZoom();
                            logger.info("ZoomTextField - Zoom level changed to: " + zoomLevel);
                        } else {
                            logger.info("ZoomTextField - Image was not uploaded");
                        }
                    }
                } catch (NumberFormatException e) {
                    logger.warn("ZoomTextField - Invalid zoom level input: " + newValue);
                }
            });
        }

        // Установка обработчиков событий мыши для перемещения изображения
        if (scrollPane != null) {
            scrollPane.addEventFilter(MouseEvent.MOUSE_PRESSED, this::scrollPaneEventMousePressed);
            scrollPane.addEventFilter(MouseEvent.MOUSE_DRAGGED, this::scrollPaneEventMouseDragged);
            scrollPane.addEventFilter(MouseEvent.MOUSE_RELEASED, this::scrollPaneEventMouseReleased);
        }

        // Инициализация GraphicsContext для рисования на Canvas
        gc = canvas.getGraphicsContext2D();
        gc.setStroke(Color.BLACK);
        gc.setLineWidth(2);

        // Установка обработчиков событий мыши для рисования
        canvas.addEventHandler(MouseEvent.MOUSE_PRESSED, this::canvasEventMousePressed);
        canvas.addEventHandler(MouseEvent.MOUSE_DRAGGED, this::canvasEventMouseDragged);
        canvas.addEventHandler(MouseEvent.MOUSE_RELEASED, this::canvasEventMouseReleased);

    }

    // Метод установки модели изображения
    public void setModel(ImageModel model) {
        this.model = model;
        logger.info("Image model set");
    }

    // Метод установки основного окна приложения и обработчика закрытия
    public void setPrimaryStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
        logger.info("Primary stage set");

        // Установка обработчика закрытия окна
        if (primaryStage != null) {
            primaryStage.setOnCloseRequest(this::handleCloseRequest);
        }
    }

    // Обработчик закрытия окна
    private void handleCloseRequest(WindowEvent windowEvent) {
        if (unsavedChanges) {
            logger.info("Displaying unsaved changes alert");
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Unsaved Changes");
            alert.setHeaderText("You have unsaved changes in the photo.");
            alert.setContentText("Do you want to save the changes?");

            ButtonType saveButton = new ButtonType("Save");
            ButtonType dontSaveButton = new ButtonType("Don't Save");
            ButtonType cancelButton = new ButtonType("Cancel");

            alert.getButtonTypes().setAll(saveButton, dontSaveButton, cancelButton);

            Optional<ButtonType> result = alert.showAndWait();
            if (result.isPresent()) {
                if (result.get() == saveButton) {
                    logger.info("User chose to save the changes");
                    // Сохранение изменений
                    saveImage();
                    if (!unsavedChanges) {
                        // Закрытие окна
                        primaryStage.close();
                    }
                } else if (result.get() == dontSaveButton) {
                    logger.info("User chose not to save the changes");
                    // Закрытие окна
                    primaryStage.close();
                } else {
                    logger.info("User chose to cancel");
                    // Отмена закрытия окна
                    windowEvent.consume();
                }
            }
        } else {
            // Если нет несохраненных изменений, просто закрываем окно
            primaryStage.close();
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

    // Обрабочик нажатия мыши на ScrollPane
    private void scrollPaneEventMousePressed(MouseEvent event) {
        if (currentMode.equals("DRAG")) {
            startDragging(event);
        }
    }

    // Обрабочик задержания мыши на ScrollPane
    private void scrollPaneEventMouseDragged(MouseEvent event) {
        if (currentMode.equals("DRAG")) {
            continueDragging(event);
        }
    }

    // Обрабочик окончания задержания мыши на ScrollPane
    private void scrollPaneEventMouseReleased(MouseEvent event) {
        if (currentMode.equals("DRAG")) {
            stopDragging();
        }
    }

    // Метод для начала рисования
    private void startDragging(MouseEvent event) {
        mouseX = event.getSceneX();
        mouseY = event.getSceneY();
        dragging = true;
    }

    // Метод для продолжения рисования
    private void continueDragging(MouseEvent event) {
        if (dragging) {
            double deltaX = event.getSceneX() - mouseX;
            double deltaY = event.getSceneY() - mouseY;
            scrollPane.setHvalue(scrollPane.getHvalue() - deltaX / scrollPane.getContent().getBoundsInLocal().getWidth());
            scrollPane.setVvalue(scrollPane.getVvalue() - deltaY / scrollPane.getContent().getBoundsInLocal().getHeight());
            mouseX = event.getSceneX();
            mouseY = event.getSceneY();
        }
    }

    // Метод для завершения рисования
    private void stopDragging() {
        dragging = false;
    }

    // Обработчик нажатия мыши на Canvas
    private void canvasEventMousePressed(MouseEvent event) {
        switch (currentMode) {
            case "DRAW":
                startDrawing(event);
                break;
            default:
                break;
        }
    }

    // Обработчик задержания мыши на Canvas
    private void canvasEventMouseDragged(MouseEvent event) {
        switch (currentMode) {
            case "DRAW":
                continueDrawing(event);
                break;
            default:
                break;
        }
    }

    // Обработчик окончания задержания мыши на Canvas
    private void canvasEventMouseReleased(MouseEvent event) {
        switch (currentMode) {
            case "DRAW":
                stopDrawing();
                break;
            default:
                break;
        }
    }

    // Метод для начала рисования
    private void startDrawing(MouseEvent event) {
        drawing = true;
        lastX = event.getX() / zoomLevel;
        lastY = event.getY() / zoomLevel;
    }

    // Метод для продолжения рисования
    private void continueDrawing(MouseEvent event) {
        if (drawing) {
            double currentX = event.getX() / zoomLevel;
            double currentY = event.getY() / zoomLevel;
            gc.strokeLine(lastX, lastY, currentX, currentY);
            lastX = currentX;
            lastY = currentY;
            // Установка флага несохраненных изменений
            unsavedChanges = true;
        }
    }

    // Метод для завершения рисования
    private void stopDrawing() {
        drawing = false;
    }

    // Метод для кнопки переключения на режим перемещения
    @FXML
    private void toggleMode() {
        currentMode = "DRAG";
        logger.info("Switched to Drag Mode");
    }

    // Метод для кнопки переключения на режим рисования
    @FXML
    private void drawMode() {
        currentMode = "DRAW";
        logger.info("Switched to Draw Mode");
    }

    // Метод для кнопки переключения на режим редактирования
    @FXML
    private void cropMode() {
        currentMode = "CROP";
        logger.info("Switched to Crop Mode");
    }

    // Метод для кнопки переключения на режим работы с текстом
    @FXML
    private void textMode() {
        currentMode = "TEXT";
        logger.info("Switched to Text Mode");
    }

    // Метод для кнопки переключения на режим работы со стикерами
    @FXML
    private void stickersMode() {
        currentMode = "STICKERS";
        logger.info("Switched to Stickers Mode");
    }

    // Метод для кнопки переключения на режим работы с фильтрами
    @FXML
    private void filtersMode() {
        currentMode = "FILTERS";
        logger.info("Switched to Filters Mode");
    }

    // Метод для кнопки переключения на режим работы со слоями
    @FXML
    private void layersMode() {
        currentMode = "LAYERS";
        logger.info("Switched to Layers Mode");
    }

    // Метод для кнопки переключения на режим яркости и контраста
    @FXML
    private void brightnessAndContrastMode() {
        currentMode = "BRIGHTNESS_AND_CONTRAST";
        logger.info("Switched to Brightness and Contrast Mode");
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
            // Очистка содержимого canvas
            canvasClear();
            canvas.setWidth(imageView.getImage().getWidth());
            canvas.setHeight(imageView.getImage().getHeight());
            updateZoom();
            logger.info("Image loaded from: " + originalPath);
        } else {
            logger.warn("No image file selected");
            showNotification("Warning", "No image file selected");
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
                    unsavedChanges = false;
                    showNotification("Success", "Image saved successfully to: " + file.getAbsolutePath());
                } else {
                    logger.warn("No image to save");
                    showNotification("Warning", "No image to save");
                }
            } catch (IOException e) {
                logger.error("Error saving image", e);
                showNotification("Error", "An error occurred while saving the image");
            }
        } else {
            logger.warn("No file selected for saving image");
            showNotification("Warning", "No file selected for saving the image");
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
            return "png";
        }
        return name.substring(lastIndexOf + 1);
    }

    // Метод для кнопки получения большей информации
    @FXML
    private void moreInformation() {
        showNotification("More information", "More information...");
    }

    // Метод для кнопки отмены действия
    @FXML
    private void undo() {
        if (!undoStack.isEmpty()) {
            // Сохраняем текущее состояние в redoStack
            redoStack.push(model.clone());
            // Восстанавливаем предыдущее состояние из undoStack
            model = undoStack.pop();
            updateImageView();
            logger.info("Undo action performed");
        } else {
            logger.warn("Undo stack is empty");
        }
    }

    // Метод для кнопки возврата отмены действия
    @FXML
    private void redo() {
        if (!redoStack.isEmpty()) {
            // Сохраняем текущее состояние в undoStack
            undoStack.push(model.clone());
            // Восстанавливаем состояние из redoStack
            model = redoStack.pop();
            updateImageView();
            logger.info("Redo action performed");
        } else {
            logger.warn("Redo stack is empty");
        }
    }

    // Метод для обновления ImageView
    private void updateImageView() {
        imageView.setImage(model.getImage());
        updateScrollPane();
    }

    // Метод для кнопки увеличения масштаба
    @FXML
    private void increaseZoom() {
        if (imageView.getImage() != null) {
            zoomLevel += 0.1;
            if (zoomLevel > 5.0) {
                zoomLevel = 5.0;
                logger.info("ButtonIncreaseZoom - Zoom level is too big");
            }
            else {
                updateZoom();
                logger.info("ButtonIncreaseZoom - Zoom level increased to: " + zoomLevel);
            }
        }
        else {
            logger.info("ButtonIncreaseZoom - Image was not uploaded");
        }
    }

    // Метод для кнопки уменьшения масштаба
    @FXML
    private void decreaseZoom() {
        if (imageView.getImage() != null) {
            zoomLevel -= 0.1;
            if (zoomLevel < 0.1) {
                zoomLevel = 0.1;
                logger.info("ButtonDecreaseZoom - Zoom level is too small");
            }
            else {
                updateZoom();
                logger.info("ButtonDecreaseZoom - Zoom level decreased to: " + zoomLevel);
            }
        }
        else {
            logger.info("ButtonDecreaseZoom - Image was not uploaded");
        }
    }

    // Метод обновления масштабов imageView, canvas и содержимого canvas
    private void updateZoom() {
        // Создание новых масштабов
        double newWidth = imageView.getImage().getWidth() * zoomLevel;
        double newHeight = imageView.getImage().getHeight() * zoomLevel;

        // Установка новых масштабов для imageView
        imageView.setFitWidth(newWidth);
        imageView.setFitHeight(newHeight);

        // Сохранение текущего содержимого canvas с прозрачным фоном
        SnapshotParameters params = new SnapshotParameters();
        params.setFill(javafx.scene.paint.Color.TRANSPARENT);
        WritableImage snapshot = new WritableImage((int) canvas.getWidth(), (int) canvas.getHeight());
        canvas.snapshot(params, snapshot);

        // Очистка canvas
        canvasClear();

        // Установка новых масштабов для gc
        gc.setTransform(new Affine());
        gc.scale(zoomLevel, zoomLevel);

        // Установка новых масштабов для canvas
        canvas.setWidth(newWidth);
        canvas.setHeight(newHeight);

        // Перерисовка содержимого canvas с новым масштабом
        gc.drawImage(snapshot, 0, 0, canvas.getWidth() / zoomLevel, canvas.getHeight() / zoomLevel);

        // Установка новых масштабов для zoomTextField
        zoomTextField.setText(String.format("%.0f%%", zoomLevel * 100));

        // Обновление размеров ScrollPane
        updateScrollPane();
        logger.info("Zoom level updated to: " + zoomLevel);
    }

    // Метод обновления размеров ScrollPane
    private void updateScrollPane() {
        if (scrollPane != null) {
            scrollPane.layout();
            // Получение текущего StackPane из ScrollPane
            StackPane stackPane = (StackPane) scrollPane.getContent();
            // Удаление старых элементов из StackPane
            stackPane.getChildren().clear();
            // Добавление обновленных элементов в StackPane
            stackPane.getChildren().addAll(imageView, canvas);
            // Установка стилей для StackPane
            stackPane.setStyle("-fx-background-color: #181F31;");
            // Установка нового содержимого в ScrollPane
            scrollPane.setContent(stackPane);
            scrollPane.setFitToWidth(true);
            scrollPane.setFitToHeight(true);

            logger.info("ScrollPane layout updated");
        }
    }

    // Метод очистки содержимого canvas
    private void canvasClear() {
        gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
    }

    // Метод для создания и отображения уведомления
    private void showNotification(String title, String message) {
        Notification notification = new Notification(title, message);
        notification.show();
    }
}