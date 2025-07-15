package org.example.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import org.example.dto.BorrowRecordDTO;
import org.example.dto.ReturnDTO;
import org.example.service.BorrowService;
import org.example.service.ReturnService;
import org.example.service.impl.BorrowServiceImpl;
import org.example.service.impl.ReturnServiceImpl;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Controller for the return_form.fxml view.
 * Handles the logic for processing book returns and calculating fines.
 */
public class ReturnFormController {

    // FXML UI Components
    @FXML private TableView<BorrowRecordDTO> tblBorrowedBooks;
    @FXML private TableColumn<BorrowRecordDTO, String> colRecordId;
    @FXML private TableColumn<BorrowRecordDTO, String> colBookId;
    @FXML private TableColumn<BorrowRecordDTO, String> colUserId;
    @FXML private TableColumn<BorrowRecordDTO, LocalDate> colBorrowDate;
    @FXML private DatePicker dateReturn;
    @FXML private TextField txtFine;
    @FXML private Button btnConfirmReturn;

    // Service instances
    private final BorrowService borrowService = new BorrowServiceImpl();
    private final ReturnService returnService = new ReturnServiceImpl();

    private ObservableList<BorrowRecordDTO> borrowedList;

    /**
     * Initializes the controller, setting up the table and listeners.
     */
    @FXML
    public void initialize() {
        // The fine field is for display only
        txtFine.setEditable(false);
        txtFine.setStyle("-fx-background-color: #e2e2e2;");

        // Set up table columns
        configureTable();

        // Load initial data
        loadBorrowedRecords();

        // Add a listener to handle row selection
        setupListeners();
    }

    private void configureTable() {
        colRecordId.setCellValueFactory(new PropertyValueFactory<>("recordId"));
        colBookId.setCellValueFactory(new PropertyValueFactory<>("bookId"));
        colUserId.setCellValueFactory(new PropertyValueFactory<>("userId"));
        colBorrowDate.setCellValueFactory(new PropertyValueFactory<>("borrowDate"));
    }

    private void setupListeners() {
        // When a record is selected in the table, prepare the return form.
        tblBorrowedBooks.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                // Default the return date to today and calculate the fine.
                dateReturn.setValue(LocalDate.now());
                calculateAndDisplayFine(newSelection, LocalDate.now());
            } else {
                // If no row is selected, clear the form.
                clearReturnForm();
            }
        });

        // When the return date is changed, recalculate the fine.
        dateReturn.valueProperty().addListener((obs, oldDate, newDate) -> {
            BorrowRecordDTO selectedRecord = tblBorrowedBooks.getSelectionModel().getSelectedItem();
            if (selectedRecord != null && newDate != null) {
                calculateAndDisplayFine(selectedRecord, newDate);
            }
        });
    }

    /**
     * Handles the "Confirm Return" button click.
     */
    @FXML
    void btnConfirmReturnOnAction(ActionEvent event) {
        BorrowRecordDTO selectedRecord = tblBorrowedBooks.getSelectionModel().getSelectedItem();
        LocalDate returnDate = dateReturn.getValue();

        // Validate that a record is selected and a return date is chosen.
        if (selectedRecord == null || returnDate == null) {
            showAlert(Alert.AlertType.WARNING, "Validation Error", "Please select a borrowed record and a return date.");
            return;
        }

        // Create a DTO for the return process.
        ReturnDTO returnDTO = new ReturnDTO(
                selectedRecord.getRecordId(),
                selectedRecord.getBookId(),
                selectedRecord.getBorrowDate(),
                returnDate,
                Double.parseDouble(txtFine.getText())
        );

        try {
            // Call the transactional service method to process the return.
            boolean success = returnService.processReturn(returnDTO);

            if (success) {
                showAlert(Alert.AlertType.INFORMATION, "Success", "Book returned successfully!");
                refreshView();
            } else {
                showAlert(Alert.AlertType.ERROR, "Transaction Failed", "The book return could not be processed.");
            }
        } catch (SQLException e) {
            handleSQLException(e);
        }
    }

    // --- Helper Methods ---

    private void loadBorrowedRecords() {
        try {
            // Fetch all borrow records and filter for those not yet returned.
            List<BorrowRecordDTO> allRecords = borrowService.getAllBorrowRecords();
            List<BorrowRecordDTO> currentlyBorrowed = allRecords.stream()
                    .filter(record -> record.getReturnDate() == null)
                    .collect(Collectors.toList());

            borrowedList = FXCollections.observableArrayList(currentlyBorrowed);
            tblBorrowedBooks.setItems(borrowedList);
        } catch (SQLException e) {
            handleSQLException(e);
        }
    }

    /**
     * Calculates the fine and displays it in the text field.
     * This logic is for UI display only; the final calculation is done in the service layer.
     */
    private void calculateAndDisplayFine(BorrowRecordDTO record, LocalDate returnDate) {
        long daysBorrowed = ChronoUnit.DAYS.between(record.getBorrowDate(), returnDate);
        if (daysBorrowed > 14) { // Assuming a 14-day lending period
            long overdueDays = daysBorrowed - 14;
            double fine = overdueDays * 10.0; // Assuming a fine of 10.0 per day
            txtFine.setText(String.format("%.2f", fine));
        } else {
            txtFine.setText("0.00");
        }
    }

    private void clearReturnForm() {
        dateReturn.setValue(null);
        txtFine.clear();
    }

    private void refreshView() {
        loadBorrowedRecords();
        clearReturnForm();
    }

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void handleSQLException(SQLException e) {
        e.printStackTrace();
        showAlert(Alert.AlertType.ERROR, "Database Error", "A database error occurred: " + e.getMessage());
    }
}
