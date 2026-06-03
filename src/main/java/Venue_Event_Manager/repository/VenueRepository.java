package Venue_Event_Manager.repository;

import Venue_Event_Manager.domain.model.venue.Venue;
import java.sql.Connection;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface VenueRepository {

    List<Venue> findAll(Connection conn);

    Optional<Venue> findById(Connection conn, long venueId);

    List<Venue> findByName(Connection conn, String name);

    List<Venue> findByCity(Connection conn, String city);

    List<Venue> findByCountry(Connection conn, String country);

    List<Venue> findAllWithAvailableSpaces(Connection conn, LocalDateTime begin, LocalDateTime end);

    long insert(Connection conn, Venue venue);

    void update(Connection conn, Venue venue);

    void deleteById(Connection conn, long venueId);

}
