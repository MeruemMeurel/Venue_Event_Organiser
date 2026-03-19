package Venue_Event_Manager.repository.jdbc;

import Venue_Event_Manager.domain.model.resource.Service;
import Venue_Event_Manager.repository.ServiceRepository;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class PgServiceRepository implements ServiceRepository {

    /**
     * Lambda function to implement RowMapper interface
     */
    private static final RowMapper<Service> service_mapper = rs -> new Service(
            rs.getLong("id"),
            rs.getString("name"),
            rs.getString("description")
    );


    private final static String SQL_FIND_ALL = "SELECT id, name, description " +
                                               "FROM service";
    /**
     * Executes query to database to get all Services
     * @param conn The database connection used
     * @return List<Service> object
     */
    @Override
    public List<Service> findAll(Connection conn) {
        List<Service> services = new ArrayList<>();
        try(PreparedStatement ps = conn.prepareStatement(SQL_FIND_ALL)){
            try(ResultSet rs = ps.executeQuery()){
                while(rs.next()){
                    services.add(service_mapper.mapRow(rs));
                }
            }
            return services;
        } catch (SQLException e){
            throw new DaoException("Error while trying to find all services", e);
        }
    }


    private final static String SQL_FIND_BY_ID = SQL_FIND_ALL + " WHERE id = ?";
    /**
     * Executes query to database to get Service from its id
     * @param conn The database connection used
     * @param serviceId the id of the service
     * @return Optional<Service> object. Empty if not found
     */
    @Override
    public Optional<Service> findById(Connection conn, long serviceId) {
        try (PreparedStatement ps = conn.prepareStatement(SQL_FIND_BY_ID)){
            ps.setLong(1, serviceId);
            try (ResultSet rs = ps.executeQuery()){
                if(!rs.next()) return Optional.empty();
                return Optional.of(service_mapper.mapRow(rs));
            }
        } catch (SQLException e){
            throw new DaoException("Error while trying to find service with id: " + serviceId, e);
        }
    }

    private final static String SQL_SEARCH_BY_NAME = SQL_FIND_ALL + " WHERE LOWER(name) LIKE LOWER(?)";
    /**
     * Searches in database a name like the parameter name
     * @param conn the db connection
     * @param name the name to search
     * @return List<Service> results of query
     * @throws DaoException daoException
     */
    @Override
    public List<Service> searchByName(Connection conn, String name) {
        try(PreparedStatement ps = conn.prepareStatement(SQL_SEARCH_BY_NAME)){
            ps.setString(1, "%" + name + "%");
            ArrayList<Service> services = new ArrayList<>();

            try (ResultSet rs = ps.executeQuery()){
                while(rs.next()){
                    services.add(service_mapper.mapRow(rs));
                }
            }
            return services;
        }catch (SQLException e) {
            throw new DaoException("Error while trying to find services with name: " + name, e);
        }
    }


    private final static String SQL_INSERT = "INSERT INTO service (name, description) " +
                                             "VALUES (?, ?) RETURNING id";
    /**
     * Executes SQL Query to insert a new service to database
     * @param conn the connection to database
     * @param service the service object to insert
     * @return long id of the new service created
     */
    @Override
    public long insert(Connection conn, Service service) {
        try(PreparedStatement ps = conn.prepareStatement(SQL_INSERT)){
            ps.setString(1, service.getName());
            JdbcUtils.setNullableString(ps, 2, service.getDescription());

            try(ResultSet rs = ps.executeQuery()){
                if (rs.next()) return rs.getLong(1);
                throw new DaoException("Insert failed: no ID returned for service");
            }
        } catch (SQLException e){
            throw new DaoException("Error while trying to insert service: " + service.getName(), e);
        }
    }


    private final static String SQL_UPDATE = "UPDATE service " +
                                             "SET name = ?, description = ? " +
                                             "WHERE id = ?";
    /**
     * Executes SQL Query to update an existing service's information
     * @param conn the connection to database
     * @param service the service object with updated data
     */
    @Override
    public void update(Connection conn, Service service) {
        try(PreparedStatement ps = conn.prepareStatement(SQL_UPDATE)){
            ps.setString(1, service.getName());
            JdbcUtils.setNullableString(ps, 2, service.getDescription());
            ps.setLong(3, service.getId());

            int updated = ps.executeUpdate();
            JdbcUtils.requireUpdatedExactly(updated, 1, "updateService(id=" + service.getId() + ")");
        } catch (SQLException e){
            throw new DaoException("Error while trying to update service: " + service.getName(), e);
        }
    }


    private final static String SQL_DELETE = "DELETE FROM service " +
                                             "WHERE id = ?";
    /**
     * Deletes a service from database by its id
     * @param conn the database connection
     * @param serviceId the id of the service to delete
     */
    @Override
    public void deleteById(Connection conn, long serviceId) {
        try(PreparedStatement ps = conn.prepareStatement(SQL_DELETE)){
            ps.setLong(1, serviceId);

            int updated = ps.executeUpdate();
            JdbcUtils.requireUpdatedExactly(updated, 1, "deleteService(id=" + serviceId + ")");
        } catch (SQLException e){
            throw new DaoException("Error while trying to delete service with id: " + serviceId, e);
        }
    }
}