package com.chatroom.controllers;

import com.chatroom.network.Client;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

import java.io.IOException;

public class MessageController implements ClientAware {

    @FXML
    private HBox messageContainer;
    @FXML
    private Label usernameLabel;
    @FXML
    private Label messageBox;
    @FXML
    private Button profileButton;

    private String username;
    private Client c;
    private Image profilePicture;

    @Override
    public void setClient(Client client) {
        this.c = client;
    }

    @FXML
    protected void openProfile(){
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/com/chatroom/components/profile.fxml"));
            Stage profileStage = loader.load();
            ProfileController controller = loader.getController();
            controller.setUsername(username);
            controller.initData(c);
            controller.setPicture(profilePicture);
            profileStage.show();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void setUsername(String username){
        this.username = username;
    }

    public void setText(String username, String text, boolean isSender, boolean doubleMessage){
        profileButton.setText(null);
        messageBox.setWrapText(true);
        setSize();
        HBox root = messageContainer;
        if(isSender){
            root.setAlignment(Pos.CENTER_RIGHT);
            root.getChildren().remove(profileButton);
            messageBox.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white;" +
                    "-fx-padding: 8; -fx-background-radius: 10;");
            messageBox.setText(text);
            usernameLabel.setVisible(false);
        } else {
            if(doubleMessage){
                // keeps spacing
                profileButton.setVisible(false);
            } else{
                if(!profileButton.isVisible()){
                    profileButton.setVisible(true);
            }
                profileButton.setText(username);
                usernameLabel.setText(username);
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

    // sets the picture in the message
    public void setPicture(Image profilePicture){
        this.profilePicture = profilePicture;
        ImageView view = new ImageView(profilePicture);
        view.setFitHeight(profileButton.getPrefHeight());
        view.setFitWidth(profileButton.getPrefWidth());
        view.setPreserveRatio(true);
        profileButton.setGraphic(view);
    }

}