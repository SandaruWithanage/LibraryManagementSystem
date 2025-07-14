package org.example.service;

import org.example.dto.BorrowRecordDTO;

import java.sql.SQLException;
import java.util.List;

/**
 * The service layer interface for BorrowRecord-related business logic.
 */
public interface BorrowService {
    boolean borrowBook(BorrowRecordDTO borrowRecordDTO) throws SQLException;
    List<BorrowRecordDTO> getAllBorrowRecords() throws SQLException;
    String generateNextRecordId() throws SQLException;
}
