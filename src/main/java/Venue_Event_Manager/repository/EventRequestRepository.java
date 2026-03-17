package Venue_Event_Manager.repository;

import Venue_Event_Manager.domain.model.request.EventRequest;
import Venue_Event_Manager.domain.model.request.EventRequestStatus;
import java.sql.Connection;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface EventRequestRepository {

    List<EventRequest> findAll(Connection conn);

    Optional<EventRequest> findById(Connection conn, long eventRequestId);

    List<EventRequest> findAllByRequesterId(Connection conn, long requesterId);

    List<EventRequest> findAllByHandlerId(Connection conn, long handlerId);

    List<EventRequest> findAllByVenueId(Connection conn, long venueId);

    List<EventRequest> findAllByStatus(Connection conn, EventRequestStatus status);

    List<EventRequest> findAllByStartDate(Connection conn, LocalDateTime startDatetime);

    List<EventRequest> findAllByEndDate(Connection conn, LocalDateTime endDatetime);

    List<EventRequest> findAllAfter(Connection conn, LocalDateTime datetime);

    List<EventRequest> findAllBefore(Connection conn, LocalDateTime datetime);

    List<EventRequest> findAllBetween(Connection conn, LocalDateTime startDatetime, LocalDateTime endDatetime);

    long insert(Connection conn, EventRequest request);

    void update(Connection conn, EventRequest request);

    void updateStatus(Connection conn, long eventRequestId, EventRequestStatus status);

    void deleteById(Connection conn, long eventRequestId);

}