package Venue_Event_Manager.repository;

import Venue_Event_Manager.domain.model.resource.Space;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/** Defines persistence operations for the corresponding domain model. */
public interface SpaceRepository {

    /**
     * Performs the {@code findAll} repository operation.
     * @param conn conn value
     * @return operation result
     */
    List<Space> findAll(Connection conn);

    /**
     * Performs the {@code findById} repository operation.
     * @param conn conn value
     * @param spaceId spaceId value
     * @return operation result
     */
    Optional<Space> findById(Connection conn, long spaceId);

    /**
     * Performs the {@code findAllByVenueId} repository operation.
     * @param conn conn value
     * @param venueId venueId value
     * @return operation result
     */
    List<Space> findAllByVenueId(Connection conn, long venueId);

    /**
     * Performs the {@code searchByName} repository operation.
     * @param conn conn value
     * @param name name value
     * @return operation result
     */
    List<Space> searchByName(Connection conn, String name);

    /**
     * Performs the {@code insert} repository operation.
     * @param conn conn value
     * @param space space value
     * @return operation result
     */
    long insert(Connection conn, Space space);

    /**
     * Performs the {@code update} repository operation.
     * @param conn conn value
     * @param space space value
     */
    void update(Connection conn, Space space);

    /**
     * Performs the {@code deleteById} repository operation.
     * @param conn conn value
     * @param spaceId spaceId value
     */
    void deleteById(Connection conn, long spaceId);

    /**
     * Performs the {@code findAvailableSpaces} repository operation.
     * @param conn conn value
     * @param venueId venueId value
     * @param begin begin value
     * @param end end value
     * @return operation result
     */
    List<Space> findAvailableSpaces(Connection conn, long venueId, LocalDateTime begin, LocalDateTime end);
}
