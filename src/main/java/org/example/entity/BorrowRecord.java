package org.example.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * Represents a BorrowRecord entity, directly mapping to the 'borrow_records' table.
 * The fields in this class correspond to the columns in the table.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BorrowRecord {
    private String recordId;
    private String userId;
    private String bookId;
    private LocalDate borrowDate;
    private LocalDate returnDate;
    private double fine;
}
