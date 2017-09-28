package com.rbtest.server;

import com.rbtest.common.Message;
import com.rbtest.server.config.Config;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by nikita on 26.09.2017.
 */
public class ChatHistory implements Serializable {
    private static final long serialVersionUID = 5631178004890595811L;

    private List<Message> history;

    public ChatHistory() {
        this.history = new ArrayList<>(Config.HISTORY_LENGTH);
    }

    public void addMessage(Message message){
        if (this.history.size() > Config.HISTORY_LENGTH){
            this.history.remove(0);
        }

        this.history.add(message);
    }

    public List<Message> getHistory(){
        return this.history;
    }

}
