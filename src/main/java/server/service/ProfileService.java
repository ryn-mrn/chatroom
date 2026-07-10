package server.service;

import server.dao.ProfilePictureDAO;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.Base64;

// may add changing names bios, etc, pictures for now

public class ProfileService {

    private final ProfilePictureDAO profilePictureDAO = new ProfilePictureDAO();

    public boolean checkProfilePicture(int userID) {
        System.out.println("Checking if profile picture exists for: " + userID);
        return profilePictureDAO.checkProfilePicture(userID);
    }

    public boolean addProfilePicture(int userID, String filepath) {
        System.out.println("Adding profile picture for: " + userID);
        return profilePictureDAO.addProfilePicture(userID, filepath);
    }

    public boolean changeProfilePicture(int userID, String filepath) {
        System.out.println("Changing profile picture for: " + userID);
        return profilePictureDAO.changeProfilePicture(userID, filepath);
    }

    public boolean removeProfilePicture(int userID) {
        System.out.println("Removing profile picture for: " + userID);
        return profilePictureDAO.removeProfilePicture(userID);
    }

    // save the photo
    public boolean savePhoto(String base64Image, String fileName) {
        byte[] data = Base64.getDecoder().decode(base64Image);
        String path;
        path = "server-data/pictures/" + fileName;
        File file = new File(path);

        try (OutputStream outputStream = new BufferedOutputStream(new FileOutputStream(file))) {
            outputStream.write(data);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}
