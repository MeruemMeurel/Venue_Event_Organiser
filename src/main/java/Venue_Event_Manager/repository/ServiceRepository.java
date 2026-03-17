package Venue_Event_Manager.repository;

import Venue_Event_Manager.domain.model.resource.Service;
import java.sql.Connection;
import java.util.List;
import java.util.Optional;

public interface ServiceRepository {

    List<Service> findAll(Connection conn);

    Optional<Service> findById(Connection conn, long serviceId);

    long insert(Connection conn,Service service);

    void update(Connection conn, Service service);

    void deleteById(Connection conn, long serviceId);

}