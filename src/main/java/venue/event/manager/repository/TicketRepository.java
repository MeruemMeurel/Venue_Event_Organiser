package venue.event.manager.repository;

import venue.event.manager.domain.model.booking.Ticket;
import java.sql.Connection;
import java.util.List;
import java.util.Optional;

/** Defines persistence operations for the corresponding domain model. */
public interface TicketRepository {

    /**
     * Returns all persisted records.
     * @param conn active database connection
     * @return result produced by the repository operation
     */
    List<Ticket> findAll(Connection conn);

    /**
     * Finds a persisted record by identifier.
     * @param conn active database connection
     * @param ticketId identifier used by the operation
     * @return result produced by the repository operation
     */
    Optional<Ticket> findById(Connection conn, long ticketId);

    /**
     * Returns persisted records filtered by booking id.
     * @param conn active database connection
     * @param bookingId identifier used by the operation
     * @return result produced by the repository operation
     */
    List<Ticket> findAllByBookingId(Connection conn, long bookingId);

    /**
     * Returns persisted records filtered by event id.
     * @param conn active database connection
     * @param eventId identifier used by the operation
     * @return result produced by the repository operation
     */
    List<Ticket> findAllByEventId(Connection conn, long eventId);

    /**
     * Counts the tickets associated with an event.
     * @param conn active database connection
     * @param eventId identifier used by the operation
     * @return result produced by the repository operation
     */
    int countTicketsForEvent(Connection conn, long eventId);

    /**
     * Persists a new record.
     * @param conn active database connection
     * @param ticket record to persist
     * @return result produced by the repository operation
     */
    long insert(Connection conn, Ticket ticket);

    /**
     * Persists multiple tickets as a batch.
     * @param conn active connection
     * @param tickets tickets to persist
     */
    void insertMany(Connection conn, List<Ticket> tickets);

    /**
     * Updates an existing record.
     * @param conn active database connection
     * @param ticket record to persist
     */
    void update(Connection conn, Ticket ticket);

    /**
     * Deletebyids the supplied persistent data.
     * @param conn active database connection
     * @param ticketId identifier used by the operation
     */
    void deleteById(Connection conn, long ticketId);

}
