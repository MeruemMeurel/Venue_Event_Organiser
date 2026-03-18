package Venue_Event_Manager.repository;

import Venue_Event_Manager.domain.model.booking.Ticket;
import java.sql.Connection;
import java.util.List;
import java.util.Optional;

public interface TicketRepository {

    List<Ticket> findAll(Connection conn);

    Optional<Ticket> findById(Connection conn, long ticketId);

    List<Ticket> findAllByBookingId(Connection conn, long bookingId);

    List<Ticket> findAllByEventId(Connection conn, long eventId);

    int countTicketsForEvent(Connection conn, long eventId);

    long insert(Connection conn, Ticket ticket);

    void update(Connection conn, Ticket ticket);

    void deleteById(Connection conn, long ticketId);

}