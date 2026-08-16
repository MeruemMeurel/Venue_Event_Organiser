package venue.event.manager.repository;

import venue.event.manager.domain.model.event.Event;
import venue.event.manager.domain.model.event.EventStatus;
import venue.event.manager.domain.model.event.EventVisibility;
import java.sql.Connection;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/** Persistence operations for events and their lifecycle attributes. */
public interface EventRepository {

    /** Finds every event.
     * @param conn active connection
     * @return all events
     */
    List<Event> findAll(Connection conn);

    /** Finds an event by id.
     * @param conn active connection
     * @param eventId event id
     * @return matching event, if any
     */
    Optional<Event> findById(Connection conn, long eventId);

    /** Finds and locks an event.
     * @param conn active transaction connection
     * @param eventId event id
     * @return matching locked event, if any
     */
    Optional<Event> findByIdForUpdate(Connection conn, long eventId);

    /** Finds events hosted by a venue.
     * @param conn active connection
     * @param venueId venue id
     * @return matching events
     */
    List<Event> findAllByVenueId(Connection conn, long venueId);

    /** Finds events created by a user.
     * @param conn active connection
     * @param creatorId creator id
     * @return matching events
     */
    List<Event> findAllByCreatorId(Connection conn, long creatorId);

    /** Finds events assigned to an organiser.
     * @param conn active connection
     * @param organiserId organiser id
     * @return matching events
     */
    List<Event> findAllByOrganiserId(Connection conn, long organiserId);

    /** Finds events with a status.
     * @param conn active connection
     * @param status event status
     * @return matching events
     */
    List<Event> findAllByStatus(Connection conn, EventStatus status);

    /** Finds events with a visibility.
     * @param conn active connection
     * @param visibility visibility policy
     * @return matching events
     */
    List<Event> findAllVisibility(Connection conn, EventVisibility visibility);

    /** Finds events starting at a date and time.
     * @param conn active connection
     * @param startDatetime starting time
     * @return matching events
     */
    List<Event> findAllByStartDate(Connection conn, LocalDateTime startDatetime);

    /** Finds events ending at a date and time.
     * @param conn active connection
     * @param endDatetime ending time
     * @return matching events
     */
    List<Event> findAllByEndDate(Connection conn, LocalDateTime endDatetime);

    /** Finds events after a threshold.
     * @param conn active connection
     * @param datetime threshold
     * @return matching events
     */
    List<Event> findAllAfter(Connection conn, LocalDateTime datetime);

    /** Finds events before a threshold.
     * @param conn active connection
     * @param datetime threshold
     * @return matching events
     */
    List<Event> findAllBefore(Connection conn, LocalDateTime datetime);

    /** Finds events in a time interval.
     * @param conn active connection
     * @param startDatetime interval start
     * @param endDatetime interval end
     * @return matching events
     */
    List<Event> findAllBetween(Connection conn, LocalDateTime startDatetime, LocalDateTime endDatetime);

    /** Gets the average review rating.
     * @param conn active connection
     * @param eventId event id
     * @return average rating, if available
     */
    Optional<Double> getAverageReview(Connection conn, long eventId);

    /** Inserts an event.
     * @param conn active connection
     * @param event event to insert
     * @return generated id
     */
    long insert(Connection conn, Event event);

    /** Updates an event.
     * @param conn active connection
     * @param event event to update
     */
    void update(Connection conn, Event event);

    /** Updates event status.
     * @param conn active connection
     * @param eventId event id
     * @param status new status
     */
    void updateStatus(Connection conn, long eventId, EventStatus status);

    /** Updates event visibility.
     * @param conn active connection
     * @param eventId event id
     * @param visibility new visibility
     */
    void updateVisibility(Connection conn, long eventId, EventVisibility visibility);

    /** Updates status and publication time.
     * @param conn active connection
     * @param eventId event id
     * @param status new status
     * @param publishedAt publication time
     */
    void updateStatusAndPublishedAt(Connection conn, long eventId, EventStatus status, LocalDateTime publishedAt);

    /** Deletes an event.
     * @param conn active connection
     * @param eventId event id
     */
    void deleteById(Connection conn, long eventId);

}
