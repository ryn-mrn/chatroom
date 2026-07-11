package com.chatroom.controllers;

import com.chatroom.models.Message;
import com.chatroom.network.Client;
import javafx.application.Platform;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.function.UnaryOperator;

public class ChatroomController implements ClientAware {

    @FXML
    private AnchorPane messageBox;
    @FXML
    private ImageView profilePicture;
    @FXML
    private Button pictureButton;
    @FXML
    private Button enterButton;
    @FXML
    private TextArea messageArea;
    @FXML
    private ScrollPane messageScroll;
    @FXML
    private VBox chatBox;
    @FXML
    private Label usernameLabel;

    private String username;
    private Client client;
    private String sessionID;
    private String sender = null;


    @Override
    public void setClient(Client client) {
        this.client = client;
    }

    public void initData(String sessionID, String username, Client c) throws IOException {
        this.sessionID = sessionID;
        this.username = username;
        this.usernameLabel.setText(username);
        this.messageArea.setWrapText(true);
        setTextArea();
        setClient(c);
        // set listener first to avoid race conditions
        // this only adds messages -- need to implement so it sends over users or have the client request it
        client.setMessageListener(message -> {
            System.out.println("Loading profiles");
            Platform.runLater(() -> {
                // add profiles pictures to users
                addProfiles(message);
            });
        });
        client.setMessageListener(message -> {
            System.out.println("Listener fired");
            Platform.runLater(() -> {
                // checking if the sender is sending messages so they get rid of the profile picture -- multiple messages
                addMessage(message);
            });
        });
        // gets the username for printing message properly
        System.out.println(this.username);
        // request profiles of users
        Message profileMessage = new Message();
        profileMessage.setType("PROFILE_REQUEST");
        client.sendMessage(profileMessage.serialize());
        // let the server know client in server so send all messages
        Message chatroomMessage = new Message();
        chatroomMessage.setType("CHATROOM");
        chatroomMessage.setToken(sessionID);
        client.sendMessage(chatroomMessage.serialize());
    }

    public void setTextArea(){
        this.messageArea.setWrapText(true);
        int MAX_CHARS = 250;

        UnaryOperator<TextFormatter.Change> filter = change -> {
            String text = change.getControlNewText();
            if(text.length() <= MAX_CHARS){
                return change;
            }
            return null;
        };
        this.messageArea.setTextFormatter(new TextFormatter<>(filter));
        this.messageArea.setOnScroll(Event::consume);
    }

    public void addMessage(String raw){
        //split the message
        System.out.println("lastSender: " + sender);
        String[] parts = raw.split(":", 2);
        String username = parts[0];
        System.out.println("username: " + username);
        String message = parts.length > 1 ? parts[1] : raw;
        System.out.println(raw);
        // check if the username is the sender
        boolean isSender = username.equals(this.username);
        boolean doubleMessage = username.equals(sender);
        System.out.println("doubleMessage: " + doubleMessage);
        sender = username;
        System.out.println(isSender);
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/com/chatroom/components/message-box.fxml"));
            Node messageNode = loader.load();
            MessageController controller = loader.getController();
            controller.setUsername(username);
            controller.setText(username, message, isSender, doubleMessage);
            chatBox.getChildren().add(messageNode);
            // adding a scroll area for multiple messages
            Platform.runLater(() -> messageScroll.setVvalue(1.0));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    // format a message to send
    public void sendMessage() {
        // replaces new lines and trailing whitespaces so isSender doesn't bug
        String textMessage = messageArea.getText().strip();
        if(textMessage.isEmpty()) return;
        Message message = new Message();
        Map<String, Object> payload = new HashMap<>();
        message.setType("CHAT");
        message.setToken(sessionID);
        payload.put("username", username);
        payload.put("message", textMessage);
        message.setPayload(payload);
        try {
            System.out.println(client.sendMessage(message.serialize()));
            messageArea.clear();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void addProfiles(String profile){

    }

    // opens a screen to set profile picture
    @FXML
    protected void openProfilePicture(){
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/com/chatroom/components/profile-picture.fxml"));
            Stage profileStage = loader.load();
            ProfileController controller = loader.getController();
            controller.setUsername(username);
            profileStage.show();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}