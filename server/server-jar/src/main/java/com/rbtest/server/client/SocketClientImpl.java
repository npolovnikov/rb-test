package com.rbtest.server.client;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * Created by npolovnikov on 29.09.17.
 */
public class SocketClientImpl implements Client {

    private String login;
    private int pingIn;
    private int pingOut;
    private ObjectInputStream objectInputStream;
    private ObjectOutputStream objectOutputStream;

    public SocketClientImpl(ObjectOutputStream objectOutputStream, ObjectInputStream objectInputStream) {
        this.objectOutputStream = objectOutputStream;
        this.objectInputStream = objectInputStream;

    }

    @Override
    public ObjectInputStream getInputStream() {
        return objectInputStream;
    }

    @Override
    public ObjectOutputStream getOutputStream() {
        return objectOutputStream;
    }

    @Override
    public int getPingOut() {
        return pingOut;
    }

    @Override
    public int getPingIn() {
        return pingIn;
    }

    @Override
    public void addPingOut() {
        pingOut++;
    }

    @Override
    public void addPingIn() {
        pingIn++;
    }

    @Override
    public String getClientLogin() {
        return login;
    }

    @Override
    public void setClientLogin(String login) {
        this.login = login;
    }
}
