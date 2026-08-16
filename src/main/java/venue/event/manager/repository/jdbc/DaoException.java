package venue.event.manager.repository.jdbc;

/**
 * Exception thrown due to Dao Errors
 */
public class DaoException extends RuntimeException {
    /** Creates a data-access exception with its cause.
     * @param message operation description
     * @param cause underlying error
     */
    public DaoException(String message,  Throwable cause) {
        super(message, cause);
    }

    /** Creates a data-access exception.
     * @param message operation description
     */
    public DaoException(String message) {
        super(message);
    }
}
