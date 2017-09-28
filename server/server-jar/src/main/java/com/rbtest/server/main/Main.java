package com.rbtest.server.main;

import com.rbtest.server.Config;

/**
 * Created by npolovnikov on 28.09.17.
 */
public class Main {
    public static void main(String[] args) {
        Config.load();
        new Server();
    }
}
