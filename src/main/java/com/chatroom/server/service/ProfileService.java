package com.chatroom.server.service;

import com.chatroom.server.dao.ProfilePictureDAO;
import com.chatroom.server.dao.UserDAO;

import java.io.*;
import java.util.Base64;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// may add changing names bios, etc, pictures for now

public class ProfileService {

    private final ProfilePictureDAO profilePictureDAO = new ProfilePictureDAO();
    private final UserDAO userDAO = new UserDAO();
    private final Logger log = LoggerFactory.getLogger(ProfileService.class);

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
        byte[] data = Base64.getUrlDecoder().decode(base64Image);
        String path;
        path = "server-data/profile-pictures/" + fileName;
        File file = new File(path);
        try {
            FileOutputStream osf = new FileOutputStream(file);
            osf.write(data);
            osf.flush();
            log.info("Writing data");
            return true;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public String getPhoto(int userID) {
        String filePath = profilePictureDAO.getProfilePicturePath(userID);

        // for new users
        if (filePath == null || filePath.isBlank()) {
            newProfile(userID);
            return "no photo";
        }

        File imageFile = new File(filePath);

        // for broken files
        if(!imageFile.exists()) {
            System.out.println("Missing photo: " + filePath);
            profileFix(userID);
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

    public void newProfile(int userID){
        String fileName = "default_pfp.jpg";
        profilePictureDAO.addProfilePicture(userID, fileName);
    }

    public void profileFix(int userID){
        String fileName = "default_pfp.jpg";
        profilePictureDAO.changeProfilePicture(userID, fileName);
    }
}