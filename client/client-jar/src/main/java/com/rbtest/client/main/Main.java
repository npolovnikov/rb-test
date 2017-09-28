package com.rbtest.client.main;

import com.rbtest.client.config.Config;

/**
 * Created by npolovnikov on 28.09.17.
 */
public class Main {
    public static void main(String[] args) {
        Config.load();
        new Client();
    }
}
