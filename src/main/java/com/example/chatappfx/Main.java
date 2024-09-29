package com.example.chatappfx;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.Socket;

public class Main extends Application {

    private String nickname;

    public static void main(String[] args) {
        launch(args);  // Запуск JavaFX
    }


    @Override
    public void start(Stage primaryStage) throws Exception {

        if (!isServerRunning("localhost", 12345)) {
            new Thread(() -> {
                Server.main(new String[0]); // Запускаем сервер
            }).start();
        }   

        // Загружаем окно регистрации
        FXMLLoader loader = new FXMLLoader(getClass().getResource("registration.fxml"));
        Parent registrationRoot = loader.load();
        Stage registrationStage = new Stage();
        registrationStage.setTitle("Registration/Login");
        registrationStage.setScene(new Scene(registrationRoot, 300, 200));
        registrationStage.showAndWait(); // Ожидаем, пока окно регистрации не закроется

        // После закрытия окна регистрации, загружаем основное окно
        if (nickname != null) {
            startChat(nickname); // Передаем никнейм в основное окно
        }
    }

    public void startChat(String nickname) throws Exception {
        this.nickname = nickname;

        FXMLLoader loader = new FXMLLoader(getClass().getResource("chat.fxml"));
        Parent chatRoot = loader.load();
        Stage chatStage = new Stage();
        chatStage.setTitle("Chat Application - " + nickname);

        // Создаем клиента и устанавливаем его в контроллер
        Client client = new Client("localhost", 12345, nickname);

        // Получаем контроллер и устанавливаем никнейм и клиент
        Controller controller = loader.getController();
        controller.setNickname(nickname);
        controller.setClient(client); // Передаем клиент

        // Начинаем получать сообщения
        client.receiveMessages(controller::updateChat); // Подписка на получение сообщений

        chatStage.setScene(new Scene(chatRoot, 400, 400));
        chatStage.show();
    }

    private boolean isServerRunning(String host, int port) {
        try (Socket socket = new Socket(host, port)) {
            return true; // Сервер работает
        } catch (IOException e) {
            return false; // Сервер не запущен
        }
    }

}
