package com.leti.summer_practice.gui.prog;

import com.leti.summer_practice.R;
import com.leti.summer_practice.gui.lib.VectorCanvas;
import com.leti.summer_practice.logic.Logic;
import com.leti.summer_practice.logic.LogicInterface;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.util.Pair;

import java.io.File;
import java.net.URL;
import java.util.*;

public class SummerPracticeController implements Initializable {


    private enum GraphMode {
        DRAWING,
        MOVING
    }


    private static final double CANVAS_WIDTH = 100;
    private static final double CANVAS_HEIGHT = 100;

    private static final double CANVAS_DEFAULT_CAMERA_X = -50;
    private static final double CANVAS_DEFAULT_CAMERA_Y = -50;

    private static final double CANVAS_DEFAULT_ZOOM = 1;

    private static final Color DEFAULT_VERTEX_COLOR = Color.DARKGRAY;
    private static final Color DEFAULT_EDGE_COLOR = Color.LIGHTGRAY;


    @FXML
    public VectorCanvas canvas;
    @FXML
    public TextArea logTextArea;

    @FXML
    public Button modeButton;
    @FXML
    public Button loadFromFileButton;

    @FXML
    public TextField speedTextField;

    @FXML
    public Button startStopButton;
    @FXML
    public Button nextStepButton;
    @FXML
    public Button resetButton;

    LogicInterface logic = new Logic();

    private final Map<String,Pair<Double,Double>> verticesCoordsMap = new HashMap<>();

//    private final Set<Pair<String,String>> newEdges = new TreeSet<>(
//            Comparator.comparing((Pair<String, String> o) -> o.getKey()).thenComparing(Pair::getValue));

    private final Set<LogicInterface.EdgeInfo> newEdges = new TreeSet<>(
            Comparator.comparing((LogicInterface.EdgeInfo o) -> o.start)
                    .thenComparing(o -> o.finish)
                    .thenComparingInt(o -> o.weight));

//    private ArrayList<LogicInterface.EdgeInfo> newEdges = new ArrayList<>();

    boolean paintNewEdgesRed = true;


    private static Color getColorByInt(int n) {
        return new Object() {

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
        }.getColorByInt(n);
    }


    @Override
    public void initialize(URL location, ResourceBundle resources) {

        canvas.setCameraXY(CANVAS_DEFAULT_CAMERA_X, CANVAS_DEFAULT_CAMERA_Y);
        canvas.setZoom(CANVAS_DEFAULT_ZOOM / Math.min(CANVAS_WIDTH, CANVAS_HEIGHT) * (500));

        canvas.setDrawer(graphCanvas -> {

            Map<String, Pair<Double,Double>> verticesCoordsMap = this.verticesCoordsMap;
            if (verticesCoordsMap.isEmpty())
                return;

            // drawing all edges
            for (LogicInterface.EdgeInfo edge : logic.getEdges()) {
                Pair<Double,Double> startCoords = verticesCoordsMap.get(edge.start),
                        finishCoords = verticesCoordsMap.get(edge.finish);
                double startX = (startCoords.getKey()) * CANVAS_WIDTH / 2;
                double startY = (startCoords.getValue()) * CANVAS_HEIGHT / 2;
                double finishX = (finishCoords.getKey()) * CANVAS_WIDTH / 2;
                double finishY = (finishCoords.getValue()) * CANVAS_HEIGHT / 2;
                Color color = (edge.color == null || logic.isAlgorithmFinished()) ? DEFAULT_EDGE_COLOR : getColorByInt(edge.color);
                Line line = new Line(startX, startY, finishX, finishY);
                line.setStrokeWidth(2 / canvas.getZoom());
                line.setStroke(color);
                canvas.draw(line);
                double textX = (startX + finishX) / 2;
                double textY = (startY + finishY) / 2;
                Text text = new Text(textX, textY, "" + edge.weight);
                text.setFont(Font.font(16 / canvas.getZoom()));
                canvas.draw(text);
            }

            // drawing changed edges
            for (LogicInterface.EdgeInfo edge : newEdges) {
                Pair<Double,Double> startCoords = verticesCoordsMap.get(edge.start),
                        finishCoords = verticesCoordsMap.get(edge.finish);
                double startX = (startCoords.getKey()) * CANVAS_WIDTH / 2;
                double startY = (startCoords.getValue()) * CANVAS_HEIGHT / 2;
                double finishX = (finishCoords.getKey()) * CANVAS_WIDTH / 2;
                double finishY = (finishCoords.getValue()) * CANVAS_HEIGHT / 2;
                Color color = paintNewEdgesRed ? Color.RED : (edge.color == null) ? DEFAULT_EDGE_COLOR : getColorByInt(edge.color);
                Line line = new Line(startX, startY, finishX, finishY);
                line.setStrokeWidth(2 / canvas.getZoom());
                line.setStroke(color);
                canvas.draw(line);
                double textX = (startX + finishX) / 2;
                double textY = (startY + finishY) / 2;
                Text text = new Text(textX, textY, "" + edge.weight);
                text.setFont(Font.font(16 / canvas.getZoom()));
                canvas.draw(text);
            }

            // final edges
            if (logic.isAlgorithmFinished()) {
                for (LogicInterface.EdgeInfo edge : logic.getAnswer()) {
                    Pair<Double,Double> startCoords = verticesCoordsMap.get(edge.start),
                            finishCoords = verticesCoordsMap.get(edge.finish);
                    double startX = (startCoords.getKey()) * CANVAS_WIDTH / 2;
                    double startY = (startCoords.getValue()) * CANVAS_HEIGHT / 2;
                    double finishX = (finishCoords.getKey()) * CANVAS_WIDTH / 2;
                    double finishY = (finishCoords.getValue()) * CANVAS_HEIGHT / 2;
                    Color color = (edge.color == null) ? DEFAULT_EDGE_COLOR : getColorByInt(edge.color);
                    Line line = new Line(startX, startY, finishX, finishY);
                    line.setStrokeWidth(2 / canvas.getZoom());
                    line.setStroke(color);
                    canvas.draw(line);
                    double textX = (startX + finishX) / 2;
                    double textY = (startY + finishY) / 2;
                    Text text = new Text(textX, textY, "" + edge.weight);
                    text.setFont(Font.font(16 / canvas.getZoom()));
                    canvas.draw(text);
                }
            }

            // drawing vertices
            for (LogicInterface.VertexInfo vertex : logic.getVertices()) {
                String name = vertex.name;
                Pair<Double,Double> coords = verticesCoordsMap.get(name);
                double x = (coords.getKey()) * CANVAS_WIDTH / 2;
                double y = (coords.getValue()) * CANVAS_HEIGHT / 2;
                Color color = (vertex.color == null) ? DEFAULT_VERTEX_COLOR : getColorByInt(vertex.color);
                Circle circle = new Circle(x, y, 20 / canvas.getZoom());
                circle.setFill(color);
                canvas.draw(circle);
                Text text = new Text(x, y, name);
                text.setFont(Font.font(16 / canvas.getZoom()));
                canvas.draw(text);
            }
        });
    }


    @FXML
    public void onClearGraphClicked(ActionEvent actionEvent) {
        logic.clearGraph();
        canvas.clear();
        logTextArea.clear();
        newEdges.clear();
        verticesCoordsMap.clear();
    }


    @FXML
    public void onLoadFromFileClick(ActionEvent actionEvent) {

        class LastDirectoryContainer {
            public static File lastDirectory = null;
        }

        FileChooser fileChooser = new FileChooser();
        if (LastDirectoryContainer.lastDirectory != null)
            fileChooser.setInitialDirectory(LastDirectoryContainer.lastDirectory);
        File file = fileChooser.showOpenDialog(SummerPracticeApplication.getApplication().getPrimaryStage());
        if (file == null)
            return;
        LastDirectoryContainer.lastDirectory = file.getParentFile();
        try {
            logic.loadFile(file);
        } catch (RuntimeException re) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle(R.string("failed_to_load_file_alert_title"));
            alert.setHeaderText(R.string("default_error_alert_header_text"));
            alert.setContentText(re.getMessage());
            alert.showAndWait();
            return;
        }
        generateVerticesCoords(logic);
        logTextArea.clear();
        newEdges.clear();
        canvas.redraw();
    }

    public void generateVerticesCoords(LogicInterface logic) {
        verticesCoordsMap.clear();
        ArrayList<LogicInterface.VertexInfo> vertexInfos = logic.getVertices();
        double step = 2 * Math.PI / vertexInfos.size();
        double angle = Math.PI;
        for (int i = 0; i < vertexInfos.size(); ++i, angle += step)
            verticesCoordsMap.put(vertexInfos.get(i).name, new Pair<>(0.9*Math.cos(angle), 0.9*Math.sin(angle)));
    }


    @FXML
    public void onStartClick(ActionEvent actionEvent) {

        if (logic.isGraphEmpty())
            return;

        if (!tryToStartAlgorithm())
            return;

        while (!logic.isAlgorithmFinished()) {
            while (logic.getNewEdge() != null)
                ;
            logic.nextBigStep();
        }

        printAlgorithmResult();
        canvas.redraw();
    }


    @FXML
    public void onNextStepClicked(ActionEvent actionEvent) {

        if (logic.isGraphEmpty())
            return;

        if (!logic.isAlgorithmStarted()) {
            boolean algorithmStarted = tryToStartAlgorithm();
            if (!algorithmStarted)
                return;
            canvas.redraw();
            return;
        }

        if (logic.isAlgorithmFinished()) {
            newEdges.clear();
            printAlgorithmResult();
            canvas.redraw();
            return;
        }

        LogicInterface.EdgeInfo newEdge;

        do {
            newEdge = logic.getNewEdge();
        } while (newEdge != null && newEdges.contains(newEdge));

        if (newEdge == null) {

            logic.nextBigStep();

            newEdges.clear();

            if (logic.isAlgorithmFinished())
                printAlgorithmResult();

        } else {
            newEdges.add(newEdge);
        }

        canvas.redraw();
    }

    private boolean tryToStartAlgorithm() {
        try {
            logic.startAlgorithm();
            return true;
        } catch (RuntimeException re) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle(R.string("failed_to_start_algorithm_alert_title"));
            alert.setHeaderText(R.string("default_error_alert_header_text"));
            alert.setContentText(re.getMessage());
            alert.showAndWait();
            return false;
        }
    }

    private void printAlgorithmResult() {
        if (logic.isGraphEmpty() || !logic.isAlgorithmFinished())
            return;

        StringBuilder sb = new StringBuilder();

        for (LogicInterface.EdgeInfo edge : logic.getAnswer())
            sb.append(edge.start).append(" --- ").append(edge.finish).append(" = ").append(edge.weight).append('\n');

        logTextArea.setText(sb.toString());
    }


    @FXML
    public void onAgainClicked(ActionEvent actionEvent) {
        logTextArea.clear();
        newEdges.clear();
        logic.killAlgorithm();
        canvas.redraw();
    }
}