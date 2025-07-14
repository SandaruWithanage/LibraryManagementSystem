package org.example.dao;

import org.example.entity.Book;

import java.sql.SQLException;
import java.util.List;

/**
 * The Data Access Object interface for Book-related database operations.
 * It defines the contract for CRUD (Create, Read, Update, Delete) operations.
 */
public interface BookDAO {
    boolean save(Book book) throws SQLException;
    boolean update(Book book) throws SQLException;
    boolean delete(String bookId) throws SQLException;
    Book findById(String bookId) throws SQLException;
    List<Book> findAll() throws SQLException;

    /**
     * Generates the next book ID based on the last one in the database.
     * @return The next sequential book ID as a String (e.g., "B004").
     * @throws SQLException if a database access error occurs.
     */
    String generateNextId() throws SQLException;
}
