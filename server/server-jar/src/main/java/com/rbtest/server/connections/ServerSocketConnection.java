package com.rbtest.server.connections;

import com.rbtest.server.client.ClientThread;
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

    @Override
    public void findNewClient() {
        try {
            while (true) {
                Socket client = null;
                while (client == null) {
                    client = serverSocket.accept();
                    Thread.sleep(100);
                }
//                System.out.println("Socket client = " + client);
                ClientThread ct = new ClientThread(client); //Создаем новый поток, которому передаем сокет
//                System.out.println("ClientThread = " + ct);
            }
        } catch (Exception e){
            System.err.println(e.getMessage());
            System.exit(1);
        }
    }
}
