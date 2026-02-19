package sillyrat.ui;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import sillyrat.SillyRat;

/**
 * Controller for the main GUI.
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

    private Image userImage = new Image(this.getClass().getResourceAsStream("/images/CatBoss.png"));
    private Image ratImage = new Image(this.getClass().getResourceAsStream("/images/SillyRat.png"));

    @FXML
    public void initialize() {
        scrollPane.vvalueProperty().bind(dialogContainer.heightProperty());
    }

    /** Injects the SillyRat instance */
    public void setSillyRat(SillyRat sr) {
        this.sillyRat = sr;
    }

    /**
     * Creates two dialog boxes.
     * One echoing user input.
     * The other containing SillyRat's reply and then appends them to the dialog container.
     * Clears the user input after processing.
     */
    @FXML
    private void handleUserInput() {
        String input = userInput.getText();
        String response = sillyRat.getResponse(input);
        dialogContainer.getChildren().addAll(
                DialogBox.getUserDialog(input, userImage),
                DialogBox.getSillyRatDialog(response, ratImage)
        );
        userInput.clear();
    }
}
