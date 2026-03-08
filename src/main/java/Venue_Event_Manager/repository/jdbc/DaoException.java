package Venue_Event_Manager.repository.jdbc;

/**
 * Exception thrown due to Dao Errors
 */
public class DaoException extends RuntimeException {
    public DaoException(String message,  Throwable cause) {
        super(message, cause);
    }

    public DaoException(String message) {
        super(message);
    }
}
