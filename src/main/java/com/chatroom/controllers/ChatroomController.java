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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.function.UnaryOperator;

public class ChatroomController implements ClientAware {


    @FXML
    private Label notificationsLabel;
    @FXML
    private Button inboxButton;
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
    private final Logger log = LoggerFactory.getLogger(ChatroomController.class);


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
        this.pictureButton.setText(null);
        setTextArea();
        setClient(c);
        // one client listener
        client.setMessageListener(raw -> {
            // run on main fx thread
            Platform.runLater(() -> {
                System.out.println("Loading profile");
                String trimmed = raw.strip();
                // if is a json or not
                if(trimmed.startsWith("{")) {
                    // implement a handshake
                    try {
                        Message parsed = Message.deserialize(trimmed);
                        String type = parsed.getType();
                        // handles pictures
                        switch (type) {
                            case "PICTURE" -> addProfiles(trimmed);
                            case "CHAT" -> addMessage(parsed.getUsername() + ":" + parsed.getMessage());
                            case "INBOX" -> addInbox(parsed.getMessage());
                            case null, default -> System.out.println("Unhandled message type");
                        }
                    } catch (JsonProcessingException e) {
                        throw new RuntimeException(e);
                    }
                } else {
                    addMessage(trimmed);
                }
            });
        });
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
        Message inboxMessage = new Message();
        inboxMessage.setType("INBOX");
        inboxMessage.setToken(sessionID);
        client.sendMessage(inboxMessage.serialize());
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
            Image pfp = imageCache.get(username);
            controller.setUsername(username);
            controller.setClient(client);
            controller.setSession(sessionID);
            controller.setProfilePicture(pfp);
            Stage profileStage = new Stage();
            profileStage.setScene(new Scene(root));
            profileStage.setTitle("Profile Picture");
            profileStage.show();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    // gets the data and adds to the inbox to show notifications
    public void addInbox(String notifications){ // expecting a message with number of notifications
        notificationsLabel.setText("Notifications: " + notifications);
    }

    // this is for handling opening the inbox screen
    public void handleInbox(){
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/com/chatroom/components/inbox.fxml"));
            AnchorPane root = loader.load();
            InboxController controller = loader.getController();
            controller.setClient(client);
            Stage inboxStage = new Stage();
            inboxStage.setScene(new Scene(root));
            inboxStage.setTitle("Inbox");
            inboxStage.show();
        } catch (IOException e){
            throw new RuntimeException(e);
        }
    }

}