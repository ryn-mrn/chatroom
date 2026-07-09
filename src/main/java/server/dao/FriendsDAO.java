package server.dao;

import server.database.DatabaseConnection;

import server.models.FriendStatus;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class FriendsDAO {
    private final Connection conn;

    public FriendsDAO(){ this.conn = DatabaseConnection.getInstance().getConnection(); }

    // this could be one function by returning the status
    public FriendStatus checkStatus(int client, int profile) {
        // may can only do with on of the OR
        String sql = """
                SELECT status FROM friends
                "WHERE (user1_id = ? AND user2_id = ?) OR (user1_id = ? AND user2_id = ?)""";
        try (PreparedStatement stmt = conn.prepareStatement(sql)){
            stmt.setInt(1, client);
            stmt.setInt(2, profile);
            stmt.setInt(3, profile);
            stmt.setInt(4, client);
            try (ResultSet rs = stmt.executeQuery()) {
                if(rs.next()){
                    String raw = rs.getString("status");
                    return raw == null ? FriendStatus.NO_INTERACTION : FriendStatus.valueOf(raw);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return FriendStatus.NO_INTERACTION;
    }

    // for adding users
    public boolean addUser(int client, int profile){
        String sql = "INSERT INTO friends VALUES(?, ?, 'pending')";
        try {
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, client);
            stmt.setInt(2, profile);
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 1;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    // for removing users
    public boolean removeUser(int client){
        String sql = "DELETE FROM friends WHERE user1 = ?";
        try {
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, client);
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 1;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


    // for blocking users
    // checks if the user is added, if they are, remove them then block them
    public boolean blockUser(int client, int profile){
        String sql = "INSERT INTO friends VALUES(?, ?, 'blocked')";
        if(checkStatus(client, profile).equals("ADDED")){
            removeUser(client);
        }
        try {
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, client);
            stmt.setInt(2, profile);
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 1;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}