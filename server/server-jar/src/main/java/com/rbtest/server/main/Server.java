package com.rbtest.server.main;

import com.rbtest.common.*;
import com.rbtest.server.client.Client;
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

    private final List<Message> chatHistory  = new ArrayList<>(Integer.parseInt(Config.getProperty(Config.HISTORY_LENGTH, "100")));
    private final HashMap<String, Client> clients = new HashMap<>();

    public Server(ServerConnection serverConnection) {
        final ScheduledExecutorService finder = Executors.newSingleThreadScheduledExecutor();
        finder.scheduleAtFixedRate(() -> {
            Client client = null;
            while (client == null) {
                try {
                    client = serverConnection.getNextClient();
                } catch (IOException e) {
                    System.err.println(e.getClass().getName() + ":" + e.getMessage());
                }
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
                getClients().remove(client.getClientLogin());
                System.err.println(client.getClientLogin() + " : " + e.getMessage());
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
            getClients().remove(client.getClientLogin());
            broadcast(new Message("SYSTEM", "Пользовотель: " + client.getClientLogin() + " отключился"));
        } else {
            sendMessage(client, new Ping());
            client.addPingOut();
        }

    }

    private void readMessage(final Client client) throws IOException, ClassNotFoundException {
        Message msg = (Message) client.getInputStream().readObject();
        System.out.println("[ " + msg.getTime().toString() + " ] " + msg.getLogin() + " : " + msg.getMessage());
        if (msg instanceof Auth) {
                if (registerClient(client, (Auth) msg)) {
                    getHistory().forEach(message -> {
                        try {
                            sendMessage(client, message);
                        } catch (IOException e) {
                            getClients().remove(client.getClientLogin());
                            System.err.println(client.getClientLogin() + " : " + e.getMessage());
                            try {
                                broadcast(new Message("SYSTEM", "Пользовотель: " + client.getClientLogin() + " отключился"));
                            } catch (IOException ignored) {
                            }
                        }
                    });
                    broadcast(new Message("SYSTEM", "Поприветствуйте нового пользователя: " + msg.getLogin() + "!"));
                }
            } else if (msg instanceof Ping) {
                client.addPingIn();
            } else {
                if (msg.getMessage().equals(CommandType.help.getName())){
                    sendMessage(client, new Message("SYSTEM", Arrays.toString(CommandType.values())));
                } else if(msg.getMessage().equals(CommandType.userList.getName())) {
                    sendMessage(client, new Message("SYSTEM", getClients().keySet().toString()));
                } else {
                    broadcast(msg);
                }
            }
    }

    private void broadcast(Message msg) throws IOException {
        addMessageToHistory(msg);
        for (Client client: getClients().values()){
            sendMessage(client, msg);
        }
    }

    private void addMessageToHistory(Message msg) {
        if (getHistory().size() >= Integer.parseInt(Config.getProperty(Config.HISTORY_LENGTH, "100"))) {
            getHistory().remove(0);
        }

        getHistory().add(msg);
    }

    private void sendMessage(final Client client, Message message) throws IOException {
        client.getOutputStream().writeObject(message);
    }

    private boolean registerClient(Client client, Auth msg) throws IOException {
        if (!getClients().containsKey(msg.getLogin())) {
            getClients().put(msg.getLogin(), client);
            client.setClientLogin(msg.getLogin());
            sendMessage(client, new Auth(Auth.Status.SUCCESS));

            //Начинаем пинговать
            final ScheduledExecutorService pinger = Executors.newSingleThreadScheduledExecutor();
            pinger.scheduleAtFixedRate(() -> {
                try {
                    sendPing(client);
                } catch (IOException e) {
                    pinger.shutdown();
                }
            },1, 10, TimeUnit.SECONDS);
            return true;
        } else {
            sendMessage(client, new Auth(Auth.Status.ERROR));
            return false;
        }
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
}