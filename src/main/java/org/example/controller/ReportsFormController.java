package org.example.controller;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import org.example.dto.BookDTO;
import org.example.dto.BorrowRecordDTO;
import org.example.service.ReportService;
import org.example.service.impl.ReportServiceImpl;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

/**
 * Controller for the reports_form.fxml view.
 * Handles the logic for generating and displaying various library reports.
 */
public class ReportsFormController {

    // FXML UI Components
    @FXML private TableView<Object> tblReportData; // Using Object for generic data
    @FXML private TableColumn<Object, String> colBookId;
    @FXML private TableColumn<Object, String> colTitle;
    @FXML private TableColumn<Object, String> colAuthor;
    @FXML private TableColumn<Object, String> colUserId;
    @FXML private TableColumn<Object, LocalDate> colBorrowDate;
    @FXML private TableColumn<Object, Double> colFine;

    // Service Layer for fetching report data
    private final ReportService reportService = new ReportServiceImpl();

    /**
     * Initializes the controller. By default, it loads the available books report.
     */
    @FXML
    public void initialize() {
        // Load the default report when the view is first opened.
        btnAvailableBooksOnAction(null);
    }

    /**
     * Handles the "Available Books" button click.
     * Configures the table for book data and loads the report.
     */
    @FXML
    void btnAvailableBooksOnAction(ActionEvent event) {
        setupTableForAvailableBooks();
        try {
            List<BookDTO> availableBooks = reportService.getAvailableBooks();
            tblReportData.setItems(FXCollections.observableArrayList(availableBooks));
        } catch (SQLException e) {
            handleSQLException(e);
        }
    }

    /**
     * Handles the "Borrowed Books" button click.
     * Configures the table for borrowed records and loads the report.
     */
    @FXML
    void btnBorrowedBooksOnAction(ActionEvent event) {
        setupTableForBorrowedBooks();
        try {
            List<BorrowRecordDTO> borrowedBooks = reportService.getBorrowedBooks();
            tblReportData.setItems(FXCollections.observableArrayList(borrowedBooks));
        } catch (SQLException e) {
            handleSQLException(e);
        }
    }

    /**
     * Handles the "Overdue Books" button click.
     * Configures the table for overdue records and loads the report.
     */
    @FXML
    void btnOverdueBooksOnAction(ActionEvent event) {
        setupTableForOverdueBooks();
        try {
            List<BorrowRecordDTO> overdueBooks = reportService.getOverdueBooks();
            tblReportData.setItems(FXCollections.observableArrayList(overdueBooks));
        } catch (SQLException e) {
            handleSQLException(e);
        }
    }

    // --- Helper Methods ---

    /**
     * Configures the TableView columns for the "Available Books" report.
     * Shows book-related columns and hides borrowing-related columns.
     */
    private void setupTableForAvailableBooks() {
        // Set cell value factories for BookDTO properties
        colBookId.setCellValueFactory(new PropertyValueFactory<>("bookId"));
        colTitle.setCellValueFactory(new PropertyValueFactory<>("title"));
        colAuthor.setCellValueFactory(new PropertyValueFactory<>("author"));

        // Set column visibility
        colBookId.setVisible(true);
        colTitle.setVisible(true);
        colAuthor.setVisible(true);
        colUserId.setVisible(false);
        colBorrowDate.setVisible(false);
        colFine.setVisible(false);
    }

    /**
     * Configures the TableView columns for the "Borrowed Books" report.
     */
    private void setupTableForBorrowedBooks() {
        // Set cell value factories for BorrowRecordDTO properties
        colBookId.setCellValueFactory(new PropertyValueFactory<>("bookId"));
        colUserId.setCellValueFactory(new PropertyValueFactory<>("userId"));
        colBorrowDate.setCellValueFactory(new PropertyValueFactory<>("borrowDate"));

        // Set column visibility
        colBookId.setVisible(true);
        colTitle.setVisible(false);
        colAuthor.setVisible(false);
        colUserId.setVisible(true);
        colBorrowDate.setVisible(true);
        colFine.setVisible(false);
    }

    /**
     * Configures the TableView columns for the "Overdue Books" report.
     */
    private void setupTableForOverdueBooks() {
        // Set cell value factories for BorrowRecordDTO properties
        colBookId.setCellValueFactory(new PropertyValueFactory<>("bookId"));
        colUserId.setCellValueFactory(new PropertyValueFactory<>("userId"));
        colBorrowDate.setCellValueFactory(new PropertyValueFactory<>("borrowDate"));
        colFine.setCellValueFactory(new PropertyValueFactory<>("fine"));

        // Set column visibility
        colBookId.setVisible(true);
        colTitle.setVisible(false);
        colAuthor.setVisible(false);
        colUserId.setVisible(true);
        colBorrowDate.setVisible(true);
        colFine.setVisible(true);
    }

    private void handleSQLException(SQLException e) {
        e.printStackTrace();
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Database Error");
        alert.setHeaderText(null);
        alert.setContentText("A database error occurred while generating the report.");
        alert.showAndWait();
    }
}
