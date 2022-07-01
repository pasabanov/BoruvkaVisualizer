package com.leti.summer_practice.gui.prog;

import com.leti.summer_practice.R;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class SummerPracticeApplication extends Application {

    protected Stage primaryStage;

    @Override
    public void start(Stage primaryStage) throws Exception {
        this.primaryStage = primaryStage;
        primaryStage.setTitle(R.string("AppName"));
        primaryStage.setScene(new Scene(R.loadFXML("summer-practice.fxml"), 320, 240));
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}