package org.example.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import org.example.dto.BookDTO;
import org.example.service.BookService;
import org.example.service.impl.BookServiceImpl;

import java.sql.SQLException;
import java.util.List;

/**
 * Controller for the book_form.fxml view.
 * This version uses manual entry for the Book ID.
 */
public class BookFormController {

    // FXML UI Components
    @FXML private TextField txtBookId;
    @FXML private TextField txtIsbn;
    @FXML private TextField txtTitle;
    @FXML private TextField txtAuthor;
    @FXML private TextField txtGenre;
    @FXML private CheckBox chkAvailability;
    @FXML private TextField txtSearch;
    @FXML private TableView<BookDTO> tblBooks;
    @FXML private TableColumn<BookDTO, String> colBookId;
    @FXML private TableColumn<BookDTO, String> colTitle;
    @FXML private TableColumn<BookDTO, String> colAuthor;
    @FXML private TableColumn<BookDTO, Boolean> colAvailable;

    // NOTE: The BookService and DAO no longer need the ID generation methods.
    // You can remove them from those files to simplify the code.
    private final BookService bookService = new BookServiceImpl();
    private ObservableList<BookDTO> bookList;

    @FXML
    public void initialize() {
        // The Book ID field is now editable by the user.
        txtBookId.setEditable(true);
        txtBookId.setStyle(""); // Reset style to default

        // Set up the table columns
        colBookId.setCellValueFactory(new PropertyValueFactory<>("bookId"));
        colTitle.setCellValueFactory(new PropertyValueFactory<>("title"));
        colAuthor.setCellValueFactory(new PropertyValueFactory<>("author"));
        colAvailable.setCellValueFactory(new PropertyValueFactory<>("availability"));

        // Load data and set up listeners
        loadAllBooks();
        setupListeners();
    }

    private void setupListeners() {
        tblBooks.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                populateForm(newSelection);
            }
        });

        txtSearch.textProperty().addListener((obs, oldText, newText) -> {
            filterBooks(newText);
        });
    }

    /**
     * Handles the "Add Book" button click using a manually entered ID.
     */
    @FXML
    void btnAddOnAction(ActionEvent event) {
        BookDTO newBook = readFormData();
        if (newBook == null) return; // Validation failed

        try {
            if (bookService.addBook(newBook)) {
                showAlert(Alert.AlertType.INFORMATION, "Success", "Book added successfully!");
                refreshView();
            } else {
                showAlert(Alert.AlertType.ERROR, "Error", "Failed to add the book. The Book ID might already exist.");
            }
        } catch (SQLException e) {
            handleSQLException(e);
        }
    }

    /**
     * Handles the "Update Book" button click.
     */
    @FXML
    void btnUpdateOnAction(ActionEvent event) {
        BookDTO updatedBook = readFormData();
        if (updatedBook == null) return;

        try {
            if (bookService.updateBook(updatedBook)) {
                showAlert(Alert.AlertType.INFORMATION, "Success", "Book updated successfully!");
                refreshView();
            } else {
                showAlert(Alert.AlertType.ERROR, "Error", "Failed to update the book.");
            }
        } catch (SQLException e) {
            handleSQLException(e);
        }
    }

    /**
     * Handles the "Delete Book" button click.
     */
    @FXML
    void btnDeleteOnAction(ActionEvent event) {
        String bookId = txtBookId.getText();
        if (bookId.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Input Error", "Please select a book to delete.");
            return;
        }

        try {
            if (bookService.deleteBook(bookId)) {
                showAlert(Alert.AlertType.INFORMATION, "Success", "Book deleted successfully!");
                refreshView();
            } else {
                showAlert(Alert.AlertType.ERROR, "Error", "Failed to delete the book.");
            }
        } catch (SQLException e) {
            handleSQLException(e);
        }
    }

    @FXML
    void btnClearOnAction(ActionEvent event) {
        clearForm();
    }

    // --- Helper Methods ---

    private void loadAllBooks() {
        try {
            List<BookDTO> allBooks = bookService.getAllBooks();
            bookList = FXCollections.observableArrayList(allBooks);
            tblBooks.setItems(bookList);
        } catch (SQLException e) {
            handleSQLException(e);
        }
    }

    private void populateForm(BookDTO book) {
        txtBookId.setText(book.getBookId());
        txtIsbn.setText(book.getIsbn());
        txtTitle.setText(book.getTitle());
        txtAuthor.setText(book.getAuthor());
        txtGenre.setText(book.getGenre());
        chkAvailability.setSelected(book.isAvailability());
    }

    /**
     * Reads form data for both Add and Update, now including the manual Book ID.
     */
    private BookDTO readFormData() {
        if (txtBookId.getText().isEmpty() || txtIsbn.getText().isEmpty() || txtTitle.getText().isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Validation Error", "Please fill in at least Book ID, ISBN, and Title.");
            return null;
        }
        return new BookDTO(
                txtBookId.getText(),
                txtIsbn.getText(),
                txtTitle.getText(),
                txtAuthor.getText(),
                txtGenre.getText(),
                chkAvailability.isSelected()
        );
    }

    private void clearForm() {
        txtBookId.clear();
        txtIsbn.clear();
        txtTitle.clear();
        txtAuthor.clear();
        txtGenre.clear();
        chkAvailability.setSelected(false);
        txtSearch.clear();
        tblBooks.getSelectionModel().clearSelection();
    }

    private void refreshView() {
        loadAllBooks();
        clearForm();
    }

    private void filterBooks(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            tblBooks.setItems(bookList);
            return;
        }
        String lowerCaseFilter = keyword.toLowerCase();
        ObservableList<BookDTO> filteredData = bookList.filtered(book ->
                book.getTitle().toLowerCase().contains(lowerCaseFilter) ||
                        book.getBookId().toLowerCase().contains(lowerCaseFilter) ||
                        book.getAuthor().toLowerCase().contains(lowerCaseFilter)
        );
        tblBooks.setItems(filteredData);
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
