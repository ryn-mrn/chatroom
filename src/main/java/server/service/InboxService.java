package server.service;


// Class is for managing direct messages and friend requests

import server.dao.DirectMessageDAO;
import server.dao.FriendsDAO;

public class InboxService {

    private final DirectMessageDAO dmDAO = new DirectMessageDAO();
    private final FriendsDAO friendsDAO = new FriendsDAO();

    public int getNotifications(int client){
        int directMessages = dmDAO.getNumberOfUnreadMessages(client);
        int friendRequests = friendsDAO.checkPending(client);

        return directMessages + friendRequests;
    }

    public int getUnreadMessages(int client){
        return dmDAO.getNumberOfUnreadMessages(client);
    }

    public int getFriendRequests(int client){
        return friendsDAO.checkPending(client);
    }

}