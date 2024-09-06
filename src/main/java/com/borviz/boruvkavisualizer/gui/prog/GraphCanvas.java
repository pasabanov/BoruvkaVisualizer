package com.borviz.boruvkavisualizer.gui.prog;

import com.borviz.boruvkavisualizer.R;
import com.borviz.boruvkavisualizer.gui.lib.VectorCanvas;
import com.borviz.boruvkavisualizer.logic.LogicInterface;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.control.Alert;
import javafx.scene.control.TextFormatter;
import javafx.scene.control.TextInputDialog;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextBoundsType;
import javafx.util.Pair;
import javafx.util.StringConverter;

import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class GraphCanvas extends VectorCanvas {

    public static final double DEFAULT_CANVAS_WIDTH = 200; // relative
    public static final double DEFAULT_CANVAS_HEIGHT = 200; // relative
    public static final double DEFAULT_CANVAS_CAMERA_X = -100; // relative
    public static final double DEFAULT_CANVAS_CAMERA_Y = -100; // relative

    public static final double DEFAULT_VERTEX_RADIUS = 20; // pixels
    public static final double DEFAULT_VERTEX_NAME_TEXT_SIZE = 15; // pixels

    public static final double DEFAULT_EDGE_STROKE_WIDTH = 6; // pixels
    public static final double DEFAULT_EDGE_WEIGHT_TEXT_SIZE = 12; // pixels

    private static final Color DEFAULT_VERTEX_COLOR = Color.DARKGRAY;
    private static final Color DEFAULT_EDGE_COLOR = Color.LIGHTGRAY;

    public enum GraphMode {
        DRAWING,
        MOVING;

        public static final GraphMode DEFAULT_GRAPH_MODE = MOVING;
    }

    private class CircleEvents {

        public static final Color SELECTED_VERTEX_COLOR = Color.RED;

        private double mouseX, mouseY;
        private Circle selectedCircle = null;
        private String selectedCircleName = null;

        public final EventHandler<MouseEvent> onMousePressedEventHandler = event -> {

            if (event.getButton() != MouseButton.PRIMARY)
                return;

            mouseX = event.getX();
            mouseY = event.getY();
        };

        public final EventHandler<MouseEvent> onMouseDraggedEventHandler = event -> {

            if (event.getButton() != MouseButton.PRIMARY)
                return;
            if (graphMode != GraphMode.MOVING)
                return;

            Circle circle = (Circle) event.getSource();
            circle.setCenterX(circle.getCenterX() + (event.getX() - mouseX));
            circle.setCenterY(circle.getCenterY() + (event.getY() - mouseY));
            mouseX = event.getX();
            mouseY = event.getY();
            event.consume();
        };

        public final EventHandler<MouseEvent> onMouseClickedEventHandler = event -> {

            if (graphMode != GraphMode.DRAWING) {
                event.consume();
                return;
            }

            Circle circle = (Circle) event.getSource();
            String name = reversedVerticesMap.get(circle);

            if (event.getButton() == MouseButton.PRIMARY) {

                if (selectedCircle == null) {
                    selectedCircle = circle;
                    selectedCircleName = name;
                    redraw();
                } else {

                    if (logic.edgeExists(selectedCircleName, name)) {
                        Alert alert = new Alert(Alert.AlertType.ERROR);
                        alert.setTitle(null);
                        alert.setHeaderText(R.string("edge_between_this_vertices_already_exists"));
                        alert.setContentText(null);
                        alert.showAndWait();
                        event.consume();
                        return;
                    }

                    TextInputDialog textInputDialog = new TextInputDialog();
                    textInputDialog.setTitle(null);
                    textInputDialog.setHeaderText(R.string("weight_input_dialog_header"));

//                    DoubleNumberTextField.makeTextFieldAcceptOnlyDoubleNumbers(textInputDialog.getEditor());

                    final Pattern validEditingState = Pattern.compile("(([1-9]\\d*)|0)?");

                    textInputDialog.getEditor().setTextFormatter(
                            new TextFormatter<>(
                                    new StringConverter<>() {
                                        @Override
                                        public String toString(Integer i) {
                                            return Integer.toString(i);
                                        }
                                        @Override
                                        public Integer fromString(String s) {
                                            if (s.isEmpty())
                                                return 0;
                                            return Integer.valueOf(s);
                                        }
                                    },
                                    1,
                                    change -> {
                                        String newText = change.getControlNewText();
                                        if (validEditingState.matcher(newText).matches())
                                            return change;
                                        else
                                            return null;
                                    }
                            )
                    );

                    Optional<String> optionalWeight = textInputDialog.showAndWait();
                    if (optionalWeight.isEmpty()) {
                        event.consume();
                        return;
                    }

                    Integer weight = Integer.valueOf(optionalWeight.get());

                    logic.addEdge(selectedCircleName, name, weight);

                    addEdge(selectedCircleName, name, weight);

                    unselectVertex();

                    redraw();
                }

            } else if (event.getButton() == MouseButton.SECONDARY) {

                if (name.equals(selectedCircleName)) {
                    unselectVertex();
                }

                logic.removeVertex(name);

                verticesMap.remove(name);
                reversedVerticesMap.remove(circle);

                for (Iterator<Pair<String,String>> iterator = edgesMap.keySet().iterator(); iterator.hasNext();) {
                    Pair<String,String> key = iterator.next();
                    if (key.getKey().equals(name) || key.getValue().equals(name)) {
                        Line line = edgesMap.get(key);
                        reversedEdgesMap.remove(line);
                        erase(line);
                        iterator.remove();
                        Text text = edgesTextsMap.get(key);
                        edgesTextsMap.remove(key);
                        erase(text);
                    }
                }

                erase(circle);

                Text text = verticesTextsMap.get(name);
                verticesTextsMap.remove(name);

                erase(text);
            }

            event.consume();
        };

        public Circle getSelectedCircle() {
            return selectedCircle;
        }

        public String getSelectedCircleName() {
            return selectedCircleName;
        }

        public boolean isVertexSelected() {
            return selectedCircle != null;
        }

        public void unselectVertex() {
            selectedCircle = null;
            selectedCircleName = null;
        }
    }

    private class LineEvents {

        public final EventHandler<MouseEvent> onMouseClickedEventHandler = event -> {

            if (event.getButton() != MouseButton.SECONDARY)
                return;

            Line line = (Line) event.getSource();
            Pair<String,String> startFinish = reversedEdgesMap.get(line);

            logic.removeEdge(startFinish.getKey(), startFinish.getValue());

            edgesMap.remove(startFinish);
            reversedEdgesMap.remove(line);

            erase(line);

            Text text = edgesTextsMap.get(startFinish);
            edgesTextsMap.remove(startFinish);

            erase(text);

            event.consume();
        };
    }

    private static Color getColorByInt(int n) {
        class Colors {
            private static final ArrayList<Color> colors
                    = new ArrayList<>(
                    List.of(
                            Color.GREEN, Color.ORANGE, Color.YELLOW,
                            Color.BLUE, Color.CYAN, Color.BROWN,
                            Color.AQUAMARINE, Color.SALMON, Color.PURPLE,
                            Color.PINK, Color.YELLOWGREEN, Color.OLIVE
                    )
            );
            private Color getColorByInt(int n) {
                while (n >= colors.size())
                    colors.add(Color.color(Math.random(), Math.random(), Math.random()));
                return colors.get(n);
            }
        }
        return new Colors().getColorByInt(n);
    }

    public static Color getVertexColorByInt(Integer n) {
        if (n == null)
            return DEFAULT_VERTEX_COLOR;
        return getColorByInt(n);
    }

    public static Color getEdgeColorByInt(Integer n) {
        if (n == null)
            return DEFAULT_EDGE_COLOR;
        return getColorByInt(n);
    }

    private LogicInterface logic;

    private final Map<String,Circle> verticesMap = new HashMap<>();
    private final Map<Circle,String> reversedVerticesMap = new HashMap<>();

    private final Map<String,Text> verticesTextsMap = new HashMap<>();

    private final Map<Pair<String,String>,Line> edgesMap = new HashMap<>();
    private final Map<Line,Pair<String,String>> reversedEdgesMap = new HashMap<>();

    private final Map<Pair<String,String>,Text> edgesTextsMap = new HashMap<>();

    private final Set<LogicInterface.EdgeInfo> specialColorEdges = new TreeSet<>(
            Comparator.comparing((LogicInterface.EdgeInfo o) -> o.start)
                    .thenComparing(o -> o.finish)
                    .thenComparingInt(o -> o.weight));
    private static final Color SPECIAL_EDGE_COLOR = Color.RED;

    private final CircleEvents circleEvents = new CircleEvents();
    private final LineEvents lineEvents = new LineEvents();

    private GraphMode graphMode = GraphMode.DEFAULT_GRAPH_MODE;

    public GraphCanvas() {

        addEventHandler(MouseEvent.ANY, new EventHandler<>() {

            private boolean dragging = false;

            @Override
            public void handle(MouseEvent event) {
                if (event.getEventType() == MouseEvent.MOUSE_PRESSED) {
                    dragging = false;
                } else if (event.getEventType() == MouseEvent.DRAG_DETECTED) {
                    dragging = true;
                } else if (event.getEventType() == MouseEvent.MOUSE_CLICKED) {
                    if (dragging)
                        return;
                    if (circleEvents.isVertexSelected()) {
                        circleEvents.unselectVertex();
                        redraw();
                    } else if (graphMode == GraphMode.DRAWING) {
                        if (event.getButton() != MouseButton.PRIMARY)
                            return;
                        TextInputDialog textInputDialog = new TextInputDialog();
                        textInputDialog.setTitle(null);
                        textInputDialog.setHeaderText(R.string("name_of_vertex_input_dialog_header"));
                        Optional<String> optionalName = textInputDialog.showAndWait();
                        if (optionalName.isEmpty())
                            return;
                        String name = optionalName.get();
                        if (name.isEmpty()) {
                            Alert alert = new Alert(Alert.AlertType.INFORMATION);
                            alert.setTitle(null);
                            alert.setHeaderText(R.string("name_must_not_be_empty_alert_header"));
                            alert.setContentText(null);
                            alert.showAndWait();
                            return;
                        }
                        if (verticesMap.containsKey(name)) {
                            Alert alert = new Alert(Alert.AlertType.INFORMATION);
                            alert.setTitle(null);
                            alert.setHeaderText(R.string("name_already_exists_alert_header"));
                            alert.setContentText(null);
                            alert.showAndWait();
                            return;
                        }
                        logic.addVertex(name);
                        addVertex(
                                name,
                                (event.getX() - getContent().getTranslateX()) / getScale(),
                                (event.getY() - getContent().getTranslateY()) / getScale()
                        );
                        redraw();
                    }
                }
            }
        });

        setDrawer(canvas -> {
            // draw all gray
            drawAll(edgesMap.values()
                    .stream()
                    .filter(
                            line -> line.getStroke().equals(DEFAULT_EDGE_COLOR)
                    ).collect(Collectors.toList()));
            // draw all colored
            drawAll(edgesMap.values()
                    .stream()
                    .filter(
                            line -> !line.getStroke().equals(DEFAULT_EDGE_COLOR)
                    ).collect(Collectors.toList()));
            drawAll(edgesTextsMap.values());
            drawAll(verticesMap.values());
            drawAll(verticesTextsMap.values());
        });
    }

    @Override
    public void redraw() {
        notifyColorsChanged();
        super.redraw();
    }

    public LogicInterface getLogic() {
        return logic;
    }
    public void setLogic(LogicInterface logic) {
        this.logic = logic;
        initializeWithLogic(logic);
    }

    private void initializeWithLogic(LogicInterface logic) {

        verticesMap.clear();
        reversedVerticesMap.clear();
        verticesTextsMap.clear();
        edgesMap.clear();
        reversedEdgesMap.clear();
        edgesTextsMap.clear();

        {
            ArrayList<LogicInterface.VertexInfo> vertices = logic.getVertices();
            double angle = Math.PI;
            double step = 2 * Math.PI / vertices.size();
            for (LogicInterface.VertexInfo vertex : vertices) {
                addVertex(
                        vertex.name,
                        0.9 * DEFAULT_CANVAS_WIDTH / 2 * Math.cos(angle),
                        0.9 * DEFAULT_CANVAS_HEIGHT / 2 * Math.sin(angle)
                );
                angle += step;
            }
        }

        for (LogicInterface.EdgeInfo edge : logic.getEdges())
            addEdge(edge.start, edge.finish, edge.weight);

        notifyColorsChanged();
    }

    private void addVertex(String name, double x, double y) {

        Circle circle = new Circle();
        circle.setCenterX(x);
        circle.setCenterY(y);
        circle.setRadius(DEFAULT_VERTEX_RADIUS);
        circle.radiusProperty().bind(new SimpleDoubleProperty(DEFAULT_VERTEX_RADIUS).divide(scaleProperty()));

        circle.setOnMousePressed(circleEvents.onMousePressedEventHandler);
        circle.setOnMouseDragged(circleEvents.onMouseDraggedEventHandler);
        circle.setOnMouseClicked(circleEvents.onMouseClickedEventHandler);

        verticesMap.put(name, circle);
        reversedVerticesMap.put(circle, name);

        Text text = new Text(name);
        text.setFont(new Font(text.getFont().getFamily(), 5));
        text.scaleXProperty().bind(new SimpleDoubleProperty(5).divide(scaleProperty()));
        text.scaleYProperty().bind(new SimpleDoubleProperty(5).divide(scaleProperty()));
        text.setBoundsType(TextBoundsType.VISUAL);
        text.xProperty().bind(circle.centerXProperty().subtract(1));
        text.yProperty().bind(circle.centerYProperty().add(1));

        text.setOnMousePressed(event -> {
            Event.fireEvent(circle, event.copyFor(circle, event.getTarget()));
            event.consume();
        });
        text.setOnMouseDragged(event -> {
            Event.fireEvent(circle, event.copyFor(circle, event.getTarget()));
            event.consume();
        });
        text.setOnMouseClicked(event -> {
            Event.fireEvent(circle, event.copyFor(circle, event.getTarget()));
            event.consume();
        });

        verticesTextsMap.put(name, text);
    }

    private void addEdge(String start, String finish, int weight) {

        Line line = new Line();
        line.startXProperty().bind(verticesMap.get(start).centerXProperty());
        line.startYProperty().bind(verticesMap.get(start).centerYProperty());
        line.endXProperty().bind(verticesMap.get(finish).centerXProperty());
        line.endYProperty().bind(verticesMap.get(finish).centerYProperty());
        line.setStrokeWidth(DEFAULT_EDGE_STROKE_WIDTH);
        line.strokeWidthProperty().bind(new SimpleDoubleProperty(DEFAULT_EDGE_STROKE_WIDTH).divide(scaleProperty()));

        line.setOnMouseClicked(lineEvents.onMouseClickedEventHandler);

        Pair<String,String> startFinish = new Pair<>(start, finish);

        edgesMap.put(startFinish, line);
        reversedEdgesMap.put(line, startFinish);

        Text text = new Text(weight + "");
        text.setFont(new Font(text.getFont().getFamily(), 4.5));
        text.scaleXProperty().bind(new SimpleDoubleProperty(4.5).divide(scaleProperty()));
        text.scaleYProperty().bind(new SimpleDoubleProperty(4.5).divide(scaleProperty()));
        text.setBoundsType(TextBoundsType.VISUAL);
        text.xProperty().bind(line.startXProperty().add(line.endXProperty()).divide(2).add(2));
        text.yProperty().bind(line.startYProperty().add(line.endYProperty()).divide(2).add(2));

        text.setOnMouseClicked(event -> {
            Event.fireEvent(line, event.copyFor(line, event.getTarget()));
            event.consume();
        });

        edgesTextsMap.put(startFinish, text);
    }

    public void notifyColorsChanged() {
        for (Map.Entry<String,Circle> vertex : verticesMap.entrySet()) {
            vertex.getValue().setFill(
                    vertex.getKey().equals(circleEvents.getSelectedCircleName())
                            ? CircleEvents.SELECTED_VERTEX_COLOR
                            : getVertexColorByInt(logic.getVertexInfo(vertex.getKey()).color));
        }
        for (Map.Entry<Pair<String,String>,Line> edge : edgesMap.entrySet()) {
            LogicInterface.EdgeInfo edgeInfo = logic.getEdgeInfo(edge.getKey().getKey(), edge.getKey().getValue());
            edge.getValue().setStroke(
                    specialColorEdgesContains(edgeInfo)
                            ? SPECIAL_EDGE_COLOR
                            : getEdgeColorByInt(edgeInfo.color));
        }
    }

    public Set<LogicInterface.EdgeInfo> getSpecialColorEdges() {
        return specialColorEdges;
    }
    public void addSpecialColorEdge(LogicInterface.EdgeInfo edge) {
        specialColorEdges.add(edge);
    }
    public void clearSpecialColorEdges() {
        specialColorEdges.clear();
    }
    public boolean specialColorEdgesContains(LogicInterface.EdgeInfo edge) {
        if (specialColorEdges.contains(edge))
            return true;
        LogicInterface.EdgeInfo copy = new LogicInterface.EdgeInfo(edge.finish, edge.start, edge.weight, edge.color);
        return specialColorEdges.contains(copy);
    }

    public GraphMode getGraphMode() {
        return graphMode;
    }
    public void setGraphMode(GraphMode graphMode) {
        this.graphMode = graphMode;
    }
}