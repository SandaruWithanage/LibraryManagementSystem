package org.example.dao;

import org.example.entity.Book;

import java.sql.SQLException;
import java.util.List;

public interface BookDAO {
    boolean save(Book book) throws SQLException;
    boolean update(Book book) throws SQLException;
    boolean delete(String bookId) throws SQLException;
    Book findById(String bookId) throws SQLException;
    List<Book> findAll() throws SQLException;

    /**
     * Finds all books that are currently available.
     * @return A list of available Book entities.
     * @throws SQLException if a database error occurs.
     */
    List<Book> findAvailableBooks() throws SQLException;
}
