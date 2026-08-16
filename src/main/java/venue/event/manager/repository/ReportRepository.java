package venue.event.manager.repository;

import venue.event.manager.domain.model.feedback.Report;
import venue.event.manager.domain.model.feedback.ReportSeverity;
import java.sql.Connection;
import java.util.List;
import java.util.Optional;

/** Defines persistence operations for the corresponding domain model. */
public interface ReportRepository {

    /**
     * Returns all persisted records.
     * @param conn active database connection
     * @return result produced by the repository operation
     */
    List<Report> findAll(Connection conn);

    /**
     * Finds a persisted record by identifier.
     * @param conn active database connection
     * @param reportId identifier used by the operation
     * @return result produced by the repository operation
     */
    Optional<Report> findById(Connection conn, long reportId);

    /**
     * Returns persisted records filtered by user id.
     * @param conn active database connection
     * @param userId identifier used by the operation
     * @return result produced by the repository operation
     */
    List<Report> findAllByUserId(Connection conn, long userId);

    /**
     * Returns persisted records filtered by admin id.
     * @param conn active database connection
     * @param adminId identifier used by the operation
     * @return result produced by the repository operation
     */
    List<Report> findAllByAdminId(Connection conn, long adminId);

    /**
     * Returns persisted records filtered by event id.
     * @param conn active database connection
     * @param eventId identifier used by the operation
     * @return result produced by the repository operation
     */
    List<Report> findAllByEventId(Connection conn, long eventId);

    /**
     * Returns persisted records filtered by severity.
     * @param conn active database connection
     * @param severity filter value
     * @return result produced by the repository operation
     */
    List<Report> findAllBySeverity(Connection conn, ReportSeverity severity);

    /**
     * Finds persisted records by user id and event id.
     * @param conn active database connection
     * @param userId identifier used by the operation
     * @param eventId identifier used by the operation
     * @return result produced by the repository operation
     */
    Optional<Report> findByUserIdAndEventId(Connection conn, long userId, long eventId);

    /**
     * Returns persisted records filtered by admin id and event id.
     * @param conn active database connection
     * @param adminId identifier used by the operation
     * @param eventId identifier used by the operation
     * @return result produced by the repository operation
     */
    List<Report> findAllByAdminIdAndEventId(Connection conn, long adminId, long eventId);

    /**
     * Persists a new record.
     * @param conn active database connection
     * @param report record to persist
     * @return result produced by the repository operation
     */
    long insert(Connection conn, Report report);

    /**
     * Updates an existing record.
     * @param conn active database connection
     * @param report record to persist
     */
    void update(Connection conn, Report report);

    /**
     * Deletebyids the supplied persistent data.
     * @param conn active database connection
     * @param reportId identifier used by the operation
     */
    void deleteById(Connection conn, long reportId);

}
