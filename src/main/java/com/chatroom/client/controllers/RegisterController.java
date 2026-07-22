package com.chatroom.client.controllers;
import com.chatroom.common.*;
import com.chatroom.client.network.Client;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;


public class RegisterController implements ClientAware {

    @FXML
    private TextField usernameRegisterField;
    @FXML
    private PasswordField passwordRegisterField;
    @FXML
    private PasswordField confirmPasswordRegisterField;

    private Client client;

    @FXML
    protected void registerFunction() throws IOException {
        System.out.println("FUNC: registerFunctionCalled\n" +
                "SOURCE: RegisterController");

        String username = usernameRegisterField.getText();
        String confirmPassword = confirmPasswordRegisterField.getText();
        String password = passwordRegisterField.getText();

        if(checkUsername(username) && checkPassword(password, confirmPassword)){
            // send in the message format
            Message message = new Message();
            message.setType(MessageType.REGISTER_CONFIRM);
            message.setToken(null);
            Map<String, Object> payload = new HashMap<>();
            payload.put("username", username);
            payload.put("password", password);
            message.setPayload(payload);
            String messageFull = message.serialize();
            System.out.println(messageFull);
            client.sendMessage(messageFull);
            //set a listener for the register failure/success
            client.setOneTimeListener(response -> {
                Platform.runLater(() -> {
                    switch (response) {
                        case "REGISTER_SUCCESS" -> SceneManager.loginScene();
                        case "REGISTER_FAIL:USERNAME_TAKEN" -> showError("Username already taken");
                        case "REGISTER_FAIL:SERVER_ERROR" -> showError("Server error, try again");
                        default -> showError("Unknown error");
                    }
                });
            });
        };
    }

    @Override
    public void setClient(Client client) {
        this.client = client;
    }

    // displays an error for registration
    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Registration Failed");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private boolean checkUsername(String username){
        if((username.length() <= 3) || (username.length() >= 20)){
            System.out.println("Username must be between 3-20 characters");
            return false;
        } return true;
    }

    private boolean checkPassword(String password, String confirmPassword){
        if(!password.equals(confirmPassword)){
            System.out.println("Passwords don't match");
            return false;
        } return true;
    }
}