package org.example.dao.impl;

import org.example.dao.UserDAO;
import org.example.db.DBConnection;
import org.example.entity.User;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * The concrete implementation of the UserDAO interface.
 * This version includes the robust logic to generate the next User ID.
 */
public class UserDAOImpl implements UserDAO {

    @Override
    public String generateNextId() throws SQLException {
        Connection connection = DBConnection.getInstance().getConnection();
        // This robust query correctly sorts alphanumeric IDs (e.g., U009, U010, U011)
        String sql = "SELECT user_id FROM users ORDER BY CAST(SUBSTRING(user_id, 2) AS UNSIGNED) DESC LIMIT 1";
        try (PreparedStatement pstm = connection.prepareStatement(sql)) {
            ResultSet resultSet = pstm.executeQuery();
            if (resultSet.next()) {
                String lastId = resultSet.getString(1);
                int num = Integer.parseInt(lastId.substring(1));
                num++;
                return String.format("U%03d", num); // Formats to U002, U010, etc.
            } else {
                return "U001"; // If the table is empty
            }
        }
    }

    // --- All other CRUD and find methods remain unchanged ---
    @Override
    public boolean save(User user) throws SQLException {
        Connection connection = DBConnection.getInstance().getConnection();
        String sql = "INSERT INTO users (user_id, name, contact, membership_date, username, password) VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement pstm = connection.prepareStatement(sql)) {
            pstm.setString(1, user.getUserId());
            pstm.setString(2, user.getName());
            pstm.setString(3, user.getContact());
            pstm.setDate(4, user.getMembershipDate() != null ? Date.valueOf(user.getMembershipDate()) : null);
            pstm.setString(5, user.getUsername());
            pstm.setString(6, user.getPassword());
            return pstm.executeUpdate() > 0;
        }
    }

    @Override
    public boolean update(User user) throws SQLException {
        Connection connection = DBConnection.getInstance().getConnection();
        String sql = "UPDATE users SET name = ?, contact = ?, membership_date = ?, username = ?, password = ? WHERE user_id = ?";
        try (PreparedStatement pstm = connection.prepareStatement(sql)) {
            pstm.setString(1, user.getName());
            pstm.setString(2, user.getContact());
            pstm.setDate(3, user.getMembershipDate() != null ? Date.valueOf(user.getMembershipDate()) : null);
            pstm.setString(4, user.getUsername());
            pstm.setString(5, user.getPassword());
            pstm.setString(6, user.getUserId());
            return pstm.executeUpdate() > 0;
        }
    }

    @Override
    public boolean delete(String userId) throws SQLException {
        Connection connection = DBConnection.getInstance().getConnection();
        String sql = "DELETE FROM users WHERE user_id = ?";
        try (PreparedStatement pstm = connection.prepareStatement(sql)) {
            pstm.setString(1, userId);
            return pstm.executeUpdate() > 0;
        }
    }

    @Override
    public User findById(String userId) throws SQLException {
        Connection connection = DBConnection.getInstance().getConnection();
        String sql = "SELECT * FROM users WHERE user_id = ?";
        try (PreparedStatement pstm = connection.prepareStatement(sql)) {
            pstm.setString(1, userId);
            try (ResultSet resultSet = pstm.executeQuery()) {
                if (resultSet.next()) {
                    return buildUserFromResultSet(resultSet);
                }
            }
        }
        return null;
    }

    @Override
    public List<User> findAll() throws SQLException {
        Connection connection = DBConnection.getInstance().getConnection();
        String sql = "SELECT * FROM users";
        List<User> users = new ArrayList<>();
        try (PreparedStatement pstm = connection.prepareStatement(sql);
             ResultSet resultSet = pstm.executeQuery()) {
            while (resultSet.next()) {
                users.add(buildUserFromResultSet(resultSet));
            }
        }
        return users;
    }

    @Override
    public User findByUsernameAndPassword(String username, String password) throws SQLException {
        Connection connection = DBConnection.getInstance().getConnection();
        String sql = "SELECT * FROM users WHERE username = ? AND password = ?";
        try (PreparedStatement pstm = connection.prepareStatement(sql)) {
            pstm.setString(1, username);
            pstm.setString(2, password);
            try (ResultSet resultSet = pstm.executeQuery()) {
                if (resultSet.next()) {
                    return buildUserFromResultSet(resultSet);
                }
            }
        }
        return null;
    }

    private User buildUserFromResultSet(ResultSet resultSet) throws SQLException {
        Date sqlMembershipDate = resultSet.getDate("membership_date");
        LocalDate membershipDate = (sqlMembershipDate != null) ? sqlMembershipDate.toLocalDate() : null;

        return new User(
                resultSet.getString("user_id"),
                resultSet.getString("name"),
                resultSet.getString("contact"),
                membershipDate,
                resultSet.getString("username"),
                resultSet.getString("password")
        );
    }
}
