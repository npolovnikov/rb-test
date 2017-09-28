package com.rbtest.server.main;

import com.rbtest.server.ChatHistory;
import com.rbtest.server.UsersList;
import com.rbtest.server.client.SocketClient;
import com.rbtest.server.client.SocketClientThread;
import com.rbtest.server.connections.ServerConnection;
import com.rbtest.server.connections.ServerSocketConnection;

import java.io.IOException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Server {
    private static UsersList usersList;
    private static ChatHistory chatHistory;

    public Server() {
        try {
            final ServerSocketConnection connection = new ServerSocketConnection();//
            usersList = new UsersList();
            chatHistory = new ChatHistory();

            final ScheduledExecutorService listener = Executors.newSingleThreadScheduledExecutor();

            listener.scheduleAtFixedRate(() -> {
                SocketClient client = connection.findNewClient();
                if (client != null) {
                    startWorkWithClient(client);
                }
            }, 1, 1, TimeUnit.MILLISECONDS);

            while (!listener.isShutdown()) {
                Thread.sleep(100);
            }
        } catch (InterruptedException | IOException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
    }

    private void startWorkWithClient(SocketClient client) {
        final ScheduledExecutorService listener = Executors.newSingleThreadScheduledExecutor();

        listener.scheduleAtFixedRate(() -> new SocketClientThread(client), 1, 1, TimeUnit.MILLISECONDS);
    }

    public synchronized static UsersList getUserList() {
        return usersList;
    }

    public synchronized static ChatHistory getChatHistory() {
        return chatHistory;
    }
}