package com.rbtest.common;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Created by npolovnikov on 28.09.17.
 */
public class Config {
    public static String PORT = "PORT";
    public static String HOST = "HOST";
    public static String HISTORY_LENGTH = "HISTORY_LENGTH";
    public static String HELLO_MESSAGE = "Hello!";

    private final static Properties CONFIG = new Properties();

    public static String getProperty(String key, String defaultValue) {
        if (CONFIG.isEmpty()) {
            load();
        }
        return CONFIG.getProperty(key, defaultValue);
    }

    public static String getProperty(String key) {
        if (CONFIG.isEmpty()) {
            load();
        }
        return CONFIG.getProperty(key);
    }

    private static void load() {
        try (InputStream is = Config.class.getClassLoader().getResourceAsStream("global.properties")) {
            CONFIG.load(is);
            System.out.println(CONFIG);
        } catch (FileNotFoundException ex) {
            System.err.println("Properties config file not found");
        } catch (IOException ex) {
            System.err.println("Error while reading file");
        }
    }
}
