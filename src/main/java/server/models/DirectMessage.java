package server.models;


// For direct messages between the client and another user

import server.dao.DirectMessageDAO;

public class DirectMessage {

    // All that is need is the other user, message contents and the time it was sent at

    public int senderID;
    public String senderUsername;
    public String messageContents;
    public String timeSent;

    public DirectMessage(){}

    public DirectMessage(int senderID, String messageContents, String timeSent){
        this.senderID = senderID;
        this.messageContents = messageContents;
        this.timeSent = timeSent;
    }

    public void setSenderUsername(String senderUsername) { this.senderUsername = senderUsername; }
    public void setMessageContents(String messageContents) { this.messageContents = messageContents; }
    public void setTimeSent(String timeSent) { this.senderUsername = timeSent; }

    public String getSenderUsername(){ return this.senderUsername; }
    public String getMessageContents(){ return this.messageContents; }
    public String getTimeSent(){ return this.timeSent; }

}
