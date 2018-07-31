package com.rbtest.client.main;

import com.rbtest.client.connections.Connection;
import com.rbtest.common.Auth;
import com.rbtest.common.Message;
import com.rbtest.common.Ping;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Created by npolovnikov on 27.09.17.
 */
public class Client {
    private final static Logger LOG = LoggerFactory.getLogger(Client.class);
    private String userName;
    private Connection connection;
    private ObjectOutputStream objectOutputStream;
    private ObjectInputStream objectInputStream;
    private BufferedReader keyboard;

    public Client(Connection connection) throws IOException, ClassNotFoundException, InterruptedException {
        System.out.println("Вас приветствует клиент чата!");
        try {
            this.connection = connection;
            objectOutputStream = this.connection.getOutputStream();
            objectInputStream = this.connection.getInputStream();
            keyboard = new BufferedReader(new InputStreamReader(System.in));

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
        } finally {
            if (this.connection != null) {
                this.connection.close();
            }

            if (this.keyboard != null) {
                keyboard.close();
            }

            LOG.debug("User: {} quit from chat", userName);
        }
    }

    private void auth() throws IOException, ClassNotFoundException {
        Auth auth;
        do {
            System.out.print("Введите ваше имя: ");
            userName = keyboard.readLine();
            objectOutputStream.writeObject(new Auth(userName));
            auth = (Auth) objectInputStream.readObject();
        } while (!auth.getLogin().equals(Auth.Status.SUCCESS));
        System.out.println("Вы удачно авторизовались");
    }

    private void reader() {
        try {
            final Message messageIn = (Message) objectInputStream.readObject();
            if (messageIn != null) {
                if (messageIn instanceof Ping) {
                    objectOutputStream.writeObject(new Ping(userName));
                } else {
                    System.out.println(messageIn.parseMessage());
                }
            }
        } catch (IOException | ClassNotFoundException e) {
            LOG.error("{} : {}", e.getClass().getName(), e.getMessage(), e);
            System.exit(1);
        }
    }

    private void sender() {
        try {
            final String message = keyboard.readLine();
            objectOutputStream.writeObject(new Message(userName, message));
        } catch (IOException e) {
            LOG.error("{} : {}", e.getClass().getName(), e.getMessage(), e);
            System.exit(1);
        }
    }
}
