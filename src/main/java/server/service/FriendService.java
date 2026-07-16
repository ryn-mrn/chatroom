package server.service;

import server.dao.FriendsDAO;
import server.dao.UserDAO;

import server.models.FriendStatus;

public class FriendService {

    private final FriendsDAO friendsDAO = new FriendsDAO();
    private final UserDAO userDAO = new UserDAO();


    public FriendStatus getStatus(int client, int profile){
        return friendsDAO.checkStatus(client, profile);
    }

    public boolean addFriend(int client, int profile) {
        System.out.println("Adding friend " + userDAO.getUsername(client) + " " + userDAO.getUsername(profile));
        return friendsDAO.addUser(client, profile);
    }

    public boolean blockUser(int client, int profile) {
        System.out.println("Blocking friend"  + userDAO.getUsername(client) + " " + userDAO.getUsername(profile));
        return friendsDAO.blockUser(client, profile);
    }

    public boolean removeUser(int client) {
        System.out.println("Removing friend " + userDAO.getUsername(client));
        return friendsDAO.removeUser(client);
    }

    public int checkPending(int client){
        return friendsDAO.checkPending(client);
    }
}