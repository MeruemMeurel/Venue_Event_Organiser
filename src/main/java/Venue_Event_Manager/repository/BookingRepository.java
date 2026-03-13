package Venue_Event_Manager.repository;

import Venue_Event_Manager.domain.model.booking.Booking;
import Venue_Event_Manager.domain.model.booking.BookingStatus;

import java.sql.Connection;
import java.util.List;
import java.util.Optional;

public interface BookingRepository {
    //-----FINDERS-----
    List<Booking> findAll(Connection conn);

    Optional<Booking> findById(Connection conn, long id);

    List<Booking> findAllByUserId(Connection conn, long userId);

    List<Booking> findAllByEventId(Connection conn, long eventId);

    List<Booking> findAllByUserIdAndStatus(Connection conn, long userId, BookingStatus status);

    List<Booking> findAllByUserIdAndEventId(Connection conn, long userId, long eventId);

    List<Booking> findAllByEventIdAndStatus(Connection conn, long eventId, BookingStatus status);

   List<Booking> findAllByStatus(Connection conn, BookingStatus status);

    //-----INSERT AND UPDATE-----
    long insert(Connection conn, Booking booking);

    void update(Connection conn, Booking booking);

    void updateStatus(Connection conn, long id, BookingStatus status);

    //-----DELETE-----
    void delete(Connection conn, long id);


}
