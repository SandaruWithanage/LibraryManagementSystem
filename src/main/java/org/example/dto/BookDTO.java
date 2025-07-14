package org.example.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * A Data Transfer Object (DTO) for carrying book data between layers.
 * It is a plain Java object, decoupled from the database or any business logic.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookDTO {
    private String bookId;
    private String isbn;
    private String title;
    private String author;
    private String genre;
    private boolean availability;
}
