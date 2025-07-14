package org.example.service.impl;

import org.example.dao.BookDAO;
import org.example.dao.impl.BookDAOImpl;
import org.example.dto.BookDTO;
import org.example.entity.Book;
import org.example.service.BookService;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * The concrete implementation of the BookService interface.
 * It uses the BookDAO to interact with the database and handles the mapping
 * between BookDTOs and Book entities.
 */
public class BookServiceImpl implements BookService {

    private final BookDAO bookDAO = new BookDAOImpl();

    @Override
    public boolean addBook(BookDTO bookDTO) throws SQLException {
        Book book = mapToEntity(bookDTO);
        return bookDAO.save(book);
    }

    @Override
    public boolean updateBook(BookDTO bookDTO) throws SQLException {
        Book book = mapToEntity(bookDTO);
        return bookDAO.update(book);
    }

    @Override
    public boolean deleteBook(String bookId) throws SQLException {
        return bookDAO.delete(bookId);
    }

    @Override
    public BookDTO getBookById(String bookId) throws SQLException {
        Book book = bookDAO.findById(bookId);
        return (book != null) ? mapToDTO(book) : null;
    }

    @Override
    public List<BookDTO> getAllBooks() throws SQLException {
        List<Book> books = bookDAO.findAll();
        // Using Java Streams for a more modern mapping approach
        return books.stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public String generateNextBookId() throws SQLException {
        return null;
    }

    // Helper method to map a BookDTO to a Book entity
    private Book mapToEntity(BookDTO dto) {
        return new Book(
                dto.getBookId(),
                dto.getIsbn(),
                dto.getTitle(),
                dto.getAuthor(),
                dto.getGenre(),
                dto.isAvailability()
        );
    }

    // Helper method to map a Book entity to a BookDTO
    private BookDTO mapToDTO(Book entity) {
        return new BookDTO(
                entity.getBookId(),
                entity.getIsbn(),
                entity.getTitle(),
                entity.getAuthor(),
                entity.getGenre(),
                entity.isAvailability()
        );
    }
}
