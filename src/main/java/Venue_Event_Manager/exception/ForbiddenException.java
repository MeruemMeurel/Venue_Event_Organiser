package Venue_Event_Manager.exception;

/** Signals an operation that the current actor is not allowed to perform. */
public class ForbiddenException extends RuntimeException {
    /**
     * Creates a forbidden-operation exception.
     * @param message explanation of the authorization failure
     */
    public ForbiddenException(String message) {
        super(message);
    }
}
