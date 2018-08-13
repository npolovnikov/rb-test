package com.rbtest.server.client;

import com.rbtest.common.*;
import com.rbtest.server.UserNameAlreadyExistException;
import com.rbtest.server.main.Server;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Arrays;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static com.rbtest.common.Config.HELLO_MESSAGE;
import static com.rbtest.common.Consts.SYSTEM_USER;

public class WorkingClient {
    private final static Logger LOG = LoggerFactory.getLogger(WorkingClient.class);
    private Client client;
    private Server server;
    private ScheduledExecutorService reader;
    private ScheduledExecutorService pinger;
    private boolean work = true;

    public WorkingClient(Client client, Server server) {
        LOG.debug("start working with client: {}, server:{}", client, server);
        this.client = client;
        this.server = server;

        startReadMessagesExecutor();
    }

    private void startReadMessagesExecutor() {
        reader = Executors.newSingleThreadScheduledExecutor();
        reader.scheduleAtFixedRate(this::readMessage,1, 1, TimeUnit.MILLISECONDS);
    }

    private void readMessage() {
        try {
            final Message msg = (Message) client.getInputStream().readObject();
            System.out.println(msg.parseMessage());

            if (msg instanceof Auth) {
                if (registerClient((Auth) msg)) {
                    sendHelloMessage();
                }
            } else if (msg instanceof Ping) {
                client.addPingIn();
            } else {
                if (msg.getMessage().equals(CommandType.help.getName())) {
                    server.sendMessage(client, createSystemMessage(Arrays.toString(CommandType.values())));
                } else if (msg.getMessage().equals(CommandType.userList.getName())) {
                    server.sendMessage(client, createSystemMessage(server.getClients().keySet().toString()));
                } else if(!client.getClientLogin().equals(msg.getLogin())) {
                    server.sendMessage(client, createSystemMessage("Ошибка: ожидаемый логин отличен от заявленого в сообщении"));
                } else {
                    server.broadcast(msg);
                }
            }
        } catch (IOException | ClassNotFoundException e) {
            closeConnectionToClient();
//            LOG.error(e.getMessage(), e);
        }
    }

    private void sendHelloMessage() throws IOException {
        for (Message message: server.getHistory()) {
            server.sendMessage(client, message);
        }
        server.sendMessage(client, createSystemMessage(Config.getProperty(HELLO_MESSAGE)));
        server.broadcast(createSystemMessage(String.format("Пользователь %s успешно подключился", client.getClientLogin())));
    }

    private boolean registerClient(Auth msg) throws IOException {
        try {
            server.addClient(msg.getLogin(), client);
            client.setClientLogin(msg.getLogin());
            server.sendMessage(client, new Auth(Auth.Status.SUCCESS));

            //Начинаем пинговать
            pinger = Executors.newSingleThreadScheduledExecutor();
            pinger.scheduleAtFixedRate(this::sendPing,1, 10, TimeUnit.SECONDS);
            return true;
        } catch (UserNameAlreadyExistException e) {
            server.sendMessage(client, new Auth(Auth.Status.ERROR));
            return false;
        }
    }

    private void sendPing() {
        int out = client.getPingOut();
        int in = client.getPingIn();

        try {
            if (out > in + 1) {
                closeConnectionToClient();
            } else {
                server.sendMessage(client, new Ping(SYSTEM_USER));
                client.addPingOut();
            }
        } catch (IOException e) {
            closeConnectionToClient();
//            LOG.error(e.getMessage(), e);
        }

    }

    private void closeConnectionToClient() {
        try {
            if (work) {
                work = false;

                if (reader.isShutdown()) {
                    reader.shutdownNow();
                }
                if (pinger.isShutdown()) {
                    pinger.shutdownNow();
                }

                LOG.debug("Пользовотель: {} отключился", client.getClientLogin());
                server.removeClient(client.getClientLogin());
                server.broadcast(createSystemMessage("Пользовотель: " + client.getClientLogin() + " отключился"));
            }
        } catch (IOException e) {
            LOG.error(e.getMessage(), e);
        }
    }

    private Message createSystemMessage(String text) {
        return new Message(SYSTEM_USER, text);
    }
}
