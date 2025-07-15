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
 * This version is updated to use the correct DTO constructor.
 */
public class ReportServiceImpl implements ReportService {

    private final BookDAO bookDAO = new BookDAOImpl();
    private final BorrowRecordDAO borrowRecordDAO = new BorrowRecordDAOImpl();

    private static final int LENDING_PERIOD_DAYS = 14;
    private static final double FINE_PER_DAY = 10.0;

    @Override
    public List<BookDTO> getAvailableBooks() throws SQLException {
        List<Book> availableBooks = bookDAO.findAvailableBooks();
        return availableBooks.stream()
                .map(this::mapToBookDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<BorrowRecordDTO> getBorrowedBooks() throws SQLException {
        return borrowRecordDAO.findAll().stream()
                .filter(record -> record.getReturnDate() == null)
                .map(this::mapToBorrowDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<BorrowRecordDTO> getOverdueBooks() throws SQLException {
        return borrowRecordDAO.findAll().stream()
                .filter(record -> record.getReturnDate() == null && isOverdue(record.getBorrowDate()))
                .map(record -> {
                    long overdueDays = ChronoUnit.DAYS.between(record.getBorrowDate().plusDays(LENDING_PERIOD_DAYS), LocalDate.now());
                    double fine = overdueDays * FINE_PER_DAY;

                    BorrowRecordDTO dto = mapToBorrowDTO(record);
                    dto.setFine(fine);
                    return dto;
                })
                .collect(Collectors.toList());
    }

    private boolean isOverdue(LocalDate borrowDate) {
        long daysBorrowed = ChronoUnit.DAYS.between(borrowDate, LocalDate.now());
        return daysBorrowed > LENDING_PERIOD_DAYS;
    }

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
     * *** FIXED: This now correctly uses the 7-argument constructor. ***
     */
    private BorrowRecordDTO mapToBorrowDTO(BorrowRecord entity) {
        return new BorrowRecordDTO(
                entity.getRecordId(),
                entity.getUserId(),
                entity.getBookId(),
                entity.getBorrowDate(),
                entity.getReturnDate(),
                entity.getFine(),
                entity.isFinePaid() // The 7th argument
        );
    }
}
