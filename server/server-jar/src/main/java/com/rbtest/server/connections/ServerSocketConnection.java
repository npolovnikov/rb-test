package com.rbtest.server.connections;

import com.rbtest.server.client.SocketClient;
import com.rbtest.server.config.Config;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by npolovnikov on 28.09.17.
 */
public class ServerSocketConnection implements ServerConnection {
    private ServerSocket serverSocket;

    public ServerSocketConnection() throws IOException {
        serverSocket = new ServerSocket(Config.PORT);
    }

    public SocketClient findNewClient() {
        Socket client = null;

        try {
            client = serverSocket.accept();
        } catch (IOException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
        return client == null ? null : new SocketClient(client);
    }
}
