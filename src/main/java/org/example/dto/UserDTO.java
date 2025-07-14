package org.example.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * A Data Transfer Object (DTO) for carrying user data.
 * It is a simple object used to transfer data across application layers,
 * keeping the UI decoupled from the database entity.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {
    private String userId;
    private String name;
    private String contact;
    private LocalDate membershipDate;
    private String username;
    private String password;
}
