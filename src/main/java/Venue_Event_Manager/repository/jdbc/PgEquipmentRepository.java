package Venue_Event_Manager.repository.jdbc;

import Venue_Event_Manager.domain.model.resource.Equipment;
import Venue_Event_Manager.repository.EquipmentRepository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Optional;

public class PgEquipmentRepository implements EquipmentRepository {

    private final static String SQL_FIND_ALL = "SELECT id,venue_id,name,description,total_quantity FROM equipment";
    private final static String SQL_FIND_BY_ID = "SELECT id,venue_id,name,description,total_quantity FROM equipment WHERE id = ?";
    private final static String SQL_FIND_BY_VENUE = "SELECT id,venue_id,name,description,total_quantity FROM equipment WHERE venue_id = ?";

    private final static String SQL_INSERT = "INSERT INTO equipment (venue_id,name,description,total_quantity) VALUES (?,?,?,?)";

    private final static String SQL_UPDATE = "UPDATE equipment SET venueId=?, name=?, description=?, total_quantity=? WHERE id=?";

    private final static String SQL_DELETE =  "DELETE FROM equipment WHERE id=?";


    private static final RowMapper<Equipment> equipment_mapper = rs -> new Equipment(
            rs.getLong("id"),
            rs.getLong("venue_id"),
            rs.getString("name"),
            rs.getString("description"),
            rs.getInt("total_quantity")
    );

    @Override
    public ArrayList<Equipment> findAll(Connection conn) {
        try(PreparedStatement ps = conn.prepareStatement(SQL_FIND_ALL)){

            ArrayList<Equipment> equipments = new ArrayList<>();

            try(ResultSet rs = ps.executeQuery()){
                while(rs.next()){
                    equipments.add(equipment_mapper.mapRow(rs));
                }
            }
            return equipments;
        }catch (SQLException e){
            throw new DaoException("Error while trying to find all equipments", e);
        }

    }

    @Override
    public Optional<Equipment> findById(Connection conn, long id) {
        try (PreparedStatement ps = conn.prepareStatement(SQL_FIND_BY_ID)){

            ps.setLong(1, id);

            try (ResultSet rs = ps.executeQuery()){
                if(!rs.next()) return Optional.empty();
                else return Optional.of(equipment_mapper.mapRow(rs));
            }

        }catch (SQLException e){
            throw new DaoException("Error while trying to find equipment with id: "+id, e);
        }
    }

    @Override
    public ArrayList<Equipment> findByVenue(Connection conn, long venueId) {
        try (PreparedStatement ps = conn.prepareStatement(SQL_FIND_BY_VENUE)) {

            ps.setLong(1, venueId);

            ArrayList<Equipment> equipments = new ArrayList<>();

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    equipments.add(equipment_mapper.mapRow(rs));
                }
            }
            return equipments;
        } catch (SQLException e) {
            throw new DaoException("Error while trying to find equipments from venue with id: " + venueId, e);
        }
    }

    @Override
    public long insert(Connection conn, Equipment equipment) {
        try(PreparedStatement ps = conn.prepareStatement(SQL_INSERT)){

            ps.setLong(1, equipment.getVenueId());
            ps.setString(2, equipment.getName());
            JdbcUtils.setNullableString(ps, 3, equipment.getDescription());

            try(ResultSet rs = ps.executeQuery()){
                rs.next();
                return rs.getLong(1);
            }


        }catch (SQLException e){
            throw new DaoException("Error while trying to insert equipment with id: "+equipment.getId(), e);
        }

    }

    @Override
    public void update(Connection conn, Equipment equipment) {
        try(PreparedStatement ps = conn.prepareStatement(SQL_UPDATE)){

            ps.setLong(1, equipment.getVenueId());
            ps.setString(2, equipment.getName());
            JdbcUtils.setNullableString(ps, 3, equipment.getDescription());
            ps.setInt(4, equipment.getTotal_quantity());
            ps.setLong(4, equipment.getId());

            int updated = ps.executeUpdate();

            JdbcUtils.requireUpdatedExactly(updated,1,"update(Equipment="+equipment.toString()+")");

        }catch (SQLException e){
            throw new DaoException("Error while trying to update equipment: "+equipment.toString(), e);
        }

    }

    @Override
    public void deleteById(Connection conn, long id) {
        try(PreparedStatement ps = conn.prepareStatement(SQL_DELETE)){
            ps.setLong(1, id);

            int updated = ps.executeUpdate();

            JdbcUtils.requireUpdatedExactly(updated,1,"delete(id="+id+")");

        }catch (SQLException e){
            throw new DaoException("Error while trying to delete equipment with id: "+id, e);
        }

    }
}
