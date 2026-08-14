package Venue_Event_Manager.repository;

import Venue_Event_Manager.domain.model.event.EventGuest;
import Venue_Event_Manager.domain.model.event.EventGuestStatus;
import java.sql.Connection;
import java.util.List;
import java.util.Optional;

public interface EventGuestRepository {

    List<EventGuest> findAll(Connection conn);

    Optional<EventGuest> findById(Connection conn, long eventGuestId);

    /**
     * Finds and locks a guest entry inside the caller's transaction.
     * @param conn active database connection
     * @param eventGuestId id of the guest entry to lock
     * @return guest entry wrapped in an Optional, or an empty Optional if it does not exist
     */
    Optional<EventGuest> findByIdForUpdate(Connection conn, long eventGuestId);

    List<EventGuest> findAllByEventId(Connection conn, long eventId);

    List<EventGuest> findAllByStatus(Connection conn, EventGuestStatus status);

    List<EventGuest> findAllByEventIdAndStatus(Connection conn, long eventId, EventGuestStatus status);

    long insert(Connection conn, EventGuest guest);

    void update(Connection conn, EventGuest guest);

    void updateEventGuestStatus(Connection conn, long eventGuestId, EventGuestStatus status);

    /**
     * Cancels all active guest entries for an event.
     * @param conn active database connection
     * @param eventId id of the event whose guest entries must be cancelled
     */
    void cancelActiveByEventId(Connection conn, long eventId);

    void deleteById(Connection conn, long eventGuestId);

}
