package com.leti.summer_practice;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;

import java.io.IOException;
import java.net.URL;
import java.util.Locale;
import java.util.ResourceBundle;

public final class R {

    private static ResourceBundle strings = ResourceBundle.getBundle("com/leti/summer_practice/strings");

    private R() {
        throw new Error("Forbidden instance of " + R.class.getName() + "!");
    }

    public static ResourceBundle getResourceBundle() {
        return strings;
    }

    public static Locale getLocale() {
        return strings.getLocale();
    }

    public static void setLocale(Locale locale) {
        strings = ResourceBundle.getBundle("com/leti/summer_practice/strings", locale);
    }

    public static String string(String key) {
        return strings.getString(key);
    }

    public static URL resource(String name) {
        return R.class.getResource(name);
    }

    public static FXMLLoader fxmlLoader(URL url) {
        return new FXMLLoader(url, getResourceBundle());
    }

    public static FXMLLoader fxmlLoader(String resourceName) {
        return fxmlLoader(resource(resourceName));
    }

    public static Parent load(String resourceName) throws IOException {
        return fxmlLoader(resourceName).load();
    }
}