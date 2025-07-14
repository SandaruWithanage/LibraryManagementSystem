package org.example.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * Represents a User entity, directly mapping to the 'users' table in the database.
 * This version includes all required fields for user management and login.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {
    private String userId;
    private String name;
    private String contact;
    private LocalDate membershipDate;
    private String username;
    private String password;
}
