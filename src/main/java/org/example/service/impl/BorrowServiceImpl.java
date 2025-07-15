package org.example.service.impl;

import org.example.dao.BookDAO;
import org.example.dao.BorrowRecordDAO;
import org.example.dao.impl.BookDAOImpl;
import org.example.dao.impl.BorrowRecordDAOImpl;
import org.example.db.DBConnection;
import org.example.dto.BorrowRecordDTO;
import org.example.entity.Book;
import org.example.entity.BorrowRecord;
import org.example.service.BorrowService;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;

/**
 * The concrete implementation of the BorrowService interface.
 * This version includes the business rule for the borrowing limit.
 */
public class BorrowServiceImpl implements BorrowService {

    private final BorrowRecordDAO borrowRecordDAO = new BorrowRecordDAOImpl();
    private final BookDAO bookDAO = new BookDAOImpl();

    private static final int BORROWING_LIMIT = 3;

    /**
     * Processes a book loan, now with a check for the user's borrowing limit.
     */
    @Override
    public boolean borrowBook(BorrowRecordDTO borrowRecordDTO) throws SQLException {
        // --- Business Rule Check ---
        // First, check if the user has already reached their borrowing limit.
        int activeBorrows = borrowRecordDAO.getActiveBorrowCountForUser(borrowRecordDTO.getUserId());
        if (activeBorrows >= BORROWING_LIMIT) {
            // If the limit is reached, return false so the controller can show a specific error.
            return false;
        }

        // --- Transactional Logic ---
        // If the check passes, proceed with the transaction.
        Connection connection = null;
        try {
            connection = DBConnection.getInstance().getConnection();
            connection.setAutoCommit(false);

            // Save the new borrow record.
            boolean isRecordSaved = borrowRecordDAO.save(mapToEntity(borrowRecordDTO));
            if (!isRecordSaved) {
                connection.rollback();
                return false;
            }

            // Update the book's availability to false.
            Book book = bookDAO.findById(borrowRecordDTO.getBookId());
            if (book == null || !book.isAvailability()) {
                connection.rollback(); // Book not found or already borrowed
                return false;
            }
            book.setAvailability(false);
            boolean isBookUpdated = bookDAO.update(book);
            if (!isBookUpdated) {
                connection.rollback();
                return false;
            }

            // If both operations succeed, commit the transaction.
            connection.commit();
            return true;

        } catch (SQLException e) {
            if (connection != null) {
                connection.rollback();
            }
            throw e;
        } finally {
            if (connection != null) {
                connection.setAutoCommit(true);
            }
        }
    }

    // --- All other methods remain unchanged ---
    @Override
    public List<BorrowRecordDTO> getAllBorrowRecords() throws SQLException {
        return borrowRecordDAO.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public String generateNextRecordId() throws SQLException {
        return borrowRecordDAO.generateNextId();
    }

    private BorrowRecord mapToEntity(BorrowRecordDTO dto) {
        return new BorrowRecord(
                dto.getRecordId(),
                dto.getUserId(),
                dto.getBookId(),
                dto.getBorrowDate(),
                dto.getReturnDate(),
                dto.getFine(),
                dto.isFinePaid()
        );
    }

    private BorrowRecordDTO mapToDTO(BorrowRecord entity) {
        return new BorrowRecordDTO(
                entity.getRecordId(),
                entity.getUserId(),
                entity.getBookId(),
                entity.getBorrowDate(),
                entity.getReturnDate(),
                entity.getFine(),
                entity.isFinePaid()
        );
    }
}
