package venue.event.manager.service;

import venue.event.manager.config.TransactionManager;
import venue.event.manager.domain.model.event.Event;
import venue.event.manager.domain.model.feedback.Report;
import venue.event.manager.domain.model.feedback.ReportSeverity;
import venue.event.manager.domain.model.user.User;
import venue.event.manager.exception.ForbiddenException;
import venue.event.manager.exception.NotFoundException;
import venue.event.manager.exception.ValidationException;
import venue.event.manager.repository.EventRepository;
import venue.event.manager.repository.ReportRepository;
import venue.event.manager.repository.UserRepository;

import java.sql.Connection;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

/** Coordinates report creation, validation and moderation queries. */
public class ReportService {

    private final TransactionManager transactionManager;
    private final ReportRepository reportRepository;
    private final UserRepository userRepository;
    private final EventRepository eventRepository;

    /**
     * Initializes ReportService with all repositories needed to handle reports.
     * @param reportRepository repository used to access report data
     * @param eventRepository repository used to access event data
     * @param userRepository repository used to access user data
     */
    public ReportService(ReportRepository reportRepository, EventRepository eventRepository, UserRepository userRepository) {
        this(TransactionManager.getInstance(), reportRepository, eventRepository, userRepository);
    }

    /**
     * Initializes the service with an explicit transaction manager.
     * @param transactionManager transaction manager used to execute database work
     * @param reportRepository repository used to access report data
     * @param eventRepository repository used to access event data
     * @param userRepository repository used to access user data
     */
    public ReportService(TransactionManager transactionManager, ReportRepository reportRepository,
                         EventRepository eventRepository, UserRepository userRepository) {
        this.transactionManager = transactionManager;
        this.reportRepository = reportRepository;
        this.userRepository = userRepository;
        this.eventRepository = eventRepository;
    }

    /**
     * Gets all reports stored in database.
     * @param actorId authenticated administrator requesting the reports
     * @return List of all reports
     */
    public List<Report> getAllReports(long actorId){
        return transactionManager.inReadOnly(conn -> {
            requireAdmin(conn, actorId);
            return reportRepository.findAll(conn);
        });
    }

    /**
     * Gets a report from its id.
     * @param actorId authenticated administrator requesting the report
     * @param reportId the id of the report to find
     * @return Report object if found
     * @throws NotFoundException if no report is found with such id
     */
    public Report getReport(long actorId, long reportId){
        return transactionManager.inReadOnly(conn -> {
            requireAdmin(conn, actorId);
            return reportRepository.findById(conn,reportId)
                    .orElseThrow(() -> new NotFoundException("Report with id " + reportId + " not found"));
        });
    }

    /**
     * Gets all reports related to a specific user.
     * @param actorId authenticated administrator requesting the reports
     * @param userId the id of the reported user
     * @return List of reports related to the user
     */
    public List<Report> getReportsByUser(long actorId, long userId){
        return transactionManager.inReadOnly(conn -> { requireAdmin(conn, actorId); return reportRepository.findAllByUserId(conn,userId); });
    }

    /**
     * Gets all reports created by a specific admin.
     * @param actorId authenticated administrator requesting the reports
     * @param adminId the id of the admin
     * @return List of reports created by the admin
     */
    public List<Report> getReportsByAdmin(long actorId, long adminId){
        return transactionManager.inReadOnly(conn -> { requireAdmin(conn, actorId); return reportRepository.findAllByAdminId(conn,adminId); });
    }

    /**
     * Gets all reports related to a specific event.
     * @param actorId authenticated administrator requesting the reports
     * @param eventId the id of the event
     * @return List of reports related to the event
     */
    public List<Report> getReportsForEvent(long actorId, long eventId){
        return transactionManager.inReadOnly(conn -> { requireAdmin(conn, actorId); return reportRepository.findAllByEventId(conn,eventId); });
    }

    /**
     * Gets all reports with a specific severity.
     * @param actorId authenticated administrator requesting the reports
     * @param severity the severity used to filter reports
     * @return List of reports with the given severity
     */
    public List<Report> getReportsBySeverity(long actorId, ReportSeverity severity){
        validateSeverity(severity);

        return transactionManager.inReadOnly(conn -> { requireAdmin(conn, actorId); return reportRepository.findAllBySeverity(conn,severity); });
    }

    /**
     * Gets a report related to a user and an event.
     * @param actorId authenticated administrator requesting the report
     * @param userId the id of the user
     * @param eventId the id of the event
     * @return Report object if found
     * @throws NotFoundException if no report is found for such user and event
     */
    public Report getReportByUserAndEvent(long actorId, long userId, long eventId){
        return transactionManager.inReadOnly(conn -> {
            requireAdmin(conn, actorId);
            return reportRepository.findByUserIdAndEventId(conn,userId,eventId)
                    .orElseThrow(() -> new NotFoundException("Report for user " + userId +
                            " and event " + eventId + " not found"));
        });
    }

    /**
     * Gets all reports created by an admin for a specific event.
     * @param actorId authenticated administrator requesting the reports
     * @param adminId the id of the admin
     * @param eventId the id of the event
     * @return List of reports matching admin and event
     */
    public List<Report> getReportsByAdminAndEvent(long actorId, long adminId, long eventId){
        return transactionManager.inReadOnly(conn -> { requireAdmin(conn, actorId); return reportRepository.findAllByAdminIdAndEventId(conn,adminId,eventId); });
    }

    /**
     * Inserts a new report in database.
     * @param actorId authenticated administrator creating the report
     * @param report the report to insert
     * @return generated id of the new report
     * @throws ValidationException if report data are not valid
     * @throws NotFoundException if user, admin or event are not found
     * @throws ForbiddenException if admin privileges are missing
     */
    public long addReport(long actorId, Report report){
        validateReportNotNull(report);

        return transactionManager.inTransaction(conn -> {
            requireAdmin(conn, actorId);
            requireReportAuthor(actorId, report.getAdminId());
            validateForInsert(conn,report);
            Report finalReport = report.withCreatedAt(LocalDateTime.now());
            return reportRepository.insert(conn,finalReport);
        });
    }

    /**
     * Updates an existing report in database.
     * @param actorId authenticated administrator updating the report
     * @param report the report object with updated data
     * @throws ValidationException if report data or id are not valid
     */
    public void updateReport(long actorId, Report report){
        validateForUpdate(report);

        transactionManager.inTransaction(conn -> {
            requireAdmin(conn, actorId);
            Report storedReport = reportRepository.findById(conn,report.getId())
                    .orElseThrow(() -> new NotFoundException("Report with id " + report.getId() + " not found"));

            if(storedReport.getUserId() != report.getUserId()
                    || storedReport.getAdminId() != report.getAdminId()
                    || !Objects.equals(storedReport.getEventId(),report.getEventId())
                    || !Objects.equals(storedReport.getCreatedAt(),report.getCreatedAt())) {
                throw new ValidationException("Report user, admin, event and creation date cannot be changed");
            }

            reportRepository.update(conn,report);
            return null;
        });
    }

    /**
     * Deletes a report from database.
     * @param actorId authenticated administrator deleting the report
     * @param reportId the id of the report to delete
     * @throws NotFoundException if no report is found with such id
     */
    public void deleteReport(long actorId, long reportId){
        transactionManager.inTransaction(conn -> {
            requireAdmin(conn, actorId);
            reportRepository.findById(conn,reportId)
                    .orElseThrow(() -> new NotFoundException("Report with id " + reportId + " not found"));
            reportRepository.deleteById(conn,reportId);
            return null;
        });
    }

    /**
     * Validates report data before insert.
     * @param conn the db connection
     * @param report the report to validate
     * @throws ValidationException if one or more fields are not valid
     */
    private void validateForInsert(Connection conn, Report report){
        validateSeverity(report.getSeverity());
        validateComment(report.getComment());
        validateAdmin(conn,report.getAdminId());
        validateTargetUser(conn,report.getUserId());
        if(report.getEventId() != null) validateEventExists(conn,report.getEventId());
    }

    /**
     * Validates report data before update.
     * @param report the report to validate
     * @throws ValidationException if one or more fields are not valid
     */
    private void validateForUpdate(Report report){
        validateReportNotNull(report);
        validateId(report.getId(),"Report id");
        validateId(report.getUserId(),"User id");
        validateId(report.getAdminId(),"Admin id");
        if(report.getEventId() != null) validateId(report.getEventId(),"Event id");
        validateSeverity(report.getSeverity());
        validateComment(report.getComment());
        validateCreatedAt(report.getCreatedAt());
    }

    /**
     * Validates that report is not null.
     * @param report the report to validate
     * @throws ValidationException if report is null
     */
    private void validateReportNotNull(Report report){
        if(report == null) throw new ValidationException("Report cannot be null");
    }

    /**
     * Validates positive id.
     * @param id the id to validate
     * @param label the name of the id field
     * @throws ValidationException if id is not valid
     */
    private void validateId(long id, String label){
        if(id <= 0) throw new ValidationException(label + " is not valid");
    }

    /**
     * Validates severity.
     * @param severity the severity to validate
     * @throws ValidationException if severity is null
     */
    private void validateSeverity(ReportSeverity severity){
        if(severity == null) throw new ValidationException("Report severity cannot be null");
    }

    /**
     * Validates comment.
     * @param comment the comment to validate
     * @throws ValidationException if comment has invalid length
     */
    private void validateComment(String comment){
        if(comment != null && comment.length() > 1000) {
            throw new ValidationException("Report comment cannot exceed 1000 characters");
        }
    }

    /**
     * Validates created date.
     * @param createdAt the created date to validate
     * @throws ValidationException if created date is empty
     */
    private void validateCreatedAt(LocalDateTime createdAt){
        if(createdAt == null) throw new ValidationException("Report created date cannot be empty");
    }

    /**
     * Validates admin user.
     * @param conn the db connection
     * @param adminId the id of the admin to validate
     * @throws NotFoundException if admin is not found
     * @throws ForbiddenException if user is not admin
     */
    private void validateAdmin(Connection conn, long adminId){
        validateId(adminId,"Admin id");
        User admin = userRepository.findById(conn,adminId)
                .orElseThrow(() -> new NotFoundException("Admin with id " + adminId + " not found"));
        if(!admin.isAdmin()) throw new ForbiddenException("Only admins can add reports");
    }

    /**
     * Validates reported user.
     * @param conn the db connection
     * @param userId the id of the reported user
     * @throws NotFoundException if user is not found
     * @throws ValidationException if reported user is an admin
     */
    private void validateTargetUser(Connection conn, long userId){
        validateId(userId,"User id");
        User user = userRepository.findById(conn,userId)
                .orElseThrow(() -> new NotFoundException("User with id " + userId + " not found"));
        if(user.isAdmin()) throw new ValidationException("Reported user cannot be an admin");
    }

    /**
     * Validates if event exists.
     * @param conn the db connection
     * @param eventId the id of the event to validate
     * @return Event object if found
     * @throws NotFoundException if no event is found with such id
     */
    private Event validateEventExists(Connection conn, long eventId){
        validateId(eventId,"Event id");
        return eventRepository.findById(conn,eventId)
                .orElseThrow(() -> new NotFoundException("Event with id " + eventId + " not found"));
    }

    private User requireAdmin(Connection conn, long actorId) {
        validateId(actorId, "Actor id");
        User actor = userRepository.findById(conn, actorId)
                .orElseThrow(() -> new NotFoundException("Admin with id " + actorId + " not found"));
        if (!actor.isAdmin()) throw new ForbiddenException("Only admins can access reports");
        return actor;
    }

    private void requireReportAuthor(long actorId, long adminId) {
        if (actorId != adminId) throw new ForbiddenException("Report author must match authenticated admin");
    }
}
