package venue.event.manager.repository;

import venue.event.manager.domain.model.resource.Equipment;
import java.sql.Connection;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/** Persistence operations for equipment resources. */
public interface EquipmentRepository {

    /** Finds all equipment.
     * @param conn active connection
     * @return all equipment */
    List<Equipment> findAll(Connection conn);

    /** Finds equipment by identifier.
     * @param conn active connection
     * @param equipmentId equipment identifier
     * @return matching equipment, if present */
    Optional<Equipment> findById(Connection conn, long equipmentId);

    /** Finds equipment associated with a venue.
     * @param conn active connection
     * @param venueId venue identifier
     * @return equipment associated with the venue */
    List<Equipment> findAllByVenueId(Connection conn, long venueId);

    /** Searches equipment by name.
     * @param conn active connection
     * @param name name fragment
     * @return matching equipment */
    List<Equipment> searchByName(Connection conn, String name);

    /** Persists new equipment.
     * @param conn active connection
     * @param equipment equipment to persist
     * @return generated identifier */
    long insert(Connection conn,Equipment equipment);

    /** Updates existing equipment.
     * @param conn active connection
     * @param equipment equipment containing updated values */
    void update(Connection conn, Equipment equipment);

    /** Deletes equipment by identifier.
     * @param conn active connection
     * @param equipmentId identifier to delete */
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
