package com.rbtest.server.connections;

import com.rbtest.server.client.Client;

/**
 * Created by npolovnikov on 29.09.17.
 */
public interface ServerConnection {
    void close();

    Client getClient();
}
