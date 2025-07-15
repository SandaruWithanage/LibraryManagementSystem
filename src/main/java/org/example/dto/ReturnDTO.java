package org.example.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * A Data Transfer Object (DTO) specifically for handling a book return.
 * It carries the essential information needed to process the return transaction.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReturnDTO {
    private String recordId;
    private String bookId;
    private LocalDate borrowDate;
    private LocalDate returnDate;
    private double fine;
}
