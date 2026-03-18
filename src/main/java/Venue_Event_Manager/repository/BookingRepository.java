package Venue_Event_Manager.repository;

import Venue_Event_Manager.domain.model.booking.Booking;
import Venue_Event_Manager.domain.model.booking.BookingStatus;

import java.sql.Connection;
import java.util.List;
import java.util.Optional;

public interface BookingRepository {

    List<Booking> findAll(Connection conn);

    Optional<Booking> findById(Connection conn, long bookingId);

    List<Booking> findAllByUserId(Connection conn, long userId);

    List<Booking> findAllByEventId(Connection conn, long eventId);

    List<Booking> findAllByStatus(Connection conn, BookingStatus status);

    List<Booking> findAllByUserIdAndStatus(Connection conn, long userId, BookingStatus status);

    List<Booking> findAllByEventIdAndStatus(Connection conn, long eventId, BookingStatus status);

    List<Booking> findAllByUserIdAndEventId(Connection conn, long userId, long eventId);

    long insert(Connection conn, Booking booking);

    void update(Connection conn, Booking booking);

    void updateStatus(Connection conn, long bookingId, BookingStatus status);

    void delete(Connection conn, long bookingId);

}