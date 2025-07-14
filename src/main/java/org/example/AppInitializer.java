package org.example;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

/**
 * The main entry point for the Library Management System application.
 * This class is responsible for initializing the JavaFX toolkit and loading the initial user interface.
 */
public class AppInitializer extends Application {

    /**
     * This is the main entry point for all JavaFX applications.
     * The start method is called after the init method has returned,
     * and after the system is ready for the application to begin running.
     *
     * @param primaryStage the primary stage for this application, onto which
     * the application scene can be set.
     * @throws IOException if the FXML file cannot be loaded.
     */
    @Override
    public void start(Stage primaryStage) throws IOException {
        // 1. Load the FXML file for the login view.
        // The path is relative to the 'resources' folder.
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/view/login_form.fxml")));

        // 2. Create a new Scene, which will contain the UI loaded from the FXML file.
        Scene scene = new Scene(root);

        // 3. Set the title of the main application window (the Stage).
        primaryStage.setTitle("BookLib - Library Management System");

        // 4. Set the Scene on the Stage.
        primaryStage.setScene(scene);

        // 5. Make the window non-resizable to maintain a consistent UI layout.
        primaryStage.setResizable(false);

        // 6. Display the Stage to the user.
        primaryStage.show();
    }

    /**
     * The main method is only needed for the IDE in case it doesn't support
     * JavaFX applications that are launched directly from the Application class.
     * It does nothing but call launch(args), which in turn calls the start() method.
     *
     * @param args command line arguments.
     */
    public static void main(String[] args) {
        launch(args);
    }
}
