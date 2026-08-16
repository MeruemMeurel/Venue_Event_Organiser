package Venue_Event_Manager.repository;

import Venue_Event_Manager.domain.model.booking.Ticket;
import java.sql.Connection;
import java.util.List;
import java.util.Optional;

/** Defines persistence operations for the corresponding domain model. */
public interface TicketRepository {

    /**
     * Performs the {@code findAll} repository operation.
     * @param conn conn value
     * @return operation result
     */
    List<Ticket> findAll(Connection conn);

    /**
     * Performs the {@code findById} repository operation.
     * @param conn conn value
     * @param ticketId ticketId value
     * @return operation result
     */
    Optional<Ticket> findById(Connection conn, long ticketId);

    /**
     * Performs the {@code findAllByBookingId} repository operation.
     * @param conn conn value
     * @param bookingId bookingId value
     * @return operation result
     */
    List<Ticket> findAllByBookingId(Connection conn, long bookingId);

    /**
     * Performs the {@code findAllByEventId} repository operation.
     * @param conn conn value
     * @param eventId eventId value
     * @return operation result
     */
    List<Ticket> findAllByEventId(Connection conn, long eventId);

    /**
     * Performs the {@code countTicketsForEvent} repository operation.
     * @param conn conn value
     * @param eventId eventId value
     * @return operation result
     */
    int countTicketsForEvent(Connection conn, long eventId);

    /**
     * Performs the {@code insert} repository operation.
     * @param conn conn value
     * @param ticket ticket value
     * @return operation result
     */
    long insert(Connection conn, Ticket ticket);

    /**
     * Performs the {@code insertMany} repository operation.
     * @param conn conn value
     * @param tickets tickets value
     */
    void insertMany(Connection conn, List<Ticket> tickets);

    /**
     * Performs the {@code update} repository operation.
     * @param conn conn value
     * @param ticket ticket value
     */
    void update(Connection conn, Ticket ticket);

    /**
     * Performs the {@code deleteById} repository operation.
     * @param conn conn value
     * @param ticketId ticketId value
     */
    void deleteById(Connection conn, long ticketId);

}
