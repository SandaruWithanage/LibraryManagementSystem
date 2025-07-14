package org.example.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.layout.StackPane;

import java.io.IOException;
import java.util.Objects;

/**
 * Controller for the main dashboard view (dashboard.fxml).
 * This class handles navigation by loading different FXML views into the central content area.
 */
public class DashboardController {

    /**
     * This StackPane is the main content area of the dashboard.
     * It is injected from the FXML file using the @FXML annotation.
     * Other views will be loaded into this pane.
     */
    @FXML
    private StackPane contentArea;

    /**
     * The initialize method is called automatically by the FXMLLoader after the
     * FXML file has been loaded and all @FXML fields have been injected.
     * We use it here to load the default view when the dashboard first appears.
     */
    @FXML
    public void initialize() {
        // Load the book management form as the default page.
        loadPage("book_form.fxml");
    }

    /**
     * Handles the click event for the "Books" navigation button.
     * @param event The action event triggered by the button click.
     */
    @FXML
    void handleBooksClick(ActionEvent event) {
        loadPage("book_form.fxml");
    }

    /**
     * Handles the click event for the "Users" navigation button.
     * @param event The action event triggered by the button click.
     */
    @FXML
    void handleUsersClick(ActionEvent event) {
        loadPage("user_form.fxml");
    }

    /**
     * Handles the click event for the "Borrow Records" navigation button.
     * @param event The action event triggered by the button click.
     */
    @FXML
    void handleBorrowClick(ActionEvent event) {
        loadPage("borrow_form.fxml");
    }

    /**
     * Handles the click event for the "Returns & Fines" navigation button.
     * @param event The action event triggered by the button click.
     */
    @FXML
    void handleReturnsClick(ActionEvent event) {
        loadPage("return_form.fxml");
    }

    /**
     * A private helper method to load a specific FXML file into the contentArea StackPane.
     * This avoids duplicating the loading logic in every button handler.
     *
     * @param fxmlFile The name of the FXML file to load (e.g., "book_form.fxml").
     */
    private void loadPage(String fxmlFile) {
        try {
            // Load the new FXML file. The path is relative to the 'resources/view/' directory.
            Parent page = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/view/" + fxmlFile)));

            // Clear any existing content from the contentArea and add the new page.
            contentArea.getChildren().clear();
            contentArea.getChildren().add(page);

        } catch (IOException e) {
            // If the FXML file fails to load, print an error to the console.
            System.err.println("Failed to load FXML file: " + fxmlFile);
            e.printStackTrace();
        }
    }
}
