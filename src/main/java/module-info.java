module com.leti.summer_practice {

    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires eu.hansolo.tilesfx;

    exports com.leti.summer_practice.logic;

    opens com.leti.summer_practice.gui.lib to javafx.fxml;
    exports com.leti.summer_practice.gui.lib;
    opens com.leti.summer_practice.gui.prog to javafx.fxml;
    exports com.leti.summer_practice.gui.prog;
}