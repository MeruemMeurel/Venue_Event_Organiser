package Venue_Event_Manager.repository;

import Venue_Event_Manager.domain.model.resource.Service;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.Optional;

public interface ServiceRepository {


    ArrayList<Service> findAll(Connection conn);
    Optional<Service> findById(Connection conn, long id);

    long insert(Connection conn,Service service);

    void update(Connection conn, Service service);

    void deleteById(Connection conn, long id);

}
