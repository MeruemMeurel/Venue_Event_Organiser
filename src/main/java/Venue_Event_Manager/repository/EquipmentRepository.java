package Venue_Event_Manager.repository;

import Venue_Event_Manager.domain.model.resource.Equipment;
import java.sql.Connection;
import java.util.List;
import java.util.Optional;

public interface EquipmentRepository {

    List<Equipment> findAll(Connection conn);

    Optional<Equipment> findById(Connection conn, long equipmentId);

    List<Equipment> findAllByVenueId(Connection conn, long venueId);

    long insert(Connection conn,Equipment equipment);

    void update(Connection conn, Equipment equipment);

    void deleteById(Connection conn, long equipmentId);

}