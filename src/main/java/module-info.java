module com.borviz.boruvkavisualizer {

    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires eu.hansolo.tilesfx;

    exports com.borviz.boruvkavisualizer.logic;

    opens com.borviz.boruvkavisualizer.gui.lib to javafx.fxml;
    exports com.borviz.boruvkavisualizer.gui.lib;
    opens com.borviz.boruvkavisualizer.gui.prog to javafx.fxml;
    exports com.borviz.boruvkavisualizer.gui.prog;
}