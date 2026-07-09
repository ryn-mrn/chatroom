package server.models;

import java.time.LocalDateTime;

public class Session {

    private String sessionID;
    private String username;
    private long createdAt;
    private long lastActive;
    private static long TIMEOUT_MS = 60 * 30 * 1000;

    public Session(String sessionID, String username, long createdAt, LocalDateTime expiresAt) {
        this.sessionID = sessionID;
        this.username = username;
        this.createdAt = createdAt;
        this.lastActive = createdAt;
    }

    //for getting sessions
    public Session(String sessionId) {
    }

    public Session(String token, String username, long createdAt) {
        this.sessionID = token;
        this.username = username;
        this.createdAt = createdAt;
        this.lastActive = createdAt;
    }

    public String getSessionID() { return sessionID; }
    public String getUsername() { return username; }
    private void setSessionID(String sessionID) { this.sessionID = sessionID; };


    public boolean isExpired() {
        return System.currentTimeMillis() - lastActive > TIMEOUT_MS;
    }

    public void refreshLastActive() {
        this.lastActive = System.currentTimeMillis();
    }
}
