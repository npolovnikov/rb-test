package com.rbtest.server.connections;

import com.rbtest.server.client.Client;

import java.io.IOException;

/**
 * Created by npolovnikov on 29.09.17.
 */
public interface ServerConnection {
    void close() throws IOException;

    Client getNextClient() throws IOException;
}
