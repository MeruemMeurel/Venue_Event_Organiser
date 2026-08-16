package venue.event.manager.repository;

import venue.event.manager.domain.model.venue.Venue;
import java.sql.Connection;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/** Defines persistence operations for the corresponding domain model. */
public interface VenueRepository {

    /**
     * Returns all persisted records.
     * @param conn active database connection
     * @return result produced by the repository operation
     */
    List<Venue> findAll(Connection conn);

    /**
     * Finds a persisted record by identifier.
     * @param conn active database connection
     * @param venueId identifier used by the operation
     * @return result produced by the repository operation
     */
    Optional<Venue> findById(Connection conn, long venueId);

    /**
     * Finds persisted records matching a name.
     * @param conn active database connection
     * @param name filter value
     * @return result produced by the repository operation
     */
    List<Venue> findByName(Connection conn, String name);

    /**
     * Finds persisted records by city.
     * @param conn active database connection
     * @param city city to match
     * @return result produced by the repository operation
     */
    List<Venue> findByCity(Connection conn, String city);

    /**
     * Finds persisted records by country.
     * @param conn active database connection
     * @param country country to match
     * @return result produced by the repository operation
     */
    List<Venue> findByCountry(Connection conn, String country);

    /**
     * Returns venues with an available space in the requested interval.
     * @param conn active database connection
     * @param begin beginning of the requested interval
     * @param end end of the requested interval
     * @return result produced by the repository operation
     */
    List<Venue> findAllWithAvailableSpaces(Connection conn, LocalDateTime begin, LocalDateTime end);

    /**
     * Persists a new record.
     * @param conn active database connection
     * @param venue record to persist
     * @return result produced by the repository operation
     */
    long insert(Connection conn, Venue venue);

    /**
     * Updates an existing record.
     * @param conn active database connection
     * @param venue record to persist
     */
    void update(Connection conn, Venue venue);

    /**
     * Deletebyids the supplied persistent data.
     * @param conn active database connection
     * @param venueId identifier used by the operation
     */
    void deleteById(Connection conn, long venueId);

}
