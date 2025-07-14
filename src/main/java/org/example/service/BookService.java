package org.example.service;

import org.example.dto.BookDTO;

import java.sql.SQLException;
import java.util.List;

public interface BookService {
    boolean addBook(BookDTO bookDTO) throws SQLException;
    boolean updateBook(BookDTO bookDTO) throws SQLException;
    boolean deleteBook(String bookId) throws SQLException;
    BookDTO getBookById(String bookId) throws SQLException;
    List<BookDTO> getAllBooks() throws SQLException;

    /**
     * Gets a list of all books that are currently available.
     * @return A list of available BookDTOs.
     * @throws SQLException if a database error occurs.
     */
    List<BookDTO> getAvailableBooks() throws SQLException;
}
