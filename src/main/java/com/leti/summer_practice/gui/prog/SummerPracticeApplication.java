package com.leti.summer_practice.gui.prog;

import com.leti.summer_practice.R;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.util.Locale;

public class SummerPracticeApplication extends Application {

    private static SummerPracticeApplication application;
    private Stage primaryStage;

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

    public static void main(String[] args) {
        int i = 1337;
        System.out.println(from09toAZ(i));
        System.out.println(fromAZto09(from09toAZ(i)));
        launch(args);
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