package org.example.dao.impl;

import org.example.dao.BorrowRecordDAO;
import org.example.db.DBConnection;
import org.example.entity.BorrowRecord;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * The concrete implementation of the BorrowRecordDAO interface.
 * This version includes the logic to count active loans.
 */
public class BorrowRecordDAOImpl implements BorrowRecordDAO {

    // --- All existing save, update, findById, findAll, generateNextId methods remain here ---

    /**
     * New method to count active loans for a specific user.
     */
    @Override
    public int getActiveBorrowCountForUser(String userId) throws SQLException {
        Connection connection = DBConnection.getInstance().getConnection();
        // This query counts all records for a user where the book has not been returned.
        String sql = "SELECT COUNT(*) FROM borrow_records WHERE user_id = ? AND return_date IS NULL";
        try (PreparedStatement pstm = connection.prepareStatement(sql)) {
            pstm.setString(1, userId);
            try (ResultSet resultSet = pstm.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getInt(1); // Return the count
                }
            }
        }
        return 0; // Return 0 if user has no active loans
    }

    // --- All other methods from the previous version remain unchanged ---
    @Override
    public boolean save(BorrowRecord record) throws SQLException {
        Connection connection = DBConnection.getInstance().getConnection();
        String sql = "INSERT INTO borrow_records (record_id, user_id, book_id, borrow_date, return_date, fine) VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement pstm = connection.prepareStatement(sql)) {
            pstm.setString(1, record.getRecordId());
            pstm.setString(2, record.getUserId());
            pstm.setString(3, record.getBookId());
            pstm.setDate(4, Date.valueOf(record.getBorrowDate()));
            pstm.setDate(5, record.getReturnDate() != null ? Date.valueOf(record.getReturnDate()) : null);
            pstm.setDouble(6, record.getFine());
            return pstm.executeUpdate() > 0;
        }
    }

    @Override
    public boolean update(BorrowRecord record) throws SQLException {
        Connection connection = DBConnection.getInstance().getConnection();
        String sql = "UPDATE borrow_records SET user_id = ?, book_id = ?, borrow_date = ?, return_date = ?, fine = ? WHERE record_id = ?";
        try (PreparedStatement pstm = connection.prepareStatement(sql)) {
            pstm.setString(1, record.getUserId());
            pstm.setString(2, record.getBookId());
            pstm.setDate(3, Date.valueOf(record.getBorrowDate()));
            pstm.setDate(4, record.getReturnDate() != null ? Date.valueOf(record.getReturnDate()) : null);
            pstm.setDouble(5, record.getFine());
            pstm.setString(6, record.getRecordId());
            return pstm.executeUpdate() > 0;
        }
    }

    @Override
    public BorrowRecord findById(String recordId) throws SQLException {
        Connection connection = DBConnection.getInstance().getConnection();
        String sql = "SELECT * FROM borrow_records WHERE record_id = ?";
        try (PreparedStatement pstm = connection.prepareStatement(sql)) {
            pstm.setString(1, recordId);
            try (ResultSet resultSet = pstm.executeQuery()) {
                if (resultSet.next()) {
                    return buildBorrowRecordFromResultSet(resultSet);
                }
            }
        }
        return null;
    }

    @Override
    public List<BorrowRecord> findAll() throws SQLException {
        Connection connection = DBConnection.getInstance().getConnection();
        String sql = "SELECT * FROM borrow_records";
        List<BorrowRecord> records = new ArrayList<>();
        try (PreparedStatement pstm = connection.prepareStatement(sql);
             ResultSet resultSet = pstm.executeQuery()) {
            while (resultSet.next()) {
                records.add(buildBorrowRecordFromResultSet(resultSet));
            }
        }
        return records;
    }

    @Override
    public String generateNextId() throws SQLException {
        Connection connection = DBConnection.getInstance().getConnection();
        String sql = "SELECT record_id FROM borrow_records ORDER BY CAST(SUBSTRING(record_id, 2) AS UNSIGNED) DESC LIMIT 1";
        try (PreparedStatement pstm = connection.prepareStatement(sql)) {
            ResultSet resultSet = pstm.executeQuery();
            if (resultSet.next()) {
                String lastId = resultSet.getString(1);
                int num = Integer.parseInt(lastId.substring(1));
                num++;
                return String.format("R%03d", num); // "R" for Record
            } else {
                return "R001";
            }
        }
    }

    private BorrowRecord buildBorrowRecordFromResultSet(ResultSet resultSet) throws SQLException {
        Date sqlReturnDate = resultSet.getDate("return_date");
        LocalDate returnDate = (sqlReturnDate != null) ? sqlReturnDate.toLocalDate() : null;

        return new BorrowRecord(
                resultSet.getString("record_id"),
                resultSet.getString("user_id"),
                resultSet.getString("book_id"),
                resultSet.getDate("borrow_date").toLocalDate(),
                returnDate,
                resultSet.getDouble("fine")
        );
    }
}
