package com.rbtest.common;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Created by npolovnikov on 28.09.17.
 */
public class Config {
    private final static Logger LOG = LoggerFactory.getLogger(Config.class);
    public static String PORT = "PORT";
    public static String HOST = "HOST";
    public static String HISTORY_LENGTH = "HISTORY_LENGTH";
    public static String HELLO_MESSAGE = "HELLO_MESSAGE";

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
            LOG.debug("CONFIG: {}", CONFIG);
        } catch (IOException e) {
            LOG.error("{} : {}", e.getClass().getName(), e.getMessage(), e);
        }
    }
}
