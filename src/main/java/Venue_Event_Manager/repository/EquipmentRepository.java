package Venue_Event_Manager.repository;

import Venue_Event_Manager.domain.model.resource.Equipment;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.Optional;

public interface EquipmentRepository {
    ArrayList<Equipment> findAll(Connection conn);

    Optional<Equipment> findById(Connection conn, long id);
    ArrayList<Equipment> findByVenue(Connection conn, long venueId);

    long insert(Connection conn,Equipment equipment);

    void update(Connection conn, Equipment equipment);

    void deleteById(Connection conn, long id);
}
