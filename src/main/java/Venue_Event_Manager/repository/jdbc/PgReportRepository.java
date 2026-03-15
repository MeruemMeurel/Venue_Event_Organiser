package Venue_Event_Manager.repository.jdbc;

import Venue_Event_Manager.domain.model.feedback.Report;
import Venue_Event_Manager.domain.model.feedback.ReportSeverity;
import Venue_Event_Manager.repository.ReportRepository;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class PgReportRepository implements ReportRepository {

    /**
     * Lambda function to map report sql results to a Report object
     */
    private static final RowMapper<Report> report_mapper = rs -> {
        Long event_id = rs.getLong("event_id");

        return new Report(
                rs.getLong("id"),
                rs.getLong("user_id"),
                rs.getLong("admin_id"),
                event_id != null ? event_id : null,
                ReportSeverity.valueOf(rs.getString("severity").toUpperCase()),
                rs.getString("comment"),
                rs.getTimestamp("created_at").toLocalDateTime()
        );
    };


    private static final String SQL_FIND_ALL = "SELECT id, user_id, admin_id, event_id, severity, comment, created_at " +
                                               "FROM report";
    /**
     * Executes SQL query to get all reports
     * @param conn the connection to db
     * @return List of reports
     */
    @Override
    public List<Report> findAll(Connection conn) {
        try (PreparedStatement ps = conn.prepareStatement(SQL_FIND_ALL)) {
            List<Report> reports = new ArrayList<>();

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    reports.add(report_mapper.mapRow(rs));
                }
            }
            return reports;
        } catch (SQLException e) {
            throw new DaoException("Error while trying to find all reports", e);
        }
    }


    private static final String SQL_FIND_BY_ID = SQL_FIND_ALL + " WHERE id = ?";
    /**
     * Executes SQL query to get report with specific id
     * @param conn the db connection
     * @param reportId the id to find
     * @return Optional object containing the Report if found
     */
    @Override
    public Optional<Report> findById(Connection conn, long reportId) {
        try (PreparedStatement ps = conn.prepareStatement(SQL_FIND_BY_ID)) {
            ps.setLong(1, reportId);

            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return Optional.empty();
                return Optional.of(report_mapper.mapRow(rs));
            }
        } catch (SQLException e) {
            throw new DaoException("Error while trying to find report with id = " + reportId, e);
        }
    }


    private static final String SQL_FIND_BY_USER_ID = SQL_FIND_ALL + " WHERE user_id = ?";
    /**
     * Executes SQL query to get all reports from a specific user
     * @param conn the db connection
     * @param userId the user id
     * @return List of reports
     */
    @Override
    public List<Report> findAllByUserId(Connection conn, long userId) {
        try (PreparedStatement ps = conn.prepareStatement(SQL_FIND_BY_USER_ID)) {
            ps.setLong(1, userId);
            List<Report> reports = new ArrayList<>();

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    reports.add(report_mapper.mapRow(rs));
                }
            }
            return reports;
        } catch (SQLException e) {
            throw new DaoException("Error while trying to find reports for user id = " + userId, e);
        }
    }


    private static final String SQL_FIND_BY_ADMIN_ID = SQL_FIND_ALL + " WHERE admin_id = ?";
    /**
     * Executes SQL query to get all reports created by a specific admin
     * @param conn the db connection
     * @param adminId the admin id
     * @return List of reports
     */
    @Override
    public List<Report> findAllByAdminId(Connection conn, long adminId) {
        try (PreparedStatement ps = conn.prepareStatement(SQL_FIND_BY_ADMIN_ID)) {
            ps.setLong(1, adminId);
            List<Report> reports = new ArrayList<>();

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    reports.add(report_mapper.mapRow(rs));
                }
            }
            return reports;
        } catch (SQLException e) {
            throw new DaoException("Error while trying to find reports for admin id = " + adminId, e);
        }
    }


    private static final String SQL_FIND_BY_EVENT_ID = SQL_FIND_ALL + " WHERE event_id = ?";
    /**
     * Executes SQL query to get all reports for a specific event
     * @param conn the db connection
     * @param eventId the event id
     * @return List of reports
     */
    @Override
    public List<Report> findAllByEventId(Connection conn, long eventId) {
        try (PreparedStatement ps = conn.prepareStatement(SQL_FIND_BY_EVENT_ID)) {
            ps.setLong(1, eventId);
            List<Report> reports = new ArrayList<>();

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    reports.add(report_mapper.mapRow(rs));
                }
            }
            return reports;
        } catch (SQLException e) {
            throw new DaoException("Error while trying to find reports for event id = " + eventId, e);
        }
    }


    private static final String SQL_FIND_BY_SEVERITY = SQL_FIND_ALL + " WHERE severity = ?";
    /**
     * Executes SQL query to get all reports with specific severity
     * @param conn the db connection
     * @param severity the severity level
     * @return List of reports
     */
    @Override
    public List<Report> findAllBySeverity(Connection conn, ReportSeverity severity) {
        try (PreparedStatement ps = conn.prepareStatement(SQL_FIND_BY_SEVERITY)) {
            ps.setString(1, severity.name());
            List<Report> reports = new ArrayList<>();

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    reports.add(report_mapper.mapRow(rs));
                }
            }
            return reports;
        } catch (SQLException e) {
            throw new DaoException("Error while trying to find reports with severity = " + severity.name(), e);
        }
    }


    private static final String SQL_FIND_BY_USER_AND_EVENT = SQL_FIND_ALL + " WHERE user_id = ? AND event_id = ?";
    /**
     * Executes SQL query to find a report from a user for a specific event
     * @param conn the db connection
     * @param userId the user id
     * @param eventId the event id
     * @return Optional object containing the Report if found
     */
    @Override
    public Optional<Report> findByUserIdAndEventId(Connection conn, long userId, long eventId) {
        try (PreparedStatement ps = conn.prepareStatement(SQL_FIND_BY_USER_AND_EVENT)) {
            ps.setLong(1, userId);
            ps.setLong(2, eventId);

            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return Optional.empty();
                return Optional.of(report_mapper.mapRow(rs));
            }
        } catch (SQLException e) {
            throw new DaoException("Error finding report for userId = " + userId + " and eventId = " + eventId, e);
        }
    }


    private static final String SQL_FIND_BY_ADMIN_AND_EVENT = SQL_FIND_ALL + " WHERE admin_id = ? AND event_id = ?";
    /**
     * Executes SQL query to get all reports from an admin for a specific event
     * @param conn the db connection
     * @param adminId the admin id
     * @param eventId the event id
     * @return List of reports
     */
    @Override
    public List<Report> findAllByAdminIdAndEventId(Connection conn, long adminId, long eventId) {
        try (PreparedStatement ps = conn.prepareStatement(SQL_FIND_BY_ADMIN_AND_EVENT)) {
            ps.setLong(1, adminId);
            ps.setLong(2, eventId);
            List<Report> reports = new ArrayList<>();

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    reports.add(report_mapper.mapRow(rs));
                }
            }
            return reports;
        } catch (SQLException e) {
            throw new DaoException("Error while trying to find reports for adminId = " + adminId + " and eventId = "
                    + eventId, e);
        }
    }


    private static final String SQL_INSERT = "INSERT INTO report (user_id, admin_id, event_id, severity, " +
                                             "comment, created_at) VALUES (?, ?, ?, ?, ?, ?) RETURNING id";
    /**
     * Executes SQL query to insert a new report
     * @param conn the db connection
     * @param report the report to add
     * @return the generated id
     */
    @Override
    public long insert(Connection conn, Report report) {
        try (PreparedStatement ps = conn.prepareStatement(SQL_INSERT)) {
            ps.setLong(1, report.getUserId());
            ps.setLong(2, report.getAdminId());
            JdbcUtils.setNullableLong(ps, 3, report.getEventId());
            ps.setString(4, report.getSeverity().name());
            JdbcUtils.setNullableString(ps, 5, report.getComment());
            ps.setTimestamp(6, Timestamp.valueOf(report.getCreatedAt()));

            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                return rs.getLong(1);
            }
        } catch (SQLException e) {
            throw new DaoException("Error while trying to insert report = " + report.toString(), e);
        }
    }


    private static final String SQL_UPDATE = "UPDATE report SET user_id = ?, admin_id = ?, event_id = ?, severity = ?, " +
                                             "comment = ?, created_at = ? WHERE id = ?";
    /**
     * Executes SQL query to update an existing report
     * @param conn the db connection
     * @param report the updated report
     */
    @Override
    public void update(Connection conn, Report report) {
        try (PreparedStatement ps = conn.prepareStatement(SQL_UPDATE)) {
            ps.setLong(1, report.getUserId());
            ps.setLong(2, report.getAdminId());
            JdbcUtils.setNullableLong(ps, 3, report.getEventId());
            ps.setString(4, report.getSeverity().name());
            JdbcUtils.setNullableString(ps, 5, report.getComment());
            ps.setTimestamp(6, Timestamp.valueOf(report.getCreatedAt()));
            ps.setLong(7, report.getId());

            int updated = ps.executeUpdate();
            JdbcUtils.requireUpdatedExactly(updated, 1, "update(report_id=" + report.getId() + ")");
        } catch (SQLException e) {
            throw new DaoException("Error while trying to update report = " + report.toString(), e);
        }
    }


    private static final String SQL_DELETE = "DELETE FROM report WHERE id = ?";
    /**
     * Executes SQL query to delete a report by id
     * @param conn the db connection
     * @param reportId the id to delete
     */
    @Override
    public void deleteById(Connection conn, long reportId) {
        try (PreparedStatement ps = conn.prepareStatement(SQL_DELETE)) {
            ps.setLong(1, reportId);

            int deleted = ps.executeUpdate();
            JdbcUtils.requireUpdatedExactly(deleted, 1, "delete(report_id=" + reportId + ")");
        } catch (SQLException e) {
            throw new DaoException("Error while trying to delete report with id = " + reportId, e);
        }
    }
}



