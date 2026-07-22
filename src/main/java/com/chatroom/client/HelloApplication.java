package com.chatroom.client;

import com.chatroom.client.controllers.SceneManager;
import com.chatroom.client.network.Client;
import javafx.application.Application;
import javafx.stage.Stage;
import java.io.IOException;

public class HelloApplication extends Application {

    @Override
    public void start(Stage stage) throws IOException {

        Client c = new Client();
        c.connect(3000);
        SceneManager.setClient(c);
        SceneManager.setStage(stage);
        SceneManager.switchScene("/com/chatroom/views/hello-view.fxml");
        stage.setTitle("Hello!");
        stage.show();

    }

    public static void main(String[] args) {
        launch();
    }
}