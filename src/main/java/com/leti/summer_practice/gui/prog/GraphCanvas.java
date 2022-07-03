package com.leti.summer_practice.gui.prog;

import com.leti.summer_practice.logic.LogicInterface;
import javafx.beans.InvalidationListener;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.scene.layout.Pane;
import javafx.util.Pair;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public class GraphCanvas extends Pane {
    
    private Consumer<GraphCanvas> drawer = null;

    private final DoubleProperty
            cameraX = new SimpleDoubleProperty(0),
            cameraY = new SimpleDoubleProperty(0);
    private final DoubleProperty
            zoom = new SimpleDoubleProperty(1);

    private Map<String,Pair<Double,Double>> verticesCoordsMap = new HashMap<>();

    private boolean redrawLock = false;


    public GraphCanvas() {
        InvalidationListener listener = observable -> {
            if (!redrawLock)
                redraw();
        };
        widthProperty().addListener(listener);
        heightProperty().addListener(listener);
        cameraXProperty().addListener(listener);
        cameraYProperty().addListener(listener);
        zoomProperty().addListener(listener);
    }


    /**
     * Graphic methods.
     */
    public void clear() {
        getChildren().clear();
    }
    public void draw() {
        if (drawer != null)
            drawer.accept(this);
    }
    public void redraw() {
        clear();
        draw();
    }


    public double getRelativeX(double x) {
        return (getWidth()/2 * (x+1) - cameraX.get()) / zoom.get();
    }

    public double getRelativeY(double y) {
        return (getHeight()/2 * (y+1) - cameraY.get()) / zoom.get();
    }


    public void consumeLogic(LogicInterface logic) {
        ArrayList<LogicInterface.Node_info> node_infos = logic.getVertices();
        double step = 2 * Math.PI / node_infos.size();
        double angle = Math.PI;
        for (int i = 0; i < node_infos.size(); ++i, angle += step)
            verticesCoordsMap.put(node_infos.get(i).name, new Pair<>(0.9*Math.cos(angle), 0.9*Math.sin(angle)));
    }


    /**
     * Getter, setter and remover of the drawer.
     */
    public Consumer<GraphCanvas> getDrawer() {
        return drawer;
    }
    public void setDrawer(Consumer<GraphCanvas> drawer) {
        this.drawer = drawer;
    }
    public void removeDrawer() {
        drawer = null;
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
    /**
     * Redraws canvas maximum 1 time, even if both cameraX and cameraY changed.
     */
    public void setCameraXY(double cameraX, double cameraY) {
        if (getCameraX() != cameraX && getCameraY() != cameraY)
            redrawLock = true;
        setCameraX(cameraX);
        redrawLock = false;
        setCameraY(cameraY);
    }


    /**
     * Getter and setter for zoom.
     */
    public double getZoom() {
        return zoom.get();
    }
    public DoubleProperty zoomProperty() {
        return zoom;
    }
    public void setZoom(double zoom) {
        this.zoom.set(zoom);
    }


    public Map<String, Pair<Double, Double>> getVerticesCoordsMap() {
        return verticesCoordsMap;
    }
    public void setVerticesCoordsMap(Map<String, Pair<Double, Double>> verticesCoordsMap) {
        this.verticesCoordsMap = verticesCoordsMap;
    }


    /**
     * Getter, setter and switcher of the redrawLock;
     */
    public boolean isRedrawLock() {
        return redrawLock;
    }
    public void setRedrawLock(boolean redrawLock) {
        this.redrawLock = redrawLock;
    }
    public void switchRedrawLock() {
        redrawLock = !redrawLock;
    }


    /**
     * Resizes GraphCanvas.
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