package org.example.service;

import java.sql.SQLException;

/**
 * The service layer interface for Fine-related business logic.
 * It defines the contract for operations like paying a fine.
 */
public interface FineService {

    /**
     * Marks a fine as paid for a specific borrow record.
     *
     * @param recordId The ID of the borrow record whose fine is being paid.
     * @return true if the fine was successfully marked as paid, false otherwise.
     * @throws SQLException if a database error occurs.
     */
    boolean payFine(String recordId) throws SQLException;
}
