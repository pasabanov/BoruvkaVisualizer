package com.leti.summer_practice.gui.lib;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class ResizableCanvas extends Canvas {

    private boolean redrawLock = false;

    public ResizableCanvas() {
        // Redraw canvas when size changes.
        widthProperty().addListener(evt -> {
            if (!redrawLock)
                refresh();
        });
        heightProperty().addListener(evt -> {
            if (!redrawLock)
                refresh();
        });
    }

    public void clear() {
        GraphicsContext gc = getGraphicsContext2D();
        gc.clearRect(0, 0, getWidth(), getHeight());
    }

    public void draw() {
        double width = getWidth();
        double height = getHeight();

        GraphicsContext gc = getGraphicsContext2D();

        gc.setStroke(Color.RED);
        gc.strokeLine(0, 0, width, height);
        gc.strokeLine(0, height, width, 0);
    }

    public void refresh() {
        clear();
        draw();
    }

    @Override
    public boolean isResizable() {
        return true;
    }

    @Override
    public double prefWidth(double height) {
        return getWidth();
    }

    @Override
    public double prefHeight(double width) {
        return getHeight();
    }

    @Override
    public double minWidth(double height) {
        return Double.NEGATIVE_INFINITY;
    }

    @Override
    public double minHeight(double width) {
        return Double.NEGATIVE_INFINITY;
    }

    @Override
    public double maxHeight(double width) {
        return Double.POSITIVE_INFINITY;
    }

    @Override
    public double maxWidth(double height) {
        return Double.POSITIVE_INFINITY;
    }

    @Override
    public void resize(double width, double height) {
        redrawLock = true;
        setWidth(width);
        redrawLock = false;
        setHeight(height);
    }
}