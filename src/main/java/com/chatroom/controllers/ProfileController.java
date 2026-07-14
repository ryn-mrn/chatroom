package com.chatroom.controllers;

import com.chatroom.models.Message;
import com.chatroom.network.Client;

import javafx.scene.image.Image;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.image.ImageView;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

public class ProfileController implements ClientAware, Initializable {
    @FXML
    private Button addButton;
    @FXML
    private Button blockButton;
    @FXML
    private Button messageButton;
    @FXML
    private Button removeButton;
    @FXML
    private Label usernameLabel;
    @FXML
    private ImageView profileImage;

    private Client client;
    private boolean blocked;
    private static final Logger log = LoggerFactory.getLogger(ProfileController.class);

    @Override
    public void setClient(Client client) {
        this.client = client;
    }

    // should check if the other user has blocked them
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        //send message to server for a profile request
        messageButton.setVisible(false);
    }

    public void initData(Client c){
        setClient(c);
        Map<String, Object> payload = new HashMap<>();
        payload.put("username", usernameLabel.getText());
        client.setMessageListener(response -> {
                    Platform.runLater(() -> {
                        switch(response){
                            case "ADDED":
                                addButton.setVisible(false);
                                messageButton.setVisible(true);
                                break;
                            case "PENDING":
                                addButton.setText("Pending");
                                break;
                            case "BLOCKED_CLIENT":
                                addButton.setVisible(false);
                                removeButton.setVisible(false);
                                blockButton.setText("Unblock");
                            default:
                                removeButton.setVisible(false);
                        }
                    });
                }
        );
        log.info("Message listener set.");
        client.sendMessage("PROFILE_OPEN", null, payload);
        log.info("'PROFILE_OPEN' sent to the server.");
    }

    @FXML
    protected void add(){
        Map<String, Object> payload = new HashMap<>();
        Message message = new Message();
        message.setType("ADD");
        payload.put("username", usernameLabel.getText());
        message.setPayload(payload);
        try{
            System.out.println("ADDING " + usernameLabel.getText() + client.sendMessage(message.serialize()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    @FXML
    protected void block(){
        Map<String, Object> payload = new HashMap<>();
        Message message = new Message();
        message.setType("BLOCK");
        payload.put("username", usernameLabel.getText());
        message.setPayload(payload);
        try{
            System.out.println("BLOCKING " + usernameLabel.getText() + client.sendMessage(message.serialize()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    // opens a message window
    @FXML
    protected void message(){

    }

    @FXML
    protected void remove(){
        Map<String, Object> payload = new HashMap<>();
        Message message = new Message();
        message.setType("REMOVE");
        payload.put("username", usernameLabel.getText());
        message.setPayload(payload);
        try{
            System.out.println("REMOVING " + usernameLabel.getText() + client.sendMessage(message.serialize()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void setUsername(String username){
        this.usernameLabel.setText(username);
    }

    public void setPicture(Image image){
        profileImage.setImage(image);
        profileImage.setPreserveRatio(true);
    }

}