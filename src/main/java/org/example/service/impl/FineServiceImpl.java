package org.example.service.impl;

import org.example.dao.BorrowRecordDAO;
import org.example.dao.impl.BorrowRecordDAOImpl;
import org.example.entity.BorrowRecord;
import org.example.service.FineService;

import java.sql.SQLException;

/**
 * The concrete implementation of the FineService interface.
 * This class contains the business logic for managing fine payments.
 */
public class FineServiceImpl implements FineService {

    private final BorrowRecordDAO borrowRecordDAO = new BorrowRecordDAOImpl();

    @Override
    public boolean payFine(String recordId) throws SQLException {
        // 1. Find the specific borrow record by its ID.
        BorrowRecord record = borrowRecordDAO.findById(recordId);

        // 2. Check if the record exists and actually has a fine.
        if (record != null && record.getFine() > 0) {
            // 3. Update the status to indicate the fine has been paid.
            record.setFinePaid(true);

            // 4. Use the DAO to save the updated record back to the database.
            return borrowRecordDAO.update(record);
        }

        // Return false if the record was not found or had no fine to pay.
        return false;
    }
}
