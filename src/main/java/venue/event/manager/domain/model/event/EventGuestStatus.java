package venue.event.manager.domain.model.event;

/**
 * Define possible statuses for an event guest.
 */
public enum EventGuestStatus {
    /** Guest has been invited but has not yet accepted or paid. */
    INVITED,

    /** Guest is officially attending the event. */
    CONFIRMED,

    /** Guest invitation or attendance has been revoked. */
    CANCELLED
}
