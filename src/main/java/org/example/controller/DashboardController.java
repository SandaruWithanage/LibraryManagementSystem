package org.example.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;

/**
 * Controller for the main dashboard view (dashboard.fxml).
 * This version includes improved error handling for loading FXML files.
 */
public class DashboardController {

    @FXML
    private StackPane contentArea;

    @FXML
    public void initialize() {
        // Load the book form as the default view when the dashboard opens
        loadPage("book_form.fxml");
    }

    @FXML
    void handleBooksClick(ActionEvent event) {
        loadPage("book_form.fxml");
    }

    @FXML
    void handleUsersClick(ActionEvent event) {
        loadPage("user_form.fxml");
    }

    @FXML
    void handleBorrowClick(ActionEvent event) {
        // You will need to create 'borrow_form.fxml' for this to work
        loadPage("borrow_form.fxml");
    }

    @FXML
    void handleReturnsClick(ActionEvent event) {
        // You will need to create 'return_form.fxml' for this to work
        loadPage("return_form.fxml");
    }

    /**
     * A private helper method to load a specific FXML file into the contentArea StackPane.
     * This version includes robust error checking.
     *
     * @param fxmlFile The name of the FXML file to load (e.g., "book_form.fxml").
     */
    private void loadPage(String fxmlFile) {
        try {
            // *** CRITICAL FIX: Check if the FXML file exists before trying to load it. ***
            URL resourceUrl = getClass().getResource("/view/" + fxmlFile);

            if (resourceUrl == null) {
                // If the file is not found, show a helpful error message.
                showErrorAlert("File Not Found", "Could not find the required FXML file: " + fxmlFile);
                return;
            }

            // Load the new FXML file.
            Parent page = FXMLLoader.load(resourceUrl);

            // Clear any existing content and add the new page.
            contentArea.getChildren().clear();
            contentArea.getChildren().add(page);

        } catch (IOException e) {
            // This will catch other loading errors (e.g., problems inside the FXML itself).
            showErrorAlert("FXML Load Error", "An error occurred while loading the view: " + fxmlFile);
            e.printStackTrace();
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
