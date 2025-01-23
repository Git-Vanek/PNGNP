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

// Объявление класса ImageModel
public class ImageModel {
    // Приватное поле для хранения изображения
    private Image image;

    // Метод для загрузки изображения из файла
    public void loadImage(String filePath) {
        try {
            // Загрузка изображения из файла
            image = new Image(new FileInputStream(filePath));
        } catch (FileNotFoundException e) {
            // Обработка исключения, если файл не найден
            //noinspection CallToPrintStackTrace
            e.printStackTrace();
        }
    }

    // Метод для получения текущего изображения
    public Image getImage() {
        return image;
    }

    // Метод для применения фильтра к изображению
    public void apply1Filter() {
        // Применение фильтра к изображению
        image = applyFilter((x, y, color) -> {
            // Логика фильтра
            return color;
        });
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
        return newImage;
    }

    // Объявление функционального интерфейса для фильтров
    @FunctionalInterface
    interface FilterFunction {
        // Метод для применения фильтра к цвету пикселя
        Color apply(int x, int y, Color color);
    }
}