package com.example.chatappfx;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class Client {
    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;
    private String nickname;

    public Client(String serverAddress, int serverPort, String nickname) {
        this.nickname = nickname;
        try {
            socket = new Socket(serverAddress, serverPort);
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            // Отправляем никнейм сразу после подключения
            out.println(nickname);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendMessage(String message) {
        out.println(message);  // Отправляем сообщение на сервер
    }

    public void receiveMessages(MessageListener listener) {
        new Thread(() -> {
            String message;
            try {
                while ((message = in.readLine()) != null) {
                    listener.onMessageReceived(message);  // Получаем сообщения от сервера
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }

    public interface MessageListener {
        void onMessageReceived(String message);
    }
}
