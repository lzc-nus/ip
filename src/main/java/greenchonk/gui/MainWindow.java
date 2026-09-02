package greenchonk.gui;

import greenchonk.GreenChonk;
import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

/**
 * Controls Green Chonk's main chat window.
 */
public class MainWindow extends AnchorPane {
    private static final String WELCOME_MESSAGE = "Hey! I'm Green Chonk.\n"
            + "Tell me what you need to remember, and I'll carry it for you.";
    private static final Duration EXIT_DELAY = Duration.millis(900);

    @FXML
    private ScrollPane scrollPane;

    @FXML
    private VBox dialogContainer;

    @FXML
    private TextField userInput;

    private GreenChonk greenChonk;

    /**
     * Configures behavior that is independent of the application engine.
     */
    @FXML
    public void initialize() {
        dialogContainer.heightProperty().addListener(observable -> scrollPane.setVvalue(1.0));
        Platform.runLater(userInput::requestFocus);
    }

    /**
     * Injects the application engine and displays its welcome message.
     *
     * @param greenChonk the application engine used to process commands.
     */
    public void setGreenChonk(GreenChonk greenChonk) {
        this.greenChonk = greenChonk;
        dialogContainer.getChildren().add(DialogBox.getGreenChonkDialog(WELCOME_MESSAGE));
    }

    /**
     * Sends the current command to Green Chonk and displays both sides of the exchange.
     */
    @FXML
    private void handleUserInput() {
        String input = userInput.getText().strip();
        String response = greenChonk.getResponse(input);

        if (!input.isEmpty()) {
            dialogContainer.getChildren().add(DialogBox.getUserDialog(input));
        }
        dialogContainer.getChildren().add(createResponseDialog(response));
        userInput.clear();
        userInput.requestFocus();

        if (input.equalsIgnoreCase("bye")) {
            PauseTransition exitPause = new PauseTransition(EXIT_DELAY);
            exitPause.setOnFinished(event -> Platform.exit());
            exitPause.play();
        }
    }

    /**
     * Creates a normal or error-styled response dialog based on its contents.
     *
     * @param response the response returned by the application engine.
     * @return a dialog box styled for the response type.
     */
    private static DialogBox createResponseDialog(String response) {
        if (response.startsWith("Oops!")) {
            return DialogBox.getErrorDialog(response);
        }
        return DialogBox.getGreenChonkDialog(response);
    }
}
