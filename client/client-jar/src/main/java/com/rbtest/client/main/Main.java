package com.rbtest.client.main;

import com.rbtest.client.connections.SocketConnectionImpl;

import java.io.IOException;

/**
 * Created by npolovnikov on 28.09.17.
 */
public class Main {
    public static void main(String[] args) {
        try {
            new Client(new SocketConnectionImpl());
        } catch (IOException e) {
            System.out.println(e.getClass().getName() + ":" + e.getMessage());
            System.exit(1);
        }
    }
}
