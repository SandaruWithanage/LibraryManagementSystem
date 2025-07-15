package org.example.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BorrowRecordDTO {
    private String recordId;
    private String userId;
    private String bookId;
    private LocalDate borrowDate;
    private LocalDate returnDate;
    private double fine;
    private boolean isFinePaid; // The 7th field
}
