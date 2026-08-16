package Venue_Event_Manager.repository;

import Venue_Event_Manager.domain.model.feedback.Report;
import Venue_Event_Manager.domain.model.feedback.ReportSeverity;
import java.sql.Connection;
import java.util.List;
import java.util.Optional;

/** Defines persistence operations for the corresponding domain model. */
public interface ReportRepository {

    /**
     * Performs the {@code findAll} repository operation.
     * @param conn conn value
     * @return operation result
     */
    List<Report> findAll(Connection conn);

    /**
     * Performs the {@code findById} repository operation.
     * @param conn conn value
     * @param reportId reportId value
     * @return operation result
     */
    Optional<Report> findById(Connection conn, long reportId);

    /**
     * Performs the {@code findAllByUserId} repository operation.
     * @param conn conn value
     * @param userId userId value
     * @return operation result
     */
    List<Report> findAllByUserId(Connection conn, long userId);

    /**
     * Performs the {@code findAllByAdminId} repository operation.
     * @param conn conn value
     * @param adminId adminId value
     * @return operation result
     */
    List<Report> findAllByAdminId(Connection conn, long adminId);

    /**
     * Performs the {@code findAllByEventId} repository operation.
     * @param conn conn value
     * @param eventId eventId value
     * @return operation result
     */
    List<Report> findAllByEventId(Connection conn, long eventId);

    /**
     * Performs the {@code findAllBySeverity} repository operation.
     * @param conn conn value
     * @param severity severity value
     * @return operation result
     */
    List<Report> findAllBySeverity(Connection conn, ReportSeverity severity);

    /**
     * Performs the {@code findByUserIdAndEventId} repository operation.
     * @param conn conn value
     * @param userId userId value
     * @param eventId eventId value
     * @return operation result
     */
    Optional<Report> findByUserIdAndEventId(Connection conn, long userId, long eventId);

    /**
     * Performs the {@code findAllByAdminIdAndEventId} repository operation.
     * @param conn conn value
     * @param adminId adminId value
     * @param eventId eventId value
     * @return operation result
     */
    List<Report> findAllByAdminIdAndEventId(Connection conn, long adminId, long eventId);

    /**
     * Performs the {@code insert} repository operation.
     * @param conn conn value
     * @param report report value
     * @return operation result
     */
    long insert(Connection conn, Report report);

    /**
     * Performs the {@code update} repository operation.
     * @param conn conn value
     * @param report report value
     */
    void update(Connection conn, Report report);

    /**
     * Performs the {@code deleteById} repository operation.
     * @param conn conn value
     * @param reportId reportId value
     */
    void deleteById(Connection conn, long reportId);

}
