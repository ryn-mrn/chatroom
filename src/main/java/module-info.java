module com.chatroom {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;

    requires org.kordamp.bootstrapfx.core;
    requires org.slf4j;

    requires com.fasterxml.jackson.databind;

    requires java.sql;
    requires java.desktop;

    opens com.chatroom to javafx.fxml, javafx.graphics;
    opens com.chatroom.client.controllers to javafx.fxml;
    opens com.chatroom.server.models to com.fasterxml.jackson.databind;
    opens com.chatroom.client.models to com.fasterxml.jackson.databind;
    exports com.chatroom.client;
    opens com.chatroom.client to javafx.fxml, javafx.graphics;
}