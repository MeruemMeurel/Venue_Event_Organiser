package venue.event.manager.repository.jdbc;

import venue.event.manager.domain.model.event.EventGuest;
import venue.event.manager.domain.model.event.EventGuestStatus;
import venue.event.manager.repository.EventGuestRepository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/** PostgreSQL repository implementation. */
public class PgEventGuestRepository implements EventGuestRepository {

    /** Creates a repository instance. */
    public PgEventGuestRepository() {}


    /**
     * Lambda function to map event_guest sql results to an EventGuest object
     */
    private static final RowMapper<EventGuest> guest_mapper = rs -> {
        Date birthday = rs.getDate("birthday");

        return new EventGuest(
                rs.getLong("id"),
                rs.getLong("event_id"),
                rs.getString("firstname"),
                rs.getString("lastname"),
                birthday != null ? birthday.toLocalDate() : null,
                EventGuestStatus.valueOf(rs.getString("status").toUpperCase()),
                rs.getString("note")
        );
    };


    private final static String SQL_FIND_ALL = "SELECT id, event_id, firstname, lastname, birthday, status, note " +
                                               "FROM event_guest";
    /**
     * Executes SQL query to get all event guests
     * @param conn the connection to db
     * @return List of event guests
     */
    @Override
    public List<EventGuest> findAll(Connection conn) {
        try (PreparedStatement ps = conn.prepareStatement(SQL_FIND_ALL)) {
            List<EventGuest> guests = new ArrayList<>();

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    guests.add(guest_mapper.mapRow(rs));
                }
            }
            return guests;
        } catch (SQLException e) {
            throw new DaoException("Error while trying to find all event guests", e);
        }
    }


    private final static String SQL_FIND_BY_ID = SQL_FIND_ALL + " WHERE id = ?";
    /**
     * Executes SQL query to get event guest with specific id
     * @param conn the db connection
     * @param eventGuestId the id to find
     * @return Optional object containing the EventGuest if found
     */
    @Override
    public Optional<EventGuest> findById(Connection conn, long eventGuestId) {
        try (PreparedStatement ps = conn.prepareStatement(SQL_FIND_BY_ID)) {
            ps.setLong(1, eventGuestId);

            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return Optional.empty();
                return Optional.of(guest_mapper.mapRow(rs));
            }
        } catch (SQLException e) {
            throw new DaoException("Error while trying to find event guest with id = " + eventGuestId, e);
        }
    }


    private static final String SQL_FIND_BY_ID_FOR_UPDATE = SQL_FIND_BY_ID + " FOR UPDATE";

    /**
     * Finds and locks a guest entry for the duration of the current transaction.
     * @param conn active database connection
     * @param eventGuestId id of the guest entry to find and lock
     * @return guest entry wrapped in an Optional, or an empty Optional if it does not exist
     * @throws DaoException if the query cannot be executed
     */
    @Override
    public Optional<EventGuest> findByIdForUpdate(Connection conn, long eventGuestId) {
        try (PreparedStatement ps = conn.prepareStatement(SQL_FIND_BY_ID_FOR_UPDATE)) {
            ps.setLong(1, eventGuestId);

            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return Optional.empty();
                return Optional.of(guest_mapper.mapRow(rs));
            }
        } catch (SQLException e) {
            throw new DaoException("Error while trying to lock event guest with id = " + eventGuestId, e);
        }
    }


    private final static String SQL_FIND_ALL_BY_EVENT_ID = SQL_FIND_ALL + " WHERE event_id = ?";
    /**
     * Executes SQL query to get all guests for a specific event
     * @param conn the db connection
     * @param eventId the event id
     * @return List of event guests
     */
    @Override
    public List<EventGuest> findAllByEventId(Connection conn, long eventId) {
        try (PreparedStatement ps = conn.prepareStatement(SQL_FIND_ALL_BY_EVENT_ID)) {
            ps.setLong(1, eventId);
            List<EventGuest> guests = new ArrayList<>();

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    guests.add(guest_mapper.mapRow(rs));
                }
            }
            return guests;
        } catch (SQLException e) {
            throw new DaoException("Error while trying to find guests for event id = " + eventId, e);
        }
    }


    private final static String SQL_FIND_ALL_BY_STATUS = SQL_FIND_ALL + " WHERE status = ?::guest_status";
    /**
     * Executes SQL query to get all guests with a specific status
     * @param conn the db connection
     * @param status the guest status
     * @return List of event guests
     */
    @Override
    public List<EventGuest> findAllByStatus(Connection conn, EventGuestStatus status) {
        try (PreparedStatement ps = conn.prepareStatement(SQL_FIND_ALL_BY_STATUS)) {
            ps.setString(1, status.name());
            List<EventGuest> guests = new ArrayList<>();

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    guests.add(guest_mapper.mapRow(rs));
                }
            }
            return guests;
        } catch (SQLException e) {
            throw new DaoException("Error while trying to find guests with status = " + status, e);
        }
    }


    private final static String SQL_FIND_ALL_BY_EVENT_AND_STATUS = SQL_FIND_ALL + " WHERE event_id = ? AND status = ?::guest_status";
    /**
     * Executes SQL query to get guests for an event filtered by status
     * @param conn the db connection
     * @param eventId the event id
     * @param status the guest status
     * @return List of event guests
     */
    @Override
    public List<EventGuest> findAllByEventIdAndStatus(Connection conn, long eventId, EventGuestStatus status) {
        try (PreparedStatement ps = conn.prepareStatement(SQL_FIND_ALL_BY_EVENT_AND_STATUS)) {
            ps.setLong(1, eventId);
            ps.setString(2, status.name());
            List<EventGuest> guests = new ArrayList<>();

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    guests.add(guest_mapper.mapRow(rs));
                }
            }
            return guests;
        } catch (SQLException e) {
            throw new DaoException("Error while trying to find guests for event " + eventId + " with status " + status, e);
        }
    }


    private final static String SQL_INSERT = "INSERT INTO event_guest (event_id, firstname, lastname, birthday, " +
                                                                      "status, note) " +
                                             "VALUES (?, ?, ?, ?, ?::guest_status, ?) RETURNING id";
    /**
     * Executes SQL query to insert a new event guest
     * @param conn the db connection
     * @param guest the event guest to add
     * @return the generated id
     */
    @Override
    public long insert(Connection conn, EventGuest guest) {
        try (PreparedStatement ps = conn.prepareStatement(SQL_INSERT)) {
            ps.setLong(1, guest.getEventId());
            ps.setString(2, guest.getFirstname());
            ps.setString(3, guest.getLastname());
            JdbcUtils.setNullableLocalDate(ps, 4, guest.getBirthday());
            ps.setString(5, guest.getStatus().name());
            JdbcUtils.setNullableString(ps, 6, guest.getNote());

            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                return rs.getLong(1);
            }
        } catch (SQLException e) {
            throw new DaoException("Error while trying to insert event guest: " + guest.toString(), e);
        }
    }


    private final static String SQL_UPDATE = "UPDATE event_guest " +
                                             "SET event_id = ?, firstname = ?, lastname = ?, birthday = ?, status = ?::guest_status, " +
                                                 "note = ? " +
                                             "WHERE id = ?";
    /**
     * Executes SQL query to update an existing event guest
     * @param conn the db connection
     * @param guest the updated event guest
     */
    @Override
    public void update(Connection conn, EventGuest guest) {
        try (PreparedStatement ps = conn.prepareStatement(SQL_UPDATE)) {
            ps.setLong(1, guest.getEventId());
            ps.setString(2, guest.getFirstname());
            ps.setString(3, guest.getLastname());
            JdbcUtils.setNullableLocalDate(ps, 4, guest.getBirthday());
            ps.setString(5, guest.getStatus().name());
            JdbcUtils.setNullableString(ps, 6, guest.getNote());
            ps.setLong(7, guest.getId());

            int updated = ps.executeUpdate();
            JdbcUtils.requireUpdatedExactly(updated, 1, "update(event_guest_id=" + guest.getId() + ")");
        } catch (SQLException e) {
            throw new DaoException("Error while trying to update event guest: " + guest.toString(), e);
        }
    }


    private final static String SQL_UPDATE_STATUS = "UPDATE event_guest " +
                                                    "SET status = ?::guest_status " +
                                                    "WHERE id = ?";
    /**
     * Executes SQL query to update only the status of an event guest
     * @param conn the db connection
     * @param eventGuestId the id of the guest
     * @param status the new status
     */
    @Override
    public void updateEventGuestStatus(Connection conn, long eventGuestId, EventGuestStatus status) {
        try (PreparedStatement ps = conn.prepareStatement(SQL_UPDATE_STATUS)) {
            ps.setString(1, status.name());
            ps.setLong(2, eventGuestId);

            int updated = ps.executeUpdate();
            JdbcUtils.requireUpdatedExactly(updated, 1, "updateEventGuestStatus(id=" + eventGuestId + ")");
        } catch (SQLException e) {
            throw new DaoException("Error while trying to update status for event guest id = " + eventGuestId, e);
        }
    }


    private static final String SQL_CANCEL_ACTIVE_BY_EVENT_ID = "UPDATE event_guest " +
            "SET status = 'CANCELLED' " +
            "WHERE event_id = ? AND status IN ('INVITED', 'CONFIRMED')";

    /**
     * Cancels every invited or confirmed guest associated with an event.
     * @param conn active database connection
     * @param eventId id of the cancelled event
     * @throws DaoException if the update cannot be executed
     */
    @Override
    public void cancelActiveByEventId(Connection conn, long eventId) {
        try (PreparedStatement ps = conn.prepareStatement(SQL_CANCEL_ACTIVE_BY_EVENT_ID)) {
            ps.setLong(1, eventId);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new DaoException("Error while cancelling guests for event id = " + eventId, e);
        }
    }


    private final static String SQL_DELETE = "DELETE FROM event_guest " +
                                             "WHERE id = ?";
    /**
     * Executes SQL query to delete an event guest by id
     * @param conn the db connection
     * @param eventGuestId the id to delete
     */
    @Override
    public void deleteById(Connection conn, long eventGuestId) {
        try (PreparedStatement ps = conn.prepareStatement(SQL_DELETE)) {
            ps.setLong(1, eventGuestId);

            int deleted = ps.executeUpdate();
            JdbcUtils.requireUpdatedExactly(deleted, 1, "delete(event_guest_id=" + eventGuestId + ")");
        } catch (SQLException e) {
            throw new DaoException("Error while trying to delete event guest with id = " + eventGuestId, e);
        }
    }
}
