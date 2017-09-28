package com.rbtest.client.main;

import com.rbtest.client.connections.Connection;
import com.rbtest.client.connections.SocketConnectionImpl;
import com.rbtest.common.Auth;
import com.rbtest.common.Message;
import com.rbtest.common.Ping;

import java.io.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Created by npolovnikov on 27.09.17.
 */
public class Client {
    private static String userName;
    private Connection connection;
    private ObjectOutputStream objectOutputStream;
    private ObjectInputStream objectInputStream;

    public Client() {
        System.out.println("Вас приветствует клиент чата!");
        try {
            connection = new SocketConnectionImpl();
            objectOutputStream = connection.getOutputStream();
            objectInputStream = connection.getInputStream();

            auth();

            //Поток принимает сообщения от сервера
            final ScheduledExecutorService reader = Executors.newSingleThreadScheduledExecutor();
            reader.scheduleWithFixedDelay(this::reader,1,1, TimeUnit.MILLISECONDS);

            //Поток отправляет сообщения на сервер
            final ScheduledExecutorService sender = Executors.newSingleThreadScheduledExecutor();
            sender.scheduleWithFixedDelay(this::sender, 1,1, TimeUnit.MILLISECONDS);

            while (!reader.isShutdown() || !sender.isShutdown()){
                //Работаем))
                Thread.sleep(100);
            }
        } catch (IOException | InterruptedException e){
            System.err.println(e.getMessage());
            System.exit(1);
        } finally {
            if (connection != null) {
                connection.close();
                System.out.println("Connection close!");
            }
        }
    }

    private void auth() {
        final BufferedReader keyboard = new BufferedReader(new InputStreamReader(System.in));
        try {
            do {
                System.out.print("Введите ваше имя: ");
                userName = keyboard.readLine();
                objectOutputStream.writeObject(new Auth(userName));
            } while (!(objectInputStream.readObject() instanceof Auth));
            System.out.println("вышел из как хотелось бы из вечного");
        } catch (Exception e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
    }

    private void reader() {
        try {
            final Message messageIn = (Message) objectInputStream.readObject();
            if (messageIn != null) {
                if (messageIn instanceof Ping) {
                    objectOutputStream.writeObject(new Ping());
                } else {
                    System.out.println("[ " + messageIn.getTime().toString() + " ] " + messageIn.getLogin() + " : " + messageIn.getMessage());
                }
            }
        } catch (IOException | ClassNotFoundException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
    }

    private void sender() {
        final BufferedReader keyboard = new BufferedReader(new InputStreamReader(System.in));
        try {
            final String message = keyboard.readLine();
            objectOutputStream.writeObject(new Message(userName, message));
        } catch (IOException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
    }
}
