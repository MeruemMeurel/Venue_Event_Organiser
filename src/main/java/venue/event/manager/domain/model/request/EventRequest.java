package venue.event.manager.domain.model.request;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;
import static venue.event.manager.domain.model.request.EventRequestStatus.*;


/**
 * Domain entity representing a request to organize an event.
 * Implemented as an immutable object, with multiple constructors to handle default and nullable fields.
 */
public class EventRequest {

    //attributes
    private final long id;
    private final long requester_id; //reference to User (isAdmin=false)
    private final Long handler_id; //reference to User (isAdmin=true), NULLABLE
    private final long venue_id; //reference to Venue
    private final String name;
    private final String description;
    private final LocalDateTime begin_datetime;
    private final LocalDateTime end_datetime;
    private final EventRequestStatus status;
    private final LocalDateTime created_at; // Null only for transient entities; assigned before persistence.
    private final LocalDateTime closed_at; //NULLABLE
    private final BigDecimal quote; //NULLABLE


    //constructors
    /** Initializes an empty EventRequest with default and empty values. */
    public EventRequest(){
        this(0, 0, null, 0, "", "", null, null,
                null, null, null, null);
    }

    /** Master constructor for full initialization.
     * @param id persistent identifier
     * @param requester_id requesting user identifier
     * @param handler_id optional administrator handling the request
     * @param venue_id requested venue identifier
     * @param name proposed event name
     * @param description request description
     * @param begin_datetime requested start
     * @param end_datetime requested end
     * @param status request status, defaulting to {@link EventRequestStatus#PENDING} when {@code null}
     * @param created_at creation timestamp
     * @param closed_at optional closure timestamp
     * @param quote optional quoted price */
    public EventRequest(long id, long requester_id, Long handler_id, long venue_id, String name, String description,
                        LocalDateTime begin_datetime, LocalDateTime end_datetime, EventRequestStatus status,
                        LocalDateTime created_at, LocalDateTime closed_at, BigDecimal quote){
        this.id = id;
        this.requester_id = requester_id;
        this.handler_id = handler_id;
        this.venue_id = venue_id;
        this.name = name;
        this.description = description;
        this.begin_datetime = begin_datetime;
        this.end_datetime = end_datetime;
        this.status = status != null ? status : PENDING;
        this.created_at = created_at;
        this.closed_at = closed_at;
        this.quote = quote;
    }

    /** Constructor for an unsaved event request whose identifier defaults to zero.
     * @param requester_id requesting user identifier
     * @param handler_id optional administrator handling the request
     * @param venue_id requested venue identifier
     * @param name proposed event name
     * @param description request description
     * @param begin_datetime requested start
     * @param end_datetime requested end
     * @param status request status
     * @param created_at creation timestamp
     * @param closed_at optional closure timestamp
     * @param quote optional quoted price */
    public EventRequest(long requester_id, Long handler_id, long venue_id, String name, String description,
                        LocalDateTime begin_datetime, LocalDateTime end_datetime, EventRequestStatus status,
                        LocalDateTime created_at, LocalDateTime closed_at, BigDecimal quote){
        this(0, requester_id, handler_id, venue_id, name, description, begin_datetime, end_datetime, status,
                created_at, closed_at, quote);
    }


    //getters and withers
    /** Gets the identifier.
     * @return request identifier, or zero when unsaved
     */
    public long getId() { return id; }
    /** Returns a copy with another identifier.
     * @param newId replacement identifier
     * @return updated copy
     */
    public EventRequest withId(long newId) {
        return new EventRequest(newId, requester_id, handler_id, venue_id, name, description, begin_datetime,
                end_datetime, status, created_at, closed_at, quote);
    }

    /** Gets the requester identifier.
     * @return requester identifier
     */
    public long getRequesterId() { return requester_id; }
    /** Returns a copy with another requester.
     * @param newRequesterId replacement requester id
     * @return updated copy
     */
    public EventRequest withRequesterId(long newRequesterId) {
        return new EventRequest(id, newRequesterId, handler_id, venue_id, name, description, begin_datetime,
                end_datetime, status, created_at, closed_at, quote);
    }

    /** Gets the handler identifier.
     * @return handler identifier, or {@code null}
     */
    public Long getHandlerId() { return handler_id; }
    /** Returns a copy with another handler.
     * @param newHandlerId replacement handler id
     * @return updated copy
     */
    public EventRequest withHandlerId(Long newHandlerId) {
        return new EventRequest(id, requester_id, newHandlerId, venue_id, name, description, begin_datetime,
                end_datetime, status, created_at, closed_at, quote);
    }

    /** Gets the venue identifier.
     * @return venue identifier
     */
    public long getVenueId() { return venue_id; }
    /** Returns a copy with another venue.
     * @param newVenueId replacement venue id
     * @return updated copy
     */
    public EventRequest withVenueId(long newVenueId) {
        return new EventRequest(id, requester_id, handler_id, newVenueId, name, description, begin_datetime,
                end_datetime, status, created_at, closed_at, quote);
    }

    /** Gets the request name.
     * @return request name
     */
    public String getName() { return name; }
    /** Returns a copy with another name.
     * @param newName replacement name
     * @return updated copy
     */
    public EventRequest withName(String newName) {
        return new EventRequest(id, requester_id, handler_id, venue_id, newName, description, begin_datetime,
                end_datetime, status, created_at, closed_at, quote);
    }

    /** Gets the description.
     * @return request description
     */
    public String getDescription() { return description; }
    /** Returns a copy with another description.
     * @param newDescription replacement description
     * @return updated copy
     */
    public EventRequest withDescription(String newDescription) {
        return new EventRequest(id, requester_id, handler_id, venue_id, name, newDescription, begin_datetime,
                end_datetime, status, created_at, closed_at, quote);
    }

    /** Gets the requested beginning.
     * @return requested beginning date and time
     */
    public LocalDateTime getBeginDatetime() { return begin_datetime; }
    /** Returns a copy with another beginning.
     * @param newBeginDateTime replacement beginning
     * @return updated copy
     */
    public EventRequest withBeginDateTime(LocalDateTime newBeginDateTime) {
        return new EventRequest(id, requester_id, handler_id, venue_id, name, description, newBeginDateTime,
                end_datetime, status, created_at, closed_at, quote);
    }

    /** Gets the requested ending.
     * @return requested ending date and time
     */
    public LocalDateTime getEndDatetime() { return end_datetime; }
    /** Returns a copy with another ending.
     * @param newEndDateTime replacement ending
     * @return updated copy
     */
    public EventRequest withEndDateTime(LocalDateTime newEndDateTime) {
        return new EventRequest(id, requester_id, handler_id, venue_id, name, description, begin_datetime,
                newEndDateTime, status, created_at, closed_at, quote);
    }

    /** Gets the request status.
     * @return current request status
     */
    public EventRequestStatus getStatus() { return status; }
    /** Returns a copy with another status.
     * @param newStatus replacement status
     * @return updated copy
     */
    public EventRequest withStatus(EventRequestStatus newStatus) {
        return new EventRequest(id, requester_id, handler_id, venue_id, name, description, begin_datetime,
                end_datetime, newStatus, created_at, closed_at, quote);
    }

    /** Gets the creation timestamp.
     * @return request creation time
     */
    public LocalDateTime getCreatedAt() { return created_at; }
    /** Returns a copy with another creation time.
     * @param newCreatedAt replacement creation time
     * @return updated copy
     */
    public EventRequest withCreatedAt(LocalDateTime newCreatedAt) {
        return new EventRequest(id, requester_id, handler_id, venue_id, name, description, begin_datetime,
                end_datetime, status, newCreatedAt, closed_at, quote);
    }

    /** Gets the closure timestamp.
     * @return closure time, or {@code null} while pending
     */
    public LocalDateTime getClosedAt() { return closed_at; }
    /** Returns a copy with another closure time.
     * @param newClosedAt replacement closure time
     * @return updated copy
     */
    public EventRequest withClosedAt(LocalDateTime newClosedAt) {
        return new EventRequest(id, requester_id, handler_id, venue_id, name, description, begin_datetime,
                end_datetime, status, created_at, newClosedAt, quote);
    }

    /** Gets the proposed quote.
     * @return quote, or {@code null}
     */
    public BigDecimal getQuote() { return quote; }
    /** Returns a copy with another quote.
     * @param newQuote replacement quote
     * @return updated copy
     */
    public EventRequest withQuote(BigDecimal newQuote) {
        return new EventRequest(id, requester_id, handler_id, venue_id, name, description, begin_datetime,
                end_datetime, status, created_at, closed_at, newQuote);
    }


    @Override
    public String toString(){
        return "EventRequest{" +
                "id=" + id + "; " +
                "requester_id=" + requester_id + "; " +
                "handler_id=" + handler_id + "; " +
                "venue_id=" + venue_id + "; " +
                "name=" + name + "; " +
                "description=" + description + "; " +
                "begin_datetime=" + begin_datetime + "; " +
                "end_datetime=" + end_datetime + "; " +
                "status=" + status + "; " +
                "created_at=" + created_at+ "; " +
                "closed_at=" + closed_at + "; " +
                "quote=" + quote + ";" +
                "}";
    }

    /** Compares event request based on ID or requester_id, booking_id, name and begin_datetime uniqueness */
    @Override
    public boolean equals(Object other){
        if (this == other) return true;
        if (other == null || getClass() != other.getClass()) return false;
        EventRequest eventRequest = (EventRequest) other;
        if (id != 0 && eventRequest.id != 0){
            return Objects.equals(id, eventRequest.id);
        }
        return Objects.equals(requester_id, eventRequest.requester_id) && Objects.equals(venue_id, eventRequest.venue_id)
                && Objects.equals(name, eventRequest.name) && Objects.equals(begin_datetime, eventRequest.begin_datetime)
                && Objects.equals(created_at, eventRequest.created_at);
    }

    @Override
    public int hashCode(){
        if(id != 0){
            return Objects.hash(id);
        }
        return Objects.hash(requester_id, venue_id, name, begin_datetime, created_at);
    }

}
