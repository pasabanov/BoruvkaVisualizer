package com.leti.summer_practice;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;

import java.io.IOException;
import java.net.URL;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

/**
 * A class for accessing resources.
 * It has the main strings bundle inside, that contains all strings, that are used in application.
 */
public final class R {

    private static final String BUNDLES_PATH = "com/leti/summer_practice/";
    private static final String MAIN_STRINGS_BUNDLE_NAME = "strings";
    private static final String MAIN_STRINGS_BUNDLE_PATH = BUNDLES_PATH + MAIN_STRINGS_BUNDLE_NAME;

    private static final String DEFAULT_NOT_FOUND_STRING = "%string_not_found%";


    private static ResourceBundle mainStringsBundle = ResourceBundle.getBundle(MAIN_STRINGS_BUNDLE_PATH);


    /**
     * Class can not be instantiated.
     */
    private R() {
        throw new Error("Forbidden instance of " + R.class.getName() + "!");
    }


    /**
     * @return main strings bundle.
     */
    public static ResourceBundle getMainStringsBundle() {
        return mainStringsBundle;
    }

    /**
     * Get current Locale of the main strings bundle.
     *
     * @return locale of the bundle.
     */
    public static Locale getLocale() {
        return mainStringsBundle.getLocale();
    }

    /**
     * Sets Locale for the main strings bundle.
     * (Creates new bundle with specified locale)
     *
     * @param locale - required locale
     */
    public static void setLocale(Locale locale) {
        mainStringsBundle = ResourceBundle.getBundle(MAIN_STRINGS_BUNDLE_PATH, locale);
    }

    /**
     * Refreshes main strings bundle: re-uploads bundle with current locale.
     */
    public static void refresh() {
        setLocale(getLocale());
    }


    /**
     * Get string from main strings bundle.
     *
     * @param key - name (id) of the required string.
     * @return required string from bundle.
     */
    public static String string(String key) {
        try {
            return mainStringsBundle.getString(key);
        } catch (NullPointerException | MissingResourceException | ClassCastException e) {
            e.printStackTrace();
            return DEFAULT_NOT_FOUND_STRING;
        }
    }

    /**
     * Get URL to resource by name.
     *
     * @param name - name of resource.
     * @return URL to resource.
     */
    public static URL resource(String name) {
        return R.class.getResource(name);
    }


    /**
     * Get FXMLLoader instance from a string or url with (or with not) specified bundle.
     *
     * @param url (string) - name of the .fxml file.
     * @return FXMLLoader instance.
     */
    public static FXMLLoader fxmlLoader(URL url) {
        return fxmlLoader(url, getMainStringsBundle());
    }
    public static FXMLLoader fxmlLoader(URL url, ResourceBundle resourceBundle) {
        return new FXMLLoader(url, resourceBundle);
    }
    public static FXMLLoader fxmlLoader(String resourceName) {
        return fxmlLoader(resourceName, getMainStringsBundle());
    }
    public static FXMLLoader fxmlLoader(String resourceName, ResourceBundle resourceBundle) {
        return fxmlLoader(resource(resourceName), resourceBundle);
    }


    /**
     * Load a Parent (View) from an .fxml file by a url or filename with (or with not) specified bundle).
     *
     * @param url (string) - name of the .fxml file.
     * @return a Parent instance.
     * @throws IOException if parsing was unsuccessful.
     */
    public static Parent loadFXML(URL url) throws IOException {
        return loadFXML(url, getMainStringsBundle());
    }
    public static Parent loadFXML(URL url, ResourceBundle resourceBundle) throws IOException {
        return fxmlLoader(url, resourceBundle).load();
    }
    public static Parent loadFXML(String resourceName) throws IOException {
        return loadFXML(resourceName, getMainStringsBundle());
    }
    public static Parent loadFXML(String resourceName, ResourceBundle resourceBundle) throws IOException {
        return fxmlLoader(resourceName, resourceBundle).load();
    }
}