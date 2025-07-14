package org.example.service.impl;

import org.example.dao.UserDAO;
import org.example.dao.impl.UserDAOImpl;
import org.example.dto.UserDTO;
import org.example.entity.User;
import org.example.service.UserService;

import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;

/**
 * The concrete implementation of the UserService interface.
 * The generateNextUserId method has been removed.
 */
public class UserServiceImpl implements UserService {

    private final UserDAO userDAO = new UserDAOImpl();

    @Override
    public boolean addUser(UserDTO userDTO) throws SQLException {
        return userDAO.save(mapToEntity(userDTO));
    }

    @Override
    public boolean updateUser(UserDTO userDTO) throws SQLException {
        return userDAO.update(mapToEntity(userDTO));
    }

    @Override
    public boolean deleteUser(String userId) throws SQLException {
        return userDAO.delete(userId);
    }

    @Override
    public UserDTO getUserById(String userId) throws SQLException {
        User user = userDAO.findById(userId);
        return (user != null) ? mapToDTO(user) : null;
    }

    @Override
    public List<UserDTO> getAllUsers() throws SQLException {
        return userDAO.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    // --- Helper mapping methods ---
    private User mapToEntity(UserDTO dto) {
        return new User(
                dto.getUserId(),
                dto.getName(),
                dto.getContact(),
                dto.getMembershipDate(),
                dto.getUsername(),
                dto.getPassword()
        );
    }

    private UserDTO mapToDTO(User entity) {
        return new UserDTO(
                entity.getUserId(),
                entity.getName(),
                entity.getContact(),
                entity.getMembershipDate(),
                entity.getUsername(),
                entity.getPassword()
        );
    }
}
