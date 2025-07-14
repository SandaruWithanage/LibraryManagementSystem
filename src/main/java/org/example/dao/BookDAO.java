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
}
