package Venue_Event_Manager.config;

/** Signals a failure while opening, executing or cleaning up a transaction. */
public class TransactionException extends RuntimeException {
    /**
     * Creates a transaction exception.
     * @param message description of the failed transaction operation
     * @param cause underlying database error
     */
    public TransactionException(String message, Throwable cause) {
        super(message,cause);
    }
}
