package Venue_Event_Manager.repository;

import Venue_Event_Manager.domain.model.resource.Service;
import Venue_Event_Manager.domain.model.resource.Space;

import java.sql.Connection;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface ServiceRepository {

    List<Service> findAll(Connection conn);

    Optional<Service> findById(Connection conn, long serviceId);

    List<Service> searchByName(Connection conn, String name);

    long insert(Connection conn,Service service);

    void update(Connection conn, Service service);

    void deleteById(Connection conn, long serviceId);

    List<Service> findAvailableServicesForEvent(Connection conn, long eventId);

}