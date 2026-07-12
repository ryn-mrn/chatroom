package com.chatroom.models;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.Map;

// make it so jackson can detect the fields
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class Message {
    public int message_id;
    public String type;
    public String token;
    public Map<String, Object> payload;
    @JsonIgnore
    public static final ObjectMapper mapper = new ObjectMapper();
    @JsonIgnore
    public String message;
    @JsonIgnore
    public String username;


    // for sending -- probably not needed
    public String serialize() throws IOException {
        return String.format("{\"type\":\"%s\",\"token\":\"%s\",\"payload\":%s}",
                type, token, payloadToJson(payload));
    }

    public String serializeMessage(String username, String message){
        return String.format("\"username\":\"%s,\"message\":\"%s", username, message);
    }

    public Message() {

    }

    public Message(int message_id, String username, String message){
        this.message_id = message_id;
        this.username = username;
        this.message = message;
    }

    // for receiving
    public static Message deserialize(String jsonString) throws JsonProcessingException {
        return mapper.readValue(jsonString, Message.class);
    }

    public String payloadToJson(Map<String, Object> payload) throws JsonProcessingException {
        return mapper.writeValueAsString(payload);
    }

    public void setType(String type){
        this.type = type;
    }

    public void setToken(String token){
        this.token = token;
    }

    public void setPayload(Map<String, Object> payload){
        this.payload = payload;
    }

    public String getType(){
        return this.type;
    }

    public String getSessionID(){
        return this.token;
    }

    public Map<String, Object> getPayload(){
        return this.payload;
    }

    public String getUsername() {
        if (payload == null) return null;
        return (String) payload.get("username");
    }

    public String getPassword(){
        if (payload == null) return null;
        return (String) getPayload().get("password");
    }

    public String getBlank(String blank){
        if (payload == null) return null;
        return (String) getPayload().get(blank);
    }

    public String getMessage(){
        return (String) getPayload().get("message");
    }
}