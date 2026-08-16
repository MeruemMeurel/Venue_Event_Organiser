package venue.event.manager.exception;

/** Signals a request that conflicts with the current domain state. */
public class ConflictException extends RuntimeException {
    /** Creates a conflict exception.
     * @param message explanation of the conflict
     */
    public ConflictException(String message) {
        super(message);
    }
}
