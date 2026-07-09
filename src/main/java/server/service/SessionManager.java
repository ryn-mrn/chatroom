package server.service;

import server.models.Session;

import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class SessionManager {
    private final Map<String, Session> sessions = new ConcurrentHashMap<>();
    private final ScheduledExecutorService cleaner = Executors.newSingleThreadScheduledExecutor();
    private final Set<String> loggedInUsers = ConcurrentHashMap.newKeySet();

    public SessionManager() {
        // clean expired sessions every minute
        cleaner.scheduleAtFixedRate(this::removeExpiredSessions, 1, 1, TimeUnit.MINUTES);
    }

    public Session createSession(String username) {
        String token = UUID.randomUUID().toString();
        Session session = new Session(token, username, System.currentTimeMillis());
        sessions.put(token, session);
        System.out.println("SESSIONS ONGOING: " + sessions.size());
        loggedInUsers.add(username);
        return session;
    }

    // this is for checking back in after stopping a while
    public Optional<Session> validateSession(String token) {
        System.out.println("VALIDATING TOKEN: " + token);
        System.out.println("STORE SIZE: " + sessions.size());
        System.out.println("STORE CONTENTS: " + sessions.keySet());
        Session session = sessions.get(token);
        if (session == null){
            return Optional.empty();
        }
        if (session.isExpired()){
            invalidateSession(token);
            return Optional.empty();
        }
        session.refreshLastActive(); // sliding expiry
        return Optional.of(session);
    }

    public void invalidateSession(String token) {
        Session session = sessions.remove(token);
        if (session != null){
            removeLoggedInUser(session.getUsername());
        }
    }

    private void removeExpiredSessions() {
        sessions.entrySet().removeIf(e -> {
            boolean expired = e.getValue().isExpired();
            if (expired) loggedInUsers.remove(e.getValue().getUsername());
            return expired;
        });
    }

    public boolean isLoggedIn(String username){
        return loggedInUsers.contains(username);
    }

    private void removeLoggedInUser(String username){
        loggedInUsers.remove(username);
    }
}