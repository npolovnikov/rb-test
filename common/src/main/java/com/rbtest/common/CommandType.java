package com.rbtest.common;

/**
 * Created by npolovnikov on 29.09.17.
 */
public enum CommandType {
    userList("--user", "Получить список пользователей"),
    help("--help", "Справка о командах");

    String name;
    String deskr;

    CommandType(String name, String deskr){
        this.name = name;
        this.deskr = deskr;
    }

    public String getName() {
        return name;
    }

    public String getDeskr() {
        return deskr;
    }

    @Override
    public String toString() {
        return "Команда '" + name + '\'' +
                " = '" + deskr + '\'' +
                '\n';
    }
}
