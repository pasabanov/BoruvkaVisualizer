package com.borviz.boruvkavisualizer.gui.prog;

import com.borviz.boruvkavisualizer.R;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class BorVizApplication extends Application {

	private static BorVizApplication application;

	private Stage primaryStage;

	public static void launchBorVizApplication(String... args) {
		launch(args);
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		application = this;
		setPrimaryStage(primaryStage);
		primaryStage.setTitle(R.string("AppName"));
		primaryStage.setScene(new Scene(R.loadFXML("layouts/boruvka-visualizer.fxml"), 800, 600));
		primaryStage.show();
	}

	public static BorVizApplication getApplication() {
		return application;
	}

	public Stage getPrimaryStage() {
		return primaryStage;
	}
	public void setPrimaryStage(Stage primaryStage) {
		this.primaryStage = primaryStage;
	}
}