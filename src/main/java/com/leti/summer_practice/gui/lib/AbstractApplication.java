package com.leti.summer_practice.gui.lib;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public abstract class AbstractApplication extends Application {

    protected Stage stage;

    @Override
    public void start(Stage primaryStage) throws Exception {
        stage = primaryStage;
        primaryStage.setTitle(getTitle());
        primaryStage.setScene(createStartScene());
        primaryStage.show();
    }

    protected abstract String getTitle();

    protected abstract Scene createStartScene() throws IOException;
}
