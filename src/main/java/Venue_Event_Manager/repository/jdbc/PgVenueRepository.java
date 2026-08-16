package Venue_Event_Manager.repository.jdbc;

import Venue_Event_Manager.domain.model.venue.Venue;
import Venue_Event_Manager.domain.model.venue.Address;
import Venue_Event_Manager.repository.VenueRepository;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/** PostgreSQL repository implementation. */
public class PgVenueRepository implements VenueRepository {

    /** Creates a repository instance. */
    public PgVenueRepository() {}


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

    private final static String SQL_FIND_BY_NAME = SQL_FIND_ALL + " WHERE name ILIKE ?";
    /**
     * Executes SQL query to get venues with a matching name
     * @param conn the db connection
     * @param name the name or part of name to search
     * @return List of venues matching the given name
     */
    @Override
    public List<Venue> findByName(Connection conn, String name) {
        try (PreparedStatement ps = conn.prepareStatement(SQL_FIND_BY_NAME)) {
            ps.setString(1, "%" + name + "%");
            List<Venue> venues = new ArrayList<>();

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    venues.add(venue_mapper.mapRow(rs));
                }
            }
            return venues;
        } catch (SQLException e) {
            throw new DaoException("Error while trying to find venues with name = " + name, e);
        }
    }


    private final static String SQL_FIND_BY_CITY = SQL_FIND_ALL + " WHERE city ILIKE ?";
    /**
     * Executes SQL query to get venues in a matching city
     * @param conn the db connection
     * @param city the city or part of city to search
     * @return List of venues matching the given city
     */
    @Override
    public List<Venue> findByCity(Connection conn, String city) {
        try (PreparedStatement ps = conn.prepareStatement(SQL_FIND_BY_CITY)) {
            ps.setString(1, "%" + city + "%");
            List<Venue> venues = new ArrayList<>();

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    venues.add(venue_mapper.mapRow(rs));
                }
            }
            return venues;
        } catch (SQLException e) {
            throw new DaoException("Error while trying to find venues with city = " + city, e);
        }
    }


    private final static String SQL_FIND_BY_COUNTRY = SQL_FIND_ALL + " WHERE country ILIKE ?";
    /**
     * Executes SQL query to get venues in a matching country
     * @param conn the db connection
     * @param country the country or part of country to search
     * @return List of venues matching the given country
     */
    @Override
    public List<Venue> findByCountry(Connection conn, String country) {
        try (PreparedStatement ps = conn.prepareStatement(SQL_FIND_BY_COUNTRY)) {
            ps.setString(1, "%" + country + "%");
            List<Venue> venues = new ArrayList<>();

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    venues.add(venue_mapper.mapRow(rs));
                }
            }
            return venues;
        } catch (SQLException e) {
            throw new DaoException("Error while trying to find venues with country = " + country, e);
        }
    }

    private final static String SQL_FIND_ALL_WITH_AVAILABLE_SPACES = SQL_FIND_ALL + " v " +
            "WHERE EXISTS ( " +
            "    SELECT 1 " +
            "    FROM space s " +
            "    WHERE s.venue_id = v.id " +
            "    AND NOT EXISTS ( " +
            "        SELECT 1 " +
            "        FROM event_space es " +
            "        INNER JOIN event e ON e.id = es.event_id " +
            "        WHERE es.space_id = s.id " +
            "        AND e.status <> 'CANCELLED' " +
            "        AND e.begin_datetime < ? " +
            "        AND e.end_datetime > ? " +
            "    ) " +
            ")";
    /**
     * Executes SQL query to get venues with at least one available space in a time range
     * @param conn the db connection
     * @param begin the beginning of the time range
     * @param end the end of the time range
     * @return List of venues with at least one available space
     */
    @Override
    public List<Venue> findAllWithAvailableSpaces(Connection conn, LocalDateTime begin, LocalDateTime end) {
        try (PreparedStatement ps = conn.prepareStatement(SQL_FIND_ALL_WITH_AVAILABLE_SPACES)) {
            ps.setTimestamp(1, Timestamp.valueOf(end));
            ps.setTimestamp(2, Timestamp.valueOf(begin));
            List<Venue> venues = new ArrayList<>();

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    venues.add(venue_mapper.mapRow(rs));
                }
            }
            return venues;
        } catch (SQLException e) {
            throw new DaoException("Error while trying to find venues with available spaces", e);
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
