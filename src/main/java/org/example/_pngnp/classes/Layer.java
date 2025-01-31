package org.example._pngnp.classes;

import javafx.scene.canvas.Canvas;

public class Layer {
    private final String name;
    private boolean visible;
    private final Canvas canvas;

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
}