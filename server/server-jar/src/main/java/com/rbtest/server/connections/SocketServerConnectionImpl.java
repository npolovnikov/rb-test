package com.rbtest.server.connections;

import com.rbtest.common.Config;
import com.rbtest.server.client.Client;
import com.rbtest.server.client.SocketClientImpl;

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

    public SocketServerConnectionImpl() throws IOException {
        serverSocket = new ServerSocket(Integer.parseInt(Config.getProperty(Config.PORT, "1111")));
    }

    @Override
    public void close() throws IOException {
        serverSocket.close();
    }

    @Override
    public Client getNextClient() throws IOException {
        final Socket socket = serverSocket.accept();
        System.out.println(socket);
        return socket == null ? null : new SocketClientImpl(new ObjectOutputStream(socket.getOutputStream()), new ObjectInputStream(socket.getInputStream()));
    }
}
