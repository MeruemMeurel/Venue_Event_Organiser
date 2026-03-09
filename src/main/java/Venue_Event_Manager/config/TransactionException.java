package Venue_Event_Manager.config;

public class TransactionException extends RuntimeException {
    public TransactionException(String message, Throwable cause) {
        super(message,cause);
    }
}
