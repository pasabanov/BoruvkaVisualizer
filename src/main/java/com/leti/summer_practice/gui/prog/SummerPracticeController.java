package com.leti.summer_practice.gui.prog;

import com.leti.summer_practice.R;
import com.leti.summer_practice.logic.Graph;
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
import java.util.ArrayList;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

public class SummerPracticeController implements Initializable {

    @FXML
    public GraphCanvas canvas;
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

    boolean graphExists = false;

    boolean outputMatrix = true;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
//        canvas.setDrawer(() -> {
//
//            double width = canvas.getWidth(), height = canvas.getHeight();
//            double w2 = width/2, h2 = height/2;
//
//            GraphicsContext gc = canvas.getGraphicsContext2D();
//            gc.setStroke(Color.BLACK);
//            gc.setFill(Color.YELLOW);
//
////            ArrayList<Pair<Double,Double>> verticesCoords = graphDataGUI.getVerticesCoords();
////            for (int i = 0; i < verticesCoords.size(); ++i) {
////                Pair<Double,Double> coords = verticesCoords.get(i);
////                double x = w2 + coords.getKey()*w2*0.8;
////                double y = h2 + coords.getValue()*h2*0.8;
////                gc.fillOval(x - 10, y - 10, 20, 20);
////                gc.fillRect(0 - 10, 0 - 10, 20, 20);
////                gc.strokeText("" + (i+1), x - 5, y + 5);
////            }
//
////            for (int i = 0; i < verticesCoords.size(); ++i) {
////                Pair<Double,Double> coords = verticesCoords.get(i);
////                double x = w2 + coords.getKey()*w2*0.8;
////                double y = h2 + coords.getValue()*h2*0.8;
////                Circle circle = new Circle(x, y, 20);
////                circle.setFill(Color.YELLOW);
////                circle.setOnMouseClicked(event -> System.out.println("circle clicked"));
////                Text text = new Text(circle.getCenterX(), circle.getCenterY(), "" + (i+1));
////                text.setOnMouseClicked(event -> System.out.println("text clicked"));
////                pane.getChildren().addAll(circle, text);
////            }
//        });

        canvas.setDrawer(graphCanvas -> {
            Map<String, Pair<Double,Double>> verticesCoordsMap = graphCanvas.getVerticesCoordsMap();
            if (verticesCoordsMap.isEmpty())
                return;
            for (LogicInterface.Edge_info edge : logic.getEdges()) {
                Pair<Double,Double> startCoords = verticesCoordsMap.get(edge.start),
                        finishCoords = verticesCoordsMap.get(edge.finish);
                double startX = graphCanvas.getRelativeX(startCoords.getKey());
                double startY = graphCanvas.getRelativeY(startCoords.getValue());
                double finishX = graphCanvas.getRelativeX(finishCoords.getKey());
                double finishY = graphCanvas.getRelativeY(finishCoords.getValue());
                Line line = new Line(startX, startY, finishX, finishY);
                line.setStrokeWidth(2);
                line.setStroke(Color.GREEN);
                graphCanvas.getChildren().add(line);
                double textX = (startX + finishX) / 2;
                double textY = (startY + finishY) / 2;
                Text text = new Text(textX, textY, "" + edge.weight);
                text.setFont(Font.font(16));
                graphCanvas.getChildren().add(text);
            }
            for (String name : verticesCoordsMap.keySet()) {
                Pair<Double,Double> coords = verticesCoordsMap.get(name);
                double x = graphCanvas.getRelativeX(coords.getKey());
                double y = graphCanvas.getRelativeY(coords.getValue());
                Circle circle = new Circle(x, y, 0.08 * (graphCanvas.getWidth() + graphCanvas.getHeight())/2);
                circle.setFill(Color.ORANGE);
                graphCanvas.getChildren().add(circle);
                Text text = new Text(x, y, name);
                text.setFont(Font.font(16));
                graphCanvas.getChildren().add(text);
            }
        });
    }

    public void onLoadFromFileClick(ActionEvent actionEvent) {
        FileChooser fileChooser = new FileChooser();
        File file = fileChooser.showOpenDialog(SummerPracticeApplication.getApplication().getPrimaryStage());
        if (file == null)
            return;
        boolean success = logic.load_file(file);
        if (!success) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle(R.string("failed_to_load_file_alert_title"));
            alert.setHeaderText(R.string("failed_to_load_file_alert_header_text"));
            alert.setContentText(R.string("failed_to_load_file_alert_content_text"));
            alert.showAndWait();
        }
        logic.start_algorithm();
        canvas.consumeLogic(logic);
        canvas.redraw();
        graphExists = true;
    }

    public void onStartClick(ActionEvent actionEvent) {
        if (!graphExists)
            return;
        while (!logic.isAlgorithmFinished()) {
            while (logic.get_new_edges() != null)
                logic.next_big_step();
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
            for (LogicInterface.Edge_info edge : logic.get_answer()) {
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
            for (LogicInterface.Edge_info edge : logic.get_answer())
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