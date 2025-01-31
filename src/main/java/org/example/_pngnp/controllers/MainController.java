// Объявление пакета, к которому принадлежит класс
package org.example._pngnp.controllers;

// Импорт необходимых классов из библиотеки JavaFX для работы с графическим интерфейсом

import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.SnapshotParameters;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.image.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.transform.Affine;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example._pngnp.classes.Notification;
import org.example._pngnp.classes.Settings;
import org.example._pngnp.models.ImageModel;

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

    // Модель
    private ImageModel model;

    // Главное окно
    private Stage primaryStage;

    // Текущий уровень масштабирования
    private double zoomLevel = 1.0;

    // Флаг перетаскивания
    private boolean dragging = false;
    private double speed = 1.0;

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

    // Аннотации FXML для связывания с элементами интерфейса
    @FXML
    private HBox leftBox;

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

    @FXML
    public VBox settings_toggle_mode;

    @FXML
    public VBox settings_draw_mode;

    @FXML
    public VBox settings_crop_mode;

    @FXML
    public VBox settings_text_mode;

    @FXML
    public VBox settings_stickers_mode;

    @FXML
    public VBox settings_filters_mode;

    @FXML
    public VBox settings_layers_mode;

    @FXML
    public VBox settings_brightness_and_contrast_mode;

    @FXML
    private Slider speedSlider;

    @FXML
    private ColorPicker colorPicker;

    @FXML
    private Slider lineWidthSlider;

    @FXML
    private TextField cropX;

    @FXML
    private TextField cropY;

    @FXML
    private TextField cropWidth;

    @FXML
    private TextField cropHeight;

    @FXML
    private TextField stickerX;

    @FXML
    private TextField stickerY;

    @FXML
    private TextField textX;

    @FXML
    private TextField textY;

    @FXML
    private TextField textInput;

    @FXML
    private ColorPicker textColorPicker;

    @FXML
    private Slider textSizeSlider;

    @FXML
    private ComboBox<String> stickerComboBox;

    @FXML
    private ComboBox<String> filterComboBox;

    @FXML
    private ListView<String> layersList;

    @FXML
    private Slider brightnessSlider;

    @FXML
    private Slider contrastSlider;

    @FXML
    private Label coordinates;

    // Метод установки модели изображения
    public void setModel(ImageModel model) {
        this.model = model;
        logger.info("Image model set");
    }

    // Метод установки основного окна приложения, обработчика закрытия и темы
    public void setPrimaryStage(Stage primaryStage) {
        this.primaryStage = primaryStage;

        // Установка темы
        setTheme();

        // Установка обработчика закрытия окна
        primaryStage.setOnCloseRequest(windowEvent ->
                handleUnsavedChanges(windowEvent, primaryStage::close));
        logger.info("Properties set");
    }

    // Метод для установки темы
    private void setTheme() {
        // Загрузка настроек
        try {
            Settings settings = Settings.loadSettings("settings.json");
            String themePath = settings.getThemePath();
            Scene scene = primaryStage.getScene();
            scene.getStylesheets().clear();
            String cssPath = Objects.requireNonNull(getClass().getResource(themePath)).toExternalForm();
            if (cssPath != null) {
                scene.getStylesheets().add(cssPath);
                logger.info("The theme is fixed");
            } else {
                logger.error("CSS file not found: {}", themePath);
            }
        } catch (IOException e) {
            logger.error("Error occurred during setting theme", e);
        }
    }

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

        // Изначально горит кнопка button_toggle_mode и показан ее контейнер
        button_toggle_mode.getStyleClass().add("button-selected");

        // Установка обработчиков для поля ввода масштаба
        if (zoomTextField != null) {
            zoomTextField.textProperty().addListener((observable, oldValue, newValue) -> {
                try {
                    double newZoomLevel = Double.parseDouble(newValue.replace("%", "")) / 100.0;
                    if (newZoomLevel > 0) {
                        if (imageView.getImage() != null) {
                            // Ограничение минимального и максимального значения масштаба
                            if (newZoomLevel < 1) {
                                newZoomLevel = 1;
                            } else if (newZoomLevel > 5.0) {
                                newZoomLevel = 5.0;
                            }
                            zoomLevel = newZoomLevel;
                            updateZoom();
                            logger.info("ZoomTextField - Zoom level changed to: {}", zoomLevel);
                        } else {
                            logger.info("ZoomTextField - Image was not uploaded");
                        }
                    }
                } catch (NumberFormatException e) {
                    logger.warn("ZoomTextField - Invalid zoom level input: {}", newValue);
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
        gc.setStroke(Color.WHITE);
        gc.setLineWidth(2);

        // Инициализация обработчика изменения значения слайдера
        speedSlider.valueProperty().addListener((observable, oldValue, newValue) -> speed = newValue.doubleValue());

        // Установка обработчиков событий для настроек
        colorPicker.setOnAction(event -> gc.setStroke(colorPicker.getValue()));
        lineWidthSlider.valueProperty().addListener((observable, oldValue, newValue) -> gc.setLineWidth(newValue.doubleValue()));

        // Установка обработчиков событий мыши для рисования
        canvas.addEventHandler(MouseEvent.MOUSE_PRESSED, this::canvasEventMousePressed);
        canvas.addEventHandler(MouseEvent.MOUSE_DRAGGED, this::canvasEventMouseDragged);
        canvas.addEventHandler(MouseEvent.MOUSE_RELEASED, this::canvasEventMouseReleased);

        // Установка обработчика мыши для обновления координат
        canvas.addEventHandler(MouseEvent.MOUSE_MOVED, this::canvasEventMouseMoved);

        // Инициализация других компонентов и обработчиков событий
        textColorPicker.setOnAction(event -> gc.setFill(textColorPicker.getValue()));
        textSizeSlider.valueProperty().addListener((observable, oldValue, newValue) -> gc.setFont(new Font(newValue.doubleValue())));

        // Добавление стикеров в ComboBox
        stickerComboBox.getItems().addAll("raiden", "sonic", "vergil");

        // Добавление фильтров в ComboBox
        filterComboBox.getItems().addAll("Grayscale", "Sepia", "Invert", "Blur", "Noise", "Pixelate", "Posterize");

        // Инициализация списка слоев
        layersList.getItems().addAll("Layer 1", "Layer 2", "Layer 3");
    }

    // Обработчик диалогов
    private void handleUnsavedChanges(Event event, Runnable onNoUnsavedChanges) {
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
                        // Выполнение действия после сохранения
                        onNoUnsavedChanges.run();
                    } else {
                        // Отмена закрытия окна
                        event.consume();
                    }
                } else if (result.get() == dontSaveButton) {
                    logger.info("User chose not to save the changes");
                    // Выполнение действия без сохранения
                    onNoUnsavedChanges.run();
                } else {
                    logger.info("User chose to cancel");
                    // Отмена закрытия окна
                    event.consume();
                }
            }
        } else {
            // Выполнение действия, если нет несохраненных изменений
            onNoUnsavedChanges.run();
        }
    }

    // Метод установки иконки для кнопки
    private void setButtonImage(Button button, String imagePath) {
        Image image = new Image(Objects.requireNonNull(getClass().getResourceAsStream(imagePath)));
        ImageView imageView = new ImageView(image);
        imageView.setFitWidth(30);
        imageView.setFitHeight(30);
        button.setGraphic(imageView);
        logger.info("Button image set for: {}", button.getId());
    }

    // Обработчик нажатия мыши на ScrollPane
    private void scrollPaneEventMousePressed(MouseEvent event) {
        if (currentMode.equals("DRAG")) {
            startDragging(event);
        }
    }

    // Обработчик задержания мыши на ScrollPane
    private void scrollPaneEventMouseDragged(MouseEvent event) {
        if (currentMode.equals("DRAG")) {
            continueDragging(event);
        }
    }

    // Обработчик окончания задержания мыши на ScrollPane
    private void scrollPaneEventMouseReleased(MouseEvent event) {
        if (currentMode.equals("DRAG")) {
            stopDragging();
        }
    }

    // Метод для начала перемещения
    private void startDragging(MouseEvent event) {
        mouseX = event.getSceneX();
        mouseY = event.getSceneY();
        dragging = true;
    }

    // Метод для продолжения перемещения
    private void continueDragging(MouseEvent event) {
        if (dragging) {
            double deltaX = (event.getSceneX() - mouseX) * speed;
            double deltaY = (event.getSceneY() - mouseY) * speed;
            scrollPane.setHvalue(scrollPane.getHvalue() - deltaX /
                    scrollPane.getContent().getBoundsInLocal().getWidth());
            scrollPane.setVvalue(scrollPane.getVvalue() - deltaY /
                    scrollPane.getContent().getBoundsInLocal().getHeight());
            mouseX = event.getSceneX();
            mouseY = event.getSceneY();
        }
    }

    // Метод для завершения перемещения
    private void stopDragging() {
        dragging = false;
    }

    // Обработчик нажатия мыши на Canvas
    private void canvasEventMousePressed(MouseEvent event) {
        if (currentMode.equals("DRAW")) {
            startDrawing(event);
        } else {
            defaultPressed(event);
        }
    }

    // Метод для начала рисования
    private void startDrawing(MouseEvent event) {
        drawing = true;
        defaultPressed(event);
    }

    // Метод получения начальных координат
    private void defaultPressed(MouseEvent event) {
        lastX = event.getX() / zoomLevel;
        lastY = event.getY() / zoomLevel;
    }

    // Обработчик задержания мыши на Canvas
    private void canvasEventMouseDragged(MouseEvent event) {
        if (currentMode.equals("DRAW")) {
            continueDrawing(event);
        }
    }

    // Метод для продолжения рисования
    private void continueDrawing(MouseEvent event) {
        if (drawing) {
            double currentX = event.getX() / zoomLevel;
            double currentY = event.getY() / zoomLevel;
            gc.strokeLine(lastX, lastY, currentX, currentY);
            // Установка флага несохраненных изменений
            unsavedChanges = true;
            lastX = currentX;
            lastY = currentY;
            // Обновление координат
            coordinates.setText(lastX + ", " + lastY);
        }
    }

    // Обработчик окончания задержания мыши на Canvas
    private void canvasEventMouseReleased(MouseEvent event) {
        if (currentMode.equals("DRAW")) {
            stopDrawing();
        }
    }

    // Метод для завершения рисования
    private void stopDrawing() {
        drawing = false;
    }

    // Метод для обновления координат
    private void canvasEventMouseMoved(MouseEvent event) {
        double currentX = event.getX() / zoomLevel;
        double currentY = event.getY() / zoomLevel;
        lastX = currentX;
        lastY = currentY;
        coordinates.setText(lastX + ", " + lastY);
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
            leftBox.setVisible(true);
            // Очистка содержимого canvas
            canvasClear();
            canvas.setWidth(imageView.getImage().getWidth());
            canvas.setHeight(imageView.getImage().getHeight());
            updateZoom();
            logger.info("Image loaded from: {}", originalPath);
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
                    logger.info("Image saved to: {}", file.getAbsolutePath());
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

    // Метод для кнопки перехода на приветствующий экран
    @FXML
    private void goBack(ActionEvent event) {
        handleUnsavedChanges(event, this::goHello);
    }

    private void goHello() {
        try {
            // Загрузка FXML файла для создания графического интерфейса
            FXMLLoader loader = new FXMLLoader(getClass().
                    getResource("/org/example/_pngnp/views/hello-view.fxml"));
            Parent root = loader.load();
            logger.info("Hello FXML file loaded successfully");

            // Создание сцены с загруженным интерфейсом
            Scene scene = new Scene(root, 1200, 800);
            logger.info("Scene created");

            // Настройка и отображение окна
            primaryStage.setTitle("PNGNP");
            primaryStage.setScene(scene);
            primaryStage.setMinWidth(1200);
            primaryStage.setMinHeight(800);

            // Установка максимального размера окна
            primaryStage.setMaximized(true);

            // Применение настроек темы
            HelloController controller = loader.getController();
            controller.setProperties(primaryStage);

            primaryStage.show();
            logger.info("Hello scene displayed");
        } catch (Exception e) {
            logger.error("Error occurred during application startup", e);
        }
    }

    // Метод для переключения кнопок и контейнеров
    private void selectButton(Button button, VBox settings) {
        // Обновление изображения
        imageView.setImage(model.getImage());
        // Сбросить стиль всех кнопок
        button_toggle_mode.getStyleClass().remove("button-selected");
        button_draw_mode.getStyleClass().remove("button-selected");
        button_crop_mode.getStyleClass().remove("button-selected");
        button_text_mode.getStyleClass().remove("button-selected");
        button_stickers_mode.getStyleClass().remove("button-selected");
        button_filters_mode.getStyleClass().remove("button-selected");
        button_layers_mode.getStyleClass().remove("button-selected");
        button_brightness_and_contrast_mode.getStyleClass().remove("button-selected");

        // Скрыть все VBox
        settings_toggle_mode.setVisible(false);
        settings_draw_mode.setVisible(false);
        settings_crop_mode.setVisible(false);
        settings_text_mode.setVisible(false);
        settings_stickers_mode.setVisible(false);
        settings_filters_mode.setVisible(false);
        settings_layers_mode.setVisible(false);
        settings_brightness_and_contrast_mode.setVisible(false);

        // Установить стиль для выбранной кнопки и показать соответствующий VBox
        button.getStyleClass().add("button-selected");
        settings.setVisible(true);
    }

    // Метод для кнопки переключения на режим перемещения
    @FXML
    private void toggleMode() {
        currentMode = "DRAG";
        logger.info("Switched to Drag Mode");

        // Отображение настроек мода
        selectButton(button_toggle_mode, settings_toggle_mode);
    }

    // Метод для кнопки переключения на режим рисования
    @FXML
    private void drawMode() {
        currentMode = "DRAW";
        logger.info("Switched to Draw Mode");

        // Отображение настроек мода
        selectButton(button_draw_mode, settings_draw_mode);
    }

    // Метод для кнопки переключения на режим редактирования
    @FXML
    private void cropMode() {
        currentMode = "CROP";
        logger.info("Switched to Crop Mode");

        // Очистка значений
        cropX.setText("");
        cropY.setText("");
        cropWidth.setText("");
        cropHeight.setText("");

        // Отображение настроек мода
        selectButton(button_crop_mode, settings_crop_mode);
    }

    // Метод для кнопки применения обрезания
    @FXML
    private void applyCrop() {
        // Проверка на заполненные поля
        if (cropX.getText().isEmpty() || cropY.getText().isEmpty() ||
                cropWidth.getText().isEmpty() || cropHeight.getText().isEmpty()) {
            // Вывод уведомления об ошибке, если поля не заполнены
            showNotification("Error", "All fields must be filled");
            return;
        }

        // Получение параметров для обрезания
        double x, y, width, height;
        try {
            x = Double.parseDouble(cropX.getText());
            y = Double.parseDouble(cropY.getText());
            width = Double.parseDouble(cropWidth.getText());
            height = Double.parseDouble(cropHeight.getText());
        } catch (NumberFormatException e) {
            // Вывод уведомления об ошибке, если введены некорректные значения
            showNotification("Error", "Invalid input. Please enter numeric values.");
            return;
        }

        // Проверка параметров для обрезания
        if (x < 0 || y < 0 || width <= 0 || height <= 0 ||
                x + width > (int) imageView.getImage().getWidth() ||
                y + height > (int) imageView.getImage().getHeight()) {
            // Вывод уведомления об ошибке в параметрах обрезания
            showNotification("Error", "Crop zone does not have to go beyond the area of the image");
        } else {
            // Применение обрезания
            model.applyCrop((int) x, (int) y, (int) width, (int) height);
            // Установка изображения и обновление масштабов
            imageView.setImage(model.getImage());
            updateZoom();
        }
    }

    // Метод для кнопки переключения на режим работы с текстом
    @FXML
    private void textMode() {
        currentMode = "TEXT";
        logger.info("Switched to Text Mode");

        // Очистка значений
        stickerComboBox.setValue("");
        textX.setText("");
        textY.setText("");
        textInput.setText("");

        // Отображение настроек мода
        selectButton(button_text_mode, settings_text_mode);
    }

    // Метод для кнопки добавления текста
    @FXML
    private void addText() {
        // Проверка на заполненные поля
        if (textX.getText().isEmpty() || textY.getText().isEmpty() ||
                textInput.getText().isEmpty()) {
            // Вывод уведомления об ошибке, если поля не заполнены
            showNotification("Error", "All fields must be filled");
            return;
        }

        // Получение параметров для вставки текста
        double x, y;
        try {
            x = Double.parseDouble(textX.getText());
            y = Double.parseDouble(textY.getText());
        } catch (NumberFormatException e) {
            // Вывод уведомления об ошибке, если введены некорректные значения
            showNotification("Error", "Invalid input. Please enter numeric values.");
            return;
        }

        // Проверка параметров для вставки текста
        if (x < 0 || y < 0 ||
                x > (int) imageView.getImage().getWidth() ||
                y > (int) imageView.getImage().getHeight()) {
            // Вывод уведомления об ошибке в параметрах обрезания
            showNotification("Error", "Entry point does not have to go beyond the area of the image");
        } else {
            // Получение других параметров
            String text = textInput.getText();
            Color color = textColorPicker.getValue();
            double size = textSizeSlider.getValue();

            // Вставка текста
            gc.setFill(color);
            gc.setFont(new Font(size));
            gc.fillText(text, x, y);
            logger.info("Text has been added");
        }
    }

    // Метод для кнопки переключения на режим работы со стикерами
    @FXML
    private void stickersMode() {
        currentMode = "STICKERS";
        logger.info("Switched to Stickers Mode");

        // Очистка значений
        stickerX.setText("");
        stickerY.setText("");

        // Отображение настроек мода
        selectButton(button_stickers_mode, settings_stickers_mode);
    }

    // Метод для кнопки добавления стикера
    @FXML
    private void add_stickers() {
        // Проверка на заполненные поля
        if (stickerX.getText().isEmpty() || stickerY.getText().isEmpty() ||
                stickerComboBox.getValue() == null) {
            // Вывод уведомления об ошибке, если поля не заполнены
            showNotification("Error", "All fields must be filled");
            return;
        }

        // Получение параметров для добавления стикеров
        double x, y;
        try {
            x = Double.parseDouble(stickerX.getText());
            y = Double.parseDouble(stickerY.getText());
        } catch (NumberFormatException e) {
            // Вывод уведомления об ошибке, если введены некорректные значения
            showNotification("Error", "Invalid input. Please enter numeric values.");
            return;
        }

        // Проверка параметров для добавления стикеров
        if (x < 0 || y < 0 ||
                x > (int) imageView.getImage().getWidth() ||
                y > (int) imageView.getImage().getHeight()) {
            // Вывод уведомления об ошибке в параметрах обрезания
            showNotification("Error", "Entry point does not have to go beyond the area of the image");
        } else {
            // Получение стикера
            String selectedSticker = stickerComboBox.getValue();
            Image stickerImage = new Image(Objects.requireNonNull(getClass().
                    getResourceAsStream("/org/example/_pngnp/stickers/" + selectedSticker + ".png")));

            // Вставка стикера
            gc.drawImage(stickerImage, x, y);
            logger.info("Sticker has been added");
        }
    }


    // Метод для кнопки переключения на режим работы с фильтрами
    @FXML
    private void filtersMode() {
        currentMode = "FILTERS";
        logger.info("Switched to Filters Mode");

        // Очистка значений
        filterComboBox.setValue("");

        // Отображение настроек мода
        selectButton(button_filters_mode, settings_filters_mode);
    }

    // Метод применения фильтра к изображению
    @FXML
    private void setFilter() {
        // Проверка на заполненные поля
        if (filterComboBox.getValue() == null) {
            // Вывод уведомления об ошибке, если поля не заполнены
            showNotification("Error", "All fields must be filled");
            return;
        }

        // Применение фильтра
        String selectedFilter = filterComboBox.getValue();
        Image filteredImage = model.setFilter(imageView.getImage(), selectedFilter);

        // Обновление изображения
        imageView.setImage(filteredImage);
    }

    // Метод для кнопки удаления фильтра
    @FXML
    private void deleteFilter() {
        filterComboBox.setValue("");
        // Обновление изображения
        imageView.setImage(model.getImage());
    }

    // Метод для установки фильтра
    @FXML
    private void applyFilter() {
        filterComboBox.setValue("");
        // Установка изображения с фильтром
        model.setImage(imageView.getImage());
    }

    // Метод для кнопки переключения на режим работы со слоями
    @FXML
    private void layersMode() {
        currentMode = "LAYERS";
        logger.info("Switched to Layers Mode");

        // Отображение настроек мода
        selectButton(button_layers_mode, settings_layers_mode);
    }

    @FXML
    private void addLayer() {
        // Логика добавления нового слоя
    }

    @FXML
    private void removeLayer() {
        // Логика удаления выбранного слоя
    }

    // Метод для кнопки переключения на режим яркости и контраста
    @FXML
    private void brightnessAndContrastMode() {
        currentMode = "BRIGHTNESS_AND_CONTRAST";
        logger.info("Switched to Brightness and Contrast Mode");

        // Отображение настроек мода
        selectButton(button_brightness_and_contrast_mode, settings_brightness_and_contrast_mode);
    }

    @FXML
    private void applyBrightnessAndContrast() {
        double brightness = brightnessSlider.getValue();
        double contrast = contrastSlider.getValue();

        // Применение параметров
        Image adjustedImage = model.adjustBrightnessAndContrast(brightness, contrast);

        // Обновление изображения
        imageView.setImage(adjustedImage);
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
            } else {
                updateZoom();
                logger.info("ButtonIncreaseZoom - Zoom level increased to: {}", zoomLevel);
            }
        } else {
            logger.info("ButtonIncreaseZoom - Image was not uploaded");
        }
    }

    // Метод для кнопки уменьшения масштаба
    @FXML
    private void decreaseZoom() {
        if (imageView.getImage() != null) {
            zoomLevel -= 0.1;
            if (zoomLevel < 1) {
                zoomLevel = 1;
                logger.info("ButtonDecreaseZoom - Zoom level is too small");
            } else {
                updateZoom();
                logger.info("ButtonDecreaseZoom - Zoom level decreased to: {}", zoomLevel);
            }
        } else {
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
        logger.info("Zoom level updated to: {}", zoomLevel);
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
            stackPane.getStyleClass().add("stack-pane");
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