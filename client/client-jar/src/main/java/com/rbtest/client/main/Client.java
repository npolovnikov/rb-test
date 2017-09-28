package com.rbtest.client.main;

import com.rbtest.client.Connection;
import com.rbtest.client.connections.SocketConnectionImpl;
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
        System.out.print("Напишите свой ник: ");
        final BufferedReader keyboard = new BufferedReader(new InputStreamReader(System.in));
        try {
            userName = keyboard.readLine();

            connection = new SocketConnectionImpl();

            final ObjectOutputStream objectOutputStream = connection.getOutputStream();
            final ObjectInputStream objectInputStream = connection.getInputStream();;

            final ScheduledExecutorService reader = Executors.newSingleThreadScheduledExecutor();
            reader.scheduleWithFixedDelay(() -> reader(objectOutputStream, objectInputStream),1,1, TimeUnit.MILLISECONDS);

            final ScheduledExecutorService sender = Executors.newSingleThreadScheduledExecutor();
            sender.scheduleWithFixedDelay(() -> sender(keyboard, objectOutputStream), 1,1, TimeUnit.MILLISECONDS);

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

    private void reader(ObjectOutputStream objectOutputStream, ObjectInputStream objectInputStream) {
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

    private void sender(BufferedReader keyboard, ObjectOutputStream objectOutputStream) {
        try {
            final String message = keyboard.readLine();
            objectOutputStream.writeObject(new Message(userName, message));
        } catch (IOException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
    }
}
