package Venue_Event_Manager.repository.jdbc;

import Venue_Event_Manager.domain.model.resource.Service;
import Venue_Event_Manager.repository.ServiceRepository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Optional;

public class PgServiceRepository implements ServiceRepository {

    private final static String SQL_FIND_ALL = "SELECT id,name,description FROM service";
    private final static String SQL_FIND_BY_ID = "SELECT id,name,description FROM service WHERE id = ?";

    private final static String SQL_INSERT = "INSERT INTO service (name,description) VALUES (?,?)";

    private final static String SQL_UPDATE = "UPDATE service SET name=?, description=? WHERE id=?";

    private final static String SQL_DELETE =  "DELETE FROM service WHERE id=?";


    private static final RowMapper<Service> service_mapper = rs -> new Service(
            rs.getLong("id"),
            rs.getString("name"),
            rs.getString("description")
    );

    @Override
    public ArrayList<Service> findAll(Connection conn) {
        try(PreparedStatement ps = conn.prepareStatement(SQL_FIND_ALL)){

            ArrayList<Service> services = new ArrayList<>();

            try(ResultSet rs = ps.executeQuery()){
                while(rs.next()){
                    services.add(service_mapper.mapRow(rs));
                }
            }
            return services;
        }catch (SQLException e){
            throw new DaoException("Error while trying to find all services", e);
        }

    }

    @Override
    public Optional<Service> findById(Connection conn, long id) {
        try (PreparedStatement ps = conn.prepareStatement(SQL_FIND_BY_ID)){

            ps.setLong(1, id);

            try (ResultSet rs = ps.executeQuery()){
                if(!rs.next()) return Optional.empty();
                else return Optional.of(service_mapper.mapRow(rs));
            }

        }catch (SQLException e){
            throw new DaoException("Error while trying to find service with id: "+id, e);
        }
    }

    @Override
    public long insert(Connection conn, Service service) {
        try(PreparedStatement ps = conn.prepareStatement(SQL_INSERT)){

            ps.setString(1, service.getName());
            JdbcUtils.setNullableString(ps, 2, service.getDescription());

            try(ResultSet rs = ps.executeQuery()){
                rs.next();
                return rs.getLong(1);
            }


        }catch (SQLException e){
            throw new DaoException("Error while trying to insert service with id: "+service.getId(), e);
        }

    }

    @Override
    public void update(Connection conn, Service service) {
        try(PreparedStatement ps = conn.prepareStatement(SQL_UPDATE)){

            ps.setString(1, service.getName());
            JdbcUtils.setNullableString(ps, 2, service.getDescription());
            ps.setLong(3, service.getId());

            int updated = ps.executeUpdate();

            JdbcUtils.requireUpdatedExactly(updated,1,"update(Service="+service.toString()+")");

        }catch (SQLException e){
            throw new DaoException("Error while trying to update service: "+service.toString(), e);
        }

    }

    @Override
    public void deleteById(Connection conn, long id) {
        try(PreparedStatement ps = conn.prepareStatement(SQL_DELETE)){
            ps.setLong(1, id);

            int updated = ps.executeUpdate();

            JdbcUtils.requireUpdatedExactly(updated,1,"delete(id="+id+")");

        }catch (SQLException e){
            throw new DaoException("Error while trying to delete service with id: "+id, e);
        }

    }

}
