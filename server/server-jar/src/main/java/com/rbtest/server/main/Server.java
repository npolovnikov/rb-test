package com.rbtest.server.main;

import com.rbtest.common.*;
import com.rbtest.server.UserNameAlreadyExistException;
import com.rbtest.server.client.Client;
import com.rbtest.server.client.WorkingClient;
import com.rbtest.server.connections.ServerConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class Server {
    private final static Logger LOG = LoggerFactory.getLogger(Server.class);
    private final List<Message> chatHistory  = new ArrayList<>(Integer.parseInt(Config.getProperty(Config.HISTORY_LENGTH, "100")));
    private final HashMap<String, Client> clients = new HashMap<>();
    private final ServerConnection serverConnection;
    private final ReadWriteLock clientsLock = new ReentrantReadWriteLock();
    private final ReadWriteLock historyLock = new ReentrantReadWriteLock();

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
                    LOG.error("{} : {}", e.getClass().getName() + ":" + e.getMessage(), e);
                }
            }
        },1, 1, TimeUnit.MILLISECONDS);
    }


    public void addMessageToHistory(Message msg) {
        if (getHistory().size() >= Integer.parseInt(Config.getProperty(Config.HISTORY_LENGTH, "100"))) {
            removeFirstHistory();
        }

        addHistory(msg);
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

    public List<Message> getHistory() {
        historyLock.readLock().lock();
        try {
            return chatHistory;
        } finally {
            historyLock.readLock().unlock();
        }
    }

    public void addHistory(Message message) {
        historyLock.writeLock().lock();
        try {
            chatHistory.add(message);
        } finally {
            historyLock.writeLock().unlock();
        }
    }

    public void removeFirstHistory() {
        historyLock.writeLock().lock();
        try {
            chatHistory.remove(0);
        } finally {
            historyLock.writeLock().unlock();
        }
    }

    public HashMap<String, Client> getClients(){
        clientsLock.readLock().lock();
        try {
            return clients;
        } finally {
            clientsLock.readLock().unlock();
        }
    }

    public void addClient(String login, Client client) throws UserNameAlreadyExistException {
        if (clients.containsKey(login)) {
            throw new UserNameAlreadyExistException(login);
        } else {
            clientsLock.writeLock().lock();
            try {
                if (clients.containsKey(login)) {
                    throw new UserNameAlreadyExistException(login);
                } else {
                    clients.put(login, client);
                }
            } finally {
                clientsLock.writeLock().unlock();
            }
        }
    }

    public void removeClient(String login) {
        clientsLock.writeLock().lock();
        try {
            clients.remove(login);
        } finally {
            clientsLock.writeLock().unlock();
        }
    }


    @Override
    public String toString() {
        return "Server{" +
                "serverConnection=" + serverConnection +
                '}';
    }
}