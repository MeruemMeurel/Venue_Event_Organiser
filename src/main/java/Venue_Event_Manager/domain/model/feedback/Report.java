package Venue_Event_Manager.domain.model.feedback;

import java.time.LocalDateTime;
import java.util.Objects;
import static Venue_Event_Manager.domain.model.feedback.ReportSeverity.*;

/**
 * Domain entity representing a formal report or complaint.
 * Implemented as an immutable object, with multiple constructors to handle default and nullable fields.
 */
public class Report {

    //attributes
    private final long id;
    private final long user_id;     //reference to User (isAdmin=false)
    private final long admin_id;    //reference to User (isAdmint=true)
    private final Long event_id;    //reference to Venue, NULLABLE
    private final ReportSeverity severity;
    private final String comment;    //NULLABLE, default value is " "
    private final LocalDateTime created_at; // Null only for transient entities; assigned before persistence.


    //constructors
    /** Initializes an empty report with default and empty values. */
    public Report() { this(0, 0, 0, null, null, "", null); }

    /** Master constructor for full initialization.
     *
     * @param id persistent identifier
     * @param user_id reporting user identifier
     * @param admin_id assigned administrator
     *
     * @param event_id optional reported event
     * @param severity report severity
     * @param comment report description
     *
     * @param created_at creation timestamp */
    public Report(long id, long user_id, long admin_id, Long event_id, ReportSeverity severity,
                  String comment, LocalDateTime created_at) {
        this.id = id;
        this.user_id = user_id;
        this.admin_id = admin_id;
        this.event_id = event_id;
        this.severity = severity != null ? severity : MIDDLE;
        this.comment = comment != null ? comment : "";
        this.created_at = created_at;
    }

    /** Creates an unsaved report.
     *
     * @param user_id reporting user identifier
     * @param admin_id assigned administrator
     *
     * @param event_id optional reported event
     * @param severity report severity
     * @param comment report description
     *
     * @param created_at creation timestamp */
    public Report(long user_id, long admin_id, Long event_id, ReportSeverity severity, String comment,
                  LocalDateTime created_at) {
        this(0, user_id, admin_id, event_id, severity, comment, created_at);
    }


    //getters and withers
    /**
     * Performs the {@code getId} operation.
     * @return operation result
     */
    public long getId() { return id; }
    /**
     * Performs the {@code withId} operation.
     * @param newId newId value
     * @return operation result
     */
    public Report withId(long newId) {
        return new Report(newId, user_id, admin_id, event_id, severity, comment, created_at);
    }

    /**
     * Performs the {@code getUserId} operation.
     * @return operation result
     */
    public long getUserId() { return user_id; }
    /**
     * Performs the {@code withUserId} operation.
     * @param newUserId newUserId value
     * @return operation result
     */
    public Report withUserId(long newUserId) {
        return new Report(id, newUserId, admin_id, event_id, severity, comment, created_at);
    }

    /**
     * Performs the {@code getAdminId} operation.
     * @return operation result
     */
    public long getAdminId() { return admin_id; }
    /**
     * Performs the {@code withAdminId} operation.
     * @param newAdminId newAdminId value
     * @return operation result
     */
    public Report withAdminId(long newAdminId) {
        return new Report(id, user_id, newAdminId, event_id, severity, comment, created_at);
    }

    /**
     * Performs the {@code getEventId} operation.
     * @return operation result
     */
    public Long getEventId() { return event_id; }
    /**
     * Performs the {@code withEventId} operation.
     * @param newEventId newEventId value
     * @return operation result
     */
    public Report withEventId(Long newEventId) {
        return new Report(id, user_id, admin_id, newEventId, severity, comment, created_at);
    }

    /**
     * Performs the {@code getSeverity} operation.
     * @return operation result
     */
    public ReportSeverity getSeverity() { return severity; }
    /**
     * Performs the {@code withSeverity} operation.
     * @param newSeverity newSeverity value
     * @return operation result
     */
    public Report withSeverity(ReportSeverity newSeverity) {
        return new Report(id, user_id, admin_id, event_id, newSeverity, comment, created_at);
    }

    /**
     * Performs the {@code getComment} operation.
     * @return operation result
     */
    public String getComment() { return comment; }
    /**
     * Performs the {@code withComment} operation.
     * @param newComment newComment value
     * @return operation result
     */
    public Report withComment(String newComment) {
        return new Report(id, user_id, admin_id, event_id, severity, newComment, created_at);
    }

    /**
     * Performs the {@code getCreatedAt} operation.
     * @return operation result
     */
    public LocalDateTime getCreatedAt() { return created_at; }
    /**
     * Performs the {@code withCreatedAt} operation.
     * @param newCreatedAt newCreatedAt value
     * @return operation result
     */
    public Report withCreatedAt(LocalDateTime newCreatedAt) {
        return new Report(id, user_id, admin_id, event_id, severity, comment, newCreatedAt);
    }


    @Override
    /**
     * Performs the {@code toString} operation.
     * @return operation result
     */
    public String toString() {
        return "Report{" +
                "id=" + id + "; " +
                "user_id=" + user_id + "; " +
                "admin_id=" + admin_id + "; " +
                "event_id=" + event_id + "; " +
                "severity=" + severity + "; " +
                "comment='" + comment + "'; " +
                "created_at=" + created_at + ";" +
                "}";
    }

    /** Compares report based on ID or user_id, admin_id and created_at uniqueness */
    @Override
    /**
     * Performs the {@code equals} operation.
     * @param other other value
     * @return operation result
     */
    public boolean equals(Object other) {
        if (this == other) return true;
        if (other == null || getClass() != other.getClass()) return false;
        Report report = (Report) other;
        if (id != 0 && report.id != 0){
            return Objects.equals(id, report.id);
        }
        return Objects.equals(user_id, report.user_id) && Objects.equals(admin_id, report.admin_id) &&
                Objects.equals(created_at, report.created_at);
    }

    @Override
    /**
     * Performs the {@code hashCode} operation.
     * @return operation result
     */
    public int hashCode() {
        if (id != 0){
            return Objects.hash(id);
        }
        return Objects.hash(user_id, admin_id, created_at);
    }
}
