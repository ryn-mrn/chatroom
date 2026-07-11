package com.chatroom.network;

import com.chatroom.models.Message;

import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.function.Consumer;

public class Client {

    // Initialize socket and input/output streams
    private Socket socket = null;
    private String host;

    private Consumer<String> messageListener;

    private PrintWriter out;
    private BufferedReader fromServer;

    public Client() {}

    public void setMessageListener(Consumer<String> listener) {
        this.messageListener = listener;
    }

    public void setOneTimeListener(Consumer<String> callback) {
        this.messageListener = message -> {
            callback.accept(message);
            this.messageListener = null;
        };
    }

    public void connect(int port) throws IOException {
        host = InetAddress.getLocalHost().getHostAddress();
        socket = new Socket(host, port);
        out = new PrintWriter(
                new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8), true);
        fromServer = new BufferedReader(
                new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
        startListening();
    }

    public boolean sendMessage(String message) throws IOException {
        out.println(message);
        return true;
    }

    public boolean sendMessage(String type, String token, Map<String, Object> payload){
        Message message = new Message();
        message.setType(type);
        message.setToken(token);
        message.setPayload(payload);
        try {
            out.println(message.serialize());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return true;
    }

    private void startListening() {
        new Thread(() -> {
            try {
                System.out.println("Started listening");
                String line;
                while ((line = fromServer.readLine()) != null) {
                    onMessageReceived(line);
                    if (messageListener != null) {
                        messageListener.accept(line);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }

    private void onMessageReceived(String message) {
        System.out.println("FROM SERVER -- " + message);
    }

}