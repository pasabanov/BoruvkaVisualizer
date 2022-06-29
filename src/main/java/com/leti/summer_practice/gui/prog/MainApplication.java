package com.leti.summer_practice.gui.prog;

import com.leti.summer_practice.R;
import com.leti.summer_practice.gui.lib.AbstractApplication;
import javafx.scene.Scene;

import java.io.IOException;

public class MainApplication extends AbstractApplication {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    protected String getTitle() {
        return "Hello!";
    }

    @Override
    protected Scene createStartScene() throws IOException {
        return new Scene(R.load("hello-view.fxml"), 320, 240);
    }
}