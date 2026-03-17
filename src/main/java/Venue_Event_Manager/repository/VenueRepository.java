package Venue_Event_Manager.repository;

import Venue_Event_Manager.domain.model.venue.Venue;
import java.sql.Connection;
import java.util.List;
import java.util.Optional;

public interface VenueRepository {

    List<Venue> findAll(Connection conn);

    Optional<Venue> findById(Connection conn, long venueId);

    long insert(Connection conn, Venue venue);

    void update(Connection conn, Venue venue);

    void deleteById(Connection conn, long venueId);

}