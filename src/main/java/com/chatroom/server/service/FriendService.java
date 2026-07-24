package com.chatroom.server.service;

import com.chatroom.server.dao.FriendsDAO;
import com.chatroom.server.dao.UserDAO;

import com.chatroom.common.*;

import java.util.List;

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

    public boolean acceptUser(int client, int profile){
        return friendsDAO.acceptUser(client, profile);
    }

    public int checkPending(int client){
        return friendsDAO.checkPending(client);
    }

    public boolean checkIngoingFromOneUser(int client, int profile) {
        return friendsDAO.checkIngoingFromOneUser(client, profile);
    }

    public boolean checkOutGoingFromOneUser(int client, int profile){
        return friendsDAO.checkOutgoingToOneUser(client, profile);
    }

    public int checkNumberOfIngoingRequests(int client){
        return friendsDAO.getNumberOfRequestsIngoing(client);
    }

    public int checkNumberOfOutgoingRequests(int client){
        return friendsDAO.getNumberOfRequestsOutgoing(client);
    }

    public List<Integer> getListOfRequestsIngoing(int client){
        return friendsDAO.getListOfRequestsIngoing(client);
    }

    public List<Integer> getListOfRequestsOutgoing(int client){
        return friendsDAO.getListOfRequestsOutgoing(client);
    }
}