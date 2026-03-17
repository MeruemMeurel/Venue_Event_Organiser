package Venue_Event_Manager.repository.jdbc;

import Venue_Event_Manager.domain.model.event.Event;
import Venue_Event_Manager.domain.model.event.EventStatus;
import Venue_Event_Manager.domain.model.event.EventVisibility;
import Venue_Event_Manager.repository.EventRepository;
import Venue_Event_Manager.repository.ReviewRepository;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class PgEventRepository implements EventRepository {

    /**
     * Lambda function to map event sql results to an Event object
     */
    private static final RowMapper<Event> event_mapper = rs -> {
        Long organiser_id = rs.getLong("organiser_id");
        Timestamp published_at = rs.getTimestamp("published_at");

        return new Event(
                rs.getLong("id"),
                rs.getLong("venue_id"),
                rs.getLong("creator_id"),
                organiser_id,
                rs.getString("name"),
                rs.getString("description"),
                rs.getTimestamp("begin_datetime").toLocalDateTime(),
                rs.getTimestamp("end_datetime").toLocalDateTime(),
                rs.getString("poster_filepath"),
                rs.getInt("capacity"),
                EventStatus.valueOf(rs.getString("status").toUpperCase()),
                EventVisibility.valueOf(rs.getString("visibility").toUpperCase()),
                rs.getBigDecimal("ticket_price"),
                published_at != null ? published_at.toLocalDateTime() : null
        );
    };


    private final static String SQL_FIND_ALL = "SELECT id, venue_id, creator_id, organiser_id, name, description, " +
                                                      "begin_datetime, end_datetime, poster_filepath, capacity, status, " +
                                                      "visibility, ticket_price, published_at " +
                                               "FROM event";
    /**
     * Executes SQL query to get all events
     * @param conn the connection to db
     * @return List of events
     */
    @Override
    public List<Event> findAll(Connection conn) {
        try (PreparedStatement ps = conn.prepareStatement(SQL_FIND_ALL)) {
            List<Event> events = new ArrayList<>();

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    events.add(event_mapper.mapRow(rs));
                }
            }
            return events;
        } catch (SQLException e) {
            throw new DaoException("Error while trying to find all events", e);
        }
    }


    private final static String SQL_FIND_BY_ID = SQL_FIND_ALL + " WHERE id = ?";
    /**
     * Executes SQL query to get event with specific id
     * @param conn the db connection
     * @param eventId the id to find
     * @return Optional object containing the Event if found
     */
    @Override
    public Optional<Event> findById(Connection conn, long eventId) {
        try (PreparedStatement ps = conn.prepareStatement(SQL_FIND_BY_ID)) {
            ps.setLong(1, eventId);

            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return Optional.empty();
                return Optional.of(event_mapper.mapRow(rs));
            }
        } catch (SQLException e) {
            throw new DaoException("Error while trying to find event with id = " + eventId, e);
        }
    }


    private final static String SQL_FIND_ALL_BY_VENUE = SQL_FIND_ALL + " WHERE venue_id = ?";
    /**
     * Executes SQL query to get all events in a specific venue
     * @param conn the db connection
     * @param venueId the admin creator id
     * @return List of events
     */
    @Override
    public List<Event> findAllByVenueId(Connection conn, long venueId) {
        try (PreparedStatement ps = conn.prepareStatement(SQL_FIND_ALL_BY_VENUE)) {
            ps.setLong(1, venueId);
            List<Event> events = new ArrayList<>();

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    events.add(event_mapper.mapRow(rs));
                }
            }
            return events;
        } catch (SQLException e) {
            throw new DaoException("Error while trying to find events for venue id = " + venueId, e);
        }
    }


    private final static String SQL_FIND_ALL_BY_CREATOR = SQL_FIND_ALL + " WHERE creator_id = ?";
    /**
     * Executes SQL query to get all events created by a specific admin
     * @param conn the db connection
     * @param creatorId the admin creator id
     * @return List of events
     */
    @Override
    public List<Event> findAllByCreatorId(Connection conn, long creatorId) {
        try (PreparedStatement ps = conn.prepareStatement(SQL_FIND_ALL_BY_CREATOR)) {
            ps.setLong(1, creatorId);
            List<Event> events = new ArrayList<>();

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    events.add(event_mapper.mapRow(rs));
                }
            }
            return events;
        } catch (SQLException e) {
            throw new DaoException("Error while trying to find events for creator id = " + creatorId, e);
        }
    }


    private final static String SQL_ALL_FIND_BY_ORGANISER = SQL_FIND_ALL + " WHERE organiser_id = ?";
    /**
     * Executes SQL query to get all events managed by a specific organiser
     * @param conn the db connection
     * @param organiserId the organiser id
     * @return List of events
     */
    @Override
    public List<Event> findAllByOrganiserId(Connection conn, long organiserId) {
        try (PreparedStatement ps = conn.prepareStatement(SQL_ALL_FIND_BY_ORGANISER)) {
            ps.setLong(1, organiserId);
            List<Event> events = new ArrayList<>();

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    events.add(event_mapper.mapRow(rs));
                }
            }
            return events;
        } catch (SQLException e) {
            throw new DaoException("Error while trying to find events for organiser id = " + organiserId, e);
        }
    }


    private final static String SQL_FIND_ALL_BY_STATUS = SQL_FIND_ALL + " WHERE status = ?";
    /**
     * Executes SQL query to get all events with a specific status
     * @param conn the db connection
     * @param status the event status
     * @return List of events
     */
    @Override
    public List<Event> findAllByStatus(Connection conn, EventStatus status) {
        try (PreparedStatement ps = conn.prepareStatement(SQL_FIND_ALL_BY_STATUS)) {
            ps.setString(1, status.name());
            List<Event> events = new ArrayList<>();

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    events.add(event_mapper.mapRow(rs));
                }
            }
            return events;
        } catch (SQLException e) {
            throw new DaoException("Error while trying to find events with status = " + status, e);
        }
    }


    private final static String SQL_FIND_ALL_BY_VISIBILITY = SQL_FIND_ALL + " WHERE visibility = ?";
    /**
     * Executes SQL query to get all events with a specific visibility
     * @param conn the db connection
     * @param visibility the visibility level
     * @return List of events
     */
    @Override
    public List<Event> findAllVisibility(Connection conn, EventVisibility visibility) {
        try (PreparedStatement ps = conn.prepareStatement(SQL_FIND_ALL_BY_VISIBILITY)) {
            ps.setString(1, visibility.name());
            List<Event> events = new ArrayList<>();

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    events.add(event_mapper.mapRow(rs));
                }
            }
            return events;
        } catch (SQLException e) {
            throw new DaoException("Error while trying to find events with visibility = " + visibility, e);
        }
    }


    private final static String SQL_FIND_ALL_BY_START = SQL_FIND_ALL + " WHERE begin_datetime = ?";
    /**
     * Executes SQL query to find events starting exactly at a specific time
     * @param conn the db connection
     * @param startDateTime the exact start time
     * @return List of events
     */
    @Override
    public List<Event> findAllByStartDate(Connection conn, LocalDateTime startDateTime) {
        try (PreparedStatement ps = conn.prepareStatement(SQL_FIND_ALL_BY_START)) {
            ps.setTimestamp(1, Timestamp.valueOf(startDateTime));
            List<Event> events = new ArrayList<>();

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    events.add(event_mapper.mapRow(rs));
                }
            }
            return events;
        } catch (SQLException e) {
            throw new DaoException("Error finding events starting at: " + startDateTime, e);
        }
    }


    private final static String SQL_FIND_ALL_BY_END = SQL_FIND_ALL + " WHERE end_datetime = ?";
    /**
     * Executes SQL query to find events ending exactly at a specific time
     * @param conn the db connection
     * @param endDateTime the exact end time
     * @return List of events
     */
    @Override
    public List<Event> findAllByEndDate(Connection conn, LocalDateTime endDateTime) {
        try (PreparedStatement ps = conn.prepareStatement(SQL_FIND_ALL_BY_END)) {
            ps.setTimestamp(1, Timestamp.valueOf(endDateTime));
            List<Event> events = new ArrayList<>();

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    events.add(event_mapper.mapRow(rs));
                }
            }
            return events;
        } catch (SQLException e) {
            throw new DaoException("Error finding events ending at: " + endDateTime, e);
        }
    }


    private final static String SQL_FIND_ALL_AFTER = SQL_FIND_ALL + " WHERE begin_datetime > ?";
    /**
     * Executes SQL query to find events that start after a certain date
     * @param conn the db connection
     * @param dateTime the threshold date
     * @return List of events
     */
    @Override
    public List<Event> findAllAfter(Connection conn, LocalDateTime dateTime) {
        try (PreparedStatement ps = conn.prepareStatement(SQL_FIND_ALL_AFTER)) {
            ps.setTimestamp(1, Timestamp.valueOf(dateTime));
            List<Event> events = new ArrayList<>();

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    events.add(event_mapper.mapRow(rs));
                }
            }
            return events;
        } catch (SQLException e) {
            throw new DaoException("Error finding events after: " + dateTime, e);
        }
    }


    private final static String SQL_FIND_ALL_BEFORE = SQL_FIND_ALL + " WHERE begin_datetime < ?";
    /**
     * Executes SQL query to find events that start before a certain date
     * @param conn the db connection
     * @param dateTime the threshold date
     * @return List of events
     */
    @Override
    public List<Event> findAllBefore(Connection conn, LocalDateTime dateTime) {
        try (PreparedStatement ps = conn.prepareStatement(SQL_FIND_ALL_BEFORE)) {
            ps.setTimestamp(1, Timestamp.valueOf(dateTime));
            List<Event> events = new ArrayList<>();

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    events.add(event_mapper.mapRow(rs));
                }
            }
            return events;
        } catch (SQLException e) {
            throw new DaoException("Error finding events before: " + dateTime, e);
        }
    }


    private final static String SQL_FIND_ALL_BETWEEN = SQL_FIND_ALL + " WHERE begin_datetime >= ? AND end_datetime <= ?";
    /**
     * Executes SQL query to find events within a specific time range
     * @param conn the db connection
     * @param startDateTime range start
     * @param endDateTime range end
     * @return List of events
     */
    @Override
    public List<Event> findAllBetween(Connection conn, LocalDateTime startDateTime, LocalDateTime endDateTime) {
        try (PreparedStatement ps = conn.prepareStatement(SQL_FIND_ALL_BETWEEN)) {
            ps.setTimestamp(1, Timestamp.valueOf(startDateTime));
            ps.setTimestamp(2, Timestamp.valueOf(endDateTime));
            List<Event> events = new ArrayList<>();

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    events.add(event_mapper.mapRow(rs));
                }
            }
            return events;
        } catch (SQLException e) {
            throw new DaoException("Error while trying to find events between dates", e);
        }
    }


    /**
     * Calculates the average review score for a specific event
     * @param conn the database connection
     * @param eventId the id of the event
     * @return Optional<Double> with the average score
     */
    @Override
    public Optional<Double> getAverageReview(Connection conn, long eventId) {
        ReviewRepository reviewRepository = new PgReviewRepository();
        double average = reviewRepository.getAverageRatingByEvent(conn, eventId);
        return average != 0.0 ? Optional.of(average) : Optional.empty();
    }


    private final static String SQL_INSERT = "INSERT INTO event (venue_id, creator_id, organiser_id, name, " +
                                                                "description, begin_datetime, end_datetime, " +
                                                                "poster_filepath, capacity, status, visibility, " +
                                                                "ticket_price, published_at) " +
                                             "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) RETURNING id";
    /**
     * Executes SQL query to insert a new event using JdbcUtils for nullable fields
     * @param conn the db connection
     * @param event the event to add
     * @return the generated id
     */
    @Override
    public long insert(Connection conn, Event event) {
        try (PreparedStatement ps = conn.prepareStatement(SQL_INSERT)) {
            ps.setLong(1, event.getVenueId());
            ps.setLong(2, event.getCreatorId());
            JdbcUtils.setNullableLong(ps, 3, event.getOrganiserId());
            ps.setString(4, event.getName());
            ps.setString(5, event.getDescription());
            ps.setTimestamp(6, Timestamp.valueOf(event.getBeginDatetime()));
            ps.setTimestamp(7, Timestamp.valueOf(event.getEndDatetime()));
            JdbcUtils.setNullableString(ps, 8, event.getPosterFilepath());
            ps.setInt(9, event.getCapacity());
            ps.setString(10, event.getStatus().name());
            ps.setString(11, event.getVisibility().name());
            JdbcUtils.setNullableBigDecimal(ps, 12, event.getTicketPrice());
            JdbcUtils.setNullableLocalDateTime(ps, 13, event.getPublishedAt());

            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                return rs.getLong(1);
            }
        } catch (SQLException e) {
            throw new DaoException("Error while trying to insert event: " + event.getName(), e);
        }
    }


    private final static String SQL_UPDATE = "UPDATE event " +
                                             "SET venue_id = ?, creator_id = ?, organiser_id = ?, name = ?, description = ?, " +
                                                 "begin_datetime = ?, end_datetime = ?, poster_filepath = ?, capacity = ?, " +
                                                 "status = ?, visibility = ?, ticket_price = ?, published_at = ? " +
                                             "WHERE id = ?";
    /**
     * Executes SQL query to update an existing event
     * @param conn the db connection
     * @param event the updated event
     */
    @Override
    public void update(Connection conn, Event event) {
        try (PreparedStatement ps = conn.prepareStatement(SQL_UPDATE)) {
            ps.setLong(1, event.getVenueId());
            ps.setLong(2, event.getCreatorId());
            JdbcUtils.setNullableLong(ps, 3, event.getOrganiserId());
            ps.setString(4, event.getName());
            ps.setString(5, event.getDescription());
            ps.setTimestamp(6, Timestamp.valueOf(event.getBeginDatetime()));
            ps.setTimestamp(7, Timestamp.valueOf(event.getEndDatetime()));
            JdbcUtils.setNullableString(ps, 8, event.getPosterFilepath());
            ps.setInt(9, event.getCapacity());
            ps.setString(10, event.getStatus().name());
            ps.setString(11, event.getVisibility().name());
            JdbcUtils.setNullableBigDecimal(ps, 12, event.getTicketPrice());
            JdbcUtils.setNullableLocalDateTime(ps, 13, event.getPublishedAt());
            ps.setLong(14, event.getId());

            int updated = ps.executeUpdate();
            JdbcUtils.requireUpdatedExactly(updated, 1, "update(event_id=" + event.getId() + ")");
        } catch (SQLException e) {
            throw new DaoException("Error while trying to update event: " + event.getName(), e);
        }
    }


    private final static String SQL_UPDATE_STATUS = "UPDATE event " +
                                                    "SET status = ? " +
                                                    "WHERE id = ?";
    /**
     * Executes SQL query to update only the status of an event
     * @param conn the db connection
     * @param eventId the id of the event
     * @param status the new status
     */
    @Override
    public void updateStatus(Connection conn, long eventId, EventStatus status) {
        try (PreparedStatement ps = conn.prepareStatement(SQL_UPDATE_STATUS)) {
            ps.setString(1, status.name());
            ps.setLong(2, eventId);

            int updated = ps.executeUpdate();
            JdbcUtils.requireUpdatedExactly(updated, 1, "updateStatus(event_id=" + eventId + ")");
        } catch (SQLException e) {
            throw new DaoException("Error while trying to update status for event id = " + eventId, e);
        }
    }


    private final static String SQL_UPDATE_VISIBILITY = "UPDATE event " +
                                                        "SET visibility = ? " +
                                                        "WHERE id = ?";
    /**
     * Updates only the visibility level of an event
     * @param conn the db connection
     * @param eventId the id of the event
     * @param visibility the new visibility level
     */
    @Override
    public void updateVisibility(Connection conn, long eventId, EventVisibility visibility) {
        try (PreparedStatement ps = conn.prepareStatement(SQL_UPDATE_VISIBILITY)) {
            ps.setString(1, visibility.name());
            ps.setLong(2, eventId);

            int updated = ps.executeUpdate();
            JdbcUtils.requireUpdatedExactly(updated, 1, "updateVisibility(event_id=" + eventId + ")");
        } catch (SQLException e) {
            throw new DaoException("Error while trying to update visibility for event id = " + eventId, e);
        }
    }


    private final static String SQL_DELETE = "DELETE FROM event " +
                                             "WHERE id = ?";
    /**
     * Deletes an event by id
     * @param conn the db connection
     * @param eventId the id to delete
     */
    @Override
    public void deleteById(Connection conn, long eventId) {
        try (PreparedStatement ps = conn.prepareStatement(SQL_DELETE)) {
            ps.setLong(1, eventId);

            int deleted = ps.executeUpdate();
            JdbcUtils.requireUpdatedExactly(deleted, 1, "delete(event_id=" + eventId + ")");
        } catch (SQLException e) {
            throw new DaoException("Error while trying to delete event with id = " + eventId, e);
        }
    }
}