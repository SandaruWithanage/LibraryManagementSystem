package org.example.dao;

import org.example.entity.BorrowRecord;

import java.sql.SQLException;
import java.util.List;

/**
 * The Data Access Object interface for BorrowRecord-related database operations.
 * This version includes a method to count active loans for a user.
 */
public interface BorrowRecordDAO {
    boolean save(BorrowRecord record) throws SQLException;
    boolean update(BorrowRecord record) throws SQLException;
    BorrowRecord findById(String recordId) throws SQLException;
    List<BorrowRecord> findAll() throws SQLException;
    String generateNextId() throws SQLException;

    /**
     * Counts the number of books a user has currently borrowed (not yet returned).
     * @param userId The ID of the user to check.
     * @return The number of active loans.
     * @throws SQLException if a database error occurs.
     */
    int getActiveBorrowCountForUser(String userId) throws SQLException;
}
