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

    /** Master constructor for full initialization. */
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

    /** Constructor for unsaved report (ID defaults to 0). */
    public Report(long user_id, long admin_id, Long event_id, ReportSeverity severity, String comment,
                  LocalDateTime created_at) {
        this(0, user_id, admin_id, event_id, severity, comment, created_at);
    }


    //getters and withers
    public long getId() { return id; }
    public Report withId(long newId) {
        return new Report(newId, user_id, admin_id, event_id, severity, comment, created_at);
    }

    public long getUserId() { return user_id; }
    public Report withUserId(long newUserId) {
        return new Report(id, newUserId, admin_id, event_id, severity, comment, created_at);
    }

    public long getAdminId() { return admin_id; }
    public Report withAdminId(long newAdminId) {
        return new Report(id, user_id, newAdminId, event_id, severity, comment, created_at);
    }

    public Long getEventId() { return event_id; }
    public Report withEventId(Long newEventId) {
        return new Report(id, user_id, admin_id, newEventId, severity, comment, created_at);
    }

    public ReportSeverity getSeverity() { return severity; }
    public Report withSeverity(ReportSeverity newSeverity) {
        return new Report(id, user_id, admin_id, event_id, newSeverity, comment, created_at);
    }

    public String getComment() { return comment; }
    public Report withComment(String newComment) {
        return new Report(id, user_id, admin_id, event_id, severity, newComment, created_at);
    }

    public LocalDateTime getCreatedAt() { return created_at; }
    public Report withCreatedAt(LocalDateTime newCreatedAt) {
        return new Report(id, user_id, admin_id, event_id, severity, comment, newCreatedAt);
    }


    @Override
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
    public int hashCode() {
        if (id != 0){
            return Objects.hash(id);
        }
        return Objects.hash(user_id, admin_id, created_at);
    }
}
