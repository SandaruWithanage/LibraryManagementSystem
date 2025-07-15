package org.example.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.DatePicker;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import org.example.dto.UserDTO;
import org.example.service.UserService;
import org.example.service.impl.UserServiceImpl;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

public class UserFormController {

    @FXML private TextField txtUserId;
    @FXML private TextField txtName;
    @FXML private TextField txtContact;
    @FXML private DatePicker dateMembership;
    @FXML private TextField txtUsername;
    @FXML private PasswordField txtPassword;
    @FXML private TextField txtSearch;
    @FXML private TableView<UserDTO> tblUsers;
    @FXML private TableColumn<UserDTO, String> colUserId;
    @FXML private TableColumn<UserDTO, String> colName;
    @FXML private TableColumn<UserDTO, String> colContact;
    @FXML private TableColumn<UserDTO, String> colUsername;
    @FXML private TableColumn<UserDTO, LocalDate> colMembershipDate;

    private final UserService userService = new UserServiceImpl();
    private ObservableList<UserDTO> userList;

    @FXML
    public void initialize() {
        // The User ID field is non-editable.
        txtUserId.setEditable(false);
        txtUserId.setStyle("-fx-background-color: #e2e2e2;");

        configureTable();
        loadAllUsers();
        generateAndSetNextId();
        setupListeners();
    }

    private void configureTable() {
        colUserId.setCellValueFactory(new PropertyValueFactory<>("userId"));
        colName.setCellValueFactory(new PropertyValueFactory<>("name"));
        colContact.setCellValueFactory(new PropertyValueFactory<>("contact"));
        colUsername.setCellValueFactory(new PropertyValueFactory<>("username"));
        colMembershipDate.setCellValueFactory(new PropertyValueFactory<>("membershipDate"));
    }

    private void setupListeners() {
        tblUsers.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                populateForm(newSelection);
            }
        });

        txtSearch.textProperty().addListener((obs, oldText, newText) -> {
            filterUsers(newText);
        });
    }

    @FXML
    void btnAddOnAction(ActionEvent event) {
        UserDTO newUser = readFormData();
        if (newUser == null) return;

        try {
            if (userService.addUser(newUser)) {
                showAlert(Alert.AlertType.INFORMATION, "Success", "User added successfully!");
                refreshView();
            } else {
                showAlert(Alert.AlertType.ERROR, "Error", "Failed to add the user.");
            }
        } catch (SQLException e) {
            handleSQLException(e);
        }
    }

    @FXML
    void btnUpdateOnAction(ActionEvent event) {
        UserDTO updatedUser = readFormData();
        if (updatedUser == null) return;

        try {
            if (userService.updateUser(updatedUser)) {
                showAlert(Alert.AlertType.INFORMATION, "Success", "User updated successfully!");
                refreshView();
            } else {
                showAlert(Alert.AlertType.ERROR, "Error", "Failed to update the user.");
            }
        } catch (SQLException e) {
            handleSQLException(e);
        }
    }

    @FXML
    void btnDeleteOnAction(ActionEvent event) {
        String userId = txtUserId.getText();
        if (userId.isEmpty() || !userId.startsWith("U")) {
            showAlert(Alert.AlertType.WARNING, "Selection Error", "Please select a user to delete.");
            return;
        }

        try {
            if (userService.deleteUser(userId)) {
                showAlert(Alert.AlertType.INFORMATION, "Success", "User deleted successfully!");
                refreshView();
            } else {
                showAlert(Alert.AlertType.ERROR, "Error", "Failed to delete the user.");
            }
        } catch (SQLException e) {
            handleSQLException(e);
        }
    }

    @FXML
    void btnClearOnAction(ActionEvent event) {
        clearForm();
        generateAndSetNextId();
    }

    // --- Helper Methods ---

    private void loadAllUsers() {
        try {
            List<UserDTO> allUsers = userService.getAllUsers();
            userList = FXCollections.observableArrayList(allUsers);
            tblUsers.setItems(userList);
        } catch (SQLException e) {
            handleSQLException(e);
        }
    }

    private void generateAndSetNextId() {
        try {
            txtUserId.setText(userService.generateNextUserId());
        } catch (SQLException e) {
            handleSQLException(e);
        }
    }

    private void populateForm(UserDTO user) {
        txtUserId.setText(user.getUserId());
        txtName.setText(user.getName());
        txtContact.setText(user.getContact());
        dateMembership.setValue(user.getMembershipDate());
        txtUsername.setText(user.getUsername());
        txtPassword.setText(user.getPassword());
    }

    private UserDTO readFormData() {
        if (txtUserId.getText().isEmpty() || txtName.getText().isEmpty() || txtUsername.getText().isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Validation Error", "Please fill in all required fields.");
            return null;
        }
        return new UserDTO(
                txtUserId.getText(),
                txtName.getText(),
                txtContact.getText(),
                dateMembership.getValue(),
                txtUsername.getText(),
                txtPassword.getText()
        );
    }

    private void clearForm() {
        // Don't clear the auto-generated ID
        txtName.clear();
        txtContact.clear();
        dateMembership.setValue(null);
        txtUsername.clear();
        txtPassword.clear();
        txtSearch.clear();
        tblUsers.getSelectionModel().clearSelection();
    }

    private void refreshView() {
        loadAllUsers();
        clearForm();
        generateAndSetNextId();
    }

    private void filterUsers(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            tblUsers.setItems(userList);
            return;
        }
        String lowerCaseFilter = keyword.toLowerCase();
        ObservableList<UserDTO> filteredData = userList.filtered(user ->
                user.getName().toLowerCase().contains(lowerCaseFilter) ||
                        user.getUserId().toLowerCase().contains(lowerCaseFilter) ||
                        user.getUsername().toLowerCase().contains(lowerCaseFilter)
        );
        tblUsers.setItems(filteredData);
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
