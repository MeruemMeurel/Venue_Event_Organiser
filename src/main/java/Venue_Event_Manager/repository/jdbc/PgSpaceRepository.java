package Venue_Event_Manager.repository.jdbc;

import Venue_Event_Manager.domain.model.resource.Space;
import Venue_Event_Manager.repository.SpaceRepository;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class PgSpaceRepository implements SpaceRepository {

    /**
     * Lambda function to implement RowMapper interface
     */
    private static final RowMapper<Space> space_mapper = rs -> new Space(
            rs.getLong("id"),
            rs.getLong("venue_id"),
            rs.getString("name"),
            rs.getString("description")
    );


    private final static String SQL_FIND_ALL = "SELECT id, venue_id, name, description " +
                                               "FROM space";
    /**
     * Executes query to database to get all Spaces
     * @param conn The database connection used
     * @return {@code List<Space>} object
     */
    @Override
    public List<Space> findAll(Connection conn) {
        List<Space> spaces = new ArrayList<>();
        try(PreparedStatement ps = conn.prepareStatement(SQL_FIND_ALL)){

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


    private final static String SQL_FIND_BY_ID = SQL_FIND_ALL + " WHERE id = ?";
    /**
     * Executes query to database to get Space from its id
     * @param conn The database connection used
     * @param spaceId the id of the space
     * @return {@code Optional<Space>} object. Empty if not found
     */
    @Override
    public Optional<Space> findById(Connection conn, long spaceId) {
        try (PreparedStatement ps = conn.prepareStatement(SQL_FIND_BY_ID)){
            ps.setLong(1, spaceId);

            try (ResultSet rs = ps.executeQuery()){
                if(!rs.next()) return Optional.empty();
                return Optional.of(space_mapper.mapRow(rs));
            }
        }catch (SQLException e){
            throw new DaoException("Error while trying to find space with id: " + spaceId, e);
        }
    }


    private final static String SQL_FIND_ALL_BY_VENUE = SQL_FIND_ALL + " WHERE venue_id = ?";
    /**
     * Executes query to database to get all Spaces belonging to a venue
     * @param conn The database connection used
     * @param venueId the id of the venue
     * @return {@code List<Space>} object
     */
    @Override
    public List<Space> findAllByVenueId(Connection conn, long venueId) {
        List<Space> spaces = new ArrayList<>();
        try (PreparedStatement ps = conn.prepareStatement(SQL_FIND_ALL_BY_VENUE)) {
            ps.setLong(1, venueId);

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

    private final static String SQL_SEARCH_BY_NAME = SQL_FIND_ALL + " WHERE LOWER(name) LIKE LOWER(?)";
    /**
     * Searches in database a name like the parameter name
     * @param conn the db connection
     * @param name the name to search
     * @return {@code List<Space>} results of query
     * @throws DaoException daoException
     */
    @Override
    public List<Space> searchByName(Connection conn, String name) {
        try(PreparedStatement ps = conn.prepareStatement(SQL_SEARCH_BY_NAME)){
            ps.setString(1, "%" + name + "%");
            ArrayList<Space> spaces = new ArrayList<>();

            try (ResultSet rs = ps.executeQuery()){
                while(rs.next()){
                    spaces.add(space_mapper.mapRow(rs));
                }
            }
            return spaces;
        }catch (SQLException e) {
            throw new DaoException("Error while trying to find spaces with name: " + name, e);
        }
    }

    private final static String SQL_INSERT = "INSERT INTO space (venue_id, name, description) " +
                                             "VALUES (?, ?, ?) RETURNING id";
    /**
     * Executes SQL Query to insert a new space to database
     * @param conn the connection to database
     * @param space the space object to insert
     * @return long id of the new space created
     */
    @Override
    public long insert(Connection conn, Space space) {
        try(PreparedStatement ps = conn.prepareStatement(SQL_INSERT)){
            ps.setLong(1, space.getVenueId());
            ps.setString(2, space.getName());
            JdbcUtils.setNullableString(ps, 3, space.getDescription());

            try(ResultSet rs = ps.executeQuery()){
                if (rs.next()) return rs.getLong(1);
                throw new DaoException("Insert failed: no ID returned for space");
            }
        }catch (SQLException e){
            throw new DaoException("Error while trying to insert space: " + space.getName(), e);
        }
    }


    private final static String SQL_UPDATE = "UPDATE space " +
                                             "SET venue_id = ?, name = ?, description = ? " +
                                             "WHERE id = ?";
    /**
     * Executes SQL Query to update an existing space's information
     * @param conn the connection to database
     * @param space the space object with updated data
     */
    @Override
    public void update(Connection conn, Space space) {
        try(PreparedStatement ps = conn.prepareStatement(SQL_UPDATE)){
            ps.setLong(1, space.getVenueId());
            ps.setString(2, space.getName());
            JdbcUtils.setNullableString(ps, 3, space.getDescription());
            ps.setLong(4, space.getId());

            int updated = ps.executeUpdate();
            JdbcUtils.requireUpdatedExactly(updated, 1, "updateSpace(id=" + space.getId() + ")");
        }catch (SQLException e){
            throw new DaoException("Error while trying to update space: " + space.getName(), e);
        }
    }


    private final static String SQL_DELETE = "DELETE FROM space " +
                                             "WHERE id = ?";
    /**
     * Deletes a space from database by its id
     * @param conn the database connection
     * @param id the id of the space to delete
     */
    @Override
    public void deleteById(Connection conn, long id) {
        try(PreparedStatement ps = conn.prepareStatement(SQL_DELETE)){
            ps.setLong(1, id);

            int updated = ps.executeUpdate();
            JdbcUtils.requireUpdatedExactly(updated, 1, "deleteSpace(id=" + id + ")");
        }catch (SQLException e){
            throw new DaoException("Error while trying to delete space with id: " + id, e);
        }
    }

    private final static String SQL_FIND_AVAILABLE_SPACE = "SELECT * FROM space " +
                                                     "WHERE venue_id = ? " +
                                                     "AND id NOT IN " +
                                                     "(SELECT space_id FROM event_space " +
                                                     "JOIN event ON event_space.event_id = event.id " +
                                                     "WHERE event.begin_datetime < ? AND event.end_datetime > ?)";

    /**
     * Executes SQL query to database to find all available spaces in a venue within a specific time interval
     * @param conn the database connection
     * @param venueId the id of the venue to search in
     * @param begin the start time of the interval
     * @param end the end time of the interval
     * @return {@code List<Space>} object containing available spaces
     */
    @Override
    public List<Space> findAvailableSpaces(Connection conn, long venueId, LocalDateTime begin, LocalDateTime end) {
        List<Space> spaces = new ArrayList<>();

        try (PreparedStatement ps = conn.prepareStatement(SQL_FIND_AVAILABLE_SPACE)) {
            ps.setLong(1, venueId);
            ps.setTimestamp(2, java.sql.Timestamp.valueOf(end));
            ps.setTimestamp(3, java.sql.Timestamp.valueOf(begin));

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    spaces.add(space_mapper.mapRow(rs));
                }
            }
            return spaces;


        } catch (SQLException e) {
            throw new DaoException("Error while trying to find available " +
                    "spaces from venue with id: " + venueId, e);
        }
    }
}
