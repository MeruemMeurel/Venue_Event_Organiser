package venue.event.manager.repository.jdbc;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Functional interface to map a result row from sql to T object.
 * Allows lambda function usage
 *
 * @param <T> mapped domain type
 */
@FunctionalInterface
public interface RowMapper<T> {
    /**
     * Maps the current row of a result set.
     *
     * @param rs result set positioned on the row to map
     * @return mapped object
     * @throws SQLException if a column cannot be read
     */
    T mapRow(ResultSet rs) throws SQLException;
}
