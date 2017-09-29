package com.rbtest.client.connections;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * Created by npolovnikov on 28.09.17.
 */
public interface Connection {
    ObjectOutputStream getOutputStream();

    ObjectInputStream getInputStream();

    void close();
}
