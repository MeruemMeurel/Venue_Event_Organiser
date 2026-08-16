package Venue_Event_Manager.repository;

import Venue_Event_Manager.domain.model.resource.Service;
import Venue_Event_Manager.domain.model.resource.Space;

import java.sql.Connection;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/** Defines persistence operations for the corresponding domain model. */
public interface ServiceRepository {

    /**
     * Performs the {@code findAll} repository operation.
     * @param conn conn value
     * @return operation result
     */
    List<Service> findAll(Connection conn);

    /**
     * Performs the {@code findById} repository operation.
     * @param conn conn value
     * @param serviceId serviceId value
     * @return operation result
     */
    Optional<Service> findById(Connection conn, long serviceId);

    /**
     * Performs the {@code searchByName} repository operation.
     * @param conn conn value
     * @param name name value
     * @return operation result
     */
    List<Service> searchByName(Connection conn, String name);

    /**
     * Performs the {@code insert} repository operation.
     * @param conn conn value
     * @param service service value
     * @return operation result
     */
    long insert(Connection conn,Service service);

    /**
     * Performs the {@code update} repository operation.
     * @param conn conn value
     * @param service service value
     */
    void update(Connection conn, Service service);

    /**
     * Performs the {@code deleteById} repository operation.
     * @param conn conn value
     * @param serviceId serviceId value
     */
    void deleteById(Connection conn, long serviceId);

    /**
     * Performs the {@code findAvailableServicesForEvent} repository operation.
     * @param conn conn value
     * @param eventId eventId value
     * @return operation result
     */
    List<Service> findAvailableServicesForEvent(Connection conn, long eventId);

}
