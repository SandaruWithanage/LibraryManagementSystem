package org.example.dao;

import org.example.entity.Book;
import java.sql.SQLException;
import java.util.List;

/**
 * The Data Access Object interface for Book-related database operations.
 * This version includes the method for auto-generating Book IDs.
 */
public interface BookDAO {
    boolean save(Book book) throws SQLException;
    boolean update(Book book) throws SQLException;
    boolean delete(String bookId) throws SQLException;
    Book findById(String bookId) throws SQLException;
    List<Book> findAll() throws SQLException;

    List<Book> findAvailableBooks() throws SQLException;

    String generateNextId() throws SQLException;
}
