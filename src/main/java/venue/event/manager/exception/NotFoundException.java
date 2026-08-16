package venue.event.manager.exception;

/** Signals that a requested domain entity does not exist. */
public class NotFoundException extends RuntimeException {
    /**
     * Creates a not-found exception.
     * @param message description of the missing entity
     */
    public NotFoundException(String message) {
        super(message);
    }
}
