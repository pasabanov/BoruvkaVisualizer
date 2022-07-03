package com.leti.summer_practice.gui.prog;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.text.Text;

import java.util.ArrayList;

public class GraphCanvas extends Pane {

    private final DoubleProperty
            cameraX = new SimpleDoubleProperty(0),
            cameraY = new SimpleDoubleProperty(0);
    private final DoubleProperty
            zoom = new SimpleDoubleProperty(1);

    private ArrayList<Circle> vertices = new ArrayList<>();
    private ArrayList<Line> edges = new ArrayList<>();

    private boolean redrawLock = false;

    public GraphCanvas() {
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
        getChildren().clear();
    }

    public void draw() {

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
}