package org.example.controller;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.StringConverter;
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
 * This version includes debugging print statements to trace the initialization process.
 */
public class BorrowFormController {

    // FXML UI Components
    @FXML private TextField txtRecordId;
    @FXML private ComboBox<UserDTO> cmbUserId;
    @FXML private Label lblUserName;
    @FXML private ComboBox<BookDTO> cmbBookId;
    @FXML private Label lblBookTitle;
    @FXML private DatePicker dateBorrow;
    @FXML private Button btnConfirmBorrow;
    @FXML private TableView<BorrowRecordDTO> tblBorrowedBooks;
    @FXML private TableColumn<BorrowRecordDTO, String> colRecordId;
    @FXML private TableColumn<BorrowRecordDTO, String> colBookId;
    @FXML private TableColumn<BorrowRecordDTO, String> colUserId;
    @FXML private TableColumn<BorrowRecordDTO, LocalDate> colBorrowDate;

    // Service instances
    private final BorrowService borrowService = new BorrowServiceImpl();
    private final BookService bookService = new BookServiceImpl();
    private final UserService userService = new UserServiceImpl();

    @FXML
    public void initialize() {
        System.out.println("[DEBUG] BorrowFormController: Starting initialize()...");
        try {
            // Configure table columns
            configureTable();

            // Load data and set up UI
            loadCurrentlyBorrowed();
            generateAndSetNextId();
            loadUsersAndBooksIntoComboBoxes();
            setupComboBoxListeners();

            System.out.println("[DEBUG] BorrowFormController: initialize() completed successfully.");
        } catch (Exception e) {
            // If any part of the initialization fails, print the error.
            System.err.println("[DEBUG] !!! FATAL ERROR during BorrowFormController initialize() !!!");
            e.printStackTrace();
        }
    }

    private void configureTable() {
        System.out.println("[DEBUG] Configuring table columns...");
        colRecordId.setCellValueFactory(new PropertyValueFactory<>("recordId"));
        colBookId.setCellValueFactory(new PropertyValueFactory<>("bookId"));
        colUserId.setCellValueFactory(new PropertyValueFactory<>("userId"));
        colBorrowDate.setCellValueFactory(new PropertyValueFactory<>("borrowDate"));
        System.out.println("[DEBUG] Table columns configured.");
    }

    private void loadUsersAndBooksIntoComboBoxes() {
        System.out.println("[DEBUG] Loading data into ComboBoxes...");
        try {
            List<UserDTO> allUsers = userService.getAllUsers();
            cmbUserId.setItems(FXCollections.observableArrayList(allUsers));
            System.out.println("[DEBUG] Loaded " + allUsers.size() + " users.");

            List<BookDTO> availableBooks = bookService.getAvailableBooks();
            cmbBookId.setItems(FXCollections.observableArrayList(availableBooks));
            System.out.println("[DEBUG] Loaded " + availableBooks.size() + " available books.");

        } catch (SQLException e) {
            handleSQLException(e);
        }

        cmbUserId.setConverter(new StringConverter<UserDTO>() {
            @Override
            public String toString(UserDTO user) {
                return user == null ? "" : user.getUserId() + " - " + user.getName();
            }
            @Override
            public UserDTO fromString(String string) { return null; }
        });

        cmbBookId.setConverter(new StringConverter<BookDTO>() {
            @Override
            public String toString(BookDTO book) {
                return book == null ? "" : book.getBookId() + " - " + book.getTitle();
            }
            @Override
            public BookDTO fromString(String string) { return null; }
        });
        System.out.println("[DEBUG] ComboBoxes configured.");
    }

    private void setupComboBoxListeners() {
        System.out.println("[DEBUG] Setting up ComboBox listeners...");
        cmbUserId.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                lblUserName.setText("User Name: " + newVal.getName());
            }
        });

        cmbBookId.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                lblBookTitle.setText("Book Title: " + newVal.getTitle());
            }
        });
        System.out.println("[DEBUG] ComboBox listeners configured.");
    }

    @FXML
    void btnConfirmBorrowOnAction(ActionEvent event) {
        UserDTO selectedUser = cmbUserId.getSelectionModel().getSelectedItem();
        BookDTO selectedBook = cmbBookId.getSelectionModel().getSelectedItem();

        if (selectedUser == null || selectedBook == null || dateBorrow.getValue() == null) {
            showAlert(Alert.AlertType.WARNING, "Validation Error", "Please select a User, a Book, and a Borrow Date.");
            return;
        }

        BorrowRecordDTO newRecord = new BorrowRecordDTO(
                txtRecordId.getText(),
                selectedUser.getUserId(),
                selectedBook.getBookId(),
                dateBorrow.getValue(),
                null, 0.0
        );

        try {
            boolean success = borrowService.borrowBook(newRecord);
            if (success) {
                showAlert(Alert.AlertType.INFORMATION, "Success", "Book borrowed successfully!");
                refreshView();
            } else {
                showAlert(Alert.AlertType.ERROR, "Transaction Failed", "The book could not be borrowed.");
            }
        } catch (SQLException e) {
            handleSQLException(e);
        }
    }

    private void loadCurrentlyBorrowed() {
        System.out.println("[DEBUG] Loading currently borrowed books...");
        try {
            List<BorrowRecordDTO> currentlyBorrowed = borrowService.getAllBorrowRecords().stream()
                    .filter(record -> record.getReturnDate() == null)
                    .collect(Collectors.toList());
            tblBorrowedBooks.setItems(FXCollections.observableArrayList(currentlyBorrowed));
            System.out.println("[DEBUG] Found " + currentlyBorrowed.size() + " borrowed books.");
        } catch (SQLException e) {
            handleSQLException(e);
        }
    }

    private void generateAndSetNextId() {
        System.out.println("[DEBUG] Generating next record ID...");
        try {
            String nextId = borrowService.generateNextRecordId();
            txtRecordId.setText(nextId);
            System.out.println("[DEBUG] Next record ID is: " + nextId);
        } catch (SQLException e) {
            handleSQLException(e);
        }
    }

    private void clearForm() {
        cmbUserId.getSelectionModel().clearSelection();
        cmbBookId.getSelectionModel().clearSelection();
        lblUserName.setText("User Name: -");
        lblBookTitle.setText("Book Title: -");
        dateBorrow.setValue(null);
    }

    private void refreshView() {
        loadCurrentlyBorrowed();
        clearForm();
        generateAndSetNextId();
        loadUsersAndBooksIntoComboBoxes();
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
