package com.chatroom.controllers;

import com.chatroom.models.Message;
import com.chatroom.network.Client;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

public class ProfilePictureController implements ClientAware {

    private Desktop desktop = Desktop.getDesktop();

    @FXML
    private AnchorPane ap;
    @FXML
    private Button openFileButton;

    private Client client;
    private String username;

    @Override
    public void setClient(Client client){ this.client = client; }

    @FXML
    protected void addFile(){
        // This opens the file, then it has to be sent to the server, and back and then stored
        Stage stage = (Stage) ap.getScene().getWindow();
        FileChooser fileChooser = new FileChooser();
        configureFileChooser(fileChooser);
        fileChooser.setTitle("Choose your picture");
        File file = fileChooser.showOpenDialog(stage);
        if(file != null){
            // save to the computer and send to the server
            openFile(file);
        }
    }

    public void setUsername(String username){
        this.username = username;
    }

    // adds in a configuration so that only images are selected and the
    // directory starts at the user's home
    private static void configureFileChooser(
            final FileChooser fileChooser) {
        fileChooser.setInitialDirectory(
                new File(System.getProperty("user.home"))
        );
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("All Images", "*.*"),
                new FileChooser.ExtensionFilter("JPG", "*.jpg"),
                new FileChooser.ExtensionFilter("PNG", "*.png")
        );
    }

    // opens the file -- needless as it will only be saved
    private void openFile(File file){
        try{
            desktop.open(file);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    // send the file to the server
    private void sendFile(File file){
        try {
            // convert the file to bytes
            byte[] imageData = Files.readAllBytes(file.getAbsoluteFile().toPath());
            String base64image = Base64.getEncoder().encodeToString(imageData);
            Map<String, Object> payload = new HashMap<>();
            Message message = new Message();
            message.setType("PICTURE");
            payload.put("username", username);
            payload.put("picture", base64image);
            payload.put("filename", file.getName());
            message.setPayload(payload);
            client.sendMessage(message.serialize());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
