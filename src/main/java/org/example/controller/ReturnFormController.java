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
import org.example.service.FineService;
import org.example.service.ReturnService;
import org.example.service.impl.BorrowServiceImpl;
import org.example.service.impl.FineServiceImpl;
import org.example.service.impl.ReturnServiceImpl;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

/**
 * Controller for the return_form.fxml view.
 * Handles the logic for processing book returns and managing fine payments.
 */
public class ReturnFormController {

    // FXML UI Components
    @FXML private TableView<BorrowRecordDTO> tblBorrowRecords;
    @FXML private TableColumn<BorrowRecordDTO, String> colRecordId;
    @FXML private TableColumn<BorrowRecordDTO, String> colBookId;
    @FXML private TableColumn<BorrowRecordDTO, String> colUserId;
    @FXML private TableColumn<BorrowRecordDTO, LocalDate> colBorrowDate;
    @FXML private TableColumn<BorrowRecordDTO, LocalDate> colReturnDate;
    @FXML private TableColumn<BorrowRecordDTO, Double> colFine;
    @FXML private DatePicker dateReturn;
    @FXML private TextField txtFine;
    @FXML private Button btnConfirmReturn;
    @FXML private Button btnPayFine; // The new button

    // Service instances
    private final BorrowService borrowService = new BorrowServiceImpl();
    private final ReturnService returnService = new ReturnServiceImpl();
    private final FineService fineService = new FineServiceImpl(); // The new service

    private ObservableList<BorrowRecordDTO> recordList;

    @FXML
    public void initialize() {
        txtFine.setEditable(false);
        txtFine.setStyle("-fx-background-color: #e2e2e2;");

        configureTable();
        loadAllRecords();
        setupListeners();

        // Initially disable buttons until a selection is made
        btnConfirmReturn.setDisable(true);
        btnPayFine.setDisable(true);
    }

    private void configureTable() {
        colRecordId.setCellValueFactory(new PropertyValueFactory<>("recordId"));
        colBookId.setCellValueFactory(new PropertyValueFactory<>("bookId"));
        colUserId.setCellValueFactory(new PropertyValueFactory<>("userId"));
        colBorrowDate.setCellValueFactory(new PropertyValueFactory<>("borrowDate"));
        colReturnDate.setCellValueFactory(new PropertyValueFactory<>("returnDate"));
        colFine.setCellValueFactory(new PropertyValueFactory<>("fine"));
    }

    private void setupListeners() {
        // When a record is selected, update the form and button states.
        tblBorrowRecords.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                updateFormForSelection(newSelection);
            } else {
                clearForm();
            }
        });

        // When the return date is changed, recalculate the fine.
        dateReturn.valueProperty().addListener((obs, oldDate, newDate) -> {
            BorrowRecordDTO selectedRecord = tblBorrowRecords.getSelectionModel().getSelectedItem();
            if (selectedRecord != null && newDate != null) {
                calculateAndDisplayFine(selectedRecord.getBorrowDate(), newDate);
            }
        });
    }

    /**
     * Handles the "Confirm Return" button click.
     */
    @FXML
    void btnConfirmReturnOnAction(ActionEvent event) {
        BorrowRecordDTO selectedRecord = tblBorrowRecords.getSelectionModel().getSelectedItem();
        LocalDate returnDate = dateReturn.getValue();

        if (selectedRecord == null || returnDate == null) {
            showAlert(Alert.AlertType.WARNING, "Validation Error", "Please select a record and a return date.");
            return;
        }

        ReturnDTO returnDTO = new ReturnDTO(
                selectedRecord.getRecordId(),
                selectedRecord.getBookId(),
                selectedRecord.getBorrowDate(),
                returnDate,
                0 // Fine will be calculated by the service
        );

        try {
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

    /**
     * Handles the "Mark as Paid" button click.
     */
    @FXML
    void btnPayFineOnAction(ActionEvent event) {
        BorrowRecordDTO selectedRecord = tblBorrowRecords.getSelectionModel().getSelectedItem();
        if (selectedRecord == null) return;

        try {
            boolean success = fineService.payFine(selectedRecord.getRecordId());
            if (success) {
                showAlert(Alert.AlertType.INFORMATION, "Success", "Fine marked as paid!");
                refreshView();
            } else {
                showAlert(Alert.AlertType.ERROR, "Error", "Failed to update fine payment status.");
            }
        } catch (SQLException e) {
            handleSQLException(e);
        }
    }

    // --- Helper Methods ---

    private void loadAllRecords() {
        try {
            recordList = FXCollections.observableArrayList(borrowService.getAllBorrowRecords());
            tblBorrowRecords.setItems(recordList);
        } catch (SQLException e) {
            handleSQLException(e);
        }
    }

    private void updateFormForSelection(BorrowRecordDTO record) {
        // If the book has not been returned yet
        if (record.getReturnDate() == null) {
            dateReturn.setValue(LocalDate.now());
            calculateAndDisplayFine(record.getBorrowDate(), LocalDate.now());
            btnConfirmReturn.setDisable(false);
            btnPayFine.setDisable(true); // Can't pay a fine until it's officially recorded
        } else {
            // If the book has been returned
            dateReturn.setValue(record.getReturnDate());
            txtFine.setText(String.format("%.2f", record.getFine()));
            btnConfirmReturn.setDisable(true); // Already returned

            // Only enable the pay button if there is an unpaid fine
            if (record.getFine() > 0 && !record.isFinePaid()) {
                btnPayFine.setDisable(false);
            } else {
                btnPayFine.setDisable(true);
            }
        }
    }

    private void calculateAndDisplayFine(LocalDate borrowDate, LocalDate returnDate) {
        long daysBorrowed = ChronoUnit.DAYS.between(borrowDate, returnDate);
        if (daysBorrowed > 14) {
            long overdueDays = daysBorrowed - 14;
            double fine = overdueDays * 10.0;
            txtFine.setText(String.format("%.2f", fine));
        } else {
            txtFine.setText("0.00");
        }
    }

    private void clearForm() {
        dateReturn.setValue(null);
        txtFine.clear();
        btnConfirmReturn.setDisable(true);
        btnPayFine.setDisable(true);
        tblBorrowRecords.getSelectionModel().clearSelection();
    }

    private void refreshView() {
        loadAllRecords();
        clearForm();
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
