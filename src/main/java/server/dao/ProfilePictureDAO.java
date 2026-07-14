package server.dao;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Base64;
import java.util.List;

import server.database.DatabaseConnection;

public class ProfilePictureDAO {

    private final Connection conn;

    public ProfilePictureDAO() { this.conn = DatabaseConnection.getInstance().getConnection(); }

    public boolean checkProfilePicture(int userID){
        String sql = "SELECT * FROM profile_pictures WHERE user_id = ?";

        try(PreparedStatement stmt = conn.prepareStatement(sql)){
            stmt.setInt(1, userID);
            ResultSet rs = stmt.executeQuery();
            return rs.next();
        } catch(SQLException e){
            throw new RuntimeException(e);
        }
    }

    public boolean addProfilePicture(int userID, String filepath){
        String sql = "INSERT INTO profile_pictures (user_id, file_path) VALUES (?, ?)";

        try (PreparedStatement stmt = conn.prepareStatement(sql)){
            stmt.setInt(1, userID);
            stmt.setString(2, filepath);

            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean changeProfilePicture(int userID, String filepath) {
        String sql = "UPDATE profile_pictures SET file_path = ? WHERE user_id = ?";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, filepath);
            stmt.setInt(2, userID);
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean removeProfilePicture(int userID){
        String sql = "DELETE FROM profile_pictures WHERE user_id = ?";

        try (PreparedStatement stmt = conn.prepareStatement(sql)){
            stmt.setInt(1, userID);
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e){
            throw new RuntimeException(e);
        }
    }

    public String getProfilePictureFileName(int userID){
        String sql = "SELECT * FROM profile_pictures WHERE user_id = ?";

        // get the filepath then get the image to send to the user
        try (PreparedStatement stmt = conn.prepareStatement(sql)){
            stmt.setInt(1, userID);
            try (ResultSet rs = stmt.executeQuery()){
                if(rs.next()){
                    // got the file path
                    return rs.getString("file_path");
                }
            } catch (SQLException e){
                throw new RuntimeException(e);
            }
        } catch (SQLException e){
            throw new RuntimeException(e);
        }
        return null;
    }

    public String getProfilePicturePath(int userID){
        String sql = "SELECT * FROM profile_pictures WHERE user_id = ?";

        // get the filepath then get the image to send to the user
        try (PreparedStatement stmt = conn.prepareStatement(sql)){
            stmt.setInt(1, userID);
            try (ResultSet rs = stmt.executeQuery()){
                if(rs.next()){
                    // got the file path
                    String fileName = rs.getString("file_path");
                    return "server-data/profile-pictures/" + fileName;
                }
            } catch (SQLException e){
                throw new RuntimeException(e);
            }
        } catch (SQLException e){
            throw new RuntimeException(e);
        }
        return null;
    }


}