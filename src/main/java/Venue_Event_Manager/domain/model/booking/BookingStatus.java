package Venue_Event_Manager.domain.model.booking;

/**
 * Define possible statuses for a booking.
 */
public enum BookingStatus {
    /** The booking has been created but the payment process is not yet complete. */
    PENDING_PAYMENT,

    /** The booking is finalized and the tickets are valid for entry. */
    CONFIRMED,

    /** The booking has been invalidated by the user or the administrator. */
    CANCELLED
}