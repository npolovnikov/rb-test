package com.rbtest.client.connections;

import com.rbtest.common.Config;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import static com.rbtest.common.Config.HOST;
import static com.rbtest.common.Config.PORT;

/**
 * Created by npolovnikov on 28.09.17.
 */
public class SocketConnectionImpl implements Connection {
    private final static Logger LOG = LoggerFactory.getLogger(SocketConnectionImpl.class);
    private Socket socket;

    public SocketConnectionImpl() throws IOException {
        socket = new Socket(Config.getProperty(HOST, "127.0.0.1"), Integer.parseInt(Config.getProperty(PORT, "1111")));
        LOG.debug("Start connection to {}", socket);
    }

    @Override
    public ObjectOutputStream getOutputStream() throws IOException {
        return new ObjectOutputStream(socket.getOutputStream());
    }

    @Override
    public ObjectInputStream getInputStream() throws IOException {
        return new ObjectInputStream(socket.getInputStream());
    }

    @Override
    public void close() throws IOException {
        socket.close();
    }

}
