package Venue_Event_Manager.repository.jdbc;

import Venue_Event_Manager.domain.model.booking.Booking;
import Venue_Event_Manager.domain.model.booking.BookingStatus;
import Venue_Event_Manager.repository.BookingRepository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class PgBookingRepository implements BookingRepository {

    public static final String SQL_FIND_ALL = "SELECT id,user_id,event_id,created_at,status, total_price FROM Booking";
    public static final String SQL_FIND_ALL_BY_ID = "SELECT id,user_id,event_id,created_at,status, total_price FROM Booking WHERE id = ?";
    public static final String SQL_FIND_BY_USER_ID = "SELECT id,user_id,event_id,created_at,status, total_price FROM Booking WHERE user_id = ?";
    public static final String SQL_FIND_BY_EVENT_ID = "SELECT id,user_id,event_id,created_at,status, total_price FROM Booking WHERE event_id = ?";
    public static final String SQL_FIND_BY_USER_AND_EVENT = "SELECT id,user_id,event_id,created_at,status, total_price FROM BOOKING WHERE user_id = ? AND event_id = ?";
    public static final String SQL_FIND_BY_USER_AND_STATUS = "SELECT id,user_id,event_id,created_at,status, total_price FROM BOOKING WHERE user_id = ? AND status = ?";
    public static final String SQL_FIND_BY_EVENT_AND_STATUS = "SELECT id,user_id,event_id,created_at,status, total_price FROM BOOKING WHERE event_id = ? AND status = ?";
    public static final String SQL_FIND_BY_STATUS = "SELECT id,user_id,event_id,created_at,status, total_price FROM BOOKING WHERE status = ?";

    public static final String SQL_INSERT = "INSERT INTO BOOKING (user_id, event_id, created_at, status, total_price) VALUES (?,?,?,?)";

    public static final String SQL_UPDATE = "UPDATE SET id = ?, user_id = ?, event_id = ?, created_at = ? WHERE id = ?";
    public static final String SQL_UPDATE_STATUS = "UPDATE SET  status = ? WHERE id = ?";

    public static final String SQL_DELETE = "DELETE FROM BOOKING WHERE id = ?";

    /**
     * Lambda function to map booking sql results to a Bookign object
     * @param rs ResultSet
     * @return new Booking object
     */
    public static RowMapper<Booking> booking_mapper = rs -> new Booking(
            rs.getLong("id"),
            rs.getLong("user_id"),
            rs.getLong("event_id"),
            rs.getTimestamp("created_at").toLocalDateTime(),
            BookingStatus.valueOf(rs.getString("status").toUpperCase()),
            rs.getDouble("total_price")
    );

    /**
     * Executes SQL query to get all bookings
     * @param conn the connection to db
     * @return bookings The list of bookings
     */
    @Override
    public List<Booking> findAll(Connection conn) {
        try(PreparedStatement ps = conn.prepareStatement(SQL_FIND_ALL)){

            ArrayList<Booking> bookings = new ArrayList<>();


            try (ResultSet rs = ps.executeQuery()){

                while(rs.next()){
                    bookings.add(booking_mapper.mapRow(rs));
                }
            }
            return bookings;



        }catch (SQLException e){
            throw new DaoException("Error while trying to find all bookings",e);
        }


    }

    /**
     * Executes SQL query to get booking with specific id
     * @param conn the db connection
     * @param id the id to find
     * @return Optional object that contains the Booking if found
     */
    @Override
    public Optional<Booking> findById(Connection conn, long id) {
        try(PreparedStatement ps = conn.prepareStatement(SQL_FIND_ALL_BY_ID)){

            ps.setLong(1, id);

            try (ResultSet rs = ps.executeQuery()){
                if(!rs.next()) return Optional.empty();
                else return Optional.of(booking_mapper.mapRow(rs));
            }

        }catch (SQLException e){
            throw new DaoException("Error while trying to find booking with id = "+id,e);
        }
    }

    /**
     * Executes SQL query to get all bookings made by specific User
     * @param conn the db connection
     * @param userId the User
     * @return List of bookings
     */
    @Override
    public List<Booking> findAllByUserId(Connection conn, long userId) {
        try(PreparedStatement ps = conn.prepareStatement(SQL_FIND_BY_USER_ID)){

            ps.setLong(1, userId);

            ArrayList<Booking> bookings = new ArrayList<>();

            try (ResultSet rs = ps.executeQuery()){

                while(rs.next()){
                    bookings.add(booking_mapper.mapRow(rs));
                }
                return bookings;

            }

        }catch (SQLException e){
            throw new DaoException("Error while trying to find booking with user id = "+ userId,e);
        }
    }

    /**
     * Executes SQL query to get all bookings for a event
     * @param conn the db connection
     * @param eventId the event
     * @return List of bookings
     */
    @Override
    public List<Booking> findAllByEventId(Connection conn, long eventId) {
        try(PreparedStatement ps = conn.prepareStatement(SQL_FIND_BY_EVENT_ID)){

            ps.setLong(1, eventId);

            ArrayList<Booking> bookings = new ArrayList<>();

            try (ResultSet rs = ps.executeQuery()){

                while(rs.next()){
                    bookings.add(booking_mapper.mapRow(rs));
                }
                return bookings;

            }

        }catch (SQLException e){
            throw new DaoException("Error while trying to find bookings with  eventId = "+ eventId,e);
        }
    }

    /**
     * Executes SQL query to get all bookings with specific status
     * @param conn the db connection
     * @param status the status to look for
     * @return List of bookings
     */
    @Override
    public List<Booking> findAllByStatus(Connection conn, BookingStatus status) {
        try(PreparedStatement ps = conn.prepareStatement(SQL_FIND_BY_STATUS)){

            ps.setString(1, status.name());

            ArrayList<Booking> bookings = new ArrayList<>();

            try (ResultSet rs = ps.executeQuery()){

                while(rs.next()){
                    bookings.add(booking_mapper.mapRow(rs));
                }
                return bookings;

            }

        }catch (SQLException e){
            throw new DaoException("Error while trying to find bookings with status = "+ status.toString(),e);
        }
    }

    /**
     * Executes SQL query to get all bookings from a user for a event
     * @param conn the db connection
     * @param userId the user
     * @param eventId the event
     * @return List of bookings
     */
    @Override
    public List<Booking> findAllByUserIdAndEventId(Connection conn, long userId, long eventId) {
        try(PreparedStatement ps = conn.prepareStatement(SQL_FIND_BY_USER_AND_EVENT)){

            ps.setLong(1, userId);
            ps.setLong(2, eventId);

            ArrayList<Booking> bookings = new ArrayList<>();

            try (ResultSet rs = ps.executeQuery()){

                while(rs.next()){
                    bookings.add(booking_mapper.mapRow(rs));
                }
                return bookings;

            }

        }catch (SQLException e){
            throw new DaoException("Error while trying to find booking with userId = "+ userId+", eventId = "+eventId,e);
        }
    }

    /**
     * Executes SQL query to get all bookings with specific status from a user
     * @param conn the db connection
     * @param userId the user
     * @param status the status
     * @return List of bookings
     */
    @Override
    public List<Booking> findAllByUserIdAndStatus(Connection conn, long userId, BookingStatus status) {
        try(PreparedStatement ps = conn.prepareStatement(SQL_FIND_BY_USER_AND_STATUS)){

            ps.setLong(1, userId);
            ps.setString(2, status.name());

            ArrayList<Booking> bookings = new ArrayList<>();

            try (ResultSet rs = ps.executeQuery()){

                while(rs.next()){
                    bookings.add(booking_mapper.mapRow(rs));
                }
                return bookings;

            }

        }catch (SQLException e){
            throw new DaoException("Error while trying to find booking with userId = "+ userId+", status = "+status.name(),e);
        }    }

    /**
     * Executes SQL query to get all bookings with specific status for an event
     * @param conn the db connection
     * @param eventId the event
     * @param status the status
     * @return List of bookings
     */
    @Override
    public List<Booking> findAllByEventIdAndStatus(Connection conn, long eventId, BookingStatus status) {
        try(PreparedStatement ps = conn.prepareStatement(SQL_FIND_BY_EVENT_AND_STATUS)){

            ps.setLong(1, eventId);
            ps.setString(2, status.name());

            ArrayList<Booking> bookings = new ArrayList<>();

            try (ResultSet rs = ps.executeQuery()){

                while(rs.next()){
                    bookings.add(booking_mapper.mapRow(rs));
                }
                return bookings;

            }

        }catch (SQLException e){
            throw new DaoException("Error while trying to find booking with eventId = "+ eventId+", status = "+status.name(),e);
        }
    }

    /**
     * Executes SQL Query to add booking to database
     * @param conn the db connection
     * @param booking the booking to add
     * @return id assigned to the booking by db
     */
    @Override
    public long insert(Connection conn, Booking booking) {
        try(PreparedStatement ps = conn.prepareStatement(SQL_INSERT)){

            ps.setLong(1,booking.getUserId());
            ps.setLong(2,booking.getEventId());
            ps.setObject(3,booking.getCreatedAt());
            ps.setString(4, booking.getStatus().name());
            ps.setDouble(5,booking.getTotalPrice());

            try(ResultSet rs = ps.executeQuery()){

                rs.next();
                return rs.getLong(1);

            }


        }catch (SQLException e){
            throw new DaoException("Error while trying to insert booking = " + booking.toString(), e);
        }


    }

    /**
     * Executes SQL Query to update booking and save it to db
     * @param conn the db connection
     * @param booking the updated booking to update
     */
    @Override
    public void update(Connection conn, Booking booking) {
        try(PreparedStatement ps = conn.prepareStatement(SQL_UPDATE)){

            ps.setLong(1,booking.getId());
            ps.setLong(2,booking.getUserId());
            ps.setLong(3,booking.getEventId());
            ps.setObject(4,booking.getCreatedAt());
            ps.setString(5,booking.getStatus().name());
            ps.setDouble(6,booking.getTotalPrice());

            int updated = ps.executeUpdate();

            JdbcUtils.requireUpdatedExactly(updated,1,"booking.update(id="+booking.getId()+")");


        }catch (SQLException e){
            throw new DaoException("Error while trying to update booking = " + booking.toString(), e);
        }

    }

    /**
     * Executes SQL Query to update status of a booking
     * @param conn the db connection
     * @param id the id of booking
     * @param status the status to update to
     */
    @Override
    public void updateStatus(Connection conn, long id, BookingStatus status) {
        try(PreparedStatement ps = conn.prepareStatement(SQL_UPDATE_STATUS)){

            ps.setString(1, status.name());
            ps.setLong(2, id);

            int updated =  ps.executeUpdate();

            JdbcUtils.requireUpdatedExactly(updated,1,"booking.updateStatus(id="+id+")");


        }catch (SQLException e){
            throw new DaoException("Error while trying to update status of booking with id = "+id, e);
        }


    }

    /**
     * Executes SQL Query to delete booking from db from its id
     * @param conn the db connection
     * @param id the id of the booking to delete
     */
    @Override
    public void delete(Connection conn, long id) {
        try(PreparedStatement ps = conn.prepareStatement(SQL_DELETE)){
            ps.setLong(1, id);

            int deleted = ps.executeUpdate();

            JdbcUtils.requireUpdatedExactly(deleted,1,"booking.delete("+id+")");


        }catch (SQLException e){
            throw new DaoException("Error while trying to delete booking with id = " + id, e);
        }

    }
}
