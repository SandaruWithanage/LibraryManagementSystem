package org.example.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import org.example.dto.BookDTO; // You will need to create this DTO
import org.example.service.BookService; // You will need to create this Service
import org.example.service.impl.BookServiceImpl; // You will need to create this implementation

import java.util.List;

public class BookFormController {

    @FXML
    private TextField txtBookId;
    @FXML
    private TextField txtIsbn;
    @FXML
    private TextField txtTitle;
    @FXML
    private TextField txtAuthor;
    @FXML
    private TextField txtGenre;
    @FXML
    private CheckBox chkAvailability;
    @FXML
    private TextField txtSearch;
    @FXML
    private TableView<BookDTO> tblBooks;
    @FXML
    private TableColumn<BookDTO, String> colBookId;
    @FXML
    private TableColumn<BookDTO, String> colTitle;
    @FXML
    private TableColumn<BookDTO, String> colAuthor;
    @FXML
    private TableColumn<BookDTO, Boolean> colAvailable;

    // This part will require you to create the BookService and related classes.
    // For now, you can comment it out to test if the UI loads.
    // private final BookService bookService = new BookServiceImpl();

    @FXML
    public void initialize() {
        // Set up the table columns to bind to BookDTO properties
        colBookId.setCellValueFactory(new PropertyValueFactory<>("bookId"));
        colTitle.setCellValueFactory(new PropertyValueFactory<>("title"));
        colAuthor.setCellValueFactory(new PropertyValueFactory<>("author"));
        colAvailable.setCellValueFactory(new PropertyValueFactory<>("availability"));

        // loadBooks(); // You can uncomment this after creating the service layer
    }

    @FXML
    void btnAddOnAction(ActionEvent event) {
        // Logic to add a book will go here
    }

    @FXML
    void btnUpdateOnAction(ActionEvent event) {
        // Logic to update a book will go here
    }

    @FXML
    void btnDeleteOnAction(ActionEvent event) {
        // Logic to delete a book will go here
    }

    @FXML
    void btnClearOnAction(ActionEvent event) {
        // Logic to clear the form fields will go here
    }

    /*
    private void loadBooks() {
        // This method will load books from the database via the service
        List<BookDTO> books = bookService.getAllBooks();
        tblBooks.setItems(FXCollections.observableArrayList(books));
    }
    */
}
