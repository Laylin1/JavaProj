package com.example.chatappfx;

import org.mindrot.jbcrypt.BCrypt;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class UserDB {
    private static final Logger LOGGER = Logger.getLogger(UserDB.class.getName());

    public void saveUser(String nickname, String password) {
        String sql = "INSERT INTO users (nickname, password) VALUES (?, ?)";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, nickname);
            statement.setString(2, hashPassword(password)); // Хэшируем пароль перед сохранением
            statement.executeUpdate();
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error saving user: " + nickname, e);
        }
    }

    public boolean userExists(String nickname) {
        String sql = "SELECT COUNT(*) FROM users WHERE nickname = ?";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, nickname);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getInt(1) > 0; // Если пользователь найден, возвращаем true
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error checking if user exists: " + nickname, e);
        }
        return false; // Если возникла ошибка, считаем, что пользователь не существует
    }

    public boolean checkCredentials(String nickname, String password) {
        String sql = "SELECT password FROM users WHERE nickname = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, nickname);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                String hashedPassword = resultSet.getString("password");
                return BCrypt.checkpw(password, hashedPassword); // Проверяем хэшированный пароль
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error checking credentials for nickname: " + nickname, e);
        }
        return false; // Если произошла ошибка или учетные данные не верны
    }

    public int getUserId(String nickname, String password) {
        String sql = "SELECT id FROM users WHERE nickname = ? AND password = ?";
        int userId = -1;

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, nickname);
            statement.setString(2, password);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                userId = resultSet.getInt("id");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return userId;
    }

    private String hashPassword(String password) {
        return BCrypt.hashpw(password, BCrypt.gensalt()); // Хэшируем пароль
    }
}
