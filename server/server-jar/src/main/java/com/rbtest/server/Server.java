package com.rbtest.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;

public class Server {
    private static UsersList list = new UsersList();
    private static ChatHistory chatHistory = new ChatHistory();

    public static void main(String[] args) {
        Config.load();
        try {
            //Создаем слушатель
            ServerSocket socketListener = new ServerSocket(Config.PORT);
            System.out.println("socketListener was created" + socketListener);
            while (true) {
                Socket client = null;
                while (client == null) {
                    client = socketListener.accept();
                    Thread.sleep(100);
                }
                System.out.println("Socket client = " + client);
                ClientThread ct = new ClientThread(client); //Создаем новый поток, которому передаем сокет
                System.out.println("ClientThread = " + ct);
            }
        } catch (InterruptedException | IOException e) {
            System.err.println("Exception");
            e.printStackTrace();
        }
    }

    public synchronized static UsersList getUserList() {
        return list;
    }

    public synchronized static ChatHistory getChatHistory() {
        return chatHistory;
    }
}