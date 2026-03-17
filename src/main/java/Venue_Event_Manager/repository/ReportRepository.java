package Venue_Event_Manager.repository;

import Venue_Event_Manager.domain.model.feedback.Report;
import Venue_Event_Manager.domain.model.feedback.ReportSeverity;
import java.sql.Connection;
import java.util.List;
import java.util.Optional;

public interface ReportRepository {

    List<Report> findAll(Connection conn);

    Optional<Report> findById(Connection conn, long reportId);

    List<Report> findAllByUserId(Connection conn, long userId);

    List<Report> findAllByAdminId(Connection conn, long adminId);

    List<Report> findAllByEventId(Connection conn, long eventId);

    List<Report> findAllBySeverity(Connection conn, ReportSeverity severity);

    Optional<Report> findByUserIdAndEventId(Connection conn, long userId, long eventId);

    List<Report> findAllByAdminIdAndEventId(Connection conn, long adminId, long eventId);

    long insert(Connection conn, Report report);

    void update(Connection conn, Report report);

    void deleteById(Connection conn, long reportId);

}