package venue.event.manager.repository;

import venue.event.manager.domain.model.resource.Space;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/** Defines persistence operations for the corresponding domain model. */
public interface SpaceRepository {

    /**
     * Returns all persisted records.
     * @param conn active database connection
     * @return result produced by the repository operation
     */
    List<Space> findAll(Connection conn);

    /**
     * Finds a persisted record by identifier.
     * @param conn active database connection
     * @param spaceId identifier used by the operation
     * @return result produced by the repository operation
     */
    Optional<Space> findById(Connection conn, long spaceId);

    /**
     * Returns persisted records filtered by venue id.
     * @param conn active database connection
     * @param venueId identifier used by the operation
     * @return result produced by the repository operation
     */
    List<Space> findAllByVenueId(Connection conn, long venueId);

    /**
     * Finds persisted records matching a name.
     * @param conn active database connection
     * @param name filter value
     * @return result produced by the repository operation
     */
    List<Space> searchByName(Connection conn, String name);

    /**
     * Persists a new record.
     * @param conn active database connection
     * @param space record to persist
     * @return result produced by the repository operation
     */
    long insert(Connection conn, Space space);

    /**
     * Updates an existing record.
     * @param conn active database connection
     * @param space record to persist
     */
    void update(Connection conn, Space space);

    /**
     * Deletebyids the supplied persistent data.
     * @param conn active database connection
     * @param spaceId identifier used by the operation
     */
    void deleteById(Connection conn, long spaceId);

    /**
     * Finds spaces available in the requested interval.
     * @param conn active database connection
     * @param venueId identifier used by the operation
     * @param begin beginning of the requested interval
     * @param end end of the requested interval
     * @return result produced by the repository operation
     */
    List<Space> findAvailableSpaces(Connection conn, long venueId, LocalDateTime begin, LocalDateTime end);
}
