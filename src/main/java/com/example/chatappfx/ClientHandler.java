package com.example.chatappfx;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Set;

public class ClientHandler implements Runnable {
    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;
    private String nickname;
    private Set<ClientHandler> clients;

    public ClientHandler(Socket socket, Set<ClientHandler> clients) {
        this.socket = socket;
        this.clients = clients;
        try {
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        try {
            // Первый запрос — получение никнейма
            this.nickname = in.readLine();
            System.out.println(nickname + " has joined the chat");

            // Сообщаем всем остальным, что новый участник присоединился
            Server.broadcastMessage(nickname + " has joined the chat");

            // Чтение сообщений и рассылка всем клиентам
            String message;
            while ((message = in.readLine()) != null) {
                System.out.println("Received: " + message);
                Server.broadcastMessage(message);  // Передаем сообщение всем клиентам
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            clients.remove(this);
            Server.broadcastMessage(nickname + " has left the chat");
        }
    }

    public void sendMessage(String message) {
        out.println(message);  // Отправляем сообщение клиенту
    }
}
