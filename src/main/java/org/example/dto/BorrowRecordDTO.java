package org.example.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * A Data Transfer Object (DTO) for carrying borrow record data.
 * It is a simple container used to pass information between the UI and the service layer.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BorrowRecordDTO {
    private String recordId;
    private String userId;
    private String bookId;
    private LocalDate borrowDate;
    private LocalDate returnDate; // Can be null if the book has not been returned
    private double fine;
}
