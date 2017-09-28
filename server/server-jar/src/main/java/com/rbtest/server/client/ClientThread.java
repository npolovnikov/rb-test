package com.rbtest.server.client;

import com.rbtest.common.Auth;
import com.rbtest.common.Message;
import com.rbtest.common.Ping;
import com.rbtest.server.config.Config;

import javax.swing.*;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;

import static com.rbtest.server.main.Server.getChatHistory;
import static com.rbtest.server.main.Server.getUserList;


/**
 * Created by nikita on 26.09.2017.
 */
public class ClientThread extends Thread {
    private final static int DELAY = 30000;

    private String login;
    private int inPacks = 0;
    private int outPacks = 0;
    private boolean flag = false;
    private Timer timer;
    private Socket socket;

    public ClientThread(Socket socket) {
        this.socket = socket;
        this.start();
    }

    @Override
    public void run() {
        try {
            //Создаем потоки ввода-вывода для работы с сокетом
            final ObjectInputStream inputStream = new ObjectInputStream(this.socket.getInputStream());
            final ObjectOutputStream outputStream = new ObjectOutputStream(this.socket.getOutputStream());

            //Читаем com.rbtest.common.Message из потока
            Message c = (Message) inputStream.readObject();

            //Читаем логин отправителя
            this.login = c.getLogin();

            //Что же нам прислали?
            if (c instanceof Auth) {
                getUserList().addUser(login, socket, outputStream, inputStream);
                getChatHistory().getHistory().forEach(message -> {
                    try {
                        outputStream.writeObject(message);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
                this.broadcast(getUserList().getClientsList(), new Message("Server-Bot", "The user " + login + " has been connect")); //И сообщаем всем клиентам, что подключился новый пользователь
            } else {
                System.out.println("[" + c.getLogin() + "]: " + c.getMessage());
                getChatHistory().addMessage(c);
            }
            //Добавляем к списку пользователей - нового


            //Для ответа, указываем список доступных пользователей
            c.setAddress(getUserList().getUsers());

            //Передаем всем сообщение пользователя
            this.broadcast(getUserList().getClientsList(), c);

            //Запускаем таймер
            this.timer = new Timer(DELAY, e -> {
                try { //Если количество входящих пакетов от клиента рано исходящему, значит клиент еще не в ауте
                    if (inPacks == outPacks) {
                        outputStream.writeObject(new Ping());
                        outPacks++;
                        System.out.println(outPacks + " out");
                    } else { //Иначе, в ауте
                        throw new SocketException();
                    }
                } catch (SocketException ex1) {
                    System.out.println("packages not clash");
                    System.out.println(login + " disconnected!");
                    //Удаляем клиента из списка доступных и информируем всех
                    getUserList().deleteUser(login);
                    broadcast(getUserList().getClientsList(), new Message("Server-Bot", getUserList().getUsers(),"The user " + login + " has been disconnect"));
                    flag = true;
                    timer.stop();
                } catch (IOException ex2) {
                    ex2.printStackTrace();
                }
            });

            this.timer.start();

            //Начинаем пинговать клиента
            outputStream.writeObject(new Ping());
            this.outPacks++;
            System.out.println(outPacks + " out");

            //А теперь нам остается только ждать от него сообщений
            while (true) {
                //Как только пинг пропал - заканчиваем
                if (this.flag) {
                    this.flag = false;
                    break;
                }
                //Принимаем сообщение
                c = (Message) inputStream.readObject();

                //Если это ping
                if (c instanceof Ping) {
                    this.inPacks++;
                    System.out.println(this.inPacks + " in");
                } else if (!c.getMessage().equals(Config.HELLO_MESSAGE)) {
                    System.out.println("[" + login + "]: " + c.getMessage());
                    getChatHistory().addMessage(c);
                } else {
                    outputStream.writeObject(getChatHistory());
                    this.broadcast(getUserList().getClientsList(), new Message("Server-Bot", "The user " + login + " has been connect"));
                }

                c.setAddress((getUserList().getUsers()));

                if (!(c instanceof Ping) && !c.getMessage().equals(Config.HELLO_MESSAGE)) {
                    System.out.println("Send broadcast com.rbtest.common.Message: " + c.getMessage());
                    this.broadcast(getUserList().getClientsList(), c);
                }
            }

        } catch (SocketException e) {
            System.out.println(login + " disconnected!");
            this.broadcast(getUserList().getClientsList(), new Message("Server-Bot",  getUserList().getUsers(), "The user " + login + " has been disconnect"));
            this.timer.stop();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void broadcast(ArrayList<Client> clientsArrayList, Message message) {
        try {
            for (Client client : clientsArrayList) {
                client.getThisObjectOutputStream().writeObject(message);
            }
        } catch (SocketException e) {
            System.out.println("in broadcast: " + login + " disconnected!");
            getUserList().deleteUser(login);
            this.broadcast(getUserList().getClientsList(), new Message("System", getUserList().getUsers(), "The user " + login + " has been disconnected"));
            timer.stop();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
