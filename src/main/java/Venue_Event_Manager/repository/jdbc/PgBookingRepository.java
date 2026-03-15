package Venue_Event_Manager.repository.jdbc;

import Venue_Event_Manager.domain.model.booking.Booking;
import Venue_Event_Manager.domain.model.booking.BookingStatus;
import Venue_Event_Manager.repository.BookingRepository;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class PgBookingRepository implements BookingRepository {

    /**
     * Lambda function to map booking sql results to a Booking object
     */
    public static final RowMapper<Booking> booking_mapper = rs -> new Booking(
            rs.getLong("id"),
            rs.getLong("user_id"),
            rs.getLong("event_id"),
            rs.getTimestamp("created_at").toLocalDateTime(),
            BookingStatus.valueOf(rs.getString("status").toUpperCase()),
            rs.getBigDecimal("total_price")
    );


    public static final String SQL_FIND_ALL = "SELECT id, user_id, event_id, created_at, status, total_price FROM booking";
    /**
     * Executes SQL query to get all bookings
     * @param conn the connection to db
     * @return List of bookings
     */
    @Override
    public List<Booking> findAll(Connection conn) {
        List<Booking> bookings = new ArrayList<>();
        try(PreparedStatement ps = conn.prepareStatement(SQL_FIND_ALL)){

            try (ResultSet rs = ps.executeQuery()){
                while(rs.next()){
                    bookings.add(booking_mapper.mapRow(rs));
                }
            }
            return bookings;
        }catch (SQLException e){
            throw new DaoException("Error while trying to find all bookings", e);
        }
    }


    public static final String SQL_FIND_ALL_BY_ID = SQL_FIND_ALL + " WHERE id = ?";
    /**
     * Executes SQL query to get booking with specific id
     * @param conn the db connection
     * @param bookingId the id to find
     * @return Optional object that contains the Booking if found
     */
    @Override
    public Optional<Booking> findById(Connection conn, long bookingId) {
        try(PreparedStatement ps = conn.prepareStatement(SQL_FIND_ALL_BY_ID)){
            ps.setLong(1, bookingId);

            try (ResultSet rs = ps.executeQuery()){
                if(!rs.next()) return Optional.empty();
                return Optional.of(booking_mapper.mapRow(rs));
            }
        }catch (SQLException e){
            throw new DaoException("Error while trying to find booking with id = " + bookingId, e);
        }
    }


    public static final String SQL_FIND_BY_USER_ID = SQL_FIND_ALL + " WHERE user_id = ?";
    /**
     * Executes SQL query to get all bookings made by specific User
     * @param conn the db connection
     * @param userId the id of the User
     * @return List of bookings
     */
    @Override
    public List<Booking> findAllByUserId(Connection conn, long userId) {
        List<Booking> bookings = new ArrayList<>();
        try(PreparedStatement ps = conn.prepareStatement(SQL_FIND_BY_USER_ID)){
            ps.setLong(1, userId);

            try (ResultSet rs = ps.executeQuery()){
                while(rs.next()){
                    bookings.add(booking_mapper.mapRow(rs));
                }
                return bookings;
            }
        }catch (SQLException e){
            throw new DaoException("Error while trying to find booking with user id = " + userId, e);
        }
    }


    public static final String SQL_FIND_BY_EVENT_ID = SQL_FIND_ALL + " WHERE event_id = ?";
    /**
     * Executes SQL query to get all bookings for an event
     * @param conn the db connection
     * @param eventId the id of the event
     * @return List of bookings
     */
    @Override
    public List<Booking> findAllByEventId(Connection conn, long eventId) {
        List<Booking> bookings = new ArrayList<>();
        try(PreparedStatement ps = conn.prepareStatement(SQL_FIND_BY_EVENT_ID)){
            ps.setLong(1, eventId);

            try (ResultSet rs = ps.executeQuery()){
                while(rs.next()){
                    bookings.add(booking_mapper.mapRow(rs));
                }
                return bookings;
            }
        }catch (SQLException e){
            throw new DaoException("Error while trying to find bookings with eventId = " + eventId, e);
        }
    }


    public static final String SQL_FIND_BY_STATUS = SQL_FIND_ALL + " WHERE status = ?";
    /**
     * Executes SQL query to get all bookings with specific status
     * @param conn the db connection
     * @param status the status to look for
     * @return List of bookings
     */
    @Override
    public List<Booking> findAllByStatus(Connection conn, BookingStatus status) {
        List<Booking> bookings = new ArrayList<>();
        try(PreparedStatement ps = conn.prepareStatement(SQL_FIND_BY_STATUS)){
            ps.setString(1, status.name());

            try (ResultSet rs = ps.executeQuery()){
                while(rs.next()){
                    bookings.add(booking_mapper.mapRow(rs));
                }
                return bookings;
            }
        }catch (SQLException e){
            throw new DaoException("Error while trying to find bookings with status = " + status.name(), e);
        }
    }


    public static final String SQL_FIND_BY_USER_AND_STATUS = SQL_FIND_ALL + " WHERE user_id = ? AND status = ?";
    /**
     * Executes SQL query to get all bookings with specific status from a user
     * @param conn the db connection
     * @param userId the user id
     * @param status the status
     * @return List of bookings
     */
    @Override
    public List<Booking> findAllByUserIdAndStatus(Connection conn, long userId, BookingStatus status) {
        List<Booking> bookings = new ArrayList<>();
        try(PreparedStatement ps = conn.prepareStatement(SQL_FIND_BY_USER_AND_STATUS)){
            ps.setLong(1, userId);
            ps.setString(2, status.name());

            try (ResultSet rs = ps.executeQuery()){
                while(rs.next()){
                    bookings.add(booking_mapper.mapRow(rs));
                }
                return bookings;
            }
        }catch (SQLException e){
            throw new DaoException("Error while trying to find booking with userId = " + userId + ", status = " + status.name(), e);
        }
    }


    public static final String SQL_FIND_BY_EVENT_AND_STATUS = SQL_FIND_ALL + " WHERE event_id = ? AND status = ?";
    /**
     * Executes SQL query to get all bookings with specific status for an event
     * @param conn the db connection
     * @param eventId the event id
     * @param status the status
     * @return List of bookings
     */
    @Override
    public List<Booking> findAllByEventIdAndStatus(Connection conn, long eventId, BookingStatus status) {
        List<Booking> bookings = new ArrayList<>();
        try(PreparedStatement ps = conn.prepareStatement(SQL_FIND_BY_EVENT_AND_STATUS)){
            ps.setLong(1, eventId);
            ps.setString(2, status.name());

            try (ResultSet rs = ps.executeQuery()){
                while(rs.next()){
                    bookings.add(booking_mapper.mapRow(rs));
                }
                return bookings;
            }
        }catch (SQLException e){
            throw new DaoException("Error while trying to find booking with eventId = " + eventId + ", status = " + status.name(), e);
        }
    }


    public static final String SQL_FIND_BY_USER_AND_EVENT = SQL_FIND_ALL + " WHERE user_id = ? AND event_id = ?";
    /**
     * Executes SQL query to get all bookings from a user for an event
     * @param conn the db connection
     * @param userId the user id
     * @param eventId the event id
     * @return List of bookings
     */
    @Override
    public List<Booking> findAllByUserIdAndEventId(Connection conn, long userId, long eventId) {
        List<Booking> bookings = new ArrayList<>();
        try(PreparedStatement ps = conn.prepareStatement(SQL_FIND_BY_USER_AND_EVENT)){
            ps.setLong(1, userId);
            ps.setLong(2, eventId);

            try (ResultSet rs = ps.executeQuery()){
                while(rs.next()){
                    bookings.add(booking_mapper.mapRow(rs));
                }
                return bookings;
            }
        }catch (SQLException e){
            throw new DaoException("Error while trying to find booking with userId = " + userId + ", eventId = " + eventId, e);
        }
    }


    public static final String SQL_INSERT = "INSERT INTO booking (user_id, event_id, created_at, status, total_price) " +
                                            "VALUES (?, ?, ?, ?, ?) RETURNING id";
    /**
     * Executes SQL Query to add booking to database
     * @param conn the db connection
     * @param booking the booking to add
     * @return id assigned to the booking by db
     */
    @Override
    public long insert(Connection conn, Booking booking) {
        try(PreparedStatement ps = conn.prepareStatement(SQL_INSERT)){
            ps.setLong(1, booking.getUserId());
            ps.setLong(2, booking.getEventId());
            ps.setTimestamp(3, Timestamp.valueOf(booking.getCreatedAt()));
            ps.setString(4, booking.getStatus().name());
            ps.setBigDecimal(5, booking.getTotalPrice());

            try(ResultSet rs = ps.executeQuery()){
                if (rs.next()) return rs.getLong(1);
                throw new DaoException("Insert failed: no ID returned");
            }
        }catch (SQLException e){
            throw new DaoException("Error while trying to insert booking: " + booking.toString(), e);
        }
    }


    public static final String SQL_UPDATE = "UPDATE booking SET user_id = ?, event_id = ?, created_at = ?, status = ?, " +
                                            "total_price = ? WHERE id = ?";
    /**
     * Executes SQL Query to update booking and save it to db
     * @param conn the db connection
     * @param booking the updated booking
     */
    @Override
    public void update(Connection conn, Booking booking) {
        try(PreparedStatement ps = conn.prepareStatement(SQL_UPDATE)){
            ps.setLong(1, booking.getUserId());
            ps.setLong(2, booking.getEventId());
            ps.setTimestamp(3, Timestamp.valueOf(booking.getCreatedAt()));
            ps.setString(4, booking.getStatus().name());
            ps.setBigDecimal(5, booking.getTotalPrice());
            ps.setLong(6, booking.getId());

            int updated = ps.executeUpdate();
            JdbcUtils.requireUpdatedExactly(updated, 1, "updateBooking(id=" + booking.getId() + ")");
        }catch (SQLException e){
            throw new DaoException("Error while trying to update booking: " + booking.toString(), e);
        }
    }


    public static final String SQL_UPDATE_STATUS = "UPDATE booking SET status = ? WHERE id = ?";
    /**
     * Executes SQL Query to update status of a booking
     * @param conn the db connection
     * @param bookingId the id of booking
     * @param status the status to update to
     */
    @Override
    public void updateStatus(Connection conn, long bookingId, BookingStatus status) {
        try(PreparedStatement ps = conn.prepareStatement(SQL_UPDATE_STATUS)){
            ps.setString(1, status.name());
            ps.setLong(2, bookingId);

            int updated = ps.executeUpdate();
            JdbcUtils.requireUpdatedExactly(updated, 1, "updateStatus(id=" + bookingId + ")");
        }catch (SQLException e){
            throw new DaoException("Error while trying to update status of booking with id = " + bookingId, e);
        }
    }


    public static final String SQL_DELETE = "DELETE FROM booking WHERE id = ?";
    /**
     * Executes SQL Query to delete booking from db from its id
     * @param conn the db connection
     * @param bookingId the id of the booking to delete
     */
    @Override
    public void delete(Connection conn, long bookingId) {
        try(PreparedStatement ps = conn.prepareStatement(SQL_DELETE)){
            ps.setLong(1, bookingId);

            int deleted = ps.executeUpdate();
            JdbcUtils.requireUpdatedExactly(deleted, 1, "deleteBooking(id=" + bookingId + ")");
        }catch (SQLException e){
            throw new DaoException("Error while trying to delete booking with id = " + bookingId, e);
        }
    }
}