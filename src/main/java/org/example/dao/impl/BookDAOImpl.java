package org.example.dao.impl;

import org.example.dao.BookDAO;
import org.example.db.DBConnection;
import org.example.entity.Book;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * The concrete implementation of the BookDAO interface using JDBC.
 * This class contains the actual SQL queries to interact with the 'books' table.
 */
public class BookDAOImpl implements BookDAO {

    @Override
    public boolean save(Book book) throws SQLException {
        Connection connection = DBConnection.getInstance().getConnection();
        String sql = "INSERT INTO books (book_id, isbn, title, author, genre, availability) VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement pstm = connection.prepareStatement(sql)) {
            pstm.setString(1, book.getBookId());
            pstm.setString(2, book.getIsbn());
            pstm.setString(3, book.getTitle());
            pstm.setString(4, book.getAuthor());
            pstm.setString(5, book.getGenre());
            pstm.setBoolean(6, book.isAvailability());
            return pstm.executeUpdate() > 0;
        }
    }

    @Override
    public boolean update(Book book) throws SQLException {
        Connection connection = DBConnection.getInstance().getConnection();
        String sql = "UPDATE books SET isbn = ?, title = ?, author = ?, genre = ?, availability = ? WHERE book_id = ?";
        try (PreparedStatement pstm = connection.prepareStatement(sql)) {
            pstm.setString(1, book.getIsbn());
            pstm.setString(2, book.getTitle());
            pstm.setString(3, book.getAuthor());
            pstm.setString(4, book.getGenre());
            pstm.setBoolean(5, book.isAvailability());
            pstm.setString(6, book.getBookId());
            return pstm.executeUpdate() > 0;
        }
    }

    @Override
    public boolean delete(String bookId) throws SQLException {
        Connection connection = DBConnection.getInstance().getConnection();
        String sql = "DELETE FROM books WHERE book_id = ?";
        try (PreparedStatement pstm = connection.prepareStatement(sql)) {
            pstm.setString(1, bookId);
            return pstm.executeUpdate() > 0;
        }
    }

    @Override
    public Book findById(String bookId) throws SQLException {
        Connection connection = DBConnection.getInstance().getConnection();
        String sql = "SELECT * FROM books WHERE book_id = ?";
        try (PreparedStatement pstm = connection.prepareStatement(sql)) {
            pstm.setString(1, bookId);
            try (ResultSet resultSet = pstm.executeQuery()) {
                if (resultSet.next()) {
                    return new Book(
                            resultSet.getString("book_id"),
                            resultSet.getString("isbn"),
                            resultSet.getString("title"),
                            resultSet.getString("author"),
                            resultSet.getString("genre"),
                            resultSet.getBoolean("availability")
                    );
                }
            }
        }
        return null;
    }

    @Override
    public List<Book> findAll() throws SQLException {
        Connection connection = DBConnection.getInstance().getConnection();
        String sql = "SELECT * FROM books";
        List<Book> books = new ArrayList<>();
        try (PreparedStatement pstm = connection.prepareStatement(sql);
             ResultSet resultSet = pstm.executeQuery()) {
            while (resultSet.next()) {
                books.add(new Book(
                        resultSet.getString("book_id"),
                        resultSet.getString("isbn"),
                        resultSet.getString("title"),
                        resultSet.getString("author"),
                        resultSet.getString("genre"),
                        resultSet.getBoolean("availability")
                ));
            }
        }
        return books;
    }

    /**
     * Generates the next sequential Book ID.
     * It queries for the highest book_id and increments the numeric part.
     */
    @Override
    public String generateNextId() throws SQLException {
        Connection connection = DBConnection.getInstance().getConnection();
        String sql = "SELECT book_id FROM books ORDER BY book_id DESC LIMIT 1";
        try (PreparedStatement pstm = connection.prepareStatement(sql);
             ResultSet resultSet = pstm.executeQuery()) {

            if (resultSet.next()) {
                String lastId = resultSet.getString("book_id");
                int num = Integer.parseInt(lastId.substring(1));
                num++;
                return String.format("B%03d", num);
            } else {
                return "B001";
            }
        }
    }
}
