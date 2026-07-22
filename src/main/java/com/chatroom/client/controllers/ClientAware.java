package com.chatroom.client.controllers;

import com.chatroom.client.network.Client;

public interface ClientAware {
    void setClient(Client client);
}
