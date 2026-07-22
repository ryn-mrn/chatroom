package com.chatroom.client.controllers;

import com.chatroom.client.network.Client;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;

import java.io.IOException;

public class HelloController implements ClientAware {
    @FXML
    public Button loginButton;
    @FXML
    private Button registerButton;
    @FXML
    private Label welcomeText;
    @FXML
    private AnchorPane mainScreen;

    private Client client;

    @FXML
    protected void loginFunction() throws IOException {
        System.out.println("Login button pressed.");
        SceneManager.loginScene();
    }

    @FXML
    protected void registerFunction() throws IOException {
        System.out.println("FUNC: registerFunctionCalled\n" +
                "SOURCE: HelloController");
        String message = "{\"type\":\"REGISTER\",\"token\":\""
                + null + "\",\"payload\":"+ null + "}";
        client.sendMessage(message);
        SceneManager.registerScene();
    }

    @Override
    public void setClient(Client client) {
        this.client = client;
    }
}