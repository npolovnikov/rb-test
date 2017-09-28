package com.rbtest.client.main;

import com.rbtest.client.Connection;
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

    public Client() {
        System.out.println("Вас приветствует клиент чата!");
        try {
            connection = new SocketConnectionImpl();

            auth(connection);

            final ScheduledExecutorService reader = Executors.newSingleThreadScheduledExecutor();
            reader.scheduleWithFixedDelay(() -> reader(connection),1,1, TimeUnit.MILLISECONDS);

            final ScheduledExecutorService sender = Executors.newSingleThreadScheduledExecutor();
            sender.scheduleWithFixedDelay(() -> sender(connection), 1,1, TimeUnit.MILLISECONDS);

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

    private void auth(Connection connection) {
        final BufferedReader keyboard = new BufferedReader(new InputStreamReader(System.in));
        try {
            do {
                System.out.print("Введите ваше имя: ");
                userName = keyboard.readLine();
                new ObjectOutputStream(connection.getOutputStream()).writeObject(new Auth());
            } while (!((new ObjectInputStream(connection.getInputStream()).readObject()) instanceof Auth));
        } catch (Exception e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
    }

    private void reader(Connection connection) {
        try {
            final Message messageIn = (Message) new ObjectInputStream(connection.getInputStream()).readObject();
            if (messageIn != null) {
                if (messageIn instanceof Ping) {
                    new ObjectOutputStream(connection.getOutputStream()).writeObject(new Ping());
                } else {
                    System.out.println("[ " + messageIn.getTime().toString() + " ] " + messageIn.getLogin() + " : " + messageIn.getMessage());
                }
            }
        } catch (IOException | ClassNotFoundException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
    }

    private void sender(Connection connection) {
        final BufferedReader keyboard = new BufferedReader(new InputStreamReader(System.in));
        try {
            final String message = keyboard.readLine();
            new ObjectOutputStream(connection.getOutputStream()).writeObject(new Message(userName, message));
        } catch (IOException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
    }
}
