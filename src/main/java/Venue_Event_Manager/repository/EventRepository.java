package Venue_Event_Manager.repository;

import Venue_Event_Manager.domain.model.event.Event;
import Venue_Event_Manager.domain.model.event.EventStatus;
import Venue_Event_Manager.domain.model.event.EventVisibility;
import java.sql.Connection;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface EventRepository {

    List<Event> findAll(Connection conn);

    Optional<Event> findById(Connection conn, long eventId);

    Optional<Event> findByIdForUpdate(Connection conn, long eventId);

    List<Event> findAllByVenueId(Connection conn, long venueId);

    List<Event> findAllByCreatorId(Connection conn, long creatorId);

    List<Event> findAllByOrganiserId(Connection conn, long organiserId);

    List<Event> findAllByStatus(Connection conn, EventStatus status);

    List<Event> findAllVisibility(Connection conn, EventVisibility visibility);

    List<Event> findAllByStartDate(Connection conn, LocalDateTime startDatetime);

    List<Event> findAllByEndDate(Connection conn, LocalDateTime endDatetime);

    List<Event> findAllAfter(Connection conn, LocalDateTime datetime);

    List<Event> findAllBefore(Connection conn, LocalDateTime datetime);

    List<Event> findAllBetween(Connection conn, LocalDateTime startDatetime, LocalDateTime endDatetime);

    Optional<Double> getAverageReview(Connection conn, long eventId);

    long insert(Connection conn, Event event);

    void update(Connection conn, Event event);

    void updateStatus(Connection conn, long eventId, EventStatus status);

    void updateVisibility(Connection conn, long eventId, EventVisibility visibility);

    void updateStatusAndPublishedAt(Connection conn, long eventId, EventStatus status, LocalDateTime publishedAt);

    void deleteById(Connection conn, long eventId);

}