package venue.event.manager.domain.model.user;

/**
 * Defines the possible states of a User account within the system.
 */
public enum AccountStatus {
    /** The account is fully functional and the user can log in. */
    ACTIVE ,

    /** The account is temporarily or permanently disabled. */
    BANNED
}
