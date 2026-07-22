package com.chatroom.server;

import com.chatroom.server.server.service.*;
import com.chatroom.server.service.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import server.service.*;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {
    private ServerSocket serverSocket;
    private final ExecutorService pool = Executors.newFixedThreadPool(10);
    private static final List<PrintWriter> clients = new CopyOnWriteArrayList<>();
    private static final Map<Integer, PrintWriter> onlineUsers = new ConcurrentHashMap<>();
    private static final SessionManager sessionManager = new SessionManager();
    private static final AuthService as = new AuthService(sessionManager);
    private static final FriendService fs = new FriendService();
    private static final MessageService ms = new MessageService();
    private static final ProfileService ps = new ProfileService();
    private static final InboxService is = new InboxService();
    private static final Logger log = LoggerFactory.getLogger(Server.class);

    public void start(int port) throws IOException {
        serverSocket = new ServerSocket(port);
        log.info("Server started on port {}", port);

        while (!serverSocket.isClosed()) {
            Socket clientSocket = serverSocket.accept();
            // add client to the thread pool -- handles all connected clients and the sessions
            pool.execute(new ClientHandler(clientSocket, clients, onlineUsers, as, fs, ms, ps, is));
            log.info("Server accepted a new client {}", clientSocket.getInetAddress());
        }
    }

    public void stop() throws IOException {
        serverSocket.close();
        pool.shutdown();
    }

    public static void broadcast(String message) {
        log.debug("Broadcasting to {} clients", clients.size());
        for (PrintWriter client : clients) {
            // sends the message to all the clients, client side handles ui
            client.println(message);
        }
    }
}