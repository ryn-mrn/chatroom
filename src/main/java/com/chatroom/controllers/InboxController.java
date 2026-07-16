package com.chatroom.controllers;

import com.chatroom.network.Client;

public class InboxController implements ClientAware {

    private Client client;

    @Override
    public void setClient(Client c){
        this.client = c;
    }

    public void handleMessages(){}

    public void handleFriends(){}

    public void handleRequests(){}
}