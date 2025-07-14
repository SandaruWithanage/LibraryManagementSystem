package org.example.dao;

import org.example.entity.BorrowRecord;

import java.sql.SQLException;
import java.util.List;

/**
 * The Data Access Object interface for BorrowRecord-related database operations.
 */
public interface BorrowRecordDAO {
    boolean save(BorrowRecord record) throws SQLException;
    boolean update(BorrowRecord record) throws SQLException;
    BorrowRecord findById(String recordId) throws SQLException;
    List<BorrowRecord> findAll() throws SQLException;
    String generateNextId() throws SQLException;
}
