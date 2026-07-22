package com.chatroom.server.dao;

import com.chatroom.server.database.DatabaseConnection;

import com.chatroom.server.models.FriendStatus;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class FriendsDAO {
    private final Connection conn;

    public FriendsDAO(){ this.conn = DatabaseConnection.getInstance().getConnection(); }

    // this could be one function by returning the status
    public FriendStatus checkStatus(int client, int profile) {
        // may can only do with on of the OR
        String sql = """
                SELECT status FROM friends
                WHERE (user1_id = ? AND user2_id = ?)
                OR (user1_id = ? AND user2_id = ?)
                """;
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
        try (PreparedStatement stmt = conn.prepareStatement(sql)){;
            stmt.setInt(1, client);
            stmt.setInt(2, profile);
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 1;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    // for accepting a friend request
    // the line of thinking is that if the client is accepted a friend request,
    // then the other user has sent one, calling "addUser" and so is stored as user1_id
    public boolean acceptUser(int client, int profile){
        String sql = """
                UPDATE friends
                SET status ='added'
                WHERE user1_id = ? AND user2_id = ?
                """;
        try(PreparedStatement stmt = conn.prepareStatement(sql)){
            // other user is user1_id if the client is accepting
            stmt.setInt(1, profile);
            stmt.setInt(2, client);
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 1;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    // for removing users
    public boolean removeUser(int client){
        String sql = "DELETE FROM friends WHERE user1 = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)){;
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
        if(checkStatus(client, profile).equals(FriendStatus.ADDED)){
            removeUser(client);
        }
        try (PreparedStatement stmt = conn.prepareStatement(sql)){;
            stmt.setInt(1, client);
            stmt.setInt(2, profile);
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 1;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    // returns the amount of pending requests
    public int checkPending(int client){
        String sql = """
                SELECT COUNT(*) FROM friends
                WHERE (user1_id = ? OR user2_id = ?)
                AND status ='pending'
                """;
        try (PreparedStatement stmt = conn.prepareStatement(sql)){
            stmt.setInt(1, client);
            stmt.setInt(2, client);
            ResultSet rs = stmt.executeQuery();
            if(rs.next()){
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return 0;
    }

    // gets the number of ingoing friend requests
    public int getNumberOfRequestsIngoing(int client){
        String sql = """
                SELECT COUNT(*) FROM friends
                WHERE user2_id = ?
                AND status ='pending'
                """;
        try(PreparedStatement stmt = conn.prepareStatement(sql)){
            stmt.setInt(1, client);
            ResultSet rs = stmt.executeQuery();
            if(rs.next()){
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return 0;
    }

    // gets the number of outgoing requests
    public int getNumberOfRequestsOutgoing(int client){
        String sql = """
                SELECT COUNT(*) FROM friends
                WHERE user1_id = ?
                AND status ='pending'
                """;
        try(PreparedStatement stmt = conn.prepareStatement(sql)){
            stmt.setInt(1, client);
            ResultSet rs = stmt.executeQuery();
            if(rs.next()){
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return 0;
    }
    // gets the list of users who are the subject of ingoing friend requests
    public List<Integer> getListOfRequestsIngoing(int client){
        String sql = """
                SELECT user1_id FROM friends
                WHERE user2_id = ?
                AND status ='pending'
                """;

        List<Integer> listOfUsers = new ArrayList<>();
        try(PreparedStatement stmt = conn.prepareStatement(sql)){
            stmt.setInt(1, client);
            ResultSet rs = stmt.executeQuery();
            if(rs.next()){
                listOfUsers.add(rs.getInt(1));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return listOfUsers;
    }

    // gets the list of users who are the subject of outgoing friend requests
    public List<Integer> getListOfRequestsOutgoing(int client){
        String sql = """
                SELECT user2_id FROM friends
                WHERE user1_id = ?
                AND status ='pending'
                """;

        List<Integer> listOfUsers = new ArrayList<>();
        try(PreparedStatement stmt = conn.prepareStatement(sql)){
            stmt.setInt(1, client);
            ResultSet rs = stmt.executeQuery();
            if(rs.next()){
                listOfUsers.add(rs.getInt(1));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return listOfUsers;
    }
}