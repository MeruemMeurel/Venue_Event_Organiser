package Venue_Event_Manager.repository.jdbc;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Functional interface to map a result row from sql to T object.
 * Allows lambda function usage
 */
@FunctionalInterface
public interface RowMapper<T> {
    T mapRow(ResultSet rs) throws SQLException;;
}
