package Venue_Event_Manager.service;

import Venue_Event_Manager.config.TransactionManager;
import Venue_Event_Manager.domain.model.feedback.Report;
import Venue_Event_Manager.domain.model.feedback.ReportSeverity;
import Venue_Event_Manager.domain.model.user.User;
import Venue_Event_Manager.domain.model.event.Event;
import Venue_Event_Manager.exception.ValidationException;
import Venue_Event_Manager.exception.NotFoundException;
import Venue_Event_Manager.exception.ForbiddenException;
import Venue_Event_Manager.repository.ReportRepository;
import Venue_Event_Manager.repository.UserRepository;
import Venue_Event_Manager.repository.EventRepository;
import java.time.LocalDateTime;
import java.util.List;

public class ReportService {
    private TransactionManager transactionManager;
    private ReportRepository reportRepository;
    private UserRepository userRepository;
    private EventRepository eventRepository;

    public ReportService(ReportRepository reportRepository, EventRepository eventRepository, UserRepository userRepository) {
        transactionManager = TransactionManager.getInstance();
        this.reportRepository = reportRepository;
        this.userRepository = userRepository;
        this.eventRepository = eventRepository;
    }

    /**
     * Retrieves all reports in the system.
     *
     * @return a list of all reports
     */
    public List<Report> getAllReports(){
         return transactionManager.inReadOnly(conn ->
                 reportRepository.findAll(conn));
    }

    /**
     * Retrieves a specific report by its ID.
     *
     * @param reportId the ID of the report to retrieve
     * @return the report matching the given ID
     * @throws NotFoundException if no report is found with the given ID
     */
    public Report getReport(long reportId){
        return transactionManager.inReadOnly(conn ->
                reportRepository.findById(conn, reportId))
                .orElseThrow(() -> new NotFoundException("Report not found"));
    }

    /**
     * Retrieves all reports filed against a specific user.
     *
     * @param userId the ID of the reported user
     * @return a list of reports against the user
     */
    public List<Report> getReportByUser(long userId){
        return transactionManager.inReadOnly(conn ->
                reportRepository.findAllByUserId(conn,userId));
    }

    /**
     * Retrieves all reports filed by a specific admin.
     *
     * @param adminId the ID of the reporting admin
     * @return a list of reports created by the admin
     */
    public List<Report> getReportsByAdmin(long adminId){
        return transactionManager.inReadOnly(conn ->
                reportRepository.findAllByAdminId(conn, adminId));
    }

    /**
     * Retrieves all reports associated with a specific event.
     *
     * @param eventId the ID of the event
     * @return a list of reports for the event
     */
    public List<Report> getReportsForEvent(long eventId){
        return transactionManager.inReadOnly(conn ->
                reportRepository.findAllByEventId(conn,eventId));
    }

    /**
     * Retrieves all reports matching a specific severity level.
     *
     * @param severity the severity level (LOW, MIDDLE, HIGH)
     * @return a list of reports matching the severity
     */
    public List<Report> getReportBySeverity(ReportSeverity severity){
        return transactionManager.inReadOnly(conn ->
                reportRepository.findAllBySeverity(conn,severity));
    }

    /**
     * Adds a new report, enforcing admin validation and entity existence constraints.
     *
     * @param report the report details to insert
     * @return the generated ID of the new report
     * @throws ValidationException if the severity is null
     * @throws NotFoundException   if the admin, user, or associated event does not exist
     * @throws ForbiddenException  if the reporting user is not an administrator
     */
    public long addReport(Report report){
        return transactionManager.inTransaction(conn ->{
            //check if severity is null
            if(report.getSeverity() == null)
                throw new ValidationException("Report severity cannot be null");
            //check if admin exist
            User admin = userRepository.findById(conn, report.getAdminId())
                    .orElseThrow(() -> new NotFoundException("Admin not found"));
            //check if it's actually and admin
            if(!admin.isAdmin())
                throw new ForbiddenException("Only admins can add reports");
            //check if user exist
            userRepository.findById(conn, report.getUserId())
                    .orElseThrow(() -> new NotFoundException("User not found"));
            //validate the event
            if(report.getEventId() != null)
                eventRepository.findById(conn, report.getEventId())
                    .orElseThrow(() -> new NotFoundException("Associated event not found"));
            Report finalReport = report.withCreatedAt(LocalDateTime.now());
            return reportRepository.insert(conn,finalReport);
        });
    }

    /**
     * Deletes a report from the system.
     *
     * @param reportId the ID of the report to delete
     * @throws NotFoundException if the report is not found
     */
    public void deleteReport(long reportId){
        transactionManager.inTransaction(conn ->{
            reportRepository.findById(conn, reportId)
                    .orElseThrow(() -> new NotFoundException("Report not found"));
            reportRepository.deleteById(conn, reportId);
            return null;
        });
    }
}
