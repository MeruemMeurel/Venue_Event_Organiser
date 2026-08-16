package venue.event.manager.exception;

/** Signals invalid input or a violated domain validation rule. */
public class ValidationException extends RuntimeException {
    /**
     * Creates a validation exception.
     * @param message description of the invalid input
     */
    public ValidationException(String message) {
        super(message);
    }
}
