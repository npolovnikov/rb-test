package com.rbtest.server;

public class UserNameAlreadyExistException extends Exception {

    public UserNameAlreadyExistException(){
        super();
    }

    public UserNameAlreadyExistException(String login) {
        super("Пользователь с именем " + login + "уже существует");
    }
}
