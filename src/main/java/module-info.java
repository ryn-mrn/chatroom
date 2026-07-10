module com.chatroom {
    requires javafx.controls;
    requires javafx.fxml;


    requires org.kordamp.bootstrapfx.core;
    requires com.fasterxml.jackson.databind;
    requires java.sql;
    requires net.bytebuddy;
    requires org.slf4j;
    requires java.desktop;
    requires jaxb.api;

    opens com.chatroom to javafx.fxml;
    exports com.chatroom;
    exports com.chatroom.controllers;
    opens com.chatroom.controllers to javafx.fxml;
    opens server.models to com.fasterxml.jackson.databind;
    opens com.chatroom.models to com.fasterxml.jackson.databind;
}