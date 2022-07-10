package com.leti.summer_practice.gui.lib;

import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.util.StringConverter;

import java.util.Locale;
import java.util.function.UnaryOperator;
import java.util.regex.Pattern;


public class DoubleNumberTextField extends TextField {


    private static final Pattern validEditingState = Pattern.compile("(([1-9]\\d*)|0)?(\\.\\d*)?");

    private static final UnaryOperator<TextFormatter.Change> filter = c -> {
        String newText = c.getControlNewText();
        if (validEditingState.matcher(newText).matches())
            return c;
        else
            return null;
    };

    private static class Converter extends StringConverter<Double> {
        int precision = 100;
        public Converter() {
        }
        public Converter(int precision) {
            this.precision = precision;
        }
        @Override
        public String toString(Double d) {
            return String.format(new Locale("en_EN"), "%." + precision + "f", d).replaceAll("([.,]\\d+?)0*$", "$1");
        }
        @Override
        public Double fromString(String s) {
            if (s.isEmpty() || s.equals("-") || s.equals(".") || s.equals("-.") || s.equals(",") || s.equals("-,"))
                return 0.0;
            else
                return Double.valueOf(s);
        }
    }


    public DoubleNumberTextField() {
        this("");
    }

    public DoubleNumberTextField(String text) {
        super(text);

        makeTextFieldAcceptOnlyDoubleNumbers(this);
    }


    public static void makeTextFieldAcceptOnlyDoubleNumbers(TextField textField) {
        textField.setTextFormatter(new TextFormatter<>(new Converter(), 1.0, filter));
    }

    public static void makeTextFieldAcceptOnlyDoubleNumbers(TextField textField, int precision) {
        if (precision < 0)
            throw new IllegalArgumentException();
        textField.setTextFormatter(new TextFormatter<>(new Converter(precision), 1.0, filter));
    }
}