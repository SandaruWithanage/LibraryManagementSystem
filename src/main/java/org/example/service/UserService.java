package org.example.service;

import org.example.dto.UserDTO;

import java.sql.SQLException;
import java.util.List;

/**
 * The service layer interface for User-related business logic.
 * It defines the operations that the UI controllers can perform.
 */
public interface UserService {
    boolean addUser(UserDTO userDTO) throws SQLException;
    boolean updateUser(UserDTO userDTO) throws SQLException;
    boolean deleteUser(String userId) throws SQLException;
    UserDTO getUserById(String userId) throws SQLException;
    List<UserDTO> getAllUsers() throws SQLException;
    String generateNextUserId() throws SQLException; // Assuming you might want this later
}
