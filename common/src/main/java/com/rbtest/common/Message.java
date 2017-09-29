package com.rbtest.common;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by nikita on 26.09.2017.
 */
public class Message implements Serializable {
    private static final long serialVersionUID = 4691549225227062606L;

    private String login;
    private Date time;
    private String message;

    public Message(String login, String message) {
        this.login = login;
        this.message = message;
        this.time = new Date();
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
