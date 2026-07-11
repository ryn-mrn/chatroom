package server.service;

import server.dao.UserDAO;
import server.models.Session;
import server.models.User;

import java.util.ArrayList;
import java.util.Optional;
import java.util.Set;

public class AuthService {

    private final UserDAO userDAO;
    private final SessionManager sessionManager;

    // creating an authentication service to separate the logic from the ui
    public AuthService(SessionManager sm) {
        this.userDAO = new UserDAO();
        this.sessionManager = sm;
    }

    public boolean login(String username, String password) {
        User user = userDAO.getUser(username);
        if(user == null) {
            return false;
        }
        else return password.equals(user.getPassword());
    }

    // after all checks the user can register
    public boolean register(String username, String password) {
        System.out.println("AUTH SERVICE -- SERVER REGISTER CALLED");
        return userDAO.addUser(username, password);
    }

    //checks if the user exists
    public boolean userExist(String username){
        return userDAO.getUser(username) != null;
    }

    public int getUserID(String username){
        return userDAO.getUserID(username);
    }

    // session methods
    public Session createSession(String username) { return sessionManager.createSession(username); }
    public Optional<Session> validateSession(String token) { return sessionManager.validateSession(token); }
    public void invalidateSession(String token) { sessionManager.invalidateSession(token); }
    public Set<String> getLoggedInUsernames(){
        return sessionManager.getLoggedInUsers();
    }

    public ArrayList<Integer> getLoggedInUsers(){
        Set<String> loggedInUsers = sessionManager.getLoggedInUsers();
        ArrayList<Integer> userIDs = new ArrayList<>();
        for(String user : loggedInUsers){
            userIDs.add(userDAO.getUserID(user));
        }
        return userIDs;
    }

    // handleLogin, handleRegister
    public String handleLogin(String username, String password){
        if(!isLoggedIn(username)){
            boolean success = login(username, password);
            return success ? "LOGIN_SUCCESS" : "LOGIN_FAILED";
        } else {
            return "LOGIN_FAILED";
        }
    }

    public String handleRegister(String username, String password){
        System.out.println("FUNC: handleRegister called");
        if(userExist(username)){
            return "REGISTER_FAIL:USERNAME_TAKEN";
        }
        boolean success = register(username, password);
        return success ? "REGISTER_SUCCESS" : "REGISTER_FAIL:SERVER_ERROR";
    }

    public boolean isLoggedIn(String username) {
        return sessionManager.isLoggedIn(username);
    }
}