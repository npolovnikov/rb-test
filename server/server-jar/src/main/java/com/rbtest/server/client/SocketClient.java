package com.rbtest.server.client;

import java.io.Serializable;
import java.net.Socket;

/**
 * Created by npolovnikov on 28.09.17.
 */
public class SocketClient implements Serializable {

    private Socket socket;

    public SocketClient(Socket socket){
        this.socket = socket;
    }

    public Socket getSocket() {
        return socket;
    }
}
