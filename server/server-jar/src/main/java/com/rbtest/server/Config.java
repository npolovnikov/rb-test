package com.rbtest.server;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

/**
 * Created by nikita on 26.09.2017.
 */
public class Config {
    public static int PORT;
    public static int HISTORY_LENGTH;
    public static String HELLO_MESSAGE;

    public static void load(){
        try {
            Properties properties = new Properties();

            properties.load(Config.class.getClassLoader().getResourceAsStream("server.properties"));
            PORT = Integer.parseInt(properties.getProperty("PORT"));
            HISTORY_LENGTH = Integer.parseInt(properties.getProperty("HISTORY_LENGTH"));
            HELLO_MESSAGE = properties.getProperty("HELLO_MESSAGE");

            System.out.println("properties was loaded");
            System.out.println("PORT="+PORT);
            System.out.println("HISTORY_LENGTH="+HISTORY_LENGTH);
            System.out.println("HELLO_MESSAGE="+HELLO_MESSAGE);
        } catch (FileNotFoundException ex) {
            System.err.println("Properties config file not found");
        } catch (IOException ex) {
            System.err.println("Error while reading file");
        }
    }
}