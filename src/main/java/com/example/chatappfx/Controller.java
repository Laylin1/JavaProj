package com.example.chatappfx;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ScrollPane; // Импортируйте ScrollPane
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.scene.input.KeyCode;

public class Controller {

    private Client client;
    private String nickname;

    @FXML
    private TextFlow chatArea; // Привязка к FXML

    @FXML
    private TextField messageField;

    @FXML
    private ScrollPane chatScrollPane; // Привязка к ScrollPane

    public void initialize() {
        // Инициализация
        messageField.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                sendMessage(); // Вызов метода отправки сообщения
            }
        });
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public void setClient(Client client) {
        this.client = client; // Устанавливаем клиента для работы с сетью
    }

    @FXML
    public void sendMessage() {
        if (client == null) {
            // Если клиент еще не инициализирован, показываем предупреждение
            Alert alert = new Alert(Alert.AlertType.ERROR, "Client is not initialized!");
            alert.showAndWait();
            return;
        }

        String message = messageField.getText();
        if (!message.isEmpty() && client != null) {
            client.sendMessage(nickname + ": " + message); // Добавляем никнейм к сообщению
            messageField.clear(); // Очищаем поле после отправки
        }
    }

    public void updateChat(String message) {
        javafx.application.Platform.runLater(() -> {
            Text text;
            String sender = message.split(":")[0]; // Получаем имя отправителя
            String msgContent = message.substring(sender.length()).trim(); // Извлекаем текст сообщения

            // Проверяем, является ли сообщение от текущего пользователя
            if (sender.equals(nickname)) {
                text = new Text("You: " + msgContent + "\n");
                text.setFill(Color.BLUE); // Устанавливаем синий цвет для собственных сообщений
                text.setTranslateX(10); // Устанавливаем отступ вправо
            } else {
                text = new Text(sender + ": " + msgContent + "\n");
                text.setFill(Color.BLACK); // Чёрный цвет для сообщений других
            }

            chatArea.getChildren().add(text); // Добавляем текст в TextFlow

        });
    }
}
