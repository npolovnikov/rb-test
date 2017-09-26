package com.rbtest.common;

/**
 * Created by nikita on 26.09.2017.
 */
public class Ping extends Message {
    private static final long serialVersionUID = 2660138438202931003L;

    public Ping() {
        super("Ping", "Ping");
    }
}
