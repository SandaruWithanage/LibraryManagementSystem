package org.example.service.impl;

import org.example.dao.BookDAO;
import org.example.dao.BorrowRecordDAO;
import org.example.dao.impl.BookDAOImpl;
import org.example.dao.impl.BorrowRecordDAOImpl;
import org.example.db.DBConnection;
import org.example.dto.BookDTO;
import org.example.dto.BorrowRecordDTO;
import org.example.entity.Book;
import org.example.entity.BorrowRecord;
import org.example.service.BookService;
import org.example.service.BorrowService;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;

/**
 * The concrete implementation of the BorrowService interface.
 * This version includes the transactional logic for borrowing a book.
 */
public class BorrowServiceImpl implements BorrowService {

    private final BorrowRecordDAO borrowRecordDAO = new BorrowRecordDAOImpl();
    // We now need the BookDAO to update the book's availability
    private final BookDAO bookDAO = new BookDAOImpl();

    /**
     * Processes a book loan as a single database transaction.
     * It saves the borrow record and updates the book's availability.
     * If either operation fails, the entire transaction is rolled back.
     *
     * @param borrowRecordDTO The DTO containing the loan details.
     * @return true if the transaction was successful, false otherwise.
     * @throws SQLException if a database error occurs.
     */
    @Override
    public boolean borrowBook(BorrowRecordDTO borrowRecordDTO) throws SQLException {
        Connection connection = null;
        try {
            // 1. Get a single connection for the transaction.
            connection = DBConnection.getInstance().getConnection();

            // 2. Disable auto-commit to manage the transaction manually.
            connection.setAutoCommit(false);

            // 3. Save the new borrow record.
            boolean isRecordSaved = borrowRecordDAO.save(mapToEntity(borrowRecordDTO));
            if (!isRecordSaved) {
                connection.rollback(); // If saving fails, roll back.
                return false;
            }

            // 4. Find the book that is being borrowed.
            Book book = bookDAO.findById(borrowRecordDTO.getBookId());
            if (book == null) {
                connection.rollback(); // Book not found, roll back.
                return false;
            }

            // 5. Update the book's availability to false.
            book.setAvailability(false);
            boolean isBookUpdated = bookDAO.update(book);
            if (!isBookUpdated) {
                connection.rollback(); // If update fails, roll back.
                return false;
            }

            // 6. If both operations succeed, commit the transaction.
            connection.commit();
            return true;

        } catch (SQLException e) {
            // 7. If any exception occurs, roll back the transaction.
            if (connection != null) {
                connection.rollback();
            }
            throw e; // Re-throw the exception to notify the controller.
        } finally {
            // 8. Always re-enable auto-commit in a finally block.
            if (connection != null) {
                connection.setAutoCommit(true);
            }
        }
    }

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

    // --- Helper mapping methods ---
    private BorrowRecord mapToEntity(BorrowRecordDTO dto) {
        return new BorrowRecord(
                dto.getRecordId(),
                dto.getUserId(),
                dto.getBookId(),
                dto.getBorrowDate(),
                dto.getReturnDate(),
                dto.getFine()
        );
    }

    private BorrowRecordDTO mapToDTO(BorrowRecord entity) {
        return new BorrowRecordDTO(
                entity.getRecordId(),
                entity.getUserId(),
                entity.getBookId(),
                entity.getBorrowDate(),
                entity.getReturnDate(),
                entity.getFine()
        );
    }
}
