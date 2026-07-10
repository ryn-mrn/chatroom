package com.chatroom.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Base64;

public class MessageController {
    @FXML
    private Label messageBox;
    @FXML
    private Button profileButton;

    private String username;
    
    @FXML
    protected void openProfile(){
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/com/chatroom/components/profile.fxml"));
            Stage profileStage = loader.load();
            ProfileController controller = loader.getController();
            controller.setUsername(username);
            profileStage.show();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void setUsername(String username){
        this.username = username;
    }

    public void setText(String username, String text, boolean isSender, boolean doubleMessage){
        messageBox.setWrapText(true);
        setSize();
        HBox root = (HBox) messageBox.getParent();
        if(isSender){
            root.setAlignment(Pos.CENTER_RIGHT);
            root.getChildren().remove(profileButton);
            messageBox.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white;" +
                    "-fx-padding: 8; -fx-background-radius: 10;");
            messageBox.setText(text);
        } else {
            if(doubleMessage){
                // keeps spacing
                profileButton.setVisible(false);
            } else{
                if(!profileButton.isVisible()){
                    profileButton.setVisible(true);
            }
                profileButton.setText(username);
            }
            root.setAlignment(Pos.CENTER_LEFT);
            messageBox.setStyle("-fx-background-color: #e5e5ea; -fx-text-fill: black; " +
                    "-fx-padding: 8; -fx-background-radius: 10;");
            messageBox.setText(text);
        }
    }

    public void setSize(){
        messageBox.setPrefSize(Label.USE_COMPUTED_SIZE, Label.USE_COMPUTED_SIZE);
        messageBox.setMaxWidth(250);
        messageBox.setWrapText(true);
    }

    //decode the picture then add it to the profile
    //base 64 into javafx image
    public Image getPicture(String base64, String filename){
        byte[] imageBytes = Base64.getDecoder().decode(base64);
        File file = new File("client-data/profile-pictures/"+filename);
        try(FileOutputStream outputStream = new FileOutputStream(file)){
            outputStream.write(imageBytes);
            System.out.println("Image written");
            return new Image(file.getAbsolutePath());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    // sets the picture in the message
    public void setPicture(Image profilePicture){
        ImageView view = new ImageView(profilePicture);
        view.setFitHeight(profileButton.getPrefHeight());
        view.setFitWidth(profileButton.getPrefWidth());
        view.setPreserveRatio(true);
        profileButton.setGraphic(view);
    }
}