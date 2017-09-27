package com.rbtest.client;

import com.rbtest.common.Message;
import com.rbtest.common.Ping;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * Created by npolovnikov on 27.09.17.
 */
public class ServerListenerThread implements Runnable {
    private Thread thread = null;
    private ObjectOutputStream objectOutputStream = null;
    private ObjectInputStream objectInputStream = null;

    public ServerListenerThread(ObjectOutputStream objectOutputStream, ObjectInputStream objectInputStream) {
        this.objectOutputStream = objectOutputStream;
        this.objectInputStream = objectInputStream;

        thread = new Thread(this);
        thread.start();
    }

    @Override
    public void run() {
        try {
            while (true) {
                Message messageIn = (Message) objectInputStream.readObject();
                if (messageIn instanceof Ping) {
                    Ping ping = (Ping) messageIn;
                    objectOutputStream.writeObject(ping);
                } else {
                    System.out.println("[ " + messageIn.getTime().toString() + " ] " + messageIn.getLogin() + " : " + messageIn.getMessage());
                }
            }
        } catch (ClassNotFoundException | IOException e) {
            e.getMessage();
        }
    }
}
