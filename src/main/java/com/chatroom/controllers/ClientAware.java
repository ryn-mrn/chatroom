package com.chatroom.controllers;

import com.chatroom.network.Client;

public interface ClientAware {
    void setClient(Client client);
}
