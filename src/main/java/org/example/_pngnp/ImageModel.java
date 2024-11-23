package org.example._pngnp;

import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;

public class ImageModel {
    private Image image;

    public void loadImage(String filePath) {
        image = new Image(filePath);
    }

    public Image getImage() {
        return image;
    }

    public void applyGrayscaleFilter() {
        image = applyFilter((x, y, color) -> {
            double gray = 0.299 * color.getRed() + 0.587 * color.getGreen() + 0.114 * color.getBlue();
            return Color.gray(gray);
        });
    }

    public void applyMedianFilter(int size) {
        image = applyFilter((x, y, color) -> {
            // Implement median filter logic
            return color;
        });
    }

    public void applyThresholdFilter(double threshold) {
        image = applyFilter((x, y, color) -> {
            double gray = 0.299 * color.getRed() + 0.587 * color.getGreen() + 0.114 * color.getBlue();
            return gray > threshold ? Color.WHITE : Color.BLACK;
        });
    }

    public void applySobelFilter() {
        image = applyFilter((x, y, color) -> {
            // Implement Sobel filter logic
            return color;
        });
    }

    private Image applyFilter(FilterFunction filterFunction) {
        int width = (int) image.getWidth();
        int height = (int) image.getHeight();
        WritableImage newImage = new WritableImage(width, height);
        PixelReader reader = image.getPixelReader();
        PixelWriter writer = newImage.getPixelWriter();

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                Color color = reader.getColor(x, y);
                Color newColor = filterFunction.apply(x, y, color);
                writer.setColor(x, y, newColor);
            }
        }
        return newImage;
    }

    @FunctionalInterface
    interface FilterFunction {
        Color apply(int x, int y, Color color);
    }
}