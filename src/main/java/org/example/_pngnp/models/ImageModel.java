// Объявление пакета, к которому принадлежит класс
package org.example._pngnp.models;

// Импорт необходимых классов из библиотеки JavaFX для работы с изображениями

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.SnapshotParameters;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example._pngnp.classes.Layer;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

// Объявление класса ImageModel
public class ImageModel implements Cloneable {

    // Приватное поле для хранения изображения
    private Image imageModel;

    // Приватное поле для хранения изменений
    private ObservableList<Layer> layersModel;
    private List<Canvas> layerCanvasesModel;

    // Логгер для записи логов
    private static final Logger logger = LogManager.getLogger(ImageModel.class);

    // Метод для загрузки изображения из файла
    public void loadImage(String filePath) {
        try {
            // Загрузка изображения из файла
            imageModel = new Image(new FileInputStream(filePath));
            logger.info("Image loaded successfully from: {}", filePath);
        } catch (FileNotFoundException e) {
            // Обработка исключения, если файл не найден
            logger.error("File not found: {}", filePath, e);
        }
    }

    // Метод для получения текущего изображения
    public Image getImageModel() {
        logger.info("Getting current image");
        return imageModel;
    }

    // Метод для установки текущего изображения
    public void setImageModel(Image imageModel) {
        logger.info("Setting current image");
        this.imageModel = imageModel;
    }

    // Метод для получения изменений
    public ObservableList<Layer> getLayersModel() {
        logger.info("Getting current layers");
        return layersModel;
    }

    // Метод для установки изменений
    public void setLayersModel(ObservableList<Layer> layersModel) {
        logger.info("Setting current layers");
        this.layersModel = layersModel;
    }

    // Метод для получения изменений Canvas
    public List<Canvas> getLayerCanvasesModel() {
        logger.info("Getting current layerCanvases");
        return layerCanvasesModel;
    }

    // Метод для установки изменений Canvas
    public void setLayerCanvasesModel(List<Canvas> layerCanvasesModel) {
        logger.info("Setting current layerCanvases");
        this.layerCanvasesModel = layerCanvasesModel;
    }

    // Применение обрезки к изображению
    public void applyCrop(int x, int y, int width, int height) {
        WritableImage croppedImage = new WritableImage(width, height);
        PixelReader pixelReader = imageModel.getPixelReader();
        PixelWriter pixelWriter = croppedImage.getPixelWriter();

        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                pixelWriter.setArgb(i, j, pixelReader.getArgb(x + i, y + j));
            }
        }

        // Установка нового изображения в модели
        logger.info("Image has been cropped");
        imageModel = croppedImage;
    }

    // Метод для применения фильтра к изображению
    public Image setFilter(Image currentImage, String selectedFilter) {
        logger.info("Applying filter to image");
        // Применение фильтра к изображению
        switch (selectedFilter) {
            case "Grayscale":
                currentImage = applyFilter(currentImage, (int x, int y, Color color) -> {
                    double gray = 0.299 * color.getRed() + 0.587 * color.getGreen() + 0.114 * color.getBlue();
                    return Color.gray(gray);
                });
                break;
            case "Sepia":
                currentImage = applyFilter(currentImage, (int x, int y, Color color) -> {
                    double r = 0.393 * color.getRed() + 0.769 * color.getGreen() + 0.189 * color.getBlue();
                    double g = 0.349 * color.getRed() + 0.686 * color.getGreen() + 0.168 * color.getBlue();
                    double b = 0.272 * color.getRed() + 0.534 * color.getGreen() + 0.131 * color.getBlue();
                    return Color.color(Math.min(1, r), Math.min(1, g), Math.min(1, b));
                });
                break;
            case "Invert":
                currentImage = applyFilter(currentImage, (int x, int y, Color color) -> Color.color(1 - color.getRed(), 1 - color.getGreen(), 1 - color.getBlue()));
                break;
            case "Blur":
                Image finalCurrentImage = currentImage;
                currentImage = applyFilter(currentImage, (int x, int y, Color color) -> {
                    int kernelSize = 3;
                    double r = 0, g = 0, b = 0;
                    int count = 0;
                    for (int ky = -kernelSize / 2; ky <= kernelSize / 2; ky++) {
                        for (int kx = -kernelSize / 2; kx <= kernelSize / 2; kx++) {
                            int nx = x + kx;
                            int ny = y + ky;
                            if (nx >= 0 && nx < finalCurrentImage.getWidth() && ny >= 0 && ny < finalCurrentImage.getHeight()) {
                                Color neighborColor = finalCurrentImage.getPixelReader().getColor(nx, ny);
                                r += neighborColor.getRed();
                                g += neighborColor.getGreen();
                                b += neighborColor.getBlue();
                                count++;
                            }
                        }
                    }
                    return Color.color(r / count, g / count, b / count);
                });
                break;
            case "Noise":
                currentImage = applyFilter(currentImage, (int x, int y, Color color) -> {
                    double noiseFactor = 0.2; // Пример значения шума
                    double r = color.getRed() + (Math.random() - 0.5) * noiseFactor;
                    double g = color.getGreen() + (Math.random() - 0.5) * noiseFactor;
                    double b = color.getBlue() + (Math.random() - 0.5) * noiseFactor;
                    return Color.color(Math.min(1, Math.max(0, r)), Math.min(1, Math.max(0, g)), Math.min(1, Math.max(0, b)));
                });
                break;
            case "Pixelate":
                Image finalCurrentImage4 = currentImage;
                currentImage = applyFilter(currentImage, (int x, int y, Color color) -> {
                    int pixelSize = 10; // Пример размера пикселя
                    int nx = (x / pixelSize) * pixelSize;
                    int ny = (y / pixelSize) * pixelSize;
                    return finalCurrentImage4.getPixelReader().getColor(nx, ny);
                });
                break;
            case "Posterize":
                currentImage = applyFilter(currentImage, (int x, int y, Color color) -> {
                    int levels = 4; // Пример количества уровней
                    double r = (double) Math.round(color.getRed() * levels) / levels;
                    double g = (double) Math.round(color.getGreen() * levels) / levels;
                    double b = (double) Math.round(color.getBlue() * levels) / levels;
                    return Color.color(r, g, b);
                });
                break;
            default:
                logger.warn("Unknown filter: {}", selectedFilter);
                break;
        }
        return currentImage;
    }

    // Приватный метод для применения фильтра к изображению
    private Image applyFilter(Image image, FilterFunction filterFunction) {
        // Получение ширины и высоты изображения
        int width = (int) image.getWidth();
        int height = (int) image.getHeight();

        // Создание нового изображения для записи измененных пикселей
        WritableImage newImage = new WritableImage(width, height);

        // Получение объектов для чтения и записи пикселей
        PixelReader reader = image.getPixelReader();
        PixelWriter writer = newImage.getPixelWriter();

        // Проход по всем пикселям изображения
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                // Чтение цвета текущего пикселя
                Color color = reader.getColor(x, y);

                // Применение фильтра к цвету пикселя
                Color newColor = filterFunction.apply(x, y, color);

                // Запись нового цвета пикселя в новое изображение
                writer.setColor(x, y, newColor);
            }
        }

        // Возврат нового изображения с примененным фильтром
        logger.info("Filter applied successfully");
        return newImage;
    }

    // Объявление функционального интерфейса для фильтров
    @FunctionalInterface
    interface FilterFunction {
        // Метод для применения фильтра к цвету пикселя
        Color apply(int x, int y, Color color);
    }

    // Метод применения яркости и контраста к изображению
    public Image adjustBrightnessAndContrast(double brightness, double contrast) {
        int width = (int) imageModel.getWidth();
        int height = (int) imageModel.getHeight();
        WritableImage adjustedImage = new WritableImage(width, height);
        PixelReader pixelReader = imageModel.getPixelReader();
        PixelWriter pixelWriter = adjustedImage.getPixelWriter();

        double factor = (259 * (contrast + 255)) / (255 * (259 - contrast));

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int argb = pixelReader.getArgb(x, y);
                int newArgb = getNewArgb(brightness, argb, factor);
                pixelWriter.setArgb(x, y, newArgb);
            }
        }

        // Возврат нового изображения с примененными яркостью и контрастом
        logger.info("Brightness And Contrast applied successfully");
        return adjustedImage;
    }

    private static int getNewArgb(double brightness, int argb, double factor) {
        int a = (argb >> 24) & 0xff;
        int r = (argb >> 16) & 0xff;
        int g = (argb >> 8) & 0xff;
        int b = argb & 0xff;

        r = (int) (factor * (r - 128) + 128 + brightness);
        g = (int) (factor * (g - 128) + 128 + brightness);
        b = (int) (factor * (b - 128) + 128 + brightness);

        r = Math.max(0, Math.min(255, r));
        g = Math.max(0, Math.min(255, g));
        b = Math.max(0, Math.min(255, b));

        return (a << 24) | (r << 16) | (g << 8) | b;
    }

    @Override
    public ImageModel clone() {
        try {
            ImageModel clone = (ImageModel) super.clone();

            // Клонирование изображения
            clone.imageModel = new WritableImage(imageModel.getPixelReader(), (int) imageModel.getWidth(), (int) imageModel.getHeight());

            // Клонирование списка слоев
            clone.layersModel = FXCollections.observableArrayList();
            for (Layer layer : layersModel) {
                clone.layersModel.add(layer.clone());
            }

            // Клонирование списка layerCanvases
            clone.layerCanvasesModel = new ArrayList<>();
            // Сохранение всех canvas с прозрачным фоном
            SnapshotParameters params = new SnapshotParameters();
            params.setFill(Color.TRANSPARENT);
            for (Canvas layerCanvas : layerCanvasesModel) {
                Canvas clonedCanvas = new Canvas();
                GraphicsContext gc = clonedCanvas.getGraphicsContext2D();
                gc.drawImage(layerCanvas.snapshot(params, null), 0, 0);
                clone.layerCanvasesModel.add(clonedCanvas);
            }

            logger.info("ImageModel cloned successfully");
            return clone;
        } catch (CloneNotSupportedException e) {
            logger.error("Error cloning ImageModel", e);
            throw new AssertionError();
        }
    }
}