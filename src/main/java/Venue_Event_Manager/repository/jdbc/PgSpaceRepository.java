package Venue_Event_Manager.repository.jdbc;

import Venue_Event_Manager.domain.model.resource.Space;
import Venue_Event_Manager.repository.SpaceRepository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Optional;

public class PgSpaceRepository implements SpaceRepository {

    private final static String SQL_FIND_ALL = "SELECT id,venue_id,name,description FROM space";
    private final static String SQL_FIND_BY_ID = "SELECT id,venue_id,name,description FROM space WHERE id = ?";
    private final static String SQL_FIND_BY_VENUE = "SELECT id,venue_id,name,description FROM space WHERE VENUE = ?";

    private final static String SQL_INSERT = "INSERT INTO space (venue_id,name,description) VALUES (?,?,?)";

    private final static String SQL_UPDATE = "UPDATE space SET venueId=?, name=?, description=? WHERE id=?";

    private final static String SQL_DELETE =  "DELETE FROM space WHERE id=?";


    private static final RowMapper<Space> space_mapper = rs -> new Space(
            rs.getLong("id"),
            rs.getLong("venue_id"),
            rs.getString("name"),
            rs.getString("description")
    );

    @Override
    public ArrayList<Space> findAll(Connection conn) {
        try(PreparedStatement ps = conn.prepareStatement(SQL_FIND_ALL)){

            ArrayList<Space> spaces = new ArrayList<>();

            try(ResultSet rs = ps.executeQuery()){
                while(rs.next()){
                    spaces.add(space_mapper.mapRow(rs));
                }
            }
            return spaces;
        }catch (SQLException e){
            throw new DaoException("Error while trying to find all spaces", e);
        }

    }

    @Override
    public Optional<Space> findById(Connection conn, long id) {
        try (PreparedStatement ps = conn.prepareStatement(SQL_FIND_BY_ID)){

            ps.setLong(1, id);

            try (ResultSet rs = ps.executeQuery()){
                if(!rs.next()) return Optional.empty();
                else return Optional.of(space_mapper.mapRow(rs));
            }

        }catch (SQLException e){
            throw new DaoException("Error while trying to find space with id: "+id, e);
        }
    }

    @Override
    public ArrayList<Space> findByVenue(Connection conn, long venueId) {
        try (PreparedStatement ps = conn.prepareStatement(SQL_FIND_BY_VENUE)) {

            ps.setLong(1, venueId);

            ArrayList<Space> spaces = new ArrayList<>();

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    spaces.add(space_mapper.mapRow(rs));
                }
            }
            return spaces;
        } catch (SQLException e) {
            throw new DaoException("Error while trying to find spaces from venue with id: " + venueId, e);
        }
    }

    @Override
    public long insert(Connection conn, Space space) {
        try(PreparedStatement ps = conn.prepareStatement(SQL_INSERT)){

            ps.setLong(1, space.getVenueId());
            ps.setString(2, space.getName());
            JdbcUtils.setNullableString(ps, 3, space.getDescription());

            try(ResultSet rs = ps.executeQuery()){
                rs.next();
                return rs.getLong(1);
            }


        }catch (SQLException e){
            throw new DaoException("Error while trying to insert space with id: "+space.getId(), e);
        }

    }

    @Override
    public void update(Connection conn, Space space) {
        try(PreparedStatement ps = conn.prepareStatement(SQL_UPDATE)){

            ps.setLong(1, space.getVenueId());
            ps.setString(2, space.getName());
            ps.setString(3, space.getDescription());
            ps.setLong(4, space.getId());

            int updated = ps.executeUpdate();

            JdbcUtils.requireUpdatedExactly(updated,1,"update(Space="+space.toString()+")");

        }catch (SQLException e){
            throw new DaoException("Error while trying to update space: "+space.toString(), e);
        }

    }

    @Override
    public void deleteById(Connection conn, long id) {
        try(PreparedStatement ps = conn.prepareStatement(SQL_DELETE)){
            ps.setLong(1, id);

            int updated = ps.executeUpdate();

            JdbcUtils.requireUpdatedExactly(updated,1,"delete(id="+id+")");

        }catch (SQLException e){
            throw new DaoException("Error while trying to delete space with id: "+id, e);
        }

    }
}
