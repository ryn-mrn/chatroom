module com.chatroom {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;

    requires org.kordamp.bootstrapfx.core;
    requires org.slf4j;

    requires com.fasterxml.jackson.databind;

    requires java.sql;
    requires java.desktop;

    exports com.chatroom;
    opens com.chatroom to javafx.fxml, javafx.graphics;
    opens com.chatroom.controllers to javafx.fxml;
    opens server.models to com.fasterxml.jackson.databind;
    opens com.chatroom.models to com.fasterxml.jackson.databind;
}