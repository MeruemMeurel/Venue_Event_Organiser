package Venue_Event_Manager.repository;

import Venue_Event_Manager.domain.model.resource.Space;
import java.sql.*;
import java.util.List;
import java.util.Optional;

public interface SpaceRepository {

    List<Space> findAll(Connection conn);

    Optional<Space> findById(Connection conn, long spaceId);

    List<Space> findAllByVenueId(Connection conn, long venueId);

    long insert(Connection conn, Space space);

    void update(Connection conn, Space space);

    void deleteById(Connection conn, long spaceId);

}