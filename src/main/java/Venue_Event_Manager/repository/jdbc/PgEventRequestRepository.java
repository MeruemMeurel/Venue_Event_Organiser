package Venue_Event_Manager.repository.jdbc;

import Venue_Event_Manager.domain.model.request.EventRequest;
import Venue_Event_Manager.domain.model.request.EventRequestStatus;
import Venue_Event_Manager.repository.EventRequestRepository;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/** PostgreSQL repository implementation. */
public class PgEventRequestRepository implements EventRequestRepository {

    /** Creates a repository instance. */
    public PgEventRequestRepository() {}


    /**
     * Lambda function to map event_request sql results to an EventRequest object
     */
    private static final RowMapper<EventRequest> request_mapper = rs -> {
        long raw_handler_id = rs.getLong("handler_id");
        Long handler_id = rs.wasNull() ? null : raw_handler_id;
        Timestamp closed_at = rs.getTimestamp("closed_at");

        return new EventRequest(
                rs.getLong("id"),
                rs.getLong("requester_id"),
                handler_id,
                rs.getLong("venue_id"),
                rs.getString("name"),
                rs.getString("description"),
                rs.getTimestamp("begin_datetime").toLocalDateTime(),
                rs.getTimestamp("end_datetime").toLocalDateTime(),
                EventRequestStatus.valueOf(rs.getString("status").toUpperCase()),
                rs.getTimestamp("created_at").toLocalDateTime(),
                closed_at != null ? closed_at.toLocalDateTime() : null,
                rs.getBigDecimal("quote")
        );
    };


    private final static String SQL_FIND_ALL = "SELECT id, requester_id, handler_id, venue_id, name, description, " +
                                                      "begin_datetime, end_datetime, status, created_at, " +
                                                      "closed_at, quote " +
                                               "FROM event_request";
    /**
     * Executes SQL query to get all event requests
     * @param conn the connection to db
     * @return List of requests
     */
    @Override
    public List<EventRequest> findAll(Connection conn) {
        try (PreparedStatement ps = conn.prepareStatement(SQL_FIND_ALL)) {
            List<EventRequest> requests = new ArrayList<>();

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) requests.add(request_mapper.mapRow(rs));
            }
            return requests;
        } catch (SQLException e) {
            throw new DaoException("Error while trying to find all event requests", e);
        }
    }


    private final static String SQL_FIND_BY_ID = SQL_FIND_ALL + " WHERE id = ?";
    /**
     * Executes SQL query to get a request by its id
     * @param conn the db connection
     * @param eventRequestId the id to find
     * @return Optional containing the request if found
     */
    @Override
    public Optional<EventRequest> findById(Connection conn, long eventRequestId) {
        try (PreparedStatement ps = conn.prepareStatement(SQL_FIND_BY_ID)) {
            ps.setLong(1, eventRequestId);

            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return Optional.empty();
                return Optional.of(request_mapper.mapRow(rs));
            }
        } catch (SQLException e) {
            throw new DaoException("Error while trying to find event request id = " + eventRequestId, e);
        }
    }

    private static final String SQL_FIND_BY_ID_FOR_UPDATE = SQL_FIND_BY_ID + " FOR UPDATE";

    /**
     * Gets an event request and locks its row until the current transaction ends.
     * @param conn the active transaction connection
     * @param eventRequestId the id to find and lock
     * @return the locked request, or an empty optional if it does not exist
     */
    @Override
    public Optional<EventRequest> findByIdForUpdate(Connection conn, long eventRequestId) {
        try (PreparedStatement ps = conn.prepareStatement(SQL_FIND_BY_ID_FOR_UPDATE)) {
            ps.setLong(1, eventRequestId);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return Optional.empty();
                return Optional.of(request_mapper.mapRow(rs));
            }
        } catch (SQLException e) {
            throw new DaoException("Error while locking event request id = " + eventRequestId, e);
        }
    }


    private final static String SQL_FIND_BY_REQUESTER = SQL_FIND_ALL + " WHERE requester_id = ?";
    /**
     * Executes SQL query to get all requests made by a specific requester
     * @param conn the db connection
     * @param requesterId the id of the user who made the requests
     * @return List of event requests
     */
    @Override
    public List<EventRequest> findAllByRequesterId(Connection conn, long requesterId) {
        try (PreparedStatement ps = conn.prepareStatement(SQL_FIND_BY_REQUESTER)) {
            ps.setLong(1, requesterId);
            List<EventRequest> requests = new ArrayList<>();

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) requests.add(request_mapper.mapRow(rs));
            }
            return requests;
        } catch (SQLException e) {
            throw new DaoException("Error finding requests for requester id = " + requesterId, e);
        }
    }


    private final static String SQL_FIND_BY_HANDLER = SQL_FIND_ALL + " WHERE handler_id = ?";
    /**
     * Executes SQL query to get all requests handled by a specific administrator
     * @param conn the db connection
     * @param handlerId the id of the admin handling the requests
     * @return List of event requests
     */
    @Override
    public List<EventRequest> findAllByHandlerId(Connection conn, long handlerId) {
        try (PreparedStatement ps = conn.prepareStatement(SQL_FIND_BY_HANDLER)) {
            ps.setLong(1, handlerId);
            List<EventRequest> requests = new ArrayList<>();

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) requests.add(request_mapper.mapRow(rs));
            }
            return requests;
        } catch (SQLException e) {
            throw new DaoException("Error finding requests for handler id = " + handlerId, e);
        }
    }


    private final static String SQL_FIND_BY_VENUE = SQL_FIND_ALL + " WHERE venue_id = ?";
    /**
     * Executes SQL query to get all requests associated with a specific venue
     * @param conn the db connection
     * @param venueId the id of the venue
     * @return List of event requests
     */
    @Override
    public List<EventRequest> findAllByVenueId(Connection conn, long venueId) {
        try (PreparedStatement ps = conn.prepareStatement(SQL_FIND_BY_VENUE)) {
            ps.setLong(1, venueId);
            List<EventRequest> requests = new ArrayList<>();

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) requests.add(request_mapper.mapRow(rs));
            }
            return requests;
        } catch (SQLException e) {
            throw new DaoException("Error finding requests for venue id = " + venueId, e);
        }
    }


    private final static String SQL_FIND_BY_STATUS = SQL_FIND_ALL + " WHERE status = ?::request_status";
    /**
     * Executes SQL query to get all requests filtered by their current status
     * @param conn the db connection
     * @param status the status to filter by
     * @return List of event requests
     */
    @Override
    public List<EventRequest> findAllByStatus(Connection conn, EventRequestStatus status) {
        try (PreparedStatement ps = conn.prepareStatement(SQL_FIND_BY_STATUS)) {
            ps.setString(1, status.name());
            List<EventRequest> requests = new ArrayList<>();

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) requests.add(request_mapper.mapRow(rs));
            }
            return requests;
        } catch (SQLException e) {
            throw new DaoException("Error finding requests with status = " + status, e);
        }
    }


    private final static String SQL_FIND_BY_START = SQL_FIND_ALL + " WHERE begin_datetime = ?";
    /**
     * * Executes SQL query to find requests that start exactly at a specific time
     * * @param conn the db connection
     * * @param startDatetime the exact start timestamp
     * * @return List of event requests
     *
     */
    @Override
    public List<EventRequest> findAllByStartDate(Connection conn, LocalDateTime startDatetime) {
        try (PreparedStatement ps = conn.prepareStatement(SQL_FIND_BY_START)) {
            ps.setTimestamp(1, Timestamp.valueOf(startDatetime));
            List<EventRequest> requests = new ArrayList<>();

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) requests.add(request_mapper.mapRow(rs));
            }
            return requests;
        } catch (SQLException e) {
            throw new DaoException("Error finding requests starting at: " + startDatetime, e);
        }
    }


    private final static String SQL_FIND_BY_END = SQL_FIND_ALL + " WHERE end_datetime = ?";
    /**
     * Executes SQL query to find requests that end exactly at a specific time
     * @param conn the db connection
     * @param endDatetime the exact end timestamp
     * @return List of event requests
     */
    @Override
    public List<EventRequest> findAllByEndDate(Connection conn, LocalDateTime endDatetime) {
        try (PreparedStatement ps = conn.prepareStatement(SQL_FIND_BY_END)) {
            ps.setTimestamp(1, Timestamp.valueOf(endDatetime));
            List<EventRequest> requests = new ArrayList<>();

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) requests.add(request_mapper.mapRow(rs));
            }
            return requests;
        } catch (SQLException e) {
            throw new DaoException("Error finding requests ending at: " + endDatetime, e);
        }
    }


    private final static String SQL_FIND_AFTER = SQL_FIND_ALL + " WHERE begin_datetime > ?";
    /**
     * Executes SQL query to find requests whose event starts after a certain date
     * @param conn the db connection
     * @param datetime the threshold date
     * @return List of event requests
     */
    @Override
    public List<EventRequest> findAllAfter(Connection conn, LocalDateTime datetime) {
        try (PreparedStatement ps = conn.prepareStatement(SQL_FIND_AFTER)) {
            ps.setTimestamp(1, Timestamp.valueOf(datetime));
            List<EventRequest> requests = new ArrayList<>();

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) requests.add(request_mapper.mapRow(rs));
            }
            return requests;
        } catch (SQLException e) {
            throw new DaoException("Error finding requests after: " + datetime, e);
        }
    }


    private final static String SQL_FIND_BEFORE = SQL_FIND_ALL + " WHERE begin_datetime < ?";
    /**
     * Executes SQL query to find requests whose event starts before a certain date
     * @param conn the db connection
     * @param datetime the threshold date
     * @return List of event requests
     */
    @Override
    public List<EventRequest> findAllBefore(Connection conn, LocalDateTime datetime) {
        try (PreparedStatement ps = conn.prepareStatement(SQL_FIND_BEFORE)) {
            ps.setTimestamp(1, Timestamp.valueOf(datetime));
            List<EventRequest> requests = new ArrayList<>();

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) requests.add(request_mapper.mapRow(rs));
            }
            return requests;
        } catch (SQLException e) {
            throw new DaoException("Error finding requests before: " + datetime, e);
        }
    }


    private final static String SQL_FIND_BETWEEN = SQL_FIND_ALL + " WHERE begin_datetime >= ? AND end_datetime <= ?";
    /**
     * Executes SQL query to find requests within a specific time range
     * @param conn the db connection
     * @param startDatetime the beginning of the range
     * @param endDatetime the end of the range
     * @return List of event requests
     */
    @Override
    public List<EventRequest> findAllBetween(Connection conn, LocalDateTime startDatetime, LocalDateTime endDatetime) {
        try (PreparedStatement ps = conn.prepareStatement(SQL_FIND_BETWEEN)) {
            ps.setTimestamp(1, Timestamp.valueOf(startDatetime));
            ps.setTimestamp(2, Timestamp.valueOf(endDatetime));
            List<EventRequest> requests = new ArrayList<>();

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) requests.add(request_mapper.mapRow(rs));
            }
            return requests;
        } catch (SQLException e) {
            throw new DaoException("Error finding requests between dates", e);
        }
    }


    private final static String SQL_INSERT = "INSERT INTO event_request (requester_id, handler_id, venue_id, name, " +
                                                                        "description, begin_datetime, end_datetime, " +
                                                                        "status, created_at, closed_at, quote) " +
                                             "VALUES (?, ?, ?, ?, ?, ?, ?, ?::request_status, ?, ?, ?) RETURNING id";
    /**
     * Executes SQL query to insert a new event request into the database
     * @param conn the db connection
     * @param request the EventRequest object to persist
     * @return the generated unique identifier (ID)
     */
    @Override
    public long insert(Connection conn, EventRequest request) {
        try (PreparedStatement ps = conn.prepareStatement(SQL_INSERT)) {
            ps.setLong(1, request.getRequesterId());
            JdbcUtils.setNullableLong(ps, 2, request.getHandlerId());
            ps.setLong(3, request.getVenueId());
            ps.setString(4, request.getName());
            ps.setString(5, request.getDescription());
            ps.setTimestamp(6, Timestamp.valueOf(request.getBeginDatetime()));
            ps.setTimestamp(7, Timestamp.valueOf(request.getEndDatetime()));
            ps.setString(8, request.getStatus().name());
            ps.setTimestamp(9, Timestamp.valueOf(request.getCreatedAt()));
            JdbcUtils.setNullableLocalDateTime(ps, 10, request.getClosedAt());
            JdbcUtils.setNullableBigDecimal(ps, 11, request.getQuote());

            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                return rs.getLong(1);
            }
        } catch (SQLException e) {
            throw new DaoException("Error inserting event request: " + request.getName(), e);
        }
    }


    private final static String SQL_UPDATE = "UPDATE event_request " +
                                             "SET requester_id = ?, handler_id = ?, venue_id = ?, name = ?, " +
                                                 "description = ?, begin_datetime = ?, end_datetime = ?, status = ?::request_status, " +
                                                 "created_at = ?, closed_at = ?, quote = ? " +
                                             "WHERE id = ?";
    /**
     * Executes SQL query to update an existing event request
     * @param conn the db connection
     * @param request the EventRequest object with updated information
     */
    @Override
    public void update(Connection conn, EventRequest request) {
        try (PreparedStatement ps = conn.prepareStatement(SQL_UPDATE)) {
            ps.setLong(1, request.getRequesterId());
            JdbcUtils.setNullableLong(ps, 2, request.getHandlerId());
            ps.setLong(3, request.getVenueId());
            ps.setString(4, request.getName());
            ps.setString(5, request.getDescription());
            ps.setTimestamp(6, Timestamp.valueOf(request.getBeginDatetime()));
            ps.setTimestamp(7, Timestamp.valueOf(request.getEndDatetime()));
            ps.setString(8, request.getStatus().name());
            ps.setTimestamp(9, Timestamp.valueOf(request.getCreatedAt()));
            JdbcUtils.setNullableLocalDateTime(ps, 10, request.getClosedAt());
            JdbcUtils.setNullableBigDecimal(ps, 11, request.getQuote());
            ps.setLong(12, request.getId());

            int updated = ps.executeUpdate();
            JdbcUtils.requireUpdatedExactly(updated, 1, "update(request_id=" + request.getId() + ")");
        } catch (SQLException e) {
            throw new DaoException("Error updating event request: " + request.getName(), e);
        }
    }


    private final static String SQL_UPDATE_STATUS = "UPDATE event_request " +
                                                    "SET status = ?::request_status " +
                                                    "WHERE id = ?";
    /**
     * Executes SQL query to update only the status of a specific request
     * @param conn the db connection
     * @param eventRequestId the id of the request to update
     * @param status the new status to apply
     */
    @Override
    public void updateStatus(Connection conn, long eventRequestId, EventRequestStatus status) {
        try (PreparedStatement ps = conn.prepareStatement(SQL_UPDATE_STATUS)) {
            ps.setString(1, status.name());
            ps.setLong(2, eventRequestId);

            int updated = ps.executeUpdate();
            JdbcUtils.requireUpdatedExactly(updated, 1, "updateStatus(id=" + eventRequestId + ")");
        } catch (SQLException e) {
            throw new DaoException("Error updating status for request id = " + eventRequestId, e);
        }
    }


    private final static String SQL_DELETE = "DELETE FROM event_request " +
                                             "WHERE id = ?";
    /**
     * Executes SQL query to delete an event request from the database
     * @param conn the db connection
     * @param eventRequestId the id of the request to remove
     */
    @Override
    public void deleteById(Connection conn, long eventRequestId) {
        try (PreparedStatement ps = conn.prepareStatement(SQL_DELETE)) {
            ps.setLong(1, eventRequestId);

            int deleted = ps.executeUpdate();
            JdbcUtils.requireUpdatedExactly(deleted, 1, "delete(request_id=" + eventRequestId + ")");
        } catch (SQLException e) {
            throw new DaoException("Error deleting request id = " + eventRequestId, e);
        }
    }
}
