package com.rbtest.common;

/**
 * Created by npolovnikov on 28.09.17.
 */
public class Auth extends Message {
    public static class Status {
        public static final String SUCCESS = "SUCCESS";
        public static final String ERROR = "ERROR";
    }
    public Auth(String login) {
        super(login, "Auth");
    }
}
