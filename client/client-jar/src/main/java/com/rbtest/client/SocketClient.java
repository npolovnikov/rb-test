package com.rbtest.client;

import com.rbtest.common.Message;
import com.rbtest.common.Ping;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;

/**
 * Created by npolovnikov on 27.09.17.
 */
public class SocketClient {
    private final static String address = "127.0.0.1"; // это IP-адрес компьютера, где исполняется наша серверная программа
    private final static int serverPort = 1111; // здесь обязательно нужно указать порт к которому привязывается сервер

    private static String userName = "";
    static Socket socket = null;

    public static void main(String[] args) {
        System.out.println("Вас приветствует клиент чата!\n");
        System.out.println("Введите свой ник и нажмите \"Enter\"");

// Создаем поток для чтения с клавиатуры
        BufferedReader keyboard = new BufferedReader(new InputStreamReader(System.in));
        try {
// Ждем пока пользователь введет свой ник и нажмет кнопку Enter
            userName = keyboard.readLine();
            System.out.println();
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            try {
                socket = new Socket(address, serverPort); // создаем сокет используя IP-адрес и порт сервера

// Берем входной и выходной потоки сокета, теперь можем получать и отсылать данные клиентом
                InputStream inputStream = socket.getInputStream();
                OutputStream outputStream = socket.getOutputStream();

// Конвертируем потоки в другой тип, чтоб легче обрабатывать текстовые сообщения
                ObjectOutputStream objectOutputStream = new ObjectOutputStream(outputStream);
                ObjectInputStream objectInputStream = new ObjectInputStream(inputStream);

                new ServerListenerThread(objectOutputStream, objectInputStream);

// Создаем поток для чтения с клавиатуры
                String message = null;
                System.out.println("Наберите сообщение и нажмите \"Enter\"\n");

                while (true) { // Бесконечный цикл
                    message = keyboard.readLine(); // ждем пока пользователь введет что-то и нажмет кнопку Enter.
                    objectOutputStream.writeObject(new Message(userName, message)); // отсылаем введенную строку текста серверу.
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } finally {
            try {
                if (socket != null) {
                    socket.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
