package com.rbtest.client.connections;

import com.rbtest.client.config.Config;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

/**
 * Created by npolovnikov on 28.09.17.
 */
public class SocketConnectionImpl implements Connection {
    private Socket socket;

    public SocketConnectionImpl() {
        try {
            socket = new Socket(Config.HOST, Config.PORT);
//        System.out.println("Start connection to " + socket);
        } catch (IOException e) {
            System.err.println(e.getMessage());
//            e.printStackTrace();
            System.exit(1);
        }
    }

    @Override
    public ObjectOutputStream getOutputStream() {
        try {
            return new ObjectOutputStream(socket.getOutputStream());
        } catch (IOException e) {
            System.err.println(e.getMessage());
//            e.printStackTrace();
            System.exit(1);
        }
        return null;
    }

    @Override
    public ObjectInputStream getInputStream() {
        try {
            return new ObjectInputStream(socket.getInputStream());
        } catch (IOException e) {
            System.err.println(e.getMessage());
//            e.printStackTrace();
            System.exit(1);
        }
        return null;
    }

    @Override
    public void close() {
        try {
            socket.close();
        } catch (IOException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }

    }

}
