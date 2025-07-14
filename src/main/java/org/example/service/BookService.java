package org.example.service;

import org.example.dto.BookDTO;

import java.sql.SQLException;
import java.util.List;

/**
 * The service layer interface for Book-related business logic.
 * The generateNextBookId method has been removed.
 */
public interface BookService {
    boolean addBook(BookDTO bookDTO) throws SQLException;
    boolean updateBook(BookDTO bookDTO) throws SQLException;
    boolean deleteBook(String bookId) throws SQLException;
    BookDTO getBookById(String bookId) throws SQLException;
    List<BookDTO> getAllBooks() throws SQLException;
}
