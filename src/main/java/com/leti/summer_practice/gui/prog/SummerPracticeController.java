package com.leti.summer_practice.gui.prog;

import com.leti.summer_practice.R;
import com.leti.summer_practice.gui.lib.VectorCanvas;
import com.leti.summer_practice.logic.Logic;
import com.leti.summer_practice.logic.LogicInterface;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
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


    private static final double CANVAS_WIDTH = 100;
    private static final double CANVAS_HEIGHT = 100;

    private static final double CANVAS_DEFAULT_CAMERA_X = -50;
    private static final double CANVAS_DEFAULT_CAMERA_Y = -50;

    private static final double CANVAS_DEFAULT_ZOOM = 1;


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

    private Map<String,Pair<Double,Double>> verticesCoordsMap = new HashMap<>();

    boolean graphExists = false;

    boolean outputMatrix = true;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        canvas.setCameraXY(CANVAS_DEFAULT_CAMERA_X, CANVAS_DEFAULT_CAMERA_Y);
        canvas.setZoom(CANVAS_DEFAULT_ZOOM / (CANVAS_WIDTH + CANVAS_HEIGHT) * (1200));

        canvas.setDrawer(graphCanvas -> {
            Map<String, Pair<Double,Double>> verticesCoordsMap = this.verticesCoordsMap;
            if (verticesCoordsMap.isEmpty())
                return;
            for (LogicInterface.EdgeInfo edge : logic.getEdges()) {
                Pair<Double,Double> startCoords = verticesCoordsMap.get(edge.start),
                        finishCoords = verticesCoordsMap.get(edge.finish);
                double startX = (startCoords.getKey()) * CANVAS_WIDTH / 2;
                double startY = (startCoords.getValue()) * CANVAS_HEIGHT / 2;
                double finishX = (finishCoords.getKey()) * CANVAS_WIDTH / 2;
                double finishY = (finishCoords.getValue()) * CANVAS_HEIGHT / 2;
                Line line = new Line(startX, startY, finishX, finishY);
                line.setStrokeWidth(2 / canvas.getZoom());
                line.setStroke(Color.GREEN);
                canvas.draw(line);
                double textX = (startX + finishX) / 2;
                double textY = (startY + finishY) / 2;
                Text text = new Text(textX, textY, "" + edge.weight);
                text.setFont(Font.font(16 / canvas.getZoom()));
                canvas.draw(text);
            }
            for (String name : verticesCoordsMap.keySet()) {
                Pair<Double,Double> coords = verticesCoordsMap.get(name);
                double x = (coords.getKey()) * CANVAS_WIDTH / 2;
                double y = (coords.getValue()) * CANVAS_HEIGHT / 2;
                Circle circle = new Circle(x, y, 20 / canvas.getZoom());
                circle.setFill(Color.ORANGE);
                canvas.draw(circle);
                Text text = new Text(x, y, name);
                text.setFont(Font.font(16 / canvas.getZoom()));
                canvas.draw(text);
            }
        });
    }

    public void onLoadFromFileClick(ActionEvent actionEvent) {
        FileChooser fileChooser = new FileChooser();
        File file = fileChooser.showOpenDialog(SummerPracticeApplication.getApplication().getPrimaryStage());
        if (file == null)
            return;
        boolean success = logic.loadFile(file);
        if (!success) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle(R.string("failed_to_load_file_alert_title"));
            alert.setHeaderText(R.string("failed_to_load_file_alert_header_text"));
            alert.setContentText(R.string("failed_to_load_file_alert_content_text"));
            alert.showAndWait();
        }
        try {
            logic.startAlgorithm();
        } catch (RuntimeException re) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle(R.string("failed_to_load_file_alert_title"));
            alert.setContentText(R.string(re.getMessage()));
            alert.showAndWait();
        }
        generateVerticesCoords(logic);
        canvas.redraw();
        graphExists = true;
    }

    public void generateVerticesCoords(LogicInterface logic) {
        ArrayList<LogicInterface.VertexInfo> vertexInfos = logic.getVertices();
        double step = 2 * Math.PI / vertexInfos.size();
        double angle = Math.PI;
        for (int i = 0; i < vertexInfos.size(); ++i, angle += step)
            verticesCoordsMap.put(vertexInfos.get(i).name, new Pair<>(0.9*Math.cos(angle), 0.9*Math.sin(angle)));
    }

    public void onStartClick(ActionEvent actionEvent) {
        if (!graphExists)
            return;
        while (!logic.isAlgorithmFinished()) {
            while (logic.getNewEdges() != null)
                logic.nextBigStep();
        }
        StringBuilder sb = new StringBuilder();

        if (outputMatrix) {
            int n = logic.getVertices().size();
            ArrayList<ArrayList<Integer>> matrix = new ArrayList<>(n);
            for (int i = 0; i < n; ++i) {
                matrix.add(new ArrayList<>(n));
                for (int j = 0; j < n; ++j)
                    matrix.get(i).add(0);
            }
            for (LogicInterface.EdgeInfo edge : logic.getAnswer()) {
                int from = fromAZto09(edge.start), to = fromAZto09(edge.finish);
                matrix.get(from).set(to, edge.weight);
                matrix.get(to).set(from, edge.weight);
            }
            for (ArrayList<Integer> row : matrix) {
                for (Integer i : row) {
                    sb.append(i).append(' ');
                }
                sb.append('\n');
            }
        } else {
            for (LogicInterface.EdgeInfo edge : logic.getAnswer())
                sb.append(edge.start).append(" -> ").append(edge.finish).append(" = ").append(edge.weight).append('\n');
        }

        logTextArea.setText(sb.toString());
    }

    public static int fromAZto09(String string) {
        string = string.toUpperCase(Locale.ROOT);
        int result = 0;
        for (int i = 0; i < string.length(); ++i) {
            result *= 26;
            result += string.charAt(i) - 'A';
        }
        return result;
    }

    public static String from09toAZ(int n) {
        StringBuilder sb = new StringBuilder();
        while (n > 0) {
            sb.append((char)('A' + n % 26));
            n /= 26;
        }
        return sb.reverse().toString();
    }
}