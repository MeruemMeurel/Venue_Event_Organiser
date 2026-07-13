package Venue_Event_Manager.repository.jdbc;

import Venue_Event_Manager.domain.model.resource.Equipment;
import Venue_Event_Manager.repository.EquipmentRepository;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class PgEquipmentRepository implements EquipmentRepository {

    /**
     * Lambda function to implement RowMapper interface
     */
    private static final RowMapper<Equipment> equipment_mapper = rs -> {
        Long venue_id=rs.getLong("venue_id");
        return new Equipment(
                rs.getLong("id"),
                venue_id,
                rs.getString("name"),
                rs.getString("description"),
                rs.getInt("total_quantity")
        );
    };

    private final static String SQL_FIND_ALL = "SELECT id, venue_id, name, description, total_quantity " +
                                               "FROM equipment";
    /**
     * Executes query to database to get all Equipments
     * @param conn The database connection used
     * @return List<Equipment> object
     */
    @Override
    public List<Equipment> findAll(Connection conn) {
        List<Equipment> equipments = new ArrayList<>();
        try(PreparedStatement ps = conn.prepareStatement(SQL_FIND_ALL)){

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


    private final static String SQL_FIND_BY_ID = SQL_FIND_ALL + " WHERE id = ?";
    /**
     * Executes query to database to get Equipment from its id
     * @param conn The database connection used
     * @param equipmentId the id of the equipment
     * @return Optional<Equipment> object. Empty if not found
     */
    @Override
    public Optional<Equipment> findById(Connection conn, long equipmentId) {
        try (PreparedStatement ps = conn.prepareStatement(SQL_FIND_BY_ID)){
            ps.setLong(1, equipmentId);

            try (ResultSet rs = ps.executeQuery()){
                if(!rs.next()) return Optional.empty();
                return Optional.of(equipment_mapper.mapRow(rs));
            }
        }catch (SQLException e){
            throw new DaoException("Error while trying to find equipment with id: " + equipmentId, e);
        }
    }


    private final static String SQL_FIND_ALL_BY_VENUE = SQL_FIND_ALL + " WHERE venue_id = ?";
    /**
     * Executes query to database to get all Equipments belonging to a venue
     * @param conn The database connection used
     * @param venueId the id of the venue
     * @return List<Equipment> object
     */
    @Override
    public List<Equipment> findAllByVenueId(Connection conn, long venueId) {
        List<Equipment> equipments = new ArrayList<>();
        try (PreparedStatement ps = conn.prepareStatement(SQL_FIND_ALL_BY_VENUE)) {
            ps.setLong(1, venueId);

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

    private final static String SQL_SEARCH_BY_NAME = SQL_FIND_ALL + " WHERE LOWER(name) LIKE LOWER(?)";
    /**
     * Searches in database a name like the parameter name
     * @param conn the db connection
     * @param name the name to search
     * @return List<Equipment> results of query
     * @throws DaoException daoException
     */
    @Override
    public List<Equipment> searchByName(Connection conn, String name) {
        try(PreparedStatement ps = conn.prepareStatement(SQL_SEARCH_BY_NAME)){
            ps.setString(1, "%" + name + "%");
            ArrayList<Equipment> equipments = new ArrayList<>();

            try (ResultSet rs = ps.executeQuery()){
                while(rs.next()){
                    equipments.add(equipment_mapper.mapRow(rs));
                }
            }
            return equipments;
        }catch (SQLException e) {
            throw new DaoException("Error while trying to find equipments with name: " + name, e);
        }
    }


    private final static String SQL_INSERT = "INSERT INTO equipment (venue_id, name, description, total_quantity) " +
                                             "VALUES (?, ?, ?, ?) RETURNING id";
    /**
     * Executes SQL Query to insert a new equipment to database
     * @param conn the connection to database
     * @param equipment the equipment object to insert
     * @return long id of the new equipment created
     */
    @Override
    public long insert(Connection conn, Equipment equipment) {
        try(PreparedStatement ps = conn.prepareStatement(SQL_INSERT)){
            ps.setLong(1, equipment.getVenueId());
            ps.setString(2, equipment.getName());
            JdbcUtils.setNullableString(ps, 3, equipment.getDescription());
            ps.setInt(4, equipment.getTotalQuantity()); // Assicurati che il metodo si chiami così nel domain

            try(ResultSet rs = ps.executeQuery()){
                if (rs.next()) return rs.getLong(1);
                throw new DaoException("Insert failed: no ID returned for equipment");
            }
        }catch (SQLException e){
            throw new DaoException("Error while trying to insert equipment: " + equipment.getName(), e);
        }
    }


    private final static String SQL_UPDATE = "UPDATE equipment " +
                                             "SET venue_id = ?, name = ?, description = ?, total_quantity = ? " +
                                             "WHERE id = ?";
    /**
     * Executes SQL Query to update an existing equipment's information
     * @param conn the connection to database
     * @param equipment the equipment object with updated data
     */
    @Override
    public void update(Connection conn, Equipment equipment) {
        try(PreparedStatement ps = conn.prepareStatement(SQL_UPDATE)){
            ps.setLong(1, equipment.getVenueId());
            ps.setString(2, equipment.getName());
            JdbcUtils.setNullableString(ps, 3, equipment.getDescription());
            ps.setInt(4, equipment.getTotalQuantity());
            ps.setLong(5, equipment.getId()); // Corretto l'indice da 4 a 5

            int updated = ps.executeUpdate();
            JdbcUtils.requireUpdatedExactly(updated, 1, "updateEquipment(id=" + equipment.getId() + ")");
        }catch (SQLException e){
            throw new DaoException("Error while trying to update equipment: " + equipment.getName(), e);
        }
    }


    private final static String SQL_DELETE = "DELETE FROM equipment " +
                                             "WHERE id = ? ";
    /**
     * Deletes an equipment record from database by its id
     * @param conn the database connection
     * @param equipmentId the id of the equipment to delete
     */
    @Override
    public void deleteById(Connection conn, long equipmentId) {
        try(PreparedStatement ps = conn.prepareStatement(SQL_DELETE)){
            ps.setLong(1, equipmentId);

            int updated = ps.executeUpdate();
            JdbcUtils.requireUpdatedExactly(updated, 1, "deleteEquipment(id=" + equipmentId + ")");
        }catch (SQLException e){
            throw new DaoException("Error while trying to delete equipment with id: " + equipmentId, e);
        }
    }

    private final static String SQL_FIND_AVAILABLE_EQUIPMENT = "SELECT * " +
                                                     "FROM equipment e " +
                                                     "WHERE e.venue_id = ? " +
                                                     "AND e.total_quantity > COALESCE( " +
                                                     "(SELECT SUM(ee.quantity) " +
                                                     "FROM event_equipment ee " +
                                                     "JOIN event ev On ee.event_id = ev.id " +
                                                     "WHERE ee.equipment_id = e.id " +
                                                        "AND ev.begin_datetime < ? AND ev.end_datetime > ?),"+
                                                        "0 " +
                                                     ") ";

    /**
     * Executes SQL query to database to find all available equipments in a venue within a specific time interval
     * @param conn the databaseconnection
     * @param venueId the id of the venue to search in
     * @param begin the start time of the interval
     * @param end the end time of the interval
     * @return List<Equipment> object containing available equipments
     */
    @Override
    public List<Equipment> findAvailableEquipment(Connection conn, long venueId, LocalDateTime begin, LocalDateTime end) {
        List<Equipment> equipments = new ArrayList<>();

        try (PreparedStatement ps = conn.prepareStatement(SQL_FIND_AVAILABLE_EQUIPMENT)){
            ps.setLong(1,venueId);
            ps.setTimestamp(2, java.sql.Timestamp.valueOf(end));
            ps.setTimestamp(3, java.sql.Timestamp.valueOf(begin));

            try (ResultSet rs = ps.executeQuery()){
                while (rs.next()){
                    equipments.add(equipment_mapper.mapRow(rs));
                }
            }
            return equipments;

        }catch (SQLException e){
            throw new DaoException("Error while trying to find available " +
                    "equipment from venue with id: " + venueId, e);
        }
    }
}