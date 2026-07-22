package com.chatroom.client.controllers;

import com.chatroom.client.network.Client;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class SceneManager {

    private static final Logger log = LoggerFactory.getLogger(SceneManager.class);
    static Stage primaryStage;
    static Scene currentScene;

    private static Client client;

    public static void setStage(Stage stage) {
        primaryStage = stage;
    }

    public static void setScene(Scene scene) { currentScene = scene; };

    public static void showScene(){
        primaryStage.show();
    }

    public static void switchScene(String fxml) {
        try {
            FXMLLoader loader = new FXMLLoader(SceneManager.class.getResource(fxml));
            Scene scene = new Scene(loader.load());
            Object controller = loader.getController();
            if (controller instanceof ClientAware) {
                ((ClientAware) controller).setClient(client);
            }
            primaryStage.setScene(scene);
            primaryStage.show();
        } catch (IOException e) {
            log.debug("Error with switching scenes {}", e.getMessage());
        }
    }

    // sets the client to be shared throughout the program
    public static void setClient(Client c){
        client = c;
    }

    public static Client getClient(){
        return client;
    }

    public static void loginScene() {
        switchScene("/com/chatroom/views/login-view.fxml");
    }

    public static void registerScene() {
        switchScene("/com/chatroom/views/register-view.fxml");
    }

    public static void chatroomScene(String sessionID, String username, Client c) {
        try {
            FXMLLoader loader =
                    new FXMLLoader(SceneManager.class.getResource(
                            "/com/chatroom/views/chatroom-view.fxml"
                    ));
            Parent root = loader.load();

            // get controller created by FXMLLoader
            ChatroomController controller = loader.getController();

            // inject session
            controller.setClient(c);
            controller.initData(sessionID, username, c);

            // now uses the same controller

            Scene scene = new Scene(root);
            primaryStage.setScene(scene);
            primaryStage.show();

        } catch (IOException e) {
            log.debug("Error loading chatroom scene {}", e.getMessage());
        }
    }
}