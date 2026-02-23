package sillyrat.ui;

import java.util.Objects;

import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import sillyrat.SillyRat;

/**
 * Controller for the main GUI.
 * Javadoc comments in this class were written with the assistance of AI (ChatGPT, Claude).
 */
public class MainWindow extends AnchorPane {
    @FXML
    private ScrollPane scrollPane;
    @FXML
    private VBox dialogContainer;
    @FXML
    private TextField userInput;
    @FXML
    private Button sendButton;

    private SillyRat sillyRat;

    private final Image userImage = new Image(Objects.requireNonNull(this.getClass()
            .getResourceAsStream("/images/CatBoss.png")));
    private final Image ratImage = new Image(Objects.requireNonNull(this.getClass()
            .getResourceAsStream("/images/SillyRat.png")));

    @FXML
    public void initialize() {
        scrollPane.vvalueProperty().bind(dialogContainer.heightProperty());

        scrollPane.setFitToWidth(true);

        dialogContainer.prefWidthProperty().bind(scrollPane.widthProperty().subtract(20));
    }

    /**
     * Injects the SillyRat instance and displays the startup reminder dialog.
     *
     * @param sr The SillyRat application instance.
     */
    public void setSillyRat(SillyRat sr) {
        this.sillyRat = sr;
        showStartupReminder();
    }

    /**
     * Displays the startup reminder from SillyRat as an initial dialog box.
     */
    private void showStartupReminder() {
        String reminder = sillyRat.getStartupReminder();
        dialogContainer.getChildren().add(
                DialogBox.getSillyRatDialog(reminder, ratImage)
        );
    }

    /**
     * Creates two dialog boxes.
     * One echoing user input.
     * The other containing SillyRat's reply and then appends them to the dialog container.
     * Clears the user input after processing.
     */
    @FXML
    private void handleUserInput() {
        String input = userInput.getText().trim();
        if (input.isEmpty()) {
            return;
        }

        String response = sillyRat.getResponse(input);

        DialogBox userDb = DialogBox.getUserDialog(input, userImage);
        DialogBox ratDb = DialogBox.getSillyRatDialog(response, ratImage);

        userDb.prefWidthProperty().bind(dialogContainer.widthProperty());
        ratDb.prefWidthProperty().bind(dialogContainer.widthProperty());

        dialogContainer.getChildren().addAll(userDb, ratDb);
        userInput.clear();

        if (input.equalsIgnoreCase("bye")) {
            shutdown();
        }
    }

    private void shutdown() {
        userInput.setDisable(true);
        sendButton.setDisable(true);
        PauseTransition delay = new PauseTransition(Duration.seconds(2.5));
        delay.setOnFinished(event -> Platform.exit());
        delay.play();
    }
}
