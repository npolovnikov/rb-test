package com.rbtest.client.config;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

/**
 * Created by npolovnikov on 28.09.17.
 */
public class Config {
    public static int PORT;
    public static String HOST;

    public static void load(){
        try {
            Properties properties = new Properties();

            properties.load(Config.class.getClassLoader().getResourceAsStream("client.properties"));
            PORT = Integer.parseInt(properties.getProperty("PORT"));
            HOST = properties.getProperty("HOST");
        } catch (FileNotFoundException ex) {
            System.err.println("Properties config file not found");
        } catch (IOException ex) {
            System.err.println("Error while reading file");
        }
    }
}
