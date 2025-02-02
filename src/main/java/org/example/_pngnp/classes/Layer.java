package org.example._pngnp.classes;

import javafx.scene.SnapshotParameters;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class Layer implements Cloneable {
    private final String name;
    private boolean visible;
    private Canvas canvas;

    public Layer(String name, boolean visible, Canvas canvas) {
        this.name = name;
        this.visible = visible;
        this.canvas = canvas;
    }

    public String getName() {
        return name;
    }

    public boolean isVisible() {
        return visible;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    public Canvas getCanvas() {
        return canvas;
    }

    @Override
    public String toString() {
        return name + " (Visible: " + visible + ")";
    }

    @Override
    public Layer clone() {
        try {
            Layer clone = (Layer) super.clone();

            // Клонирование Canvas
            Canvas clonedCanvas = new Canvas(canvas.getWidth(), canvas.getHeight());
            GraphicsContext gc = clonedCanvas.getGraphicsContext2D();
            SnapshotParameters params = new SnapshotParameters();
            params.setFill(Color.TRANSPARENT);
            gc.drawImage(canvas.snapshot(params, null), 0, 0);
            clone.canvas = clonedCanvas;

            return clone;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }
}