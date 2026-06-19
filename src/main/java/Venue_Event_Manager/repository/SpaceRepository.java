package Venue_Event_Manager.repository;

import Venue_Event_Manager.domain.model.resource.Space;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface SpaceRepository {

    List<Space> findAll(Connection conn);

    Optional<Space> findById(Connection conn, long spaceId);

    List<Space> findAllByVenueId(Connection conn, long venueId);

    List<Space> searchByName(Connection conn, String name);

    long insert(Connection conn, Space space);

    void update(Connection conn, Space space);

    void deleteById(Connection conn, long spaceId);

    List<Space> findAvailableSpaces(Connection conn, long venueId, LocalDateTime begin, LocalDateTime end);
}