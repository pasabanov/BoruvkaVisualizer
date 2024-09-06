package com.leti.summer_practice.gui.lib;

import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.beans.property.ReadOnlyDoubleWrapper;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Shape;

import java.util.Collection;
import java.util.function.Consumer;

public class VectorCanvas extends Pane {

    public static class VectorCanvasContent extends Pane {

        private final ReadOnlyDoubleWrapper scale;

        public VectorCanvasContent() {
            this(DEFAULT_SCALE);
        }

        public VectorCanvasContent(double scale) {

            this.scale = new ReadOnlyDoubleWrapper(scale);

            scaleXProperty().bind(this.scale);
            scaleYProperty().bind(this.scale);
        }

        public double getScale() {
            return scale.get();
        }
        public ReadOnlyDoubleWrapper scaleProperty() {
            return scale;
        }
        public void setScale(double scale) {
            this.scale.set(scale);
        }

        @Override
        public boolean isResizable() {
            return false;
        }
    }

    public static final double DEFAULT_SCALE_SPEED = 1.1;
    public static final double MIN_SCALE = 0.001;
    public static final double MAX_SCALE = 1000;
    public static final double DEFAULT_SCALE = 1;

    public static final boolean DEFAULT_LOCK_CAMERA = false;
    public static final boolean DEFAULT_LOCK_SCALE = true;

    public static final boolean DEFAULT_SCALE_ON_RESIZE = true;

    public static final Consumer<VectorCanvas> DEFAULT_DRAWER = canvas -> {
        Line[] lines = {
                new Line(0, 0, canvas.getWidth(), 0),
                new Line(canvas.getWidth(), 0, canvas.getWidth(), canvas.getHeight()),
                new Line(canvas.getWidth(), canvas.getHeight(), 0, canvas.getHeight()),
                new Line(0, canvas.getHeight(), 0, 0),
                new Line(0, 0, canvas.getWidth(), canvas.getHeight()),
                new Line(0, canvas.getHeight(), canvas.getWidth(), 0)
        };
        for (Line line : lines) {
            line.setStrokeWidth(30);
            line.setStroke(Color.RED);
            line.setFill(Color.RED);
        }
        canvas.drawAll(lines);
    };

    public final EventHandler<MouseEvent> ON_MOUSE_PRESSED_EVENT_HANDLER = event -> {

        if (event.getButton() != MouseButton.PRIMARY)
            return;
        mouseX = event.getX();
        mouseY = event.getY();

        event.consume();
    };

    public final EventHandler<MouseEvent> ON_MOUSE_DRAGGED_EVENT_HANDLER = new EventHandler<>() {
        @Override
        public void handle(MouseEvent event) {

            if (event.getButton() != MouseButton.PRIMARY)
                return;

            if (lockCamera)
                return;

            content.setTranslateX(content.getTranslateX() + event.getX() - mouseX);
            content.setTranslateY(content.getTranslateY() + event.getY() - mouseY);
            mouseX = event.getX();
            mouseY = event.getY();

            event.consume();
        }
    };

    public final EventHandler<ScrollEvent> ON_SCROLL_EVENT_HANDLER = new EventHandler<>() {
        @Override
        public void handle(ScrollEvent event) {

            if (lockScale)
                return;

            if (event.getDeltaY() == 0)
                return;

            double oldScale = content.getScale();
            double newScale = (event.getDeltaY() > 0) ? (oldScale * scaleSpeed) : (oldScale / scaleSpeed);

            if (newScale < MIN_SCALE)
                newScale = MIN_SCALE;
            else if (newScale > MAX_SCALE)
                newScale = MAX_SCALE;

            double f = (newScale / oldScale) - 1;

            if (!lockCamera) {

                double dx = event.getX() - content.getBoundsInParent().getMinX() - content.getBoundsInParent().getWidth() / 2;
                double dy = event.getY() - content.getBoundsInParent().getMinY() - content.getBoundsInParent().getHeight() / 2;

                content.setScale(newScale);

                content.setTranslateX(content.getTranslateX() - f * dx);
                content.setTranslateY(content.getTranslateY() - f * dy);
            } else {
                content.setScale(newScale);
            }

            event.consume();
        }
    };

    private Consumer<VectorCanvas> drawer = DEFAULT_DRAWER;

    private final VectorCanvasContent content = new VectorCanvasContent(DEFAULT_SCALE);

    private double mouseX, mouseY;

    private double scaleSpeed = DEFAULT_SCALE_SPEED;

    private boolean lockCamera = DEFAULT_LOCK_CAMERA;
    private boolean lockScale = DEFAULT_LOCK_SCALE;

    private boolean scaleOnResize = DEFAULT_SCALE_ON_RESIZE;

    public VectorCanvas() {
        this(DEFAULT_DRAWER);
    }

    public VectorCanvas(Consumer<VectorCanvas> drawer) {
        this.drawer = drawer;

        setOnMousePressed(ON_MOUSE_PRESSED_EVENT_HANDLER);
        setOnMouseDragged(ON_MOUSE_DRAGGED_EVENT_HANDLER);
        setOnScroll(ON_SCROLL_EVENT_HANDLER);

        draw();
    }

    @Override
    public void resize(double width, double height) {
        double oldWidth = getWidth(), oldHeight = getHeight();
        if (oldWidth != 0 && oldHeight != 0) {
            if (scaleOnResize) {
                double oldScale = content.getScale();
                double newScale = oldScale * Math.min(width, height) / Math.min(oldWidth, oldHeight);

                if (newScale < MIN_SCALE)
                    newScale = MIN_SCALE;
                else if (newScale > MAX_SCALE)
                    newScale = MAX_SCALE;

                content.setScale(newScale);
            }
            content.setTranslateX(content.getTranslateX() + (width - oldWidth)  / 2);
            content.setTranslateY(content.getTranslateY() + (height - oldHeight) / 2);
        }
        super.resize(width, height);
    }

    public void clear() {
        clearContentChildren();
    }
    public void draw() {
        if (!getChildren().contains(content))
            getChildren().add(content);
        if (drawer != null)
            drawer.accept(this);
    }
    public void redraw() {
        clear();
        draw();
    }

    public Consumer<VectorCanvas> getDrawer() {
        return drawer;
    }
    public void setDrawer(Consumer<VectorCanvas> drawer) {
        this.drawer = drawer;
    }

    protected VectorCanvasContent getContent() {
        return content;
    }

    public ObservableList<Node> getContentChildren() {
        return content.getChildren();
    }
    public ObservableList<Node> getContentChildrenUnmodifiable() {
        return content.getChildrenUnmodifiable();
    }
    public void clearContentChildren() {
        getContentChildren().clear();
    }

    public void draw(Shape shape) {
        getContentChildren().add(shape);
    }
    public void drawAll(Shape... shapes) {
        getContentChildren().addAll(shapes);
    }
    public void drawAll(Collection<? extends Shape> shapes) {
        getContentChildren().addAll(shapes);
    }

    public void erase(Shape shape) {
        getContentChildren().remove(shape);
    }
    public void eraseAll(Shape... shapes) {
        getContentChildren().removeAll(shapes);
    }
    public void eraseAll(Collection<? extends Shape> shapes) {
        getContentChildren().removeAll(shapes);
    }

    public double getScaleSpeed() {
        return scaleSpeed;
    }
    public void setScaleSpeed(double scaleSpeed) {
        this.scaleSpeed = scaleSpeed;
    }

    public boolean isLockCamera() {
        return lockCamera;
    }
    public void setLockCamera(boolean lockCamera) {
        this.lockCamera = lockCamera;
    }

    public boolean isLockScale() {
        return lockScale;
    }
    public void setLockScale(boolean lockScale) {
        this.lockScale = lockScale;
    }

    public boolean isScaleOnResize() {
        return scaleOnResize;
    }
    public void setScaleOnResize(boolean scaleOnResize) {
        this.scaleOnResize = scaleOnResize;
    }

    public void setCameraX(double x) {
        content.setTranslateX(-x);
    }
    public void setCameraY(double y) {
        content.setTranslateY(-y);
    }
    public void setCameraXY(double x, double y) {
        setCameraX(x);
        setCameraY(y);
    }

    public double getScale() {
        return content.getScale();
    }
    public ReadOnlyDoubleProperty scaleProperty() {
        return content.scaleProperty().getReadOnlyProperty();
    }
    public void setScale(double scale) {
        content.setScale(scale);
    }
}