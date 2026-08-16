package Venue_Event_Manager.repository;

import Venue_Event_Manager.domain.model.venue.Venue;
import java.sql.Connection;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/** Defines persistence operations for the corresponding domain model. */
public interface VenueRepository {

    /**
     * Performs the {@code findAll} repository operation.
     * @param conn conn value
     * @return operation result
     */
    List<Venue> findAll(Connection conn);

    /**
     * Performs the {@code findById} repository operation.
     * @param conn conn value
     * @param venueId venueId value
     * @return operation result
     */
    Optional<Venue> findById(Connection conn, long venueId);

    /**
     * Performs the {@code findByName} repository operation.
     * @param conn conn value
     * @param name name value
     * @return operation result
     */
    List<Venue> findByName(Connection conn, String name);

    /**
     * Performs the {@code findByCity} repository operation.
     * @param conn conn value
     * @param city city value
     * @return operation result
     */
    List<Venue> findByCity(Connection conn, String city);

    /**
     * Performs the {@code findByCountry} repository operation.
     * @param conn conn value
     * @param country country value
     * @return operation result
     */
    List<Venue> findByCountry(Connection conn, String country);

    /**
     * Performs the {@code findAllWithAvailableSpaces} repository operation.
     * @param conn conn value
     * @param begin begin value
     * @param end end value
     * @return operation result
     */
    List<Venue> findAllWithAvailableSpaces(Connection conn, LocalDateTime begin, LocalDateTime end);

    /**
     * Performs the {@code insert} repository operation.
     * @param conn conn value
     * @param venue venue value
     * @return operation result
     */
    long insert(Connection conn, Venue venue);

    /**
     * Performs the {@code update} repository operation.
     * @param conn conn value
     * @param venue venue value
     */
    void update(Connection conn, Venue venue);

    /**
     * Performs the {@code deleteById} repository operation.
     * @param conn conn value
     * @param venueId venueId value
     */
    void deleteById(Connection conn, long venueId);

}
