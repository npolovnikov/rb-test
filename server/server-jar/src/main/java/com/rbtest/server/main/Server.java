package com.rbtest.server.main;

import com.rbtest.common.*;
import com.rbtest.server.client.Client;
import com.rbtest.server.client.WorkingClient;
import com.rbtest.server.connections.ServerConnection;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Server {

    private final List<Message> chatHistory  = new ArrayList<>(Integer.parseInt(Config.getProperty(Config.HISTORY_LENGTH, "100")));
    private final HashMap<String, Client> clients = new HashMap<>();
    private final ServerConnection serverConnection;

    public Server(ServerConnection serverConnection) {
        this.serverConnection = serverConnection;
        final ScheduledExecutorService finder = Executors.newSingleThreadScheduledExecutor();
        finder.scheduleAtFixedRate(() -> {
            Client client = null;
            while (client == null) {
                try {
                    client = serverConnection.getNextClient();
                    new WorkingClient(client, this);
                } catch (IOException e) {
                    System.err.println(e.getClass().getName() + ":" + e.getMessage());
                }
            }
        },1, 1, TimeUnit.MILLISECONDS);
    }


    public void addMessageToHistory(Message msg) {
        if (getHistory().size() >= Integer.parseInt(Config.getProperty(Config.HISTORY_LENGTH, "100"))) {
            getHistory().remove(0);
        }

        getHistory().add(msg);
    }


    public void broadcast(Message msg) throws IOException {
        addMessageToHistory(msg);
        for (Client client: getClients().values()){
            sendMessage(client, msg);
        }
    }

    public void sendMessage(Client client, Message message) throws IOException {
        client.getOutputStream().writeObject(message);
    }

    public List<Message> getHistory(){
        synchronized (chatHistory) {
            return chatHistory;
        }
    }

    public HashMap<String, Client> getClients(){
        synchronized (clients) {
            return clients;
        }
    }

    @Override
    public String toString() {
        return "Server{" +
                "serverConnection=" + serverConnection +
                '}';
    }
}