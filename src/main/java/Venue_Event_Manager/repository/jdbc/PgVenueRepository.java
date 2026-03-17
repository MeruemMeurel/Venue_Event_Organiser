package Venue_Event_Manager.repository.jdbc;

import Venue_Event_Manager.domain.model.venue.Venue;
import Venue_Event_Manager.domain.model.venue.Address;
import Venue_Event_Manager.repository.VenueRepository;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class PgVenueRepository implements VenueRepository {

    /**
     * Lambda function to map a single row of venue (including address columns)
     */
    private static final RowMapper<Venue> venue_mapper = rs -> {
        Address address = new Address(
                rs.getString("street"),
                rs.getString("street_number"),
                rs.getString("city"),
                rs.getString("postal_code"),
                rs.getString("country"),
                rs.getString("additional_info")
        );
        return new Venue(
                rs.getLong("id"),
                rs.getString("name"),
                rs.getString("description"),
                address
        );
    };

    private final static String SQL_FIND_ALL = "SELECT id, name, description, street, street_number, city, postal_code, " +
                                                      "country, additional_info " +
                                               "FROM venue";
    /**
     * Executes SQL query to get all venues
     * @param conn the db connection
     * @return List of venues
     */
    @Override
    public List<Venue> findAll(Connection conn) {
        try (PreparedStatement ps = conn.prepareStatement(SQL_FIND_ALL)) {
            List<Venue> venues = new ArrayList<>();

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    venues.add(venue_mapper.mapRow(rs));
                }
            }
            return venues;
        } catch (SQLException e) {
            throw new DaoException("Error while trying to find all venues", e);
        }
    }


    private final static String SQL_FIND_BY_ID = SQL_FIND_ALL + " WHERE id = ?";
    /**
     * Executes SQL query to get a specific venue
     * @param conn the db connection
     * @param venueId the id to find
     * @return Optional containing the Venue if found
     */
    @Override
    public Optional<Venue> findById(Connection conn, long venueId) {
        try (PreparedStatement ps = conn.prepareStatement(SQL_FIND_BY_ID)) {
            ps.setLong(1, venueId);

            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return Optional.empty();
                return Optional.of(venue_mapper.mapRow(rs));
            }
        } catch (SQLException e) {
            throw new DaoException("Error while trying to find venue id = " + venueId, e);
        }
    }


    private final static String SQL_INSERT = "INSERT INTO venue (name, description, street, street_number, city, " +
                                                                "postal_code, country, additional_info) " +
                                             "VALUES (?, ?, ?, ?, ?, ?, ?, ?) RETURNING id";
    /**
     * Executes SQL query to insert a new venue with inline address data
     * @param conn the db connection
     * @param venue the venue object to persist
     * @return the generated venue ID
     */
    @Override
    public long insert(Connection conn, Venue venue) {
        try (PreparedStatement ps = conn.prepareStatement(SQL_INSERT)) {
            Address a = venue.getAddress();
            ps.setString(1, venue.getName());
            ps.setString(2, venue.getDescription());
            ps.setString(3, a.street());
            ps.setString(4, a.street_number());
            ps.setString(5, a.city());
            ps.setString(6, a.postal_code());
            ps.setString(7, a.country());
            JdbcUtils.setNullableString(ps, 8, a.additional_info());

            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                return rs.getLong(1);
            }
        } catch (SQLException e) {
            throw new DaoException("Error while trying to insert venue: " + venue.getName(), e);
        }
    }


    private final static String SQL_UPDATE = "UPDATE venue " +
                                             "SET name = ?, description = ?, street = ?, street_number = ?, city = ?, " +
                                                 "postal_code = ?, country = ?, additional_info = ? " +
                                             "WHERE id = ?";
    /**
     * Executes SQL query to update venue and address details in a single operation
     * @param conn the db connection
     * @param venue the updated venue object
     */
    @Override
    public void update(Connection conn, Venue venue) {
        try (PreparedStatement ps = conn.prepareStatement(SQL_UPDATE)) {
            Address a = venue.getAddress();
            ps.setString(1, venue.getName());
            ps.setString(2, venue.getDescription());
            ps.setString(3, a.street());
            ps.setString(4, a.street_number());
            ps.setString(5, a.city());
            ps.setString(6, a.postal_code());
            ps.setString(7, a.country());
            JdbcUtils.setNullableString(ps, 8, a.additional_info());
            ps.setLong(9, venue.getId());

            int updated = ps.executeUpdate();
            JdbcUtils.requireUpdatedExactly(updated, 1, "update(venue_id=" + venue.getId() + ")");
        } catch (SQLException e) {
            throw new DaoException("Error while trying to update venue: " + venue.getName(), e);
        }
    }


    private final static String SQL_DELETE = "DELETE FROM venue " +
                                             "WHERE id = ?";
    /**
     * Executes SQL query to delete a venue
     * @param conn the db connection
     * @param venueId the id of the venue to remove
     */
    @Override
    public void deleteById(Connection conn, long venueId) {
        try (PreparedStatement ps = conn.prepareStatement(SQL_DELETE)) {
            ps.setLong(1, venueId);

            int deleted = ps.executeUpdate();
            JdbcUtils.requireUpdatedExactly(deleted, 1, "delete(venue_id=" + venueId + ")");
        } catch (SQLException e) {
            throw new DaoException("Error while trying to delete venue id = " + venueId, e);
        }
    }
}