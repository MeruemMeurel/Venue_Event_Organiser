package Venue_Event_Manager.repository;

import Venue_Event_Manager.domain.model.event.EventGuest;
import Venue_Event_Manager.domain.model.event.EventGuestStatus;
import java.sql.Connection;
import java.util.List;
import java.util.Optional;

/** Persistence operations for private-event guest entries. */
public interface EventGuestRepository {

    /** Finds every guest entry.
     * @param conn active connection
     * @return all entries
     */
    List<EventGuest> findAll(Connection conn);

    /** Finds a guest entry by id.
     * @param conn active connection
     * @param eventGuestId entry id
     * @return matching entry, if any
     */
    Optional<EventGuest> findById(Connection conn, long eventGuestId);

    /**
     * Finds and locks a guest entry inside the caller's transaction.
     * @param conn active database connection
     * @param eventGuestId id of the guest entry to lock
     * @return guest entry wrapped in an Optional, or an empty Optional if it does not exist
     */
    Optional<EventGuest> findByIdForUpdate(Connection conn, long eventGuestId);

    /** Finds guest entries for an event.
     * @param conn active connection
     * @param eventId event id
     * @return matching entries
     */
    List<EventGuest> findAllByEventId(Connection conn, long eventId);

    /** Finds guest entries with a status.
     * @param conn active connection
     * @param status guest status
     * @return matching entries
     */
    List<EventGuest> findAllByStatus(Connection conn, EventGuestStatus status);

    /** Finds event guest entries with a status.
     * @param conn active connection
     * @param eventId event id
     * @param status status
     * @return matching entries
     */
    List<EventGuest> findAllByEventIdAndStatus(Connection conn, long eventId, EventGuestStatus status);

    /** Inserts a guest entry.
     * @param conn active connection
     * @param guest entry to insert
     * @return generated id
     */
    long insert(Connection conn, EventGuest guest);

    /** Updates a guest entry.
     * @param conn active connection
     * @param guest entry to update
     */
    void update(Connection conn, EventGuest guest);

    /** Updates guest status.
     * @param conn active connection
     * @param eventGuestId entry id
     * @param status new status
     */
    void updateEventGuestStatus(Connection conn, long eventGuestId, EventGuestStatus status);

    /**
     * Cancels all active guest entries for an event.
     * @param conn active database connection
     * @param eventId id of the event whose guest entries must be cancelled
     */
    void cancelActiveByEventId(Connection conn, long eventId);

    /** Deletes a guest entry.
     * @param conn active connection
     * @param eventGuestId entry id
     */
    void deleteById(Connection conn, long eventGuestId);

}
