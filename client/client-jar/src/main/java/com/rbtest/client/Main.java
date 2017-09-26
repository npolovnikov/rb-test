package com.rbtest.client;

/**
 * Created by nikita on 26.09.2017.
 */
public class Main {
    private static Client client;

    public static void main(String[] args) {
        client = new Client();
        client.start();
    }
}
