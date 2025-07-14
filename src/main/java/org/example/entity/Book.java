package org.example.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Represents a Book entity, directly mapping to the 'books' table in the database.
 * The fields in this class correspond to the columns in the table.
 * Lombok annotations are used to reduce boilerplate code for getters, setters, and constructors.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Book {
    private String bookId;
    private String isbn;
    private String title;
    private String author;
    private String genre;
    private boolean availability;
}
