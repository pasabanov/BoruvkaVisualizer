module com.borviz.boruvkavisualizer {

    requires javafx.controls;
    requires javafx.fxml;

    exports com.borviz.boruvkavisualizer.logic;

    opens com.borviz.boruvkavisualizer.gui.lib to javafx.fxml;
    exports com.borviz.boruvkavisualizer.gui.lib;
    opens com.borviz.boruvkavisualizer.gui.prog to javafx.fxml;
    exports com.borviz.boruvkavisualizer.gui.prog;
}