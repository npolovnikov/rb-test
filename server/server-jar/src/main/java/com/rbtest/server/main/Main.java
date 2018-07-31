package com.rbtest.server.main;

import com.rbtest.server.connections.SocketServerConnectionImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * Created by npolovnikov on 28.09.17.
 */
public class Main {
    private final static Logger LOG = LoggerFactory.getLogger(Main.class);
    public static void main(String[] args) {
        try {
            new Server(new SocketServerConnectionImpl());
        } catch (IOException e) {
            LOG.error("{} : {}", e.getClass().getName() + ":" + e.getMessage(), e);
        }
    }
}
