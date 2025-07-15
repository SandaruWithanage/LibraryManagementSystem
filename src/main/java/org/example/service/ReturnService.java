package org.example.service;

import org.example.dto.ReturnDTO;

import java.sql.SQLException;

/**
 * The service layer interface for the Book Return module.
 * It defines the contract for processing a book return.
 */
public interface ReturnService {

    /**
     * Processes a book return as a single, atomic transaction.
     * This involves updating the borrow record and making the book available again.
     *
     * @param returnDTO The DTO containing the details of the return.
     * @return true if the return was processed successfully, false otherwise.
     * @throws SQLException if a database error occurs.
     */
    boolean processReturn(ReturnDTO returnDTO) throws SQLException;
}
