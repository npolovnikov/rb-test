package com.rbtest.server;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

/**
 * Created by nikita on 26.09.2017.
 */
public class Config {
    private static final String PROPERTIES_FILE = "server.properties";

    public static int PORT;
    public static int HISTORY_LENGTH;
    public static String HELLO_MESSAGE;

    {
        Properties properties = new Properties();

        try {
            properties.load(getClass().getResourceAsStream(PROPERTIES_FILE));

            PORT = Integer.parseInt(properties.getProperty("PORT"));
            HISTORY_LENGTH = Integer.parseInt(properties.getProperty("HISTORY_LENGTH"));
            HELLO_MESSAGE = properties.getProperty("HELLO_MESSAGE");
        } catch (FileNotFoundException ex) {
            System.err.println("Properties config file not found");
        } catch (IOException ex) {
            System.err.println("Error while reading file");
        }
    }
}