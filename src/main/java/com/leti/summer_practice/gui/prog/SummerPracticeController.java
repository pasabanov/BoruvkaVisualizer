package com.leti.summer_practice.gui.prog;

import com.leti.summer_practice.R;
import com.leti.summer_practice.logic.Logic;
import com.leti.summer_practice.logic.LogicInterface;
import com.leti.summer_practice.util.SingleTaskTimer;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.stage.FileChooser;

import java.io.File;
import java.net.URL;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.TimerTask;


public class SummerPracticeController implements Initializable {


    private static final double STEP_SPEED_CHANGE = 0.1;
    private static final double MIN_STEP_SPEED = 0.1;
    private static final double MAX_STEP_SPEED = 10;
    
    private static final String LOG_DIVIDER = ";\n";


    @FXML
    public GraphCanvas canvas;
    @FXML
    public TextArea logTextArea;

    @FXML
    public Button modeButton;
    @FXML
    public Button loadFromFileButton;
    @FXML
    public Button clearGraphButton;

    @FXML
    public TextField speedTextField;
    @FXML
    public Button minusSpeedButton;
    @FXML
    public Button plusSpeedButton;

    @FXML
    public Button startStopButton;
    @FXML
    public Button nextStepButton;
    @FXML
    public Button againButton;


    private final LogicInterface logic = new Logic();

    private boolean answerAlreadyPrinted = false;


    SingleTaskTimer autoStepTimer = new SingleTaskTimer(true);


    @Override
    public void initialize(URL location, ResourceBundle resources) {

        Runnable onSpeedTextFieldUpdated = () -> {

            double period = Double.parseDouble(speedTextField.getText());
            if (period < MIN_STEP_SPEED)
                speedTextField.setText(MIN_STEP_SPEED + "");
            else if (period > MAX_STEP_SPEED)
                speedTextField.setText(MAX_STEP_SPEED + "");

            changeTimerStepSpeed();
        };

        speedTextField.focusedProperty().addListener(observable -> onSpeedTextFieldUpdated.run());
        speedTextField.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                onSpeedTextFieldUpdated.run();
                event.consume();
            }
        });

        speedTextField.textProperty().addListener(new ChangeListener<>() {
            private static final int PRECISION = 2;
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                speedTextField.setText(
                        String.format(
                                new Locale("en_EN"),
                                "%." + PRECISION + "f",
                                Double.parseDouble(newValue)
                        ).replaceAll("([.,]\\d+?)0*$", "$1")
                );
            }
        });


        canvas.setCameraXY(-canvas.getPrefWidth() / 2, -canvas.getPrefHeight() / 2);
        canvas.setScale(
                (canvas.getPrefWidth() + canvas.getPrefHeight())
                        / (GraphCanvas.DEFAULT_CANVAS_WIDTH + GraphCanvas.DEFAULT_CANVAS_HEIGHT));
//        canvas.setCameraXY(
//                GraphCanvas.DEFAULT_CANVAS_CAMERA_X * canvas.getScale(),
//                GraphCanvas.DEFAULT_CANVAS_CAMERA_Y * canvas.getScale());

        canvas.setLogic(logic);

        canvas.redraw();
    }


    @FXML
    public void onClearGraphClicked(ActionEvent actionEvent) {

        class ClearGraphAlertContainer {
            public static final ClearGraphAlert CLEAR_GRAPH_ALERT = new ClearGraphAlert();

            static class ClearGraphAlert extends Alert {
                public ClearGraphAlert() {
                    super(Alert.AlertType.CONFIRMATION,
                            R.string("clear_graph_confirmation_alert_content_text"),
                            ButtonType.NO,
                            ButtonType.YES);
                    setTitle(R.string("confirmation_alert_title"));
                    setHeaderText(null);
                    ((Button) getDialogPane().lookupButton(ButtonType.YES)).setDefaultButton(false);
                    ((Button) getDialogPane().lookupButton(ButtonType.NO)).setDefaultButton(true);
                    ((Button) getDialogPane().lookupButton(ButtonType.NO)).setCancelButton(true);
                }
            }
        }

        ClearGraphAlertContainer.CLEAR_GRAPH_ALERT.showAndWait();

        if (ClearGraphAlertContainer.CLEAR_GRAPH_ALERT.getResult() == ButtonType.YES) {
            logic.clearGraph();
            logTextArea.clear();
            canvas.clearSpecialColorEdges();
            canvas.setLogic(logic);
            canvas.clear();
        }
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
            alert.setHeaderText(R.string("error_alert_header"));
            alert.setContentText(re.getMessage());
            alert.showAndWait();
            return;
        }
        canvas.setLogic(logic);
        logTextArea.clear();
        answerAlreadyPrinted = false;
        canvas.clearSpecialColorEdges();
        canvas.redraw();
    }


    @FXML
    public void onStartClick(ActionEvent actionEvent) {

        if (logic.isGraphEmpty())
            return;

        setMovingMode();

        if (!autoStepTimer.hasCurrentTimerTask()) {
            runTimer();
            startStopButton.setText(R.string("stop"));
        } else {
            resetStartStopButtonAndTimer();
        }
    }

    private void resetStartStopButtonAndTimer() {
        autoStepTimer.cancel();
        startStopButton.setText(R.string("start"));
    }


    @FXML
    public void onNextStepClicked(ActionEvent actionEvent) {

        boolean autoSolvingWasRunning = autoStepTimer.hasCurrentTimerTask();

        if (autoSolvingWasRunning)
            autoStepTimer.cancel();

        nextStep();

        if (autoSolvingWasRunning)
            runTimerWithDelay();
    }

    private void nextStep() {

        if (logic.isGraphEmpty())
            return;

        setMovingMode();

        if (!logic.isAlgorithmStarted()) {
            boolean algorithmStarted = tryToStartAlgorithm();
            if (!algorithmStarted) {
                autoStepTimer.cancel();
                return;
            }
            logTextArea.appendText(R.string("initial_components_vertices_colored") + LOG_DIVIDER);
            canvas.redraw();
            return;
        }

        if (logic.isAlgorithmFinished()) {
            printAlgorithmResult();
            canvas.clearSpecialColorEdges();
            canvas.redraw();
            return;
        }

        LogicInterface.EdgeInfo newEdge;

        while (true) {
            newEdge = logic.getNewEdge();
            if (newEdge == null)
                break;
            if (canvas.specialColorEdgesContains(newEdge)) {
                logTextArea.appendText(
                        R.string("added_edge")
                                + " "
                                + newEdge.toStringWithoutWeight()
                                + " "
                                + R.string("of_color")
                                + "'"
                                + GraphCanvas.getEdgeColorByInt(newEdge.color).toString()
                                + "'"
                                + " — "
                                + R.string("already_added")
                                + LOG_DIVIDER
                );
            } else {
                break;
            }
        }

        if (newEdge == null) {

            logic.nextBigStep();

            canvas.getSpecialColorEdges().clear();

            if (logic.isAlgorithmFinished()) {
                printAlgorithmResult();
            } else {
                logTextArea.appendText(R.string("connecting_components") + LOG_DIVIDER);
            }
        } else {

            canvas.addSpecialColorEdge(newEdge);

            logTextArea.appendText(
                    R.string("added_edge")
                            + " "
                            + newEdge.toStringWithoutWeight()
                            + " "
                            + R.string("of_color")
                            + "'"
                            + GraphCanvas.getEdgeColorByInt(newEdge.color).toString()
                            + "'"
                            + LOG_DIVIDER);
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
            alert.setHeaderText(R.string("error_alert_header"));
            alert.setContentText(re.getMessage());
            alert.showAndWait();
            return false;
        }
    }

    private void printAlgorithmResult() {

        if (logic.isGraphEmpty() || !logic.isAlgorithmFinished())
            return;

        if (answerAlreadyPrinted)
            return;
        answerAlreadyPrinted = true;

        StringBuilder sb = new StringBuilder();

        if (!logTextArea.getText().isEmpty())
            sb.append('\n');

        sb.append(R.string("algorithm_result_header")).append('\n');

        for (LogicInterface.EdgeInfo edge : logic.getAnswer())
            sb.append(edge.toStringWithoutWeight()).append(" = ").append(edge.weight).append('\n');

        if (sb.charAt(sb.length() - 1) == '\n')
            sb.setCharAt(sb.length() - 1, '.');

        logTextArea.appendText(sb.toString());
    }


    @FXML
    public void onAgainClicked(ActionEvent actionEvent) {
        resetStartStopButtonAndTimer();
        logTextArea.clear();
        answerAlreadyPrinted = false;
        logic.killAlgorithm();
        canvas.clearSpecialColorEdges();
        canvas.redraw();
    }


    @FXML
    public void onModeClick(ActionEvent actionEvent) {
        if (canvas.getGraphMode() == GraphCanvas.GraphMode.MOVING)
            setDrawingMode();
        else if (canvas.getGraphMode() == GraphCanvas.GraphMode.DRAWING)
            setMovingMode();
        else
            throw new Error("Stub!");
    }

    private void setDrawingMode() {
        canvas.setGraphMode(GraphCanvas.GraphMode.DRAWING);
        logic.killAlgorithm();
        canvas.clearSpecialColorEdges();
        canvas.redraw();
        modeButton.setText(R.string("drag_mode"));
    }
    private void setMovingMode() {
        canvas.setGraphMode(GraphCanvas.GraphMode.MOVING);
        modeButton.setText(R.string("draw_mode"));
    }


    @FXML
    public void onMinusSpeedClick(ActionEvent actionEvent) {
        double oldValue = Double.parseDouble(speedTextField.getText());
        double newValue = oldValue - STEP_SPEED_CHANGE;
        if (newValue < MIN_STEP_SPEED)
            return;
        speedTextField.setText(String.valueOf(newValue));

        changeTimerStepSpeed();
    }

    @FXML
    public void onPlusSpeedClick(ActionEvent actionEvent) {
        double oldValue = Double.parseDouble(speedTextField.getText());
        double newValue = oldValue + STEP_SPEED_CHANGE;
        if (newValue > MAX_STEP_SPEED)
            return;
        speedTextField.setText(String.valueOf(newValue));

        changeTimerStepSpeed();
    }


    private void changeTimerStepSpeed() {
        if (!autoStepTimer.hasCurrentTimerTask())
            return;
        autoStepTimer.cancel();
        long period = (long)(1000 / Double.parseDouble(speedTextField.getText()));
        long time = autoStepTimer.timeFromLastCompletion();
        long delay = period - time;
        if (delay < 0)
            delay = 0;
        runTimer(delay, period);
    }

    private void runTimer() {
        runTimer(0, (long)(1000 / Double.parseDouble(speedTextField.getText())));
    }

    private void runTimer(long delay, long period) {
        autoStepTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                Platform.runLater(() -> {
                    nextStep();
                    if (logic.isAlgorithmFinished())
                        autoStepTimer.cancel();
                });
            }
        }, delay, period);
    }

    private void runTimerWithDelay() {
        long delay_period = (long)(1000 / Double.parseDouble(speedTextField.getText()));
        runTimer(delay_period, delay_period);
    }
}