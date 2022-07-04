package com.leti.summer_practice.gui.lib;

import javafx.beans.InvalidationListener;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

import java.util.function.Consumer;

// TODO
// Not finished yet
public class VectorCanvas extends Pane {


    public static final double DEFAULT_ZOOM_SPEED = 1.05;

    public static final Consumer<VectorCanvas> DEFAULT_DRAWER = canvas -> {
//        double width = canvas.getWidth(), height = canvas.getHeight();
//        canvas.drawAll(new Line(0, 0, width, height), new Line(width, 0, 0, height));
        canvas.draw(new Circle(100, 100, 100));
        canvas.draw(new Circle(500, 100, 100, Color.RED));
        canvas.draw(new Circle(500, 500, 100, Color.GREEN));
        canvas.draw(new Circle(100, 500, 100, Color.ORANGE));
    };


    private Consumer<VectorCanvas> drawer = DEFAULT_DRAWER;

    private final DoubleProperty
            cameraX = new SimpleDoubleProperty(0),
            cameraY = new SimpleDoubleProperty(0);
    private final DoubleProperty zoom = new SimpleDoubleProperty(1);

    private final BooleanProperty enableCameraCoords = new SimpleBooleanProperty(true);
    private final BooleanProperty enableZoom = new SimpleBooleanProperty(true);

    private final BooleanProperty lockCamera = new SimpleBooleanProperty(false);
    private final BooleanProperty lockZoom = new SimpleBooleanProperty(false);

    private double mouseX, mouseY;
    private double zoomSpeed = DEFAULT_ZOOM_SPEED;

    private boolean redrawLock = false;


    public VectorCanvas() {

        InvalidationListener listener = observable -> {
            if (!redrawLock)
                redraw();
        };
        widthProperty().addListener(listener);
        heightProperty().addListener(listener);
        cameraXProperty().addListener(listener);
        cameraYProperty().addListener(listener);
        zoomProperty().addListener(listener);
        enableCameraCoordsProperty().addListener(listener);
        enableZoomProperty().addListener(listener);

        // TODO
        // NOT TESTED YET
        addEventFilter(MouseEvent.MOUSE_ENTERED_TARGET, event -> {
            mouseX = event.getX();
            mouseY = event.getY();
        });
        addEventFilter(MouseEvent.MOUSE_MOVED, event -> {
            mouseX = event.getX();
            mouseY = event.getY();
        });
        addEventFilter(MouseEvent.MOUSE_DRAGGED, event -> {
            setCameraXY(
                    cameraX.get() + (mouseX - event.getX()) * zoom.get(),
                    cameraY.get() + (mouseY - event.getY()) * zoom.get());
            mouseX = event.getX();
            mouseY = event.getY();
        });
        addEventFilter(ScrollEvent.SCROLL, event -> {
            if (event.getDeltaY() == 0)
                return;
            double coefficient = 1 - 1/zoomSpeed;
            if (event.getDeltaY() > 0) {
                System.out.println("------------");
                System.out.println("eventX: " + event.getX());
                System.out.println("coefficient: " + coefficient);
                System.out.println("zoom: " + zoom.get());
                System.out.println("cameraX: " + cameraX.get());
                System.out.println("relativeX: " + (cameraX.get() + event.getX() * zoom.get()));
                redrawLock = true;
                cameraX.set(cameraX.get() + event.getX() * zoom.get() * coefficient);
                cameraY.set(cameraY.get() + event.getY() * zoom.get() * coefficient);
                zoom.set(zoom.get() * zoomSpeed);
                redrawLock = false;
                redraw();
                System.out.println("...");
                System.out.println("zoom: " + zoom.get());
                System.out.println("cameraX: " + cameraX.get());
                System.out.println("relativeX: " + (cameraX.get() + event.getX() * zoom.get()));
            } else {
                redrawLock = true;
                zoom.set(zoom.get() / zoomSpeed);
                cameraX.set(cameraX.get() - event.getX() * zoom.get() * coefficient);
                cameraY.set(cameraY.get() - event.getY() * zoom.get() * coefficient);
                redrawLock = false;
                redraw();
            }
        });
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


    /**
     * Getter, setter and remover of the drawer.
     */
    public Consumer<VectorCanvas> getDrawer() {
        return drawer;
    }
    public void setDrawer(Consumer<VectorCanvas> drawer) {
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


    /**
     * Getter, setter and switcher for camera coords availability.
     */
    public boolean isEnableCameraCoords() {
        return enableCameraCoords.get();
    }
    public void setEnableCameraCoords(boolean enableCameraCoords) {
        this.enableCameraCoords.set(enableCameraCoords);
    }
    public void switchEnableCameraCoords() {
        enableCameraCoords.set(!enableCameraCoords.get());
    }
    public BooleanProperty enableCameraCoordsProperty() {
        return enableCameraCoords;
    }


    /**
     * Getter, setter and switcher for zoom availability.
     */
    public boolean isEnableZoom() {
        return enableZoom.get();
    }
    public void setEnableZoom(boolean enableZoom) {
        this.enableZoom.set(enableZoom);
    }
    public void switchEnableZoom() {
        enableZoom.set(!enableZoom.get());
    }
    public BooleanProperty enableZoomProperty() {
        return enableZoom;
    }


    /**
     * Getter, setter and switcher for camera locker.
     */
    public boolean isLockCamera() {
        return lockCamera.get();
    }
    public BooleanProperty lockCameraProperty() {
        return lockCamera;
    }
    public void setLockCamera(boolean lockCamera) {
        this.lockCamera.set(lockCamera);
    }
    public void switchLockCamera() {
        lockCamera.set(!lockCamera.get());
    }


    /**
     * Getter, setter and switcher for zoom locker.
     */
    public boolean isLockZoom() {
        return lockZoom.get();
    }
    public BooleanProperty lockZoomProperty() {
        return lockZoom;
    }
    public void setLockZoom(boolean lockZoom) {
        this.lockZoom.set(lockZoom);
    }
    public void switchLockZoom() {
        lockZoom.set(!lockZoom.get());
    }


    /**
     * Getter and setter for zoom speed.
     */
    public double getZoomSpeed() {
        return zoomSpeed;
    }
    public void setZoomSpeed(double zoomSpeed) {
        this.zoomSpeed = zoomSpeed;
    }


    /**
     * Getter, setter and switcher for redrawLock;
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
     * Resizes VectorCanvas.
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


////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//  Graphics                                                                                                          //
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////


    /**
     * Coords relative to camera position and zoom (if they are enabled) from original coords.
     */
    public double getCanvasX(double x) {
        if (enableZoom.get())
            x *= zoom.get();
        if (enableCameraCoords.get())
            x -= cameraX.get();
        return x;
    }
    public double getCanvasY(double y) {
        if (enableZoom.get())
            y *= zoom.get();
        if (enableCameraCoords.get())
            y -= cameraY.get();
        return y;
    }

    /**
     * Original coords from relative to camera position and zoom (if they are enabled).
     */
    public double getOriginalX(double x) {
        if (enableCameraCoords.get())
            x += cameraX.get();
        if (enableZoom.get())
            x /= zoom.get();
        return x;
    }
    public double getOriginalY(double y) {
        if (enableCameraCoords.get())
            y += cameraY.get();
        if (enableZoom.get())
            y /= zoom.get();
        return y;
    }


    public double scale(double n) {
        return n * zoom.get();
    }
    public void scale(DoubleProperty dp) {
        dp.set(dp.get() * zoom.get());
    }

    public double descale(double n) {
        return n / zoom.get();
    }
    public void descale(DoubleProperty dp) {
        dp.set(dp.get() / zoom.get());
    }


    public void transform(Line line) {
        line.setStartX(getCanvasX(line.getStartX()));
        line.setStartY(getCanvasY(line.getStartY()));
        line.setEndX(getCanvasX(line.getEndX()));
        line.setEndY(getCanvasY(line.getEndY()));
        if (enableZoom.get())
            scale(line.strokeWidthProperty());
//            line.setStrokeWidth(line.getStrokeWidth() / zoom.get());
    }
    public void retransform(Line line) {
        line.setStartX(getOriginalX(line.getStartX()));
        line.setStartY(getOriginalY(line.getStartY()));
        line.setEndX(getOriginalX(line.getEndX()));
        line.setEndY(getOriginalY(line.getEndY()));
        if (enableZoom.get())
            descale(line.strokeWidthProperty());
//            line.setStrokeWidth(line.getStrokeWidth() * zoom.get());
    }

    public void transform(Circle circle) {
        circle.setCenterX(getCanvasX(circle.getCenterX()));
        circle.setCenterY(getCanvasY(circle.getCenterY()));
        if (enableZoom.get()) {
            scale(circle.radiusProperty());
            scale(circle.strokeWidthProperty());
//            circle.setRadius(circle.getRadius() / zoom.get());
//            circle.setStrokeWidth(circle.getStrokeWidth() / zoom.get());
        }
    }
    public void retransform(Circle circle) {
        circle.setCenterX(getOriginalX(circle.getCenterX()));
        circle.setCenterY(getOriginalY(circle.getCenterY()));
        if (enableZoom.get()) {
            descale(circle.radiusProperty());
            descale(circle.strokeWidthProperty());
//            circle.setRadius(circle.getRadius() * zoom.get());
//            circle.setStrokeWidth(circle.getStrokeWidth() * zoom.get());
        }
    }

    public void transform(Rectangle rectangle) {
        rectangle.setX(getCanvasX(rectangle.getX()));
        rectangle.setY(getCanvasY(rectangle.getY()));
        if (enableZoom.get()) {
            scale(rectangle.widthProperty());
            scale(rectangle.heightProperty());
            scale(rectangle.strokeWidthProperty());
//            rectangle.setWidth(rectangle.getWidth() / zoom.get());
//            rectangle.setHeight(rectangle.getHeight() / zoom.get());
//            rectangle.setStrokeWidth(rectangle.getStrokeWidth() / zoom.get());
        }
    }
    public void retransform(Rectangle rectangle) {
        rectangle.setX(getOriginalX(rectangle.getX()));
        rectangle.setY(getOriginalY(rectangle.getY()));
        if (enableZoom.get()) {
            descale(rectangle.widthProperty());
            descale(rectangle.heightProperty());
            descale(rectangle.strokeWidthProperty());
//            rectangle.setWidth(rectangle.getWidth() * zoom.get());
//            rectangle.setHeight(rectangle.getHeight() * zoom.get());
//            rectangle.setStrokeWidth(rectangle.getStrokeWidth() * zoom.get());
        }
    }

    public void transform(Text text) {
        text.setX(getCanvasX(text.getX()));
        text.setY(getCanvasY(text.getY()));
        if (enableZoom.get()) {
            text.setFont(Font.font(text.getFont().getFamily(), scale(text.getFont().getSize())));
            scale(text.strokeWidthProperty());
//            text.setStrokeWidth(text.getStrokeWidth() / zoom.get());
        }
    }
    public void retransform(Text text) {
        text.setX(getOriginalX(text.getX()));
        text.setY(getOriginalY(text.getY()));
        if (enableZoom.get()) {
            text.setFont(Font.font(text.getFont().getFamily(), descale(text.getFont().getSize())));
            descale(text.strokeWidthProperty());
//            text.setFont(Font.font(text.getFont().getFamily(), text.getFont().getSize() * zoom.get()));
//            text.setStrokeWidth(text.getStrokeWidth() * zoom.get());
        }
    }


    public void draw(Line line) {
        transform(line);
        getChildren().add(line);
    }
    public void drawAll(Line... lines) {
        for (Line line : lines)
            transform(line);
        getChildren().addAll(lines);
    }

    public void draw(Circle circle) {
        transform(circle);
        getChildren().add(circle);
    }
    public void drawAll(Circle... circles) {
        for (Circle circle : circles)
            transform(circle);
        getChildren().addAll(circles);
    }

    public void draw(Rectangle rectangle) {
        transform(rectangle);
        getChildren().add(rectangle);
    }
    public void drawAll(Rectangle... rectangles) {
        for (Rectangle rectangle : rectangles)
            transform(rectangle);
        getChildren().addAll(rectangles);
    }

    public void draw(Text text) {
        transform(text);
        getChildren().add(text);
    }
    public void drawAll(Text... texts) {
        for (Text text : texts)
            transform(text);
        getChildren().addAll(texts);
    }
}