package Venue_Event_Manager.repository.jdbc;

import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * Static class that includes number of useful jdbc functions
 */
public class JdbcUtils {

    private JdbcUtils() {
    }

    /**
     * Sets value to statement, and correctly sets null if value == null
     * @param ps Prepared SQL Statement
     * @param index index of the variable in sql statement
     * @param value value to set
     * @throws SQLException
     */
    public static void setNullableString(PreparedStatement ps, int index, String value) throws SQLException {
        if (value == null) ps.setNull(index, Types.VARCHAR);
        else ps.setString(index, value);
    }

    /**
     * Sets value to statement, and correctly sets null if value == null
     * @param ps Prepared SQL Statement
     * @param index index of the variable in sql statement
     * @param value value to set
     * @throws SQLException
     */
    public static void setNullableInteger(PreparedStatement ps, int index, Integer value) throws SQLException {
        if(value == null) ps.setNull(index, Types.INTEGER);
        else ps.setInt(index, value);
    }

    /**
     * Sets value to statement, and correctly sets null if value == null
     * @param ps Prepared SQL Statement
     * @param index index of the variable in sql statement
     * @param value value to set
     * @throws SQLException
     */
    public static void setNullableLong(PreparedStatement ps, int index, Long value) throws SQLException {
        if(value == null) ps.setNull(index, Types.BIGINT);
        else ps.setLong(index, value);
    }

    /**
     * Sets value to statement, and correctly sets null if value == null
     * @param ps Prepared SQL Statement
     * @param index index of the variable in sql statement
     * @param value value to set
     * @throws SQLException
     */
    public static void setNullableDouble(PreparedStatement ps, int index, Double value) throws SQLException {
        if(value == null) ps.setNull(index, Types.DOUBLE);
        else ps.setDouble(index, value);
    }

    /**
     * Sets value to statement, and correctly sets null if value == null
     * @param ps Prepared SQL Statement
     * @param index index of the variable in sql statement
     * @param value value to set
     * @throws SQLException
     */
    public static void setNullableBigDecimal(PreparedStatement ps, int index, BigDecimal value) throws SQLException {
        if(value == null) ps.setNull(index, Types.NUMERIC);
        else ps.setBigDecimal(index, value);
    }

    /**
     * Sets value to statement, and correctly sets null if value == null
     * @param ps Prepared SQL Statement
     * @param index index of the variable in sql statement
     * @param value value to set
     * @throws SQLException
     */
    public static void setNullableLocalDate(PreparedStatement ps, int index, LocalDate value) throws SQLException {
        if(value == null) ps.setNull(index, Types.DATE);
        else ps.setDate(index, Date.valueOf(value));
    }

    /**
     * Sets value to statement, and correctly sets null if value == null
     * @param ps Prepared SQL Statement
     * @param index index of the variable in sql statement
     * @param value value to set
     * @throws SQLException
     */
    public static void setNullableLocalDateTime(PreparedStatement ps, int index, LocalDateTime value) throws SQLException {
        if(value == null) ps.setNull(index, Types.TIMESTAMP);
        else ps.setTimestamp(index, Timestamp.valueOf(value));
    }

    /**
     * Sets value to statement, and correctly sets null if value == null
     * @param ps Prepared SQL Statement
     * @param index index of the variable in sql statement
     * @param value value to set
     * @throws SQLException
     */
    public static void setNullableBoolean(PreparedStatement ps, int index, Boolean value) throws SQLException {
        if(value == null) ps.setNull(index, Types.BOOLEAN);
        else ps.setBoolean(index, value);
    }

    /**
     * Checks if sql operation updated an expected amount of rows
     * @param updated rows updated
     * @param expected rows expected
     * @param operation operation executed
     * @throws DaoException
     */
    public static void requireUpdatedExactly(int updated, int expected, String operation){
        if(updated != expected){
            throw new DaoException(operation + " expected " +expected+ " row(s), but updated " + updated + " row(s)");
        }

    }

    /**
     * Checks if sql operation updated an expected minimum amount of rows
     * @param updated rows updated
     * @param min rows expected
     * @param operation operation executed
     * @throws DaoException
     */
    public static void requiredUpdatedMin(int updated, int min, String operation){
        if(updated < min){
            throw new DaoException(operation + " expected at least " +min+ " row(s), but updated " + updated + " row(s)");
        }
    }

}