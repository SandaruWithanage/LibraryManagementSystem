package org.example.controller;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import org.example.dto.BookDTO;
import org.example.dto.BorrowRecordDTO;
import org.example.dto.UserDTO;
import org.example.service.BookService;
import org.example.service.BorrowService;
import org.example.service.UserService;
import org.example.service.impl.BookServiceImpl;
import org.example.service.impl.BorrowServiceImpl;
import org.example.service.impl.UserServiceImpl;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Controller for the borrow_form.fxml view.
 * Handles the entire process of creating a new book loan.
 */
public class BorrowFormController {

    // FXML UI Components
    @FXML private TextField txtRecordId;
    @FXML private TextField txtUserId;
    @FXML private Label lblUserName;
    @FXML private TextField txtBookId;
    @FXML private Label lblBookTitle;
    @FXML private DatePicker dateBorrow;
    @FXML private Button btnConfirmBorrow;
    @FXML private TableView<BorrowRecordDTO> tblBorrowedBooks;
    @FXML private TableColumn<BorrowRecordDTO, String> colRecordId;
    @FXML private TableColumn<BorrowRecordDTO, String> colBookId;
    @FXML private TableColumn<BorrowRecordDTO, String> colUserId;
    @FXML private TableColumn<BorrowRecordDTO, LocalDate> colBorrowDate;

    // Service instances for all required modules
    private final BorrowService borrowService = new BorrowServiceImpl();
    private final BookService bookService = new BookServiceImpl();
    private final UserService userService = new UserServiceImpl();

    private BookDTO selectedBook;
    private UserDTO selectedUser;

    @FXML
    public void initialize() {
        // Set up the table columns
        colRecordId.setCellValueFactory(new PropertyValueFactory<>("recordId"));
        colBookId.setCellValueFactory(new PropertyValueFactory<>("bookId"));
        colUserId.setCellValueFactory(new PropertyValueFactory<>("userId"));
        colBorrowDate.setCellValueFactory(new PropertyValueFactory<>("borrowDate"));

        // Load initial data and set up listeners
        loadCurrentlyBorrowed();
        generateAndSetNextId();
        setupListeners();
    }

    private void setupListeners() {
        // When the user presses Enter in the User ID field, fetch the user's name.
        txtUserId.setOnAction(event -> fetchUserDetails());
        // When the user presses Enter in the Book ID field, fetch the book's title.
        txtBookId.setOnAction(event -> fetchBookDetails());
    }

    private void fetchUserDetails() {
        String userId = txtUserId.getText();
        try {
            selectedUser = userService.getUserById(userId);
            if (selectedUser != null) {
                lblUserName.setText("User Name: " + selectedUser.getName());
            } else {
                lblUserName.setText("User Name: Not Found");
                selectedUser = null;
            }
        } catch (SQLException e) {
            handleSQLException(e);
        }
    }

    private void fetchBookDetails() {
        String bookId = txtBookId.getText();
        try {
            selectedBook = bookService.getBookById(bookId);
            if (selectedBook != null) {
                if (selectedBook.isAvailability()) {
                    lblBookTitle.setText("Book Title: " + selectedBook.getTitle());
                } else {
                    lblBookTitle.setText("Book Title: Not Available");
                    selectedBook = null; // Invalidate selection if book is not available
                }
            } else {
                lblBookTitle.setText("Book Title: Not Found");
                selectedBook = null;
            }
        } catch (SQLException e) {
            handleSQLException(e);
        }
    }

    /**
     * Handles the "Confirm Borrow" button click.
     * Validates all inputs and processes the loan transaction.
     */
    @FXML
    void btnConfirmBorrowOnAction(ActionEvent event) {
        // 1. Validate all inputs
        if (selectedUser == null || selectedBook == null || dateBorrow.getValue() == null) {
            showAlert(Alert.AlertType.WARNING, "Validation Error", "Please ensure a valid User, an available Book, and a Borrow Date are selected.");
            return;
        }

        // 2. Create the DTO for the new borrow record
        BorrowRecordDTO newRecord = new BorrowRecordDTO(
                txtRecordId.getText(),
                selectedUser.getUserId(),
                selectedBook.getBookId(),
                dateBorrow.getValue(),
                null, // Return date is null for a new loan
                0.0 // Fine is 0 for a new loan
        );

        try {
            // 3. Call the transactional service method
            boolean success = borrowService.borrowBook(newRecord);

            if (success) {
                showAlert(Alert.AlertType.INFORMATION, "Success", "Book borrowed successfully!");
                refreshView();
            } else {
                showAlert(Alert.AlertType.ERROR, "Transaction Failed", "The book could not be borrowed. The transaction was rolled back.");
            }
        } catch (SQLException e) {
            handleSQLException(e);
        }
    }

    // --- Helper Methods ---

    private void loadCurrentlyBorrowed() {
        try {
            // Filter the list to show only books that have not been returned
            List<BorrowRecordDTO> currentlyBorrowed = borrowService.getAllBorrowRecords().stream()
                    .filter(record -> record.getReturnDate() == null)
                    .collect(Collectors.toList());
            tblBorrowedBooks.setItems(FXCollections.observableArrayList(currentlyBorrowed));
        } catch (SQLException e) {
            handleSQLException(e);
        }
    }

    private void generateAndSetNextId() {
        try {
            txtRecordId.setText(borrowService.generateNextRecordId());
        } catch (SQLException e) {
            handleSQLException(e);
        }
    }

    private void clearForm() {
        txtUserId.clear();
        txtBookId.clear();
        lblUserName.setText("User Name: -");
        lblBookTitle.setText("Book Title: -");
        dateBorrow.setValue(null);
        selectedUser = null;
        selectedBook = null;
    }

    private void refreshView() {
        loadCurrentlyBorrowed();
        clearForm();
        generateAndSetNextId();
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
