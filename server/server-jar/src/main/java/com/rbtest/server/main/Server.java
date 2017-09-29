package com.rbtest.server.main;

import com.rbtest.common.Auth;
import com.rbtest.common.Message;
import com.rbtest.common.Ping;
import com.rbtest.server.client.Client;
import com.rbtest.server.connections.ServerConnection;
import org.ietf.jgss.Oid;

import java.io.IOException;
import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Server {

    private HashMap<String, Client> clients;
    private ServerConnection serverConnection;

    public Server(ServerConnection serverConnection) {
        this.serverConnection = serverConnection;
        clients = new HashMap<>();

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
        reader.scheduleAtFixedRate(() -> readMessage(client),1, 1, TimeUnit.MILLISECONDS);
    }

    private void sendPing(Client client) {
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

    private void readMessage(final Client client) {
        try {
            System.out.println("start read message");
            Message msg = (Message) client.getInputStream().readObject();
            System.out.println("new Message " + msg);
            if (msg instanceof Auth) {
                registerClient(client, (Auth) msg);
                broadcast(new Message("SYSTEM", "Поприветствуйте нового пользователя" + msg.getLogin() + "!"));
            } else if (msg instanceof Ping) {
                client.addPingIn();
            } else {
                broadcast(msg);
            }
        } catch (IOException | ClassNotFoundException e) {
            System.err.println(e.getMessage());
//            e.printStackTrace();
            System.exit(1);
        }
    }

    private void broadcast(Message msg) {
        for (Client client: clients.values()){
            sendMessage(client, msg);
        }
    }

    private void sendMessage(final Client client, Message message) {
        try {
            client.getOutputStream().writeObject(message);
        } catch (IOException e) {
            System.err.println(e.getMessage());
//            e.printStackTrace();
            System.exit(1);
        }
    }

    private void registerClient(Client client, Auth msg) {
        if (!clients.containsKey(msg.getLogin())) {
            clients.put(msg.getLogin(), client);
            client.setClientLogin(msg.getLogin());
            sendMessage(client, new Auth("Successful"));

            //Начинаем пинговать
            final ScheduledExecutorService pinger = Executors.newSingleThreadScheduledExecutor();
            pinger.scheduleAtFixedRate(() -> sendPing(client),1, 10, TimeUnit.SECONDS);
        } else {
            sendMessage(client, new Auth("Error"));
        }
    }
}