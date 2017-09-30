package com.rbtest.server.connections;

import com.rbtest.server.client.Client;
import com.rbtest.server.client.SocketClientImpl;
import com.rbtest.server.config.Config;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by npolovnikov on 29.09.17.
 */
public class SocketServerConnectionImpl implements ServerConnection {
    private ServerSocket serverSocket;

    public SocketServerConnectionImpl() {
        try {
            serverSocket = new ServerSocket(Config.PORT);
        } catch (IOException e) {
            System.err.println(e.getMessage());
//            e.printStackTrace();
//            System.exit(1);
        }
    }


    @Override
    public void close() {
        try {
            serverSocket.close();
        } catch (IOException e) {
            System.err.println(e.getMessage());
//            e.printStackTrace();
//            System.exit(1);
        }
    }

    @Override
    public Client getClient() {
        try {
            final Socket socket = serverSocket.accept();
            System.out.println(socket);
            return socket == null ? null : new SocketClientImpl(new ObjectOutputStream(socket.getOutputStream()), new ObjectInputStream(socket.getInputStream()));
        } catch (IOException e){
            System.err.println(e.getMessage());
//            e.printStackTrace();
//            System.exit(1);
        }
        return null;
    }
}
