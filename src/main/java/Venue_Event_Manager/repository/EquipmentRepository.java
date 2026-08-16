package Venue_Event_Manager.repository;

import Venue_Event_Manager.domain.model.resource.Equipment;
import java.sql.Connection;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/** Persistence operations for equipment resources. */
public interface EquipmentRepository {

    /** Finds all equipment.
     * @param conn active connection
     * @return all equipment */
    /**
     * Performs the {@code findAll} repository operation.
     * @param conn conn value
     * @return operation result
     */
    List<Equipment> findAll(Connection conn);

    /** Finds equipment by identifier.
     * @param conn active connection
     * @param equipmentId equipment identifier
     * @return matching equipment, if present */
    /**
     * Performs the {@code findById} repository operation.
     * @param conn conn value
     * @param equipmentId equipmentId value
     * @return operation result
     */
    Optional<Equipment> findById(Connection conn, long equipmentId);

    /** Finds equipment associated with a venue.
     * @param conn active connection
     * @param venueId venue identifier
     * @return equipment associated with the venue */
    /**
     * Performs the {@code findAllByVenueId} repository operation.
     * @param conn conn value
     * @param venueId venueId value
     * @return operation result
     */
    List<Equipment> findAllByVenueId(Connection conn, long venueId);

    /** Searches equipment by name.
     * @param conn active connection
     * @param name name fragment
     * @return matching equipment */
    /**
     * Performs the {@code searchByName} repository operation.
     * @param conn conn value
     * @param name name value
     * @return operation result
     */
    List<Equipment> searchByName(Connection conn, String name);

    /** Persists new equipment.
     * @param conn active connection
     * @param equipment equipment to persist
     * @return generated identifier */
    /**
     * Performs the {@code insert} repository operation.
     * @param conn conn value
     * @param equipment equipment value
     * @return operation result
     */
    long insert(Connection conn,Equipment equipment);

    /** Updates existing equipment.
     * @param conn active connection
     * @param equipment equipment containing updated values */
    /**
     * Performs the {@code update} repository operation.
     * @param conn conn value
     * @param equipment equipment value
     */
    void update(Connection conn, Equipment equipment);

    /** Deletes equipment by identifier.
     * @param conn active connection
     * @param equipmentId identifier to delete */
    /**
     * Performs the {@code deleteById} repository operation.
     * @param conn conn value
     * @param equipmentId equipmentId value
     */
    void deleteById(Connection conn, long equipmentId);

    /**
     * Finds equipment available for an interval.
     * @param conn active connection
     * @param venueId venue identifier
     * @param begin interval start
     * @param end interval end
     * @return equipment available throughout the interval
     */
    List<Equipment> findAvailableEquipment(Connection conn, long venueId, LocalDateTime begin, LocalDateTime end);

}
