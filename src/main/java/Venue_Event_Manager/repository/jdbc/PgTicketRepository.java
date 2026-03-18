package Venue_Event_Manager.repository.jdbc;

import Venue_Event_Manager.domain.model.booking.Ticket;
import Venue_Event_Manager.repository.TicketRepository;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class PgTicketRepository implements TicketRepository {

    /**
     * Lambda function to map ticket sql results to a Ticket object
     */
    private static final RowMapper<Ticket> ticket_mapper = rs -> new Ticket(
            rs.getLong("id"),
            rs.getLong("booking_id"),
            rs.getString("firstname"),
            rs.getString("lastname"),
            rs.getTimestamp("starts_at").toLocalDateTime()
    );


    private final static String SQL_FIND_ALL = "SELECT id, booking_id, firstname, lastname, starts_at " +
                                               "FROM ticket";
    /**
     * Executes SQL query to get all tickets
     * @param conn the connection to db
     * @return List of tickets
     */
    @Override
    public List<Ticket> findAll(Connection conn) {
        try (PreparedStatement ps = conn.prepareStatement(SQL_FIND_ALL)) {
            List<Ticket> tickets = new ArrayList<>();

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    tickets.add(ticket_mapper.mapRow(rs));
                }
            }
            return tickets;
        } catch (SQLException e) {
            throw new DaoException("Error while trying to find all tickets", e);
        }
    }


    private final static String SQL_FIND_BY_ID = SQL_FIND_ALL + " WHERE id = ?";
    /**
     * Executes SQL query to get ticket with specific id
     * @param conn the db connection
     * @param ticketId the id to find
     * @return Optional object containing the Ticket if found
     */
    @Override
    public Optional<Ticket> findById(Connection conn, long ticketId) {
        try (PreparedStatement ps = conn.prepareStatement(SQL_FIND_BY_ID)) {
            ps.setLong(1, ticketId);

            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return Optional.empty();
                return Optional.of(ticket_mapper.mapRow(rs));
            }
        } catch (SQLException e) {
            throw new DaoException("Error while trying to find ticket with id = " + ticketId, e);
        }
    }


    private final static String SQL_FIND_ALL_BY_BOOKING_ID = SQL_FIND_ALL + " WHERE booking_id = ?";
    /**
     * Executes SQL query to get all tickets for a specific booking
     * @param conn the db connection
     * @param bookingId the booking id
     * @return List of tickets
     */
    @Override
    public List<Ticket> findAllByBookingId(Connection conn, long bookingId) {
        try (PreparedStatement ps = conn.prepareStatement(SQL_FIND_ALL_BY_BOOKING_ID)) {
            ps.setLong(1, bookingId);
            List<Ticket> tickets = new ArrayList<>();

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    tickets.add(ticket_mapper.mapRow(rs));
                }
            }
            return tickets;
        } catch (SQLException e) {
            throw new DaoException("Error while trying to find tickets for booking id = " + bookingId, e);
        }
    }

    public static String SQL_FIND_ALL_BY_EVENT = SQL_FIND_ALL + "WHERE event = ?";

    /**
     * Executes SQL Query to get all tickets for a specific event
     * @param conn the db connection
     * @param eventId the id of the event
     * @return List of Tickets found
     * @throws DaoException daoException
     */
    @Override
    public List<Ticket> findAllByEventId(Connection conn, long eventId) {
        try (PreparedStatement ps = conn.prepareStatement(SQL_FIND_ALL_BY_EVENT)) {
            ps.setLong(1, eventId);
            List<Ticket> tickets = new ArrayList<>();

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    tickets.add(ticket_mapper.mapRow(rs));
                }
            }
            return tickets;
        } catch (SQLException e) {
            throw new DaoException("Error while trying to find tickets for event id = " + eventId, e);
        }    }

    public static String SQL_COUNT_TICKETS =    "SELECT COUNT(*) AS total_tickets " +
                                                "FROM ticket t " +
                                                "INNER JOIN booking b ON b.id = t.booking_id " +
                                                "WHERE b.event_id = ? " +
                                                "AND b.status IN ('PENDING_PAYMENT','CANCELLED') ";
    /**
     * Executes SQL query to count the tickets for an event that are pending a payment or are confirmed
     * @param conn    the db connection
     * @param eventId the id of the event
     * @return int number of tickets counted (if none = 0)
     * @throws DaoException daoException
     */
    @Override
    public int countTicketsForEvent(Connection conn, long eventId) {
        try(PreparedStatement ps = conn.prepareStatement(SQL_COUNT_TICKETS)){
            ps.setLong(1, eventId);

            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                return rs.getInt("total_tickets");
            }
        }catch (SQLException e){
            throw new DaoException("Error while trying to count tickets for event id = " + eventId, e);
        }
    }

    private final static String SQL_INSERT = "INSERT INTO ticket (booking_id, firstname, lastname, starts_at) " +
                                             "VALUES (?, ?, ?, ?) RETURNING id";
    /**
     * Executes SQL query to insert a new ticket
     * @param conn the db connection
     * @param ticket the ticket to add
     * @return the generated id
     */
    @Override
    public long insert(Connection conn, Ticket ticket) {
        try (PreparedStatement ps = conn.prepareStatement(SQL_INSERT)) {
            ps.setLong(1, ticket.getBookingId());
            JdbcUtils.setNullableString(ps, 2, ticket.getFirstname());
            JdbcUtils.setNullableString(ps, 3, ticket.getLastname());
            ps.setTimestamp(4, Timestamp.valueOf(ticket.getStartsAt()));

            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                return rs.getLong(1);
            }
        } catch (SQLException e) {
            throw new DaoException("Error while trying to insert ticket: " + ticket.toString(), e);
        }
    }

    /**
     * Executes multiple sql queries to insert a list of tickets
     * @param conn the db connection
     * @param tickets the list of tickets
     * @throws DaoException dao exception
     */
    @Override
    public void insertMany(Connection conn, List<Ticket> tickets) {
        for (Ticket ticket : tickets) {
            insert(conn, ticket);
        }
    }

    private final static String SQL_UPDATE = "UPDATE ticket " +
                                             "SET booking_id = ?, firstname = ?, lastname = ?, starts_at = ? " +
                                             "WHERE id = ?";
    /**
     * Executes SQL query to update an existing ticket
     * @param conn the db connection
     * @param ticket the updated ticket
     */
    @Override
    public void update(Connection conn, Ticket ticket) {
        try (PreparedStatement ps = conn.prepareStatement(SQL_UPDATE)) {
            ps.setLong(1, ticket.getBookingId());
            JdbcUtils.setNullableString(ps, 2, ticket.getFirstname());
            JdbcUtils.setNullableString(ps, 3, ticket.getLastname());
            ps.setTimestamp(4, Timestamp.valueOf(ticket.getStartsAt()));
            ps.setLong(5, ticket.getId());

            int updated = ps.executeUpdate();
            JdbcUtils.requireUpdatedExactly(updated, 1, "update(ticket_id=" + ticket.getId() + ")");
        } catch (SQLException e) {
            throw new DaoException("Error while trying to update ticket: " + ticket.toString(), e);
        }
    }


    private final static String SQL_DELETE = "DELETE FROM ticket " +
                                             "WHERE id = ?";
    /**
     * Executes SQL query to delete a ticket by id
     * @param conn the db connection
     * @param ticketId the id to delete
     */
    @Override
    public void deleteById(Connection conn, long ticketId) {
        try (PreparedStatement ps = conn.prepareStatement(SQL_DELETE)) {
            ps.setLong(1, ticketId);

            int deleted = ps.executeUpdate();
            JdbcUtils.requireUpdatedExactly(deleted, 1, "delete(ticket_id=" + ticketId + ")");
        } catch (SQLException e) {
            throw new DaoException("Error while trying to delete ticket with id = " + ticketId, e);
        }
    }
}