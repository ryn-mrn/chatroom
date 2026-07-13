package server.service;

import server.dao.ProfilePictureDAO;
import server.dao.UserDAO;

import java.io.*;
import java.util.Base64;

// may add changing names bios, etc, pictures for now

public class ProfileService {

    private final ProfilePictureDAO profilePictureDAO = new ProfilePictureDAO();
    private final UserDAO userDAO = new UserDAO();

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

    public String getPhoto(int userID) {
        String filePath = profilePictureDAO.getProfilePicturePath(userID);

        if (filePath == null || filePath.isBlank()) {
            return "no photo";
        }

        File imageFile = new File(filePath);
        if(!imageFile.exists()) {

            System.out.println("Missing photo: " + filePath);
            return "no photo";
        }
        try (FileInputStream imageInFile = new FileInputStream(imageFile)){
            // read the file to bytes
            // save as bytes
            byte[] imageData = imageInFile.readAllBytes();
                // add the saved stream to the bytes var
                // convert the bytes to a string
                // return the base64 string
            return Base64.getUrlEncoder().encodeToString(imageData);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public String getUsername(int userID){
        return userDAO.getUsername(userID);
    }
}