package com.rbtest.server.main;

import com.rbtest.common.Auth;
import com.rbtest.common.CommandType;
import com.rbtest.common.Message;
import com.rbtest.common.Ping;
import com.rbtest.server.client.Client;
import com.rbtest.server.config.Config;
import com.rbtest.server.connections.ServerConnection;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Server {

    private List<Message> chatHistory;
    private HashMap<String, Client> clients;

    public Server(ServerConnection serverConnection) {
        clients = new HashMap<>();
        chatHistory = new ArrayList<>(Config.HISTORY_LENGTH);

        final ScheduledExecutorService finder = Executors.newSingleThreadScheduledExecutor();
        finder.scheduleAtFixedRate(() -> {
            Client client = null;
            while (client == null) {
                client = serverConnection.getClient();
            }
            System.out.println("Founded new client " + client);
            final ExecutorService clientThread = Executors.newSingleThreadExecutor();
            Client finalClient = client;
            clientThread.execute(() -> workingClient(finalClient));
        },1, 1, TimeUnit.MILLISECONDS);
    }

    private void workingClient(final Client client) {
        System.out.println("start working with client " + client);

        final ScheduledExecutorService reader = Executors.newSingleThreadScheduledExecutor();
        reader.scheduleAtFixedRate(() -> {
            try {
                readMessage(client);
            } catch (IOException | ClassNotFoundException e) {
                clients.remove(client.getClientLogin());
                System.err.println(e.getMessage());
                try {
                    broadcast(new Message("SYSTEM", "Пользовотель: " + client.getClientLogin() + " отключился"));
                } catch (IOException e1) {
                    reader.shutdown();
                }
                reader.shutdown();
            }
        },1, 1, TimeUnit.MILLISECONDS);
    }

    private void sendPing(Client client) throws IOException {
        int out = client.getPingOut();
        int in = client.getPingIn();

        if (out > in){
            clients.remove(client.getClientLogin());
            broadcast(new Message("SYSTEM", "Пользовотель: " + client.getClientLogin() + " отключился"));
        } else {
            sendMessage(client, new Ping());
            client.addPingOut();
        }

    }

    private void readMessage(final Client client) throws IOException, ClassNotFoundException {
            Message msg = (Message) client.getInputStream().readObject();
            System.out.println("new Message " + msg);
            if (msg instanceof Auth) {
                registerClient(client, (Auth) msg);
                chatHistory.forEach(message -> {
                    try {
                        sendMessage(client, message);
                    } catch (IOException e) {
                        clients.remove(client.getClientLogin());
                        System.err.println(e.getMessage());
                        try {
                            broadcast(new Message("SYSTEM", "Пользовотель: " + client.getClientLogin() + " отключился"));
                        } catch (IOException ignored) {}
                    }
                });
                broadcast(new Message("SYSTEM", "Поприветствуйте нового пользователя: " + msg.getLogin() + "!"));
            } else if (msg instanceof Ping) {
                client.addPingIn();
            } else {
                if (msg.getMessage().equals(CommandType.help.getName())){
                    sendMessage(client, new Message("SYSTEM", Arrays.toString(CommandType.values())));
                } else if(msg.getMessage().equals(CommandType.userList.getName())) {
                    sendMessage(client, new Message("SYSTEM", clients.keySet().toString()));
                } else {
                    broadcast(msg);
                }
            }
    }

    private void broadcast(Message msg) throws IOException {
        addMessageToHistory(msg);
        for (Client client: clients.values()){
            sendMessage(client, msg);
        }
    }

    private void addMessageToHistory(Message msg) {
        if (chatHistory.size() > Config.HISTORY_LENGTH){
            chatHistory.remove(0);
        }

        chatHistory.add(msg);
    }

    private void sendMessage(final Client client, Message message) throws IOException {
        client.getOutputStream().writeObject(message);
    }

    private void registerClient(Client client, Auth msg) throws IOException {
        if (!clients.containsKey(msg.getLogin())) {
            clients.put(msg.getLogin(), client);
            client.setClientLogin(msg.getLogin());
            sendMessage(client, new Auth("Successful"));

            //Начинаем пинговать
            final ScheduledExecutorService pinger = Executors.newSingleThreadScheduledExecutor();
            pinger.scheduleAtFixedRate(() -> {
                try {
                    sendPing(client);
                } catch (IOException e) {
                    pinger.shutdown();
                }
            },1, 10, TimeUnit.SECONDS);
        } else {
            sendMessage(client, new Auth("Error"));
        }
    }
}