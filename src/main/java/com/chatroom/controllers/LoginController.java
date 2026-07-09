package com.chatroom.controllers;

import com.chatroom.models.Message;
import com.chatroom.network.Client;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class LoginController implements ClientAware{

    @FXML
    private TextField usernameLoginField;
    @FXML
    private PasswordField passwordLoginField;
    @FXML
    private Button loginButton;

    private static Client client;

    @Override
    public void setClient(Client client) {
        this.client = client;
    }

    @FXML
    protected void loginFunction() {
        String username = usernameLoginField.getText();
        String password = passwordLoginField.getText();

        Message message = new Message();
        message.setType("LOGIN_REQUEST");
        message.setToken(null);
        Map<String, Object> payload = new HashMap<>();
        payload.put("username", username);
        payload.put("password", password);
        message.setPayload(payload);

        try {;
            client.sendMessage(message.serialize());
            System.out.println("LoginController -- SENDING USERNAME AND PASSWORD SUCCESS");

            client.setOneTimeListener(response -> {
                Platform.runLater(() -> {
                    if (response.startsWith("LOGIN_SUCCESS")) {
                        String sessionID = response.substring("LOGIN_SUCCESS ".length());
                        // get the session ID
                        System.out.println("Session ID: " + sessionID);
                        SceneManager.chatroomScene(sessionID, username, client);
                    } else if (response.equals("LOGIN_FAILED")) {
                        showError("Login has failed.");
                    } else {
                        showError("Unknown error");
                    }
                });
            });
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    // displays an error for registration
    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Login Failed");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}