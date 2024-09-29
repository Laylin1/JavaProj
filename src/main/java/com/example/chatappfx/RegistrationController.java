package com.example.chatappfx;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class RegistrationController {

    @FXML
    private TextField nicknameField;

    @FXML
    private PasswordField passwordField;

    private UserDB userDB;

    public RegistrationController() {
        userDB = new UserDB();
    }

    @FXML
    public void handleRegisterOrLogin() throws Exception {
        String nickname = nicknameField.getText();
        String password = passwordField.getText();

        if (nickname.isEmpty() || password.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Nickname and password cannot be empty.");
            alert.showAndWait();
            return;
        }

        if (userDB.userExists(nickname)) {
            // Если пользователь уже существует, выполняем вход
            if (userDB.checkCredentials(nickname, password)) {
                loginUser(nickname);
            } else {
                showErrAlert("Invalid nickname or password.");
            }
        } else {
            // Регистрируем нового пользователя
            userDB.saveUser(nickname, password);
            showConfAllert("User registered successfully!");
            loginUser(nickname);
        }
    }

    private void loginUser(String nickname) throws Exception {
        // Закрываем окно регистрации
        Stage stage = (Stage) nicknameField.getScene().getWindow();
        stage.close();

        // Открываем основное окно
        Main mainApp = new Main();
        mainApp.startChat(nickname); // Передаем никнейм в основное окно
    }

    private void showConfAllert(String message){
        Alert alert = new Alert(Alert.AlertType.INFORMATION, message);
        alert.showAndWait();
    }

    private void showErrAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR, message);
        alert.showAndWait();
    }
}
