package com.chatroom.server.models;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Map;

// make it so jackson can detect the fields
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class Message {
    private static final Logger log = LoggerFactory.getLogger(Message.class);

    public int message_id;
    public MessageType type;
    public String token;
    public Map<String, Object> payload;

    @JsonIgnore
    public static final ObjectMapper mapper = new ObjectMapper();
    @JsonIgnore
    public String message;
    @JsonIgnore
    public String username;
    @JsonIgnore
    public String password;

    // for sending -- probably not needed
    public String serialize() {
        return String.format("{\"type\":\"%s\",\"token\":\"%s\",\"payload\":%s}",
                type, token, payloadToJson(payload));
    }

    public Message(){}

    public Message(int message_id, String username, String message){
        this.message_id = message_id;
        this.username = username;
        this.message = message;
    }

    // for receiving
    public static Message deserialize(String jsonString){
        try{
            return mapper.readValue(jsonString, Message.class);
        } catch (JsonProcessingException e) {
            log.warn("Error deserializing message {}. Error: {}", jsonString, e.getMessage());
            return null;
        }
    }

    // for sending. converts to a string json to be sent by handler
    public String payloadToJson(Map<String, Object> payload){
        try {
            return mapper.writeValueAsString(payload);
        } catch (JsonProcessingException e) {
            log.warn("Failed to serialize payload: {}", payload);
            return "{}"; // fallback to empty object so callers don't break
        }
    }

    public MessageType getType(){
        return this.type;
    }

    public void setType(MessageType type){
        this.type = type;
    }

    public void setPayload(Map<String, Object> payload) { this.payload = payload; }

    public String getSessionID(){
        return this.token;
    }

    public Map<String, Object> getPayload(){
        return this.payload;
    }

    public String getUsername(){
        if(payload != null){
            return (String) getPayload().get("username");
        }
        return this.username;
    }

    public String getPassword(){
        if(payload !=null){
            return (String) getPayload().get("password");
        }
        return this.password;
    }

    public String getMessage(){
        if(payload !=null){
            return (String) getPayload().get("message");
        }
        return this.message;
    }

    public String fullMessage(){
        return this.username + ": " + this.message;
    }
}