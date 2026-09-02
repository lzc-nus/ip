package greenchonk.gui;

import java.io.IOException;
import java.util.Collections;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;

/**
 * Represents one message and avatar in the chat history.
 */
public class DialogBox extends HBox {
    private static final String USER_AVATAR_TEXT = "YOU";
    private static final String GREEN_CHONK_AVATAR_TEXT = "GC";

    @FXML
    private Label dialog;

    @FXML
    private Label avatar;

    private DialogBox(String text) {
        try {
            FXMLLoader loader = new FXMLLoader(DialogBox.class.getResource("/view/DialogBox.fxml"));
            loader.setController(this);
            loader.setRoot(this);
            loader.load();
        } catch (IOException exception) {
            throw new IllegalStateException("Unable to load the dialog layout.", exception);
        }
        dialog.setText(text);
    }

    /**
     * Creates a right-aligned dialog for a command entered by the user.
     *
     * @param text the command to display.
     * @return the configured user dialog.
     */
    public static DialogBox getUserDialog(String text) {
        DialogBox dialogBox = new DialogBox(text);
        dialogBox.avatar.setText(USER_AVATAR_TEXT);
        dialogBox.avatar.getStyleClass().add("user-avatar");
        dialogBox.dialog.getStyleClass().add("user-bubble");
        return dialogBox;
    }

    /**
     * Creates a left-aligned dialog for Green Chonk's response.
     *
     * @param text the response to display.
     * @return the configured Green Chonk dialog.
     */
    public static DialogBox getGreenChonkDialog(String text) {
        DialogBox dialogBox = new DialogBox(text);
        dialogBox.avatar.setText(GREEN_CHONK_AVATAR_TEXT);
        dialogBox.avatar.getStyleClass().add("green-chonk-avatar");
        dialogBox.dialog.getStyleClass().add("green-chonk-bubble");
        dialogBox.flip();
        return dialogBox;
    }

    /**
     * Creates a left-aligned dialog for an invalid command or failed operation.
     *
     * @param text the error response to display.
     * @return the configured error dialog.
     */
    public static DialogBox getErrorDialog(String text) {
        DialogBox dialogBox = getGreenChonkDialog(text);
        dialogBox.dialog.getStyleClass().add("error-bubble");
        return dialogBox;
    }

    /**
     * Places the avatar before the message and aligns the row to the left.
     */
    private void flip() {
        ObservableList<Node> children = FXCollections.observableArrayList(getChildren());
        Collections.reverse(children);
        getChildren().setAll(children);
        setAlignment(Pos.TOP_LEFT);
    }
}
