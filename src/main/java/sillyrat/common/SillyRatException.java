package sillyrat.common;

/**
 * Custom exception class for SillyRat application.
 */

public class SillyRatException extends Exception {
    /**
     * Constructs a new exception with the specified detail message.
     * The message is saved for later retrieval by the getMessage() method.
     *
     * @param message The detail message describing the cause of the error.
     */
    public SillyRatException(String message) {
        super(message);
    }
}
