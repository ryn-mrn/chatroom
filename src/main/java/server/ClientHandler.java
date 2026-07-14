package server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import server.models.FriendStatus;
import server.models.Message;
import server.models.MessageType;
import server.models.Session;
import server.service.AuthService;
import server.service.FriendService;
import server.service.MessageService;
import server.service.ProfileService;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class ClientHandler implements Runnable {

    private static final Logger log = LoggerFactory.getLogger(ClientHandler.class);
    private final Socket clientSocket;
    private final List<PrintWriter> clients;
    private final AuthService authService;
    private final FriendService friendService;
    private final MessageService messageService;
    private final ProfileService profileService;
    private String clientUsername = "";
    private Session session;
    private record FriendContext(int clientID, int profileID, FriendStatus status) {}

    private FriendContext getFriendContext(Message message){
        int clientID = authService.getUserID(clientUsername);
        int profileID = authService.getUserID(message.getUsername());
        FriendStatus status = friendService.getStatus(clientID, profileID);
        return new FriendContext(clientID, profileID, status);
    }

    public ClientHandler(Socket socket, List<PrintWriter> clients,
                         AuthService authService,
                         FriendService friendService,
                         MessageService messageService,
                         ProfileService profileService) {
        this.clientSocket = socket;
        this.clients = clients;
        this.authService = authService;
        this.friendService = friendService;
        this.messageService = messageService;
        this.profileService = profileService;
    }

    @Override
    public void run(){
        // get the input and output of the client
        PrintWriter out = null;
        BufferedReader in = null;
        try {
            // get the outputstream of client
            out = new PrintWriter(
                    clientSocket.getOutputStream(), true);
            clients.add(out);
            // get the input stream of client
            in = new BufferedReader(
                    new InputStreamReader(
                            clientSocket.getInputStream(), StandardCharsets.UTF_8));
            log.debug("Client initialized");
            String message;
            while ((message = in.readLine()) != null) {
                System.out.println(message);
                Message messageModel = Message.deserialize(message);
                // null check to get rid of yellow warning hint
                if (messageModel == null) {
                    log.warn("Failed to deserialize message: {}", message);
                    // line skip
                    continue;
                }
                handleMessage(messageModel, out);
            }
        }
        catch (IOException e) {
            log.debug("Error running client thread {}", e.getMessage());
        }
        finally {
            try {
                if (out != null) {
                    clients.remove(out);
                    out.close();
                }
                if (in != null) {
                    in.close();
                    clientSocket.close();
                }
            }
            catch (IOException e) {
                log.debug("Error closing client thread {}", e.getMessage());
            }
        }
    }

    // enum message type
    private void handleMessage(Message message, PrintWriter out) {
        if(message.getType() == null){
            log.warn("Unknown message type, broadcasting raw message");
            Server.broadcast(message.getMessage());
            return;
        }
        // if getting the profile's username from the message then a message service could get that
        // but its already doing that from the message....
        switch(message.getType()){
            case QUIT -> {}
            case LOGIN_REQUEST -> handleLogin(message, out);
            // checks if the user is registering
            // checks if the user is applying the credentials
            case REGISTER_CONFIRM -> handleRegister(message, out);
            case LOGOUT -> handleLogout(message, out);
            case CHATROOM -> handleChatroom(out);
            case CHAT -> handleChat(message, out);
            case PROFILE_OPEN -> handleProfileOpen(message, out);
            case ADD -> handleAdd(message, out);
            case BLOCK -> handleBlock(message, out);
            case REMOVE -> handleRemove(message, out);
            case PICTURE -> handlePicture(message, out);
            case PROFILE_REQUEST -> handleProfileRequest(message, out);
        }
    }

    // all the handles
    private void handleLogin(Message message, PrintWriter out){
        log.debug("Login button pressed from LOGIN_CONTROLLER");
        // deserialize the message for the username and password
        String username = message.getUsername();
        String password = message.getPassword();
        // get the login response
        String loginReply = authService.handleLogin(username, password);
        System.out.println(loginReply);
        if(loginReply.equals("LOGIN_SUCCESS")){
            session = authService.createSession(username);
            // sets the username after logging in
            this.clientUsername = session.getUsername();
            // send login reply and session id to the user
            // e.g "LOGIN_SUCCESS ioef89s7f98dsf90sfds90f", all sent as one
            System.out.println(session.getSessionID());
            out.println(loginReply + " " + session.getSessionID());
        }
        else {
            out.println(loginReply);
        }
    }

    private void handleRegister(Message message, PrintWriter out){
        log.debug("Register button pressed from REGISTER_CONTROLLER");
        String registerUsername = message.getUsername();
        String registerPassword = message.getPassword();
        System.out.println(registerUsername);
        // checks the register works properly and returns an error to the client
        // change to authService
        String reply = authService.handleRegister(registerUsername, registerPassword);
        if(reply.equals("REGISTER_SUCCESS")){
            profileService.newProfile(authService.getUserID(registerUsername));
        }
        System.out.println(reply);
        // send the register message back to the user
        out.println(reply);
    }

    private void handleLogout(Message message, PrintWriter out){
        authService.invalidateSession(message.getSessionID());
        out.println("LOGOUT_SUCCESS");
    }

    private void handleChatroom(PrintWriter out){
        // sends all the messages to the user
        List<Message> recentMessages = messageService.getMessages();
        // sends recent messages to newly joined user
        for(Message m : recentMessages){
            System.out.println(m.fullMessage());
            out.println(m.fullMessage());
        }
    }

    private void handleChat(Message message, PrintWriter out){
        log.info("Token received from client {}", message.getSessionID());
        log.info("Message received {}", message.getMessage());
        Optional<Session> validated = authService.validateSession(message.getSessionID());
        if (validated.isEmpty()) {
            out.println("SESSION_EXPIRED");
            session = null;
            return;
        }
        // token is good -- safe to proceed
        // send message to all users
        Session validSession = validated.get();
        log.debug("Chat opened");
        System.out.println(message.fullMessage());
        System.out.println(message.getMessage());
        // Message Service
        messageService.addMessage(message.getMessage(), validSession.getUsername());
        Server.broadcast(message.getUsername() + ": " + message.getMessage());
    }

    // get the friend context to see if they are added / blocked / etc.
    private void handleProfileOpen(Message message, PrintWriter out){
        log.debug("Profile opened");
        // sends the two user's status back to the user who opened the profile
        FriendContext ctx = getFriendContext(message);
        out.println(ctx.status());
    }

    private void handleAdd(Message message, PrintWriter out){
        log.debug("Add pressed");
        FriendContext ctx = getFriendContext(message);
        if(ctx.status == FriendStatus.NO_INTERACTION){
            log.info("Friend request {}", friendService.addFriend(ctx.clientID(), ctx.profileID()));
            out.println("PENDING");
        }
    }

    private void handleBlock(Message message, PrintWriter out){
        log.debug("Block pressed");
        FriendContext ctx = getFriendContext(message);
        if (ctx.status() == FriendStatus.NO_INTERACTION || ctx.status() == FriendStatus.ADDED) {
            log.info("Blocked {}", friendService.blockUser(ctx.clientID(), ctx.profileID()));
            out.println("BLOCKED_CLIENT");
        } else {
            log.debug("Error blocking {} and {}", ctx.clientID(), ctx.profileID());
        }
    }

    private void handleRemove(Message message, PrintWriter out){
        log.debug("Remove pressed");
        FriendContext ctx = getFriendContext(message);
        if (ctx.status() == FriendStatus.ADDED) {
            log.info("Removed {}", friendService.removeUser(ctx.clientID()));
            // out.println("REMOVED_USER") -- formatted
        } else {
            log.debug("Error removing {} and {}", ctx.clientID(), ctx.profileID());
        }
    }

    // for profile pictures at the moment
    private void handlePicture(Message message, PrintWriter out){
        // needs serialized - deserialized. Server should have a GUI itself...
        log.debug("Picture sent");

        Optional<Session> validated = authService.validateSession(message.getSessionID());
        System.out.println(validated);
        if (validated.isEmpty()) {
            out.println("SESSION_EXPIRED");
            return;
        }
        String username = validated.get().getUsername();
        // used to track which profile picture is who
        int userID = authService.getUserID(username);
        // use this to save to folder that holds the pictures
        String base64image = (String) message.getPayload().get("picture");
        // removes profile picture
        // use this to save the filename of the pictures to the database
        String fileName = (String) message.getPayload().get("filename");
        if(base64image.equals("remove")){
            System.out.println("Removing photo: "+ profileService.removeProfilePicture(userID));
            out.println("REMOVED_PHOTO");
        } else {
            // save the photo
            System.out.println("Photo saved: " + profileService.savePhoto(base64image, fileName));
        }
        // checks if the picture exists or not and decides from there
        if(profileService.checkProfilePicture(userID)){
            System.out.println("Changing picture: " + profileService.changeProfilePicture(userID, fileName));
        } else {
            System.out.println("Adding picture: " + profileService.addProfilePicture(userID, fileName));
        }
        Message msg = new Message();
        msg.setType(MessageType.PICTURE);
        Map<String, Object> payload = new HashMap<>();
        payload.put("username", username);
        payload.put("photo", base64image);
        msg.setPayload(payload);
        out.println(msg);
        // handle the image to the database
        // add the file path, user id and date of creation to a profile picture table
        // save the photo to the data/profile-pictures
    }


    // get the current users in the session and send only those profile pictures
    private void handleProfileRequest(Message message, PrintWriter out){
        // get clients and their ids and send a message with their avatar
        ArrayList<Integer> loggedInUsers = authService.getLoggedInUsers();
        // get list of the profiles
        for(int userID : loggedInUsers) {
            // map of username --- picture
            Message msg = new Message();
            msg.setType(MessageType.PICTURE);
            Map<String, Object> payload = new HashMap<>();
            payload.put("username", profileService.getUsername(userID));
            payload.put("photo", profileService.getPhoto(userID));
            msg.setPayload(payload);
            out.println(msg.serialize());
        }
    }
}