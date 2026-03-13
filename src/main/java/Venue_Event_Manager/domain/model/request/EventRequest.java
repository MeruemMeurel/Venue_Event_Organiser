package Venue_Event_Manager.domain.model.request;

import java.time.LocalDateTime;
import java.util.Objects;
import static Venue_Event_Manager.domain.model.request.EventRequestStatus.*;


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
    private final LocalDateTime created_at; // TODO gestire valori null
    private final LocalDateTime closed_at; //NULLABLE
    private final Double quote; //NULLABLE


    //constructors
    /** Initializes an empty EventRequest with default and empty values. */
    public EventRequest(){
        this(0, 0, null, 0, "", "", null, null,
                null, null, null, null);
    }

    /** Master constructor for full initialization. */
    public EventRequest(long id, long requester_id, Long handler_id, long venue_id, String name, String description,
                        LocalDateTime begin_datetime, LocalDateTime end_datetime, EventRequestStatus status,
                        LocalDateTime created_at, LocalDateTime closed_at, Double quote){
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

    /** Constructor for unsaved ticket (ID defaults to 0). */
    public EventRequest(long requester_id, Long handler_id, long venue_id, String name, String description,
                        LocalDateTime begin_datetime, LocalDateTime end_datetime, EventRequestStatus status,
                        LocalDateTime created_at, LocalDateTime closed_at, Double quote){
        this(0, requester_id, handler_id, venue_id, name, description, begin_datetime, end_datetime, status,
                created_at, closed_at, quote);
    }


    //getters and withers
    public long getId() { return id; }
    public EventRequest withId(long newId) {
        return new EventRequest(newId, requester_id, handler_id, venue_id, name, description, begin_datetime,
                end_datetime, status, created_at, closed_at, quote);
    }

    public long getRequesterId() { return requester_id; }
    public EventRequest withRequesterId(long newRequesterId) {
        return new EventRequest(id, newRequesterId, handler_id, venue_id, name, description, begin_datetime,
                end_datetime, status, created_at, closed_at, quote);
    }

    public Long getHandlerId() { return handler_id; }
    public EventRequest withHandlerId(Long newHandlerId) {
        return new EventRequest(id, requester_id, newHandlerId, venue_id, name, description, begin_datetime,
                end_datetime, status, created_at, closed_at, quote);
    }

    public long getVenueId() { return venue_id; }
    public EventRequest withVenueId(long newVenueId) {
        return new EventRequest(id, requester_id, handler_id, newVenueId, name, description, begin_datetime,
                end_datetime, status, created_at, closed_at, quote);
    }

    public String getName() { return name; }
    public EventRequest withName(String newName) {
        return new EventRequest(id, requester_id, handler_id, venue_id, newName, description, begin_datetime,
                end_datetime, status, created_at, closed_at, quote);
    }

    public String getDescription() { return description; }
    public EventRequest withDescription(String newDescription) {
        return new EventRequest(id, requester_id, handler_id, venue_id, name, newDescription, begin_datetime,
                end_datetime, status, created_at, closed_at, quote);
    }

    public LocalDateTime getBeginDateTime() { return begin_datetime; }
    public EventRequest withBeginDateTime(LocalDateTime newBeginDateTime) {
        return new EventRequest(id, requester_id, handler_id, venue_id, name, description, newBeginDateTime,
                end_datetime, status, created_at, closed_at, quote);
    }

    public LocalDateTime getEndDateTime() { return end_datetime; }
    public EventRequest withEndDateTime(LocalDateTime newEndDateTime) {
        return new EventRequest(id, requester_id, handler_id, venue_id, name, description, begin_datetime,
                newEndDateTime, status, created_at, closed_at, quote);
    }

    public EventRequestStatus getStatus() { return status; }
    public EventRequest withStatus(EventRequestStatus newStatus) {
        return new EventRequest(id, requester_id, handler_id, venue_id, name, description, begin_datetime,
                end_datetime, newStatus, created_at, closed_at, quote);
    }

    public LocalDateTime getCreatedAt() { return created_at; }
    public EventRequest withCreatedAt(LocalDateTime newCreatedAt) {
        return new EventRequest(id, requester_id, handler_id, venue_id, name, description, begin_datetime,
                end_datetime, status, newCreatedAt, closed_at, quote);
    }

    public LocalDateTime getClosedAt() { return closed_at; }
    public EventRequest withClosedAt(LocalDateTime newClosedAt) {
        return new EventRequest(id, requester_id, handler_id, venue_id, name, description, begin_datetime,
                end_datetime, status, created_at, newClosedAt, quote);
    }

    public Double getQuote() { return quote; }
    public EventRequest withQuote(Double newQuote) {
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
        if (id != 0 && eventRequest.id != 0) return id == eventRequest.id;
        return requester_id == eventRequest.requester_id && venue_id == eventRequest.venue_id &&
                Objects.equals(name, eventRequest.name) && Objects.equals(begin_datetime, eventRequest.begin_datetime)
                && Objects.equals(created_at, eventRequest.created_at);
    }

    public int hashCode(){
        if(id != 0) return Objects.hash(id);
        return Objects.hash(requester_id, venue_id, name, begin_datetime, created_at);
    }

}