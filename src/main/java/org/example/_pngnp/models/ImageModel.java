// Объявление пакета, к которому принадлежит класс
package org.example._pngnp.models;

// Импорт необходимых классов из библиотеки JavaFX для работы с изображениями
import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;

// Импорт классов для работы с файлами
import java.io.FileInputStream;
import java.io.FileNotFoundException;

// Импорт классов для логирования
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

// Объявление класса ImageModel
public class ImageModel {

    // Приватное поле для хранения изображения
    private Image image;

    // Логгер для записи логов
    private static final Logger logger = LogManager.getLogger(ImageModel.class);

    // Метод для загрузки изображения из файла
    public void loadImage(String filePath) {
        try {
            // Загрузка изображения из файла
            image = new Image(new FileInputStream(filePath));
            logger.info("Image loaded successfully from: " + filePath);
        } catch (FileNotFoundException e) {
            // Обработка исключения, если файл не найден
            logger.error("File not found: " + filePath, e);
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

    public Image adjustBrightnessAndContrast(Image image, double brightness, double contrast) {
        // Применение яркости и контраста к изображению
        // Здесь можно добавить логику для регулировки яркости и контраста
        return image;
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