/**
 * Represents an invalid command or task operation specific to Green Chonk.
 */
public class GreenChonkException extends Exception {
    @java.io.Serial
    private static final long serialVersionUID = 1L;

    /**
     * Creates an exception with an explanation that can be shown to the user.
     *
     * @param message the user-facing explanation of the error
     */
    public GreenChonkException(String message) {
        super(message);
    }
}
