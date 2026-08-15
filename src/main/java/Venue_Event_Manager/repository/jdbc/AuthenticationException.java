package Venue_Event_Manager.repository.jdbc;

/** Indicates that supplied credentials could not be authenticated. */
public class AuthenticationException extends RuntimeException {
    /**
     * Creates an authentication exception.
     *
     * @param message description of the authentication failure
     */
    public AuthenticationException(String message) {
        super(message);
    }
}
