package com.rbtest.server.client;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * Created by npolovnikov on 29.09.17.
 */
public interface Client {
    ObjectInputStream getInputStream();
    ObjectOutputStream getOutputStream();

    int getPingOut();
    int getPingIn();
    void addPingOut();
    void addPingIn();

    String getClientLogin();
    void setClientLogin(String login);
}
