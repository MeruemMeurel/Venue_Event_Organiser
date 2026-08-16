package Venue_Event_Manager.domain.model.feedback;

/**
 * Define of the possible severity levels for a report.
 */
public enum ReportSeverity {
    /** Minor issues or suggestions. */
    LOW,

    /** Policy violations or disruptive behavior. */
    MIDDLE,

    /** Severe violations, harassment, or security threats. */
    HIGH
}
