package venue.event.manager.domain.model.event;

/**
 * Defines the access level and discoverability of an Event.
 */
public enum EventVisibility {
    /** Visible to all users in the application. */
    PUBLIC,

    /** Restricted to users on a specific guest list. */
    PRIVATE_GUEST_LIST
}
