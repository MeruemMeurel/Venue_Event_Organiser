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


    private final static String SQL_FIND_ALL = "SELECT id, booking_id, firstname, lastname, starts_at FROM ticket";
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


    private final static String SQL_FIND_BY_BOOKING_ID = SQL_FIND_ALL + " WHERE booking_id = ?";
    /**
     * Executes SQL query to get all tickets for a specific booking
     * @param conn the db connection
     * @param bookingId the booking id
     * @return List of tickets
     */
    @Override
    public List<Ticket> findAllByBookingId(Connection conn, long bookingId) {
        try (PreparedStatement ps = conn.prepareStatement(SQL_FIND_BY_BOOKING_ID)) {
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


    private final static String SQL_UPDATE = "UPDATE ticket SET booking_id = ?, firstname = ?, lastname = ?, " +
                                             "starts_at = ? WHERE id = ?";
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


    private final static String SQL_DELETE = "DELETE FROM ticket WHERE id = ?";
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