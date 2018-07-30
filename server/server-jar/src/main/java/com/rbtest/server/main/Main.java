package com.rbtest.server.main;

import com.rbtest.server.connections.SocketServerConnectionImpl;

import java.io.IOException;

/**
 * Created by npolovnikov on 28.09.17.
 */
public class Main {
    public static void main(String[] args) {
        try {
            new Server(new SocketServerConnectionImpl());
        } catch (IOException e) {
            System.out.println(e.getClass().getName() + ":" + e.getMessage());
            System.exit(1);
        }
    }
}
