package server.service;

import server.dao.MessageDAO;
import server.models.Message;

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