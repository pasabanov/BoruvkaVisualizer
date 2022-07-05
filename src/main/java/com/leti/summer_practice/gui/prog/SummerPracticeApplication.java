package com.leti.summer_practice.gui.prog;

import com.leti.summer_practice.R;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class SummerPracticeApplication extends Application {

    private static SummerPracticeApplication application;
    private Stage primaryStage;


    public static void launchSummerPracticeApplication(String... args) {
        launch(args);
    }


    @Override
    public void start(Stage primaryStage) throws Exception {
        application = this;
        setPrimaryStage(primaryStage);
        primaryStage.setTitle(R.string("AppName"));
        primaryStage.setScene(new Scene(R.loadFXML("summer-practice.fxml"), 800, 600));
        primaryStage.show();
    }


    public static SummerPracticeApplication getApplication() {
        return application;
    }


    public Stage getPrimaryStage() {
        return primaryStage;
    }
    public void setPrimaryStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }
}