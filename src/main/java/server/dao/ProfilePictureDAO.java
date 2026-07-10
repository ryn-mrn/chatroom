package server.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import server.database.DatabaseConnection;

public class ProfilePictureDAO {

    private final Connection conn;

    public ProfilePictureDAO() { this.conn = DatabaseConnection.getInstance().getConnection(); }

    public boolean checkProfilePicture(int userID){
        String sql = "SELECT * FROM profile_pictures WHERE user_id = ?";

        try(PreparedStatement stmt = conn.prepareStatement(sql)){
            stmt.setInt(1, userID);
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        }catch(SQLException e){
            throw new RuntimeException(e);
        }
    }

    public boolean addProfilePicture(int userID, String filepath){
        String sql = "INSERT INTO profile_pictures (user, file_path) VALUES (?, ?)";

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
        String sql = "UPDATE profile_pictures SET filepath = ? WHERE user_id = ?";

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
}
