package com.rbtest.server.main;

import com.rbtest.common.Auth;
import com.rbtest.common.Message;
import com.rbtest.server.client.Client;
import com.rbtest.server.config.Config;
import com.rbtest.server.connections.SocketServerConnectionImpl;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Random;
import java.util.StringJoiner;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static org.testng.Assert.*;

/**
 * Created by nikita on 30.09.2017.
 */
public class ServerTest {

    @Test
    public void serverTest() throws IOException, InterruptedException {
        Config.load();
        final Server server = new Server(new SocketServerConnectionImpl());
        Thread.sleep(100);
        final AtomicInteger countOfExec = new AtomicInteger();
        final ExecutorService clientExec = Executors.newFixedThreadPool(100);
        for (int i = 0; i < 100; i++) {
            clientExec.submit(() -> {
                try {
                    final String login = "login" + countOfExec.getAndIncrement();
                    final Socket client = new Socket("127.0.0.1", Config.PORT);
                    final ObjectOutputStream oos = new ObjectOutputStream(client.getOutputStream());
                    oos.writeObject(new Auth(login));
//                    System.out.println("[TEST] " + login + " send AUTH message");
                    for (int j = 0; j < 10; j++) {
                        oos.writeObject(new Message(login, "Message" + j));
//                        System.out.println("[TEST] " + login + " send message");
                        Thread.sleep(100 * new Random().nextInt(5));
                    }
                } catch (IOException | InterruptedException e) {
                    e.printStackTrace();
                }
            });
        }
        clientExec.shutdown();
        clientExec.awaitTermination(120, TimeUnit.SECONDS);


        for (Message msg : server.getHistory()) {
            System.out.println("[" + msg.getTime() + "]" + msg.getLogin() + ":" + msg.getMessage());
        }

        Assert.assertEquals(server.getHistory().size(), Config.HISTORY_LENGTH);
    }
}