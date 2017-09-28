package com.rbtest.server.client;

import com.rbtest.common.Auth;
import com.rbtest.common.Message;
import com.rbtest.common.Ping;
import com.rbtest.server.main.Server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Created by npolovnikov on 28.09.17.
 */
public class SocketClientThread {
    private SocketClient client;
    private ObjectInputStream objectInputStream;
    private ObjectOutputStream objectOutputStream;

    public SocketClientThread(SocketClient client) {
        try {
            this.client = client;
            objectInputStream = new ObjectInputStream(client.getSocket().getInputStream());
            objectOutputStream = new ObjectOutputStream(client.getSocket().getOutputStream());

            //Поток принимает сообщения от клиента
            final ScheduledExecutorService reader = Executors.newSingleThreadScheduledExecutor();
            reader.scheduleWithFixedDelay(this::reader, 1, 1, TimeUnit.MILLISECONDS);
        } catch (Exception e){
            System.err.println(e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }

    private void reader() {
        try {
            final Message message = (Message) objectInputStream.readObject();
            if (message instanceof Ping) {
                //todo
            } else if (message instanceof Auth) {
                if (Server.getUserList().addUser(message.getLogin(), client.getSocket())){
                    objectOutputStream.writeObject(new Auth(message.getLogin()));
                }
            } else {
                broadcastSendMessage(message);
            }
        } catch (IOException | ClassNotFoundException e) {
            System.err.println(e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }

    private void broadcastSendMessage(Message message) {
        for (Map.Entry<String, SocketClient> entry: Server.getUserList().getClientsList().entrySet()) {
            try {
                new ObjectOutputStream((entry.getValue()).getSocket().getOutputStream()).writeObject(message);
            } catch (Exception e){
                Server.getUserList().deleteUser(entry.getKey());
            }
        }
    }
}
