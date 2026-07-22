package com.chatroom.server.dao;

import com.chatroom.server.database.DatabaseConnection;
import com.chatroom.server.models.DirectMessage;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class DirectMessageDAO {
    private final Connection conn;


    public DirectMessageDAO(){ this.conn = DatabaseConnection.getInstance().getConnection(); }
    public List<DirectMessage> getDirectMessages(int client, int user){
        List<DirectMessage> messages = new ArrayList<>();
        String sql = "SELECT * FROM direct_messages" +
                "WHERE user1_id = ? AND user2_id = ?";

        try(PreparedStatement stmt = conn.prepareStatement(sql)){
            stmt.setInt(1, client);
            stmt.setInt(2, user);
            try(ResultSet resultSet = stmt.executeQuery()){
                if(resultSet.next()){
                    while(resultSet.next()){
                        messages.add(new DirectMessage(
                                resultSet.getInt(2),
                                resultSet.getString(3),
                                resultSet.getString(4))
                        );
                    }
                    return messages;
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    public List<DirectMessage> getUnreadMessages(int client){
        List<DirectMessage> messages = new ArrayList<>();
        String sql = "SELECT * FROM direct_messages" +
                "WHERE user1_id = ? AND status ='unread'";

        try(PreparedStatement stmt = conn.prepareStatement(sql)){
            stmt.setInt(1, client);
            try(ResultSet rs = stmt.executeQuery()){
                if(rs.next()){
                    while(rs.next()){
                        messages.add(new DirectMessage(
                                rs.getInt(2),
                                rs.getString(3),
                                rs.getString(4)
                        ));
                    }
                    return messages;
                }
            } catch (RuntimeException e) {
                throw new RuntimeException(e);
            }
            return null;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    // counts the number of unread messages
    public int getNumberOfUnreadMessages(int client){
        String sql = "SELECT COUNT(*) FROM direct_messages " +
                "WHERE user1_id = ? AND status = 'unread'";

        try(PreparedStatement stmt = conn.prepareStatement(sql)){
            stmt.setInt(1, client);
            try(ResultSet rs = stmt.executeQuery()){
                if(rs.next()){
                    return rs.getInt(1);
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return 0;
    }
}
