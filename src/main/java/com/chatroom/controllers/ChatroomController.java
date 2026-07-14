package com.chatroom.controllers;

import com.chatroom.models.Message;
import com.chatroom.network.Client;

import com.fasterxml.jackson.core.JsonProcessingException;
import javafx.application.Platform;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Base64;
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
    private Map<String, Image> imageCache;


    @Override
    public void setClient(Client client) {
        this.client = client;
    }

    public void initData(String sessionID, String username, Client c) throws IOException {
        this.sessionID = sessionID;
        this.username = username;
        this.usernameLabel.setText(username);
        this.messageArea.setWrapText(true);
        this.imageCache = new HashMap<String, Image>();
        setTextArea();
        setClient(c);
        // one client listener
        client.setMessageListener(raw -> {
            // run on main fx thread
            Platform.runLater(() -> {
                System.out.println("Loading profile");
                String trimmed = raw.strip();
                // if is a json or not
                if(trimmed.startsWith("{")){
                    try{
                        Message parsed = Message.deserialize(trimmed);
                        String type = parsed.getType();
                        // handles pictures
                        if ("PICTURE".equals(type)){
                            addProfiles(trimmed);
                            // handles chats
                        } else if ("CHAT".equals(type)) {
                            addMessage(parsed.getUsername() + ":" + parsed.getMessage());
                        } else {
                            System.out.println("Unhandled message type");
                        }
                    } catch (JsonProcessingException e) {
                        throw new RuntimeException(e);
                    }
                } else {
                    addMessage(trimmed);
                }
            });
        });

//        client.setMessageListener(message -> {
//            System.out.println("Listener fired");
//            Platform.runLater(() -> {
//                // checking if the sender is sending messages so they get rid of the profile picture -- multiple messages
//                addMessage(message);
//            });
//        });
        // gets the username for printing message properly
        System.out.println(this.username);
        // request profiles of users
        Message profileMessage = new Message();
        profileMessage.setType("PROFILE_REQUEST");
        profileMessage.setToken(sessionID);
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
            Image image = imageCache.get(username);
            controller.setUsername(username);
            controller.setText(username, message, isSender, doubleMessage);
            controller.setPicture(image);
            controller.setClient(client);
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


    // cache the photos
    public void addProfiles(String profile){
        try {
            System.out.println(profile);
            Message profilePhoto = Message.deserialize(profile);
            System.out.println(profilePhoto);
            // get the username and base64 photo
            String username = profilePhoto.getBlank("username");
            String base64Image = profilePhoto.getBlank("photo");
            // convert base64 to image
            if(imageCache.containsKey(username)) {
                return;
            }

            if (base64Image == null || "no photo".equals(base64Image)) {
                System.out.println("No photo for " + username);
                return;
            }

            byte[] imageBytes = Base64.getUrlDecoder().decode(base64Image);
            Image profilePicture = new Image(new ByteArrayInputStream(imageBytes));

            imageCache.put(username, profilePicture);
            if(username.equals(this.username)) {
                this.profilePicture.setImage(profilePicture);
            }

            System.out.println(imageCache.get(username));
        }
        catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    // opens a screen to set profile picture
    public void openProfilePicture(){
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/com/chatroom/components/profile-picture.fxml"));
            AnchorPane root = loader.load();
            ProfilePictureController controller = loader.getController();
            controller.setUsername(username);
            controller.setClient(client);
            controller.setSession(sessionID);
            Stage profileStage = new Stage();
            profileStage.setScene(new Scene(root));
            profileStage.setTitle("Profile Picture");
            profileStage.show();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}