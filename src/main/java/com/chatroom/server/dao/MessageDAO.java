package com.chatroom.server.dao;

import com.chatroom.server.database.DatabaseConnection;
import com.chatroom.common.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class MessageDAO {

    private final Connection conn;
    private final UserDAO userDAO = new UserDAO();

    public MessageDAO() {
        this.conn = DatabaseConnection.getInstance().getConnection();
    }

    // add message to the database
    public boolean addMessage(String message, String username){
        String sql = "INSERT INTO messages (message, user_id) VALUES (?, ?)";
        int userID = userDAO.getUserID(username);
        System.out.println("MESSAGEDAO USERID = " + userID);
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, message);
            stmt.setInt(2, userID);

            int rowsAffected = stmt.executeUpdate();
            System.out.println("Message added to the database\n" +
                    message+"\n"
                    +username);
            return rowsAffected > 0;

        } catch (SQLException e) {
            System.err.println("Error adding message " + e);
            return false;
        }
    }

    // load messages half hour recent
    public List<Message> getMessages(){
        List<Message> messages = new ArrayList<>();
        String sql = "SELECT * FROM messages WHERE time_sent > datetime('now', '-30 minutes')";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                messages.add(new Message(
                        rs.getInt("id"),
                        userDAO.getUsername(rs.getInt("user_id")),
                        rs.getString("message")
                ));
            }
        } catch (SQLException e) {
            System.err.println("Error getting all messages: " + e.getMessage());
        }
        return messages;
    }

    // used for adding onto messages -- should add profile pictures so the users can click each other to view profile
    public void getTimestamp(){
        String sql = "SELECT time_sent FROM messages";
        try (PreparedStatement stmt = conn.prepareStatement(sql)){
            ResultSet rs = stmt.executeQuery();
            System.out.println(rs.getTimestamp(1));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    // get messages using the users id and then sql to join it to the user to get username etc
}
