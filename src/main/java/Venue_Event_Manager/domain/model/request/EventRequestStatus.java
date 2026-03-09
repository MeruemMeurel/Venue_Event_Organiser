package Venue_Event_Manager.domain.model.request;

/**
 * Define possible statuses for an event request.
 */
public enum EventRequestStatus {
    /** The request has been submitted and is awaiting review. */
    PENDING,

    /** The admin has reviewed and approved the request. */
    ACCEPTED,

    /** The admin has reviewed and turned down the request. */
    REJECTED,

    /** The requester has withdrawn the request. */
    CANCELLED
}