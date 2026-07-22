package com.chatroom.server.service;

import com.chatroom.server.dao.MessageDAO;
import com.chatroom.common.*;

import java.util.List;

public class MessageService {

    private final MessageDAO messageDAO = new MessageDAO();

    public List<Message> getMessages() {
        return messageDAO.getMessages();
    }

    public void addMessage(String message, String username){
        messageDAO.addMessage(message, username);
    }
}