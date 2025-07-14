package org.example.dao;

import org.example.entity.User;
import java.sql.SQLException;

/**
 * The Data Access Object interface for User-related database operations.
 * It defines the contract for what operations can be performed, abstracting
 * the underlying implementation details.
 */
public interface UserDAO {

    /**
     * Finds a user by their username and password.
     * @param username The username to search for.
     * @param password The password to match.
     * @return A User object if a match is found, otherwise null.
     * @throws SQLException if a database access error occurs.
     */
    User findByUsernameAndPassword(String username, String password) throws SQLException;
}
