package com.leti.summer_practice.gui.prog;

import com.leti.summer_practice.gui.lib.AdvancedCanvas;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.util.Pair;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class SummerPracticeController implements Initializable {

    @FXML
    public AdvancedCanvas canvas;
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
    public BorderPane pane;

//    private GraphDataGUI graphDataGUI = new GraphDataGUI(12);

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        canvas.setDrawer(() -> {

            double width = canvas.getWidth(), height = canvas.getHeight();
            double w2 = width/2, h2 = height/2;

            GraphicsContext gc = canvas.getGraphicsContext2D();
            gc.setStroke(Color.BLACK);
            gc.setFill(Color.YELLOW);

//            ArrayList<Pair<Double,Double>> verticesCoords = graphDataGUI.getVerticesCoords();
//            for (int i = 0; i < verticesCoords.size(); ++i) {
//                Pair<Double,Double> coords = verticesCoords.get(i);
//                double x = w2 + coords.getKey()*w2*0.8;
//                double y = h2 + coords.getValue()*h2*0.8;
//                gc.fillOval(x - 10, y - 10, 20, 20);
//                gc.fillRect(0 - 10, 0 - 10, 20, 20);
//                gc.strokeText("" + (i+1), x - 5, y + 5);
//            }

//            for (int i = 0; i < verticesCoords.size(); ++i) {
//                Pair<Double,Double> coords = verticesCoords.get(i);
//                double x = w2 + coords.getKey()*w2*0.8;
//                double y = h2 + coords.getValue()*h2*0.8;
//                Circle circle = new Circle(x, y, 20);
//                circle.setFill(Color.YELLOW);
//                circle.setOnMouseClicked(event -> System.out.println("circle clicked"));
//                Text text = new Text(circle.getCenterX(), circle.getCenterY(), "" + (i+1));
//                text.setOnMouseClicked(event -> System.out.println("text clicked"));
//                pane.getChildren().addAll(circle, text);
//            }
        });
    }

    public void onLoadFromFileClick(ActionEvent actionEvent) {
        FileChooser fileChooser = new FileChooser();
        File file = fileChooser.showOpenDialog(SummerPracticeApplication.getApplication().getPrimaryStage());
        if (file == null)
            return;
        System.out.println(file);
    }
}