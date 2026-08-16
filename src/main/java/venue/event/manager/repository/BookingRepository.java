package venue.event.manager.repository;

import venue.event.manager.domain.model.booking.Booking;
import venue.event.manager.domain.model.booking.BookingStatus;

import java.sql.Connection;
import java.util.List;
import java.util.Optional;

/** Persistence operations for bookings. */
public interface BookingRepository {

    /** Finds every booking.
     * @param conn active connection
     * @return all bookings
     */
    List<Booking> findAll(Connection conn);

    /** Finds a booking by id.
     * @param conn active connection
     * @param bookingId booking id
     * @return matching booking, if any
     */
    Optional<Booking> findById(Connection conn, long bookingId);

    /**
     * Finds and locks a booking inside the caller's transaction.
     * @param conn active database connection
     * @param bookingId id of the booking to lock
     * @return booking wrapped in an Optional, or an empty Optional if it does not exist
     */
    Optional<Booking> findByIdForUpdate(Connection conn, long bookingId);

    /** Finds bookings owned by a user.
     * @param conn active connection
     * @param userId user id
     * @return matching bookings
     */
    List<Booking> findAllByUserId(Connection conn, long userId);

    /** Finds bookings for an event.
     * @param conn active connection
     * @param eventId event id
     * @return matching bookings
     */
    List<Booking> findAllByEventId(Connection conn, long eventId);

    /** Finds bookings with a status.
     * @param conn active connection
     * @param status booking status
     * @return matching bookings
     */
    List<Booking> findAllByStatus(Connection conn, BookingStatus status);

    /** Finds user bookings with a status.
     * @param conn active connection
     * @param userId user id
     * @param status status
     * @return matching bookings
     */
    List<Booking> findAllByUserIdAndStatus(Connection conn, long userId, BookingStatus status);

    /** Finds event bookings with a status.
     * @param conn active connection
     * @param eventId event id
     * @param status status
     * @return matching bookings
     */
    List<Booking> findAllByEventIdAndStatus(Connection conn, long eventId, BookingStatus status);

    /** Finds a user's bookings for an event.
     * @param conn active connection
     * @param userId user id
     * @param eventId event id
     * @return matching bookings
     */
    List<Booking> findAllByUserIdAndEventId(Connection conn, long userId, long eventId);

    /** Inserts a booking.
     * @param conn active connection
     * @param booking booking to insert
     * @return generated id
     */
    long insert(Connection conn, Booking booking);

    /** Updates a booking.
     * @param conn active connection
     * @param booking booking to update
     */
    void update(Connection conn, Booking booking);

    /** Updates booking status.
     * @param conn active connection
     * @param bookingId booking id
     * @param status new status
     */
    void updateStatus(Connection conn, long bookingId, BookingStatus status);

    /**
     * Cancels all active bookings for an event.
     * @param conn active database connection
     * @param eventId id of the event whose bookings must be cancelled
     */
    void cancelActiveByEventId(Connection conn, long eventId);

    /** Deletes a booking.
     * @param conn active connection
     * @param bookingId booking id
     */
    void delete(Connection conn, long bookingId);

}
