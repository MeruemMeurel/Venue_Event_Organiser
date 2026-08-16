package venue.event.manager.repository;

import venue.event.manager.domain.model.resource.Service;
import venue.event.manager.domain.model.resource.Space;

import java.sql.Connection;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/** Defines persistence operations for the corresponding domain model. */
public interface ServiceRepository {

    /**
     * Returns all persisted records.
     * @param conn active database connection
     * @return result produced by the repository operation
     */
    List<Service> findAll(Connection conn);

    /**
     * Finds a persisted record by identifier.
     * @param conn active database connection
     * @param serviceId identifier used by the operation
     * @return result produced by the repository operation
     */
    Optional<Service> findById(Connection conn, long serviceId);

    /**
     * Finds persisted records matching a name.
     * @param conn active database connection
     * @param name filter value
     * @return result produced by the repository operation
     */
    List<Service> searchByName(Connection conn, String name);

    /**
     * Persists a new record.
     * @param conn active database connection
     * @param service record to persist
     * @return result produced by the repository operation
     */
    long insert(Connection conn,Service service);

    /**
     * Updates an existing record.
     * @param conn active database connection
     * @param service record to persist
     */
    void update(Connection conn, Service service);

    /**
     * Deletebyids the supplied persistent data.
     * @param conn active database connection
     * @param serviceId identifier used by the operation
     */
    void deleteById(Connection conn, long serviceId);

    /**
     * Finds services available for an event.
     * @param conn active database connection
     * @param eventId identifier used by the operation
     * @return result produced by the repository operation
     */
    List<Service> findAvailableServicesForEvent(Connection conn, long eventId);

}
