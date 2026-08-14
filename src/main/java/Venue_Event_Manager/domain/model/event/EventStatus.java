package Venue_Event_Manager.domain.model.event;

/**
 * Defines the lifecycle states of an Event.
 */
public enum EventStatus {
    /** Event is still being prepared and is not ready for publication. */
    DRAFT,

    /** Event is approved and scheduled. */
    CONFIRMED,

    /** Event is visible to the public or guests. */
    PUBLISHED,

    /** Event has been called off. */
    CANCELLED
}
