package com.chatroom.controllers;

import com.chatroom.network.Client;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

public class InboxController implements ClientAware {

    @FXML
    private Button messagesButton;
    @FXML
    private Button friendsButton;
    @FXML
    private Button requestsButton;
    @FXML
    private Label messagesLabel;
    @FXML
    private Label friendsLabel;
    @FXML
    private Label requestsLabel;

    private Client client;

    @Override
    public void setClient(Client c){
        this.client = c;
    }

    public void handleMessages(){}

    public void handleFriends(){}

    public void handleRequests(){}
}