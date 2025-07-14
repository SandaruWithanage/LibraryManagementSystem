package org.example.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Manages the database connection using the Singleton pattern.
 * This ensures that only one instance of the connection is created,
 * which is efficient and prevents resource leaks.
 */
public class DBConnection {

    private static DBConnection dbConnection;
    private final Connection connection;

    /**
     * Private constructor to prevent direct instantiation.
     * It loads the MySQL driver and establishes a connection.
     * @throws SQLException if a database access error occurs.
     */
    private DBConnection() throws SQLException {
        // Define connection properties
        String url = "jdbc:mysql://localhost:3306/library_db";
        Properties props = new Properties();
        props.setProperty("user", "root");
        props.setProperty("password", ""); // <-- IMPORTANT: Set your MySQL password here

        // Establish the connection
        this.connection = DriverManager.getConnection(url, props);
    }

    /**
     * Provides the single instance of the DBConnection.
     * @return The singleton instance of DBConnection.
     * @throws SQLException if the connection cannot be established.
     */
    public static DBConnection getInstance() throws SQLException {
        if (dbConnection == null) {
            dbConnection = new DBConnection();
        }
        return dbConnection;
    }

    /**
     * Returns the active database connection.
     * @return The active java.sql.Connection object.
     */
    public Connection getConnection() {
        return connection;
    }
}
