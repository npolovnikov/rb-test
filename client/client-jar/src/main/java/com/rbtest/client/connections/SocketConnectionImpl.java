package com.rbtest.client.connections;

import com.rbtest.client.Connection;
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

    public SocketConnectionImpl() throws IOException {
        socket = new Socket(Config.HOST, Config.PORT);
//        System.out.println("Start connection to " + socket);
    }

    @Override
    public ObjectOutputStream getOutputStream() throws IOException {
        return new ObjectOutputStream(socket.getOutputStream());
    }

    @Override
    public ObjectInputStream getInputStream() throws IOException {
        return new ObjectInputStream(socket.getInputStream());
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
