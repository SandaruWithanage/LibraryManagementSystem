package org.example.service.impl;

import org.example.dao.BookDAO;
import org.example.dao.impl.BookDAOImpl;
import org.example.dto.BookDTO;
import org.example.entity.Book;
import org.example.service.BookService;

import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;

public class BookServiceImpl implements BookService {

    private final BookDAO bookDAO = new BookDAOImpl();

    // ... (All existing addBook, updateBook, etc. methods remain here) ...

    @Override
    public List<BookDTO> getAvailableBooks() throws SQLException {
        return bookDAO.findAvailableBooks().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    // --- All other methods from the previous version remain unchanged ---
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
        return books.stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

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
