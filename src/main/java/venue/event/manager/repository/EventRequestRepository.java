package venue.event.manager.repository;

import venue.event.manager.domain.model.request.EventRequest;
import venue.event.manager.domain.model.request.EventRequestStatus;
import java.sql.Connection;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/** Persistence operations for event requests. */
public interface EventRequestRepository {

    /** Finds every request.
     * @param conn active connection
     * @return all requests
     */
    List<EventRequest> findAll(Connection conn);

    /** Finds a request by id.
     * @param conn active connection
     * @param eventRequestId request id
     * @return matching request, if any
     */
    Optional<EventRequest> findById(Connection conn, long eventRequestId);

    /** Finds and locks a request.
     * @param conn active transaction connection
     * @param eventRequestId request id
     * @return matching locked request, if any
     */
    Optional<EventRequest> findByIdForUpdate(Connection conn, long eventRequestId);

    /** Finds requests by requester.
     * @param conn active connection
     * @param requesterId requester id
     * @return matching requests
     */
    List<EventRequest> findAllByRequesterId(Connection conn, long requesterId);

    /** Finds requests by handler.
     * @param conn active connection
     * @param handlerId handler id
     * @return matching requests
     */
    List<EventRequest> findAllByHandlerId(Connection conn, long handlerId);

    /** Finds requests by venue.
     * @param conn active connection
     * @param venueId venue id
     * @return matching requests
     */
    List<EventRequest> findAllByVenueId(Connection conn, long venueId);

    /** Finds requests by status.
     * @param conn active connection
     * @param status request status
     * @return matching requests
     */
    List<EventRequest> findAllByStatus(Connection conn, EventRequestStatus status);

    /** Finds requests starting at a date.
     * @param conn active connection
     * @param startDatetime starting time
     * @return matching requests
     */
    List<EventRequest> findAllByStartDate(Connection conn, LocalDateTime startDatetime);

    /** Finds requests ending at a date.
     * @param conn active connection
     * @param endDatetime ending time
     * @return matching requests
     */
    List<EventRequest> findAllByEndDate(Connection conn, LocalDateTime endDatetime);

    /** Finds requests after a threshold.
     * @param conn active connection
     * @param datetime threshold
     * @return matching requests
     */
    List<EventRequest> findAllAfter(Connection conn, LocalDateTime datetime);

    /** Finds requests before a threshold.
     * @param conn active connection
     * @param datetime threshold
     * @return matching requests
     */
    List<EventRequest> findAllBefore(Connection conn, LocalDateTime datetime);

    /** Finds requests in an interval.
     * @param conn active connection
     * @param startDatetime interval start
     * @param endDatetime interval end
     * @return matching requests
     */
    List<EventRequest> findAllBetween(Connection conn, LocalDateTime startDatetime, LocalDateTime endDatetime);

    /** Inserts a request.
     * @param conn active connection
     * @param request request to insert
     * @return generated id
     */
    long insert(Connection conn, EventRequest request);

    /** Updates a request.
     * @param conn active connection
     * @param request request to update
     */
    void update(Connection conn, EventRequest request);

    /** Updates request status.
     * @param conn active connection
     * @param eventRequestId request id
     * @param status new status
     */
    void updateStatus(Connection conn, long eventRequestId, EventRequestStatus status);

    /** Deletes a request.
     * @param conn active connection
     * @param eventRequestId request id
     */
    void deleteById(Connection conn, long eventRequestId);

}
