package com.chatroom.client.controllers;

import javafx.fxml.FXML;

public class MenuController {

    @FXML
    protected void menuScene() {
        SceneManager.switchScene("/com/chatroom/views/hello-view.fxml");
        SceneManager.primaryStage.show();
    }
}