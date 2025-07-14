package org.example.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.example.dao.UserDAO;
import org.example.dao.impl.UserDAOImpl;
import org.example.entity.User;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Objects;

/**
 * Controller for the login_form.fxml view.
 * This version uses a DAO to authenticate users against a database.
 */
public class LoginFormController {

    @FXML
    private TextField txtUsername;

    @FXML
    private PasswordField txtPassword;

    // Instantiate the DAO to be used for database operations.
    private final UserDAO userDAO = new UserDAOImpl();

    @FXML
    private void btnLoginOnAction(ActionEvent event) {
        String username = txtUsername.getText();
        String password = txtPassword.getText();

        // Validate that input fields are not empty.
        if (username.isEmpty() || password.isEmpty()) {
            showErrorAlert("Login Error", "Username and password cannot be empty.");
            return;
        }

        try {
            // Use the DAO to find the user.
            User user = userDAO.findByUsernameAndPassword(username, password);

            // If the user object is not null, the credentials are valid.
            if (user != null) {
                navigateToDashboard();
            } else {
                showErrorAlert("Login Failed", "Invalid username or password.");
            }
        } catch (SQLException e) {
            // Handle potential database connection errors.
            e.printStackTrace();
            showErrorAlert("Database Error", "Could not connect to the database. Please check your connection.");
        }
    }

    private void navigateToDashboard() {
        try {
            Parent dashboardRoot = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/view/dashboard.fxml")));
            Stage currentStage = (Stage) txtUsername.getScene().getWindow();
            currentStage.setScene(new Scene(dashboardRoot));
            currentStage.setTitle("BookLib - Dashboard");
            currentStage.centerOnScreen();
        } catch (IOException e) {
            e.printStackTrace();
            showErrorAlert("Navigation Error", "Could not load the dashboard view.");
        }
    }

    private void showErrorAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
