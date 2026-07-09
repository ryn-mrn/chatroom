package com.chatroom.controllers;

import com.chatroom.models.Message;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

import java.io.IOException;

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
}