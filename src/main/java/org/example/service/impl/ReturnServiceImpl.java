package org.example.service.impl;

import org.example.dao.BookDAO;
import org.example.dao.BorrowRecordDAO;
import org.example.dao.impl.BookDAOImpl;
import org.example.dao.impl.BorrowRecordDAOImpl;
import org.example.db.DBConnection;
import org.example.dto.ReturnDTO;
import org.example.entity.Book;
import org.example.entity.BorrowRecord;
import org.example.service.ReturnService;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

/**
 * The concrete implementation of the ReturnService interface.
 * This class contains the transactional logic for returning a book.
 */
public class ReturnServiceImpl implements ReturnService {

    private final BorrowRecordDAO borrowRecordDAO = new BorrowRecordDAOImpl();
    private final BookDAO bookDAO = new BookDAOImpl();

    private static final int LENDING_PERIOD_DAYS = 14;
    private static final double FINE_PER_DAY = 10.0;

    @Override
    public boolean processReturn(ReturnDTO returnDTO) throws SQLException {
        Connection connection = null;
        try {
            // 1. Get a single connection for the entire transaction.
            connection = DBConnection.getInstance().getConnection();
            // 2. Disable auto-commit to manage the transaction manually.
            connection.setAutoCommit(false);

            // 3. Fetch the original borrow record from the database.
            BorrowRecord recordToUpdate = borrowRecordDAO.findById(returnDTO.getRecordId());
            if (recordToUpdate == null) {
                connection.rollback();
                return false; // Record not found
            }

            // 4. Calculate the fine based on the business rule.
            double fine = calculateFine(recordToUpdate.getBorrowDate(), returnDTO.getReturnDate());

            // 5. Update the borrow record with the return date and calculated fine.
            recordToUpdate.setReturnDate(returnDTO.getReturnDate());
            recordToUpdate.setFine(fine);
            boolean isRecordUpdated = borrowRecordDAO.update(recordToUpdate);

            if (!isRecordUpdated) {
                connection.rollback(); // If update fails, roll back.
                return false;
            }

            // 6. Update the book's availability to true.
            Book bookToUpdate = bookDAO.findById(recordToUpdate.getBookId());
            if (bookToUpdate == null) {
                connection.rollback(); // Should not happen if data is consistent
                return false;
            }
            bookToUpdate.setAvailability(true);
            boolean isBookUpdated = bookDAO.update(bookToUpdate);

            if (!isBookUpdated) {
                connection.rollback(); // If update fails, roll back.
                return false;
            }

            // 7. If all operations succeed, commit the transaction.
            connection.commit();
            return true;

        } catch (SQLException e) {
            // 8. If any error occurs, roll back all changes.
            if (connection != null) {
                connection.rollback();
            }
            throw e; // Re-throw the exception to be handled by the controller.
        } finally {
            // 9. Always ensure the connection is returned to its normal state.
            if (connection != null) {
                connection.setAutoCommit(true);
            }
        }
    }

    /**
     * A private helper method to calculate the fine for an overdue book.
     * @param borrowDate The date the book was borrowed.
     * @param returnDate The date the book was returned.
     * @return The calculated fine, or 0.0 if not overdue.
     */
    private double calculateFine(LocalDate borrowDate, LocalDate returnDate) {
        long daysBorrowed = ChronoUnit.DAYS.between(borrowDate, returnDate);
        if (daysBorrowed > LENDING_PERIOD_DAYS) {
            long overdueDays = daysBorrowed - LENDING_PERIOD_DAYS;
            return overdueDays * FINE_PER_DAY;
        }
        return 0.0; // No fine if returned on time.
    }
}
