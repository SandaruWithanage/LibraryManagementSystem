package org.example.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Represents a User entity, mapping to the 'users' table in the database.
 * Lombok annotations are used to automatically generate getters, setters,
 * and constructors, reducing boilerplate code.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {
    private String userId;
    private String name;
    private String contact;
    private String membershipDate;
    private String username;
    private String password;
}
