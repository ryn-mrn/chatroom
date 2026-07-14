package com.chatroom.controllers;

import com.chatroom.models.Message;
import com.chatroom.network.Client;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

public class ProfilePictureController implements ClientAware {

    private final Desktop desktop = Desktop.getDesktop();

    @FXML
    private ImageView profilePicture;
    @FXML
    private AnchorPane ap;
    @FXML
    private Button openFileButton;
    @FXML
    private Button removePictureButton;
    @FXML
    private Label fileName;

    private Client client;
    private String username;
    private String session;

    public void setUsername(String username){
        this.username = username;
    }
    public void setSession(String sessionID) { this.session = sessionID; }

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
            Image image = new Image(file.toURI().toString());
            // save to the computer and send to the server
            sendFile(file);
            client.setOneTimeListener(response -> {
                Platform.runLater(() -> {
                    // when the server says ready then the photo is changed
                    if(response.equals("CHANGED_PHOTO")){
                        profilePicture.setImage(image);
                        fileName.setText(file.getName());
                    }
                });
            });
        }
    }


    @FXML
    protected void removePicture(){
        Message message = new Message();
        message.setType("PICTURE");
        Map<String, Object> payload = new HashMap<>();
        payload.put("username", username);
        payload.put("picture", "remove");
        message.setPayload(payload);
        message.setToken(session);
        try {
            client.sendMessage(message.serialize());
            // get response
            client.setOneTimeListener(response -> {
                Platform.runLater(() -> {
                    if(response.equals("REMOVED_PHOTO")){
                        // remove the image only after the server says ready
                        profilePicture.setImage(null);
                    }
                });
            });
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
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
            String base64image = Base64.getUrlEncoder().encodeToString(imageData);
            Map<String, Object> payload = new HashMap<>();
            System.out.println(file.getName());
            Message message = new Message();
            message.setType("PICTURE");
            // set token
            message.setToken(session);
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
