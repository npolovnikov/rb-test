package com.rbtest.server.main;

import com.rbtest.common.Auth;
import com.rbtest.common.Config;
import com.rbtest.common.Message;
import com.rbtest.server.connections.SocketServerConnectionImpl;
import javafx.util.Pair;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;


/**
 * Created by nikita on 30.09.2017.
 */
public class ServerTest {

    @Test(enabled = false)
    public void serverTest() throws IOException, InterruptedException {
        final Server server = new Server(new SocketServerConnectionImpl());
        Thread.sleep(100);
        final AtomicInteger countOfExec = new AtomicInteger();
        final ExecutorService clientExec = Executors.newFixedThreadPool(4);
        for (int i = 0; i < 100; i++) {
            clientExec.submit(() -> {
                ObjectOutputStream oos = null;
                try {
                    final String login = "login" + countOfExec.getAndIncrement();
                    final Socket client = new Socket(Config.getProperty(Config.HOST, "127.0.0.1"),
                            Integer.parseInt(Config.getProperty(Config.PORT, "1111")));
                    oos = new ObjectOutputStream(client.getOutputStream());
                    oos.writeObject(new Auth(login));
//                    System.out.println("[TEST] " + login + " send AUTH message");
                    for (int j = 0; j < 10; j++) {
                        oos.writeObject(new Message(login, "Message" + j));
//                        System.out.println("[TEST] " + login + " send message");
                        Thread.sleep(100 * new Random().nextInt(5));
                    }
                } catch (IOException | InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    if (oos != null) {
                        try {
                            oos.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
        }
        clientExec.shutdown();
        clientExec.awaitTermination(60, TimeUnit.SECONDS);


//        for (Message msg : server.getHistory()) {
//            System.out.println("[" + msg.getTime() + "]" + msg.getLogin() + ":" + msg.getMessage());
//        }

        Assert.assertEquals(server.getHistory().size(), Integer.parseInt(Config.getProperty(Config.HISTORY_LENGTH, "100")));
    }
}