package com.rbtest.server;

import com.rbtest.server.client.SocketClient;

import java.net.Socket;
import java.util.*;

/**
 * Created by nikita on 26.09.2017.
 */
public class UsersList {
    private Map<String, SocketClient> onlineUsers = new HashMap<>();

    public boolean addUser(String login, Socket socket) {
        if (!this.onlineUsers.containsKey(login)) {
            this.onlineUsers.put(login , new SocketClient(socket));
            return true;
        }
        return false;
    }

    public Map<String, SocketClient> getClientsList() {
        return onlineUsers;
    }

    public void deleteUser(String login) {
        onlineUsers.remove(login);
    }
}
