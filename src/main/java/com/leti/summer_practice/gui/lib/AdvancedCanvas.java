package com.leti.summer_practice.gui.lib;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class AdvancedCanvas extends Canvas {

    private boolean redrawLock = false;

    private final DoubleProperty
            cameraX = new SimpleDoubleProperty(0),
            cameraY = new SimpleDoubleProperty(0);

    private Runnable drawer = null;

    public AdvancedCanvas() {
        // Redraw canvas when size changes.
        widthProperty().addListener(observable -> {
            if (!redrawLock)
                refresh();
        });
        heightProperty().addListener(observable -> {
            if (!redrawLock)
                refresh();
        });
        cameraXProperty().addListener(observable -> {
            if (!redrawLock)
                refresh();
        });
        cameraYProperty().addListener(observable -> {
            if (!redrawLock)
                refresh();
        });
    }

    public void clear() {
        GraphicsContext gc = getGraphicsContext2D();
        gc.clearRect(0, 0, getWidth(), getHeight());
    }

    public void draw() {

        if (drawer != null) {
            drawer.run();
            return;
        }

        // default drawing
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


    /**
     * Getters and setters for camera (cameraX and cameraY).
     */
    public double getCameraX() {
        return cameraX.get();
    }
    public DoubleProperty cameraXProperty() {
        return cameraX;
    }
    public void setCameraX(double cameraX) {
        this.cameraX.set(cameraX);
    }
    public double getCameraY() {
        return cameraY.get();
    }
    public DoubleProperty cameraYProperty() {
        return cameraY;
    }
    public void setCameraY(double cameraY) {
        this.cameraY.set(cameraY);
    }
    public void setCameraXY(double cameraX, double cameraY) {
        if (getCameraX() != cameraX && getCameraY() != cameraY)
            redrawLock = true;
        setCameraX(cameraX);
        redrawLock = false;
        setCameraY(cameraY);
    }


    /**
     * Drawer getter, setter and remover.
     */
    public Runnable getDrawer() {
        return drawer;
    }
    public void setDrawer(Runnable drawer) {
        this.drawer = drawer;
    }
    public void removeDrawer() {
        drawer = null;
    }


    /**
     * This canvas is resizable.
     * @return true
     */
    @Override
    public boolean isResizable() {
        return true;
    }


    /**
     * Pref, min and max width and height.
     *
     * @param height (width) - the height that should be used if preferred width depends on it
     * @return pref, min or max width or height.
     */
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


    /**
     * Resizes ResizableCanvas.
     *
     * It will not redraw canvas if width and height are not changed.
     * It will always redraw canvas maximum one time even if both width and height are changed.
     *
     * @param width - width to resize
     * @param height - height to resize
     */
    @Override
    public void resize(double width, double height) {
        if (getWidth() != width && getHeight() != height)
            redrawLock = true;
        setWidth(width);
        redrawLock = false;
        setHeight(height);
    }
}