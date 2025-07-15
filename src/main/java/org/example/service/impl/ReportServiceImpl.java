package org.example.service.impl;

import org.example.dao.BookDAO;
import org.example.dao.BorrowRecordDAO;
import org.example.dao.impl.BookDAOImpl;
import org.example.dao.impl.BorrowRecordDAOImpl;
import org.example.dto.BookDTO;
import org.example.dto.BorrowRecordDTO;
import org.example.entity.Book;
import org.example.entity.BorrowRecord;
import org.example.service.ReportService;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

/**
 * The concrete implementation of the ReportService interface.
 * It uses existing DAOs to fetch and process data for various reports.
 */
public class ReportServiceImpl implements ReportService {

    private final BookDAO bookDAO = new BookDAOImpl();
    private final BorrowRecordDAO borrowRecordDAO = new BorrowRecordDAOImpl();

    private static final int LENDING_PERIOD_DAYS = 14;
    private static final double FINE_PER_DAY = 10.0;

    @Override
    public List<BookDTO> getAvailableBooks() throws SQLException {
        // We can reuse the existing method from BookDAO for this.
        List<Book> availableBooks = bookDAO.findAvailableBooks();
        return availableBooks.stream()
                .map(this::mapToBookDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<BorrowRecordDTO> getBorrowedBooks() throws SQLException {
        // Fetch all records and filter in Java to find those not yet returned.
        return borrowRecordDAO.findAll().stream()
                .filter(record -> record.getReturnDate() == null)
                .map(this::mapToBorrowDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<BorrowRecordDTO> getOverdueBooks() throws SQLException {
        // Fetch all records, filter for overdue ones, and calculate the current fine.
        return borrowRecordDAO.findAll().stream()
                .filter(record -> record.getReturnDate() == null && isOverdue(record.getBorrowDate()))
                .map(record -> {
                    // Calculate the fine for the report
                    long overdueDays = ChronoUnit.DAYS.between(record.getBorrowDate().plusDays(LENDING_PERIOD_DAYS), LocalDate.now());
                    double fine = overdueDays * FINE_PER_DAY;

                    // Create a DTO and set the calculated fine
                    BorrowRecordDTO dto = mapToBorrowDTO(record);
                    dto.setFine(fine);
                    return dto;
                })
                .collect(Collectors.toList());
    }

    /**
     * A helper method to check if a loan is overdue based on the current date.
     * @param borrowDate The date the book was borrowed.
     * @return true if the book is overdue, false otherwise.
     */
    private boolean isOverdue(LocalDate borrowDate) {
        long daysBorrowed = ChronoUnit.DAYS.between(borrowDate, LocalDate.now());
        return daysBorrowed > LENDING_PERIOD_DAYS;
    }

    /**
     * A helper method to map a Book entity to its DTO.
     */
    private BookDTO mapToBookDTO(Book entity) {
        return new BookDTO(
                entity.getBookId(),
                entity.getIsbn(),
                entity.getTitle(),
                entity.getAuthor(),
                entity.getGenre(),
                entity.isAvailability()
        );
    }

    /**
     * A helper method to map a BorrowRecord entity to its DTO.
     */
    private BorrowRecordDTO mapToBorrowDTO(BorrowRecord entity) {
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
