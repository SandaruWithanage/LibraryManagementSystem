package org.example.dao.impl;

import org.example.dao.UserDAO;
import org.example.db.DBConnection;
import org.example.entity.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * The concrete implementation of the UserDAO interface.
 * This class contains the actual JDBC code to interact with the 'users' table.
 */
public class UserDAOImpl implements UserDAO {

    @Override
    public User findByUsernameAndPassword(String username, String password) throws SQLException {
        // 1. Get a connection to the database.
        Connection connection = DBConnection.getInstance().getConnection();

        // 2. Define the SQL query with placeholders (?) to prevent SQL injection.
        String sql = "SELECT * FROM users WHERE username = ? AND password = ?";

        // 3. Create a PreparedStatement to safely execute the query.
        try (PreparedStatement pstm = connection.prepareStatement(sql)) {

            // 4. Set the values for the placeholders.
            pstm.setString(1, username);
            pstm.setString(2, password);

            // 5. Execute the query and get the result.
            try (ResultSet resultSet = pstm.executeQuery()) {

                // 6. If a user is found, create a User object and return it.
                if (resultSet.next()) {
                    return new User(
                            resultSet.getString("user_id"),
                            resultSet.getString("name"),
                            resultSet.getString("contact"),
                            resultSet.getString("membership_date"),
                            resultSet.getString("username"),
                            resultSet.getString("password")
                    );
                }
            }
        }
        // 7. If no user is found, return null.
        return null;
    }
}
