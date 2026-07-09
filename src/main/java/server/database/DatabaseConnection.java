package server.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseConnection {
    private static volatile DatabaseConnection instance;
    private Connection conn;
    private static final String URL = "jdbc:sqlite:chatroom.db";

    private DatabaseConnection() {
        try {
            conn = DriverManager.getConnection(URL);
            System.out.println("Database connection established.");
            initializeTables();
        } catch (SQLException e) {
            System.err.println("Database connection failed: " + e.getMessage());
        }
    }

    // single use of database connection to be used throughout the program
    public static DatabaseConnection getInstance() {
        if (instance == null) {
            synchronized (DatabaseConnection.class) {
                if (instance == null) {
                    instance = new DatabaseConnection();
                }
            }
        }
        return instance;
    }

    public Connection getConnection() {
        try {
            // Reconnect if connection is closed
            if (conn == null || conn.isClosed()) {
                conn = DriverManager.getConnection(URL);
            }
        } catch (SQLException e) {
            System.err.println("Error getting connection: " + e.getMessage());
        }
        return conn;
    }

    private void initializeTables() {
        // Fixed: AUTOINCREMENT should be one word in SQLite
        String usersTable = """
            CREATE TABLE IF NOT EXISTS Users (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                username TEXT NOT NULL UNIQUE,
                password TEXT NOT NULL
            )
            """;

        // the plan is to create a table full of messages that haven't expired yet using the sessions
        // so that when a new user joins these recent messages can be loaded in so the user can
        // join the conversation

        // create profile picture table
        String profilePictureTable = """
               CREATE TABLE IF NOT EXISTS profile_pictures (
                  user_id INTEGER NOT NULL,
                  file_path TEXT NOT NULL,
                  uploaded_at TEXT NOT NULL DEFAULT (datetime('now')),
                  FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
               )
               """;


        // timestamp for getting the correct time
        String messagesTable = """
                CREATE TABLE IF NOT EXISTS messages (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    message TEXT NOT NULL,
                    time_sent DATETIME DEFAULT CURRENT_TIMESTAMP,
                    user_id INTEGER NOT NULL,
                    FOREIGN KEY(user_id) REFERENCES users(user_id)
                )""";


        // friend request table needed
        String friendsTable = """
                CREATE TABLE IF NOT EXISTS friends (
                    user1_id INTEGER PRIMARY KEY,
                    user2_id INTEGER,
                    status TEXT,
                    FOREIGN KEY(user2_id) REFERENCES users(user_id)
                )""";


        try (Statement stmt = conn.createStatement()) {
            stmt.execute(usersTable);
            System.out.println("Users table initialized.");
            stmt.execute(profilePictureTable);
            System.out.println("Profile picture table initialized.");
            stmt.execute(messagesTable);
            System.out.println("Messages table initialized");
            stmt.execute(friendsTable);
            System.out.println("Friends table initialized");
        } catch (SQLException e) {
            System.err.println("Error creating tables: " + e.getMessage());
        }
    }

    // closes the connection when the application finishes
    public void closeConnection() {
        try {
            if (conn != null && !conn.isClosed()) {
                conn.close();
                System.out.println("Database connection closed.");
            }
        } catch (SQLException e) {
            System.err.println("Error closing connection: " + e.getMessage());
        }
    }
}