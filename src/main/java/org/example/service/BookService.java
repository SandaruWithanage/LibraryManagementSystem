package org.example.service;

import org.example.dto.BookDTO;

import java.sql.SQLException;
import java.util.List;

/**
 * The service layer interface for Book-related business logic.
 * This version is now complete and includes all necessary methods.
 */
public interface BookService {
    boolean addBook(BookDTO bookDTO) throws SQLException;
    boolean updateBook(BookDTO bookDTO) throws SQLException;
    boolean deleteBook(String bookId) throws SQLException;
    BookDTO getBookById(String bookId) throws SQLException;
    List<BookDTO> getAllBooks() throws SQLException;
    String generateNextBookId() throws SQLException;

    /**
     * Gets a list of all books that are currently available.
     * @return A list of available BookDTOs.
     * @throws SQLException if a database error occurs.
     */
    List<BookDTO> getAvailableBooks() throws SQLException;
}
