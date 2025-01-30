// Объявление пакета, к которому принадлежит класс
package org.example._pngnp.models;

// Импорт необходимых классов из библиотеки JavaFX для работы с изображениями
import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.awt.*;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

// Объявление класса ImageModel
public class ImageModel {

    // Приватное поле для хранения изображения
    private Image image;

    // Приватное поле для хранения canvas
    private Canvas canvas;

    // Логгер для записи логов
    private static final Logger logger = LogManager.getLogger(ImageModel.class);

    // Метод для загрузки изображения из файла
    public void loadImage(String filePath) {
        try {
            // Загрузка изображения из файла
            image = new Image(new FileInputStream(filePath));
            logger.info("Image loaded successfully from: {}", filePath);
        } catch (FileNotFoundException e) {
            // Обработка исключения, если файл не найден
            logger.error("File not found: {}", filePath, e);
        }
    }

    // Метод для получения текущего изображения
    public Image getImage() {
        logger.info("Getting current image");
        return image;
    }

    // Метод для применения фильтра к изображению
    public Image applyFilter(Image image, String selectedFilter) {
        logger.info("Applying filter to image");
        // Применение фильтра к изображению
        this.image = applyFilter((x, y, color) -> {
            // Логика фильтра

            return color;
        });
        return image;
    }

    // Метод применения яркости и контраста к изображению
    public Image adjustBrightnessAndContrast(Image image, double brightness, double contrast) {
        int width = (int) image.getWidth();
        int height = (int) image.getHeight();
        WritableImage adjustedImage = new WritableImage(width, height);
        PixelReader pixelReader = image.getPixelReader();
        PixelWriter pixelWriter = adjustedImage.getPixelWriter();

        double factor = (259 * (contrast + 255)) / (255 * (259 - contrast));

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int argb = pixelReader.getArgb(x, y);
                int newArgb = getNewArgb(brightness, argb, factor);
                pixelWriter.setArgb(x, y, newArgb);
            }
        }

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

    // Приватный метод для применения фильтра к изображению
    private Image applyFilter(FilterFunction filterFunction) {
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

    @Override
    public ImageModel clone() {
        try {
            ImageModel clone = (ImageModel) super.clone();
            // Клонирование изображения
            clone.image = new Image(image.getUrl());
            logger.info("ImageModel cloned successfully");
            return clone;
        } catch (CloneNotSupportedException e) {
            logger.error("Error cloning ImageModel", e);
            throw new AssertionError();
        }
    }
}