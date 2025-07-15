package org.example.dao;

import org.example.entity.User;

import java.sql.SQLException;
import java.util.List;

/**
 * The Data Access Object interface for User-related database operations.
 * This version includes the method for auto-generating User IDs.
 */
public interface UserDAO {
    boolean save(User user) throws SQLException;
    boolean update(User user) throws SQLException;
    boolean delete(String userId) throws SQLException;
    User findById(String userId) throws SQLException;
    List<User> findAll() throws SQLException;
    User findByUsernameAndPassword(String username, String password) throws SQLException;

    String generateNextId() throws SQLException;
}
