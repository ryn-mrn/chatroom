module com.chatroom {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.kordamp.bootstrapfx.core;
    requires org.slf4j;

    requires com.fasterxml.jackson.databind;

    requires java.sql;
    requires java.desktop;

    opens com.chatroom to javafx.fxml;
    opens com.chatroom.controllers to javafx.fxml;
    opens server.models to com.fasterxml.jackson.databind;
    opens com.chatroom.models to com.fasterxml.jackson.databind;
}