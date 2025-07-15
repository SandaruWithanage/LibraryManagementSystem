package org.example.service;

import org.example.dto.BookDTO;
import org.example.dto.BorrowRecordDTO;

import java.sql.SQLException;
import java.util.List;

/**
 * The service layer interface for generating various library reports.
 * It defines the contract for fetching data required by the reporting UI.
 */
public interface ReportService {

    /**
     * Retrieves a list of all books that are currently available.
     * @return A list of available BookDTOs.
     * @throws SQLException if a database error occurs.
     */
    List<BookDTO> getAvailableBooks() throws SQLException;

    /**
     * Retrieves a list of all books that are currently borrowed.
     * @return A list of BorrowRecordDTOs for books that have not been returned.
     * @throws SQLException if a database error occurs.
     */
    List<BorrowRecordDTO> getBorrowedBooks() throws SQLException;

    /**
     * Retrieves a list of all overdue books and calculates their current fines.
     * @return A list of BorrowRecordDTOs for overdue books.
     * @throws SQLException if a database error occurs.
     */
    List<BorrowRecordDTO> getOverdueBooks() throws SQLException;
}
