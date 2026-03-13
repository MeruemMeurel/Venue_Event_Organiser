package Venue_Event_Manager.domain.model.event;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;
import static Venue_Event_Manager.domain.model.event.EventStatus.*;
import static Venue_Event_Manager.domain.model.event.EventVisibility.*;

/**
 * Domain entity representing a physical Venue.
 * Implemented as an immutable object, with multiple constructors to handle default and nullable fields.
 */
public class Event {

    //attributes
    private final long id;
    private final long venue_id; //reference to Venue
    private final long creator_id; //reference to User (isAdmin=true)
    private final Long organiser_id; //reference to User (isAdmin=false), NULLABLE
    private final String name;
    private final String description;
    private final LocalDateTime begin_datetime;
    private final LocalDateTime end_datetime;
    private final String poster_filepath; //NULLABLE
    private final int capacity;
    private final EventStatus status;
    private final EventVisibility visibility;
    private final BigDecimal ticket_price; //NULLABLE
    private final LocalDateTime published_at; //NULLABLE


    //costructors
    /** Initializes an empty event with default and empty values. */
    public Event() {
        this(0, 0, 0, null, "", "", null, null,
                "", 0, null, null, null, null);
    }

    /** Master constructor for full initialization. */
    public Event(long id, long venue_id, long creator_id, Long organiser_id, String name, String description,
                 LocalDateTime begin_datetime, LocalDateTime end_datetime, String poster_filepath, int capacity,
                 EventStatus status, EventVisibility visibility, BigDecimal ticket_price, LocalDateTime published_at) {
        this.id = id;
        this.venue_id = venue_id;
        this.creator_id = creator_id;
        this.organiser_id = organiser_id;
        this.name = name;
        this.description = description;
        this.begin_datetime = begin_datetime;
        this.end_datetime = end_datetime;
        this.poster_filepath = poster_filepath;
        this.capacity = capacity;
        this.status = status != null ? status : CONFIRMED;
        this.visibility = visibility != null ? visibility : PUBLIC;
        this.ticket_price = ticket_price;
        this.published_at = published_at;
    }

    /** Constructor for unsaved event (ID defaults to 0). */
    public Event(long venue_id, long creator_id, Long organiser_id, String name, String description,
                 LocalDateTime begin_datetime, LocalDateTime end_datetime, String poster_filepath, int capacity,
                 EventStatus status, EventVisibility visibility, BigDecimal ticket_price, LocalDateTime published_at) {
        this(0, venue_id, creator_id, organiser_id, name, description, begin_datetime, end_datetime, poster_filepath,
                capacity, status, visibility, ticket_price, published_at);
    }

    //getters and withers
    public long getId() {
        return id;
    }
    public Event withId(long newId) {
        return new Event(newId, venue_id, creator_id, organiser_id, name, description, begin_datetime, end_datetime,
                poster_filepath, capacity, status, visibility, ticket_price, published_at);
    }

    public long getVenueId() {
        return venue_id;
    }
    public Event withVenueId(long newVenueId) {
        return new Event(id, newVenueId, creator_id, organiser_id, name, description, begin_datetime, end_datetime,
                poster_filepath, capacity, status, visibility, ticket_price, published_at);
    }

    public long getCreatorId() {
        return creator_id;
    }
    public Event withCreatorId(long newCreatorId) {
        return new Event(id, venue_id, newCreatorId, organiser_id, name, description, begin_datetime, end_datetime,
                poster_filepath, capacity, status, visibility, ticket_price, published_at);
    }

    public Long getOrganiserId() {
        return organiser_id;
    }
    public Event withOrganiserId(Long newOrganiserId) {
        return new Event(id, venue_id, creator_id, newOrganiserId, name, description, begin_datetime, end_datetime,
                poster_filepath, capacity, status, visibility, ticket_price, published_at);
    }

    public String getName() {
        return name;
    }
    public Event withName(String newName) {
        return new Event(id, venue_id, creator_id, organiser_id, newName, description, begin_datetime, end_datetime,
                poster_filepath, capacity, status, visibility, ticket_price, published_at);
    }

    public String getDescription() {
        return description;
    }
    public Event withDescription(String newDescription) {
        return new Event(id, venue_id, creator_id, organiser_id, name, newDescription, begin_datetime, end_datetime,
                poster_filepath, capacity, status, visibility, ticket_price, published_at);
    }

    public LocalDateTime getBeginDateTime() {
        return begin_datetime;
    }
    public Event withBeginDateTime(LocalDateTime newBeginDateTime) {
        return new Event(id, venue_id, creator_id, organiser_id, name, description, newBeginDateTime, end_datetime,
                poster_filepath, capacity, status, visibility, ticket_price, published_at);
    }

    public LocalDateTime getEndDateTime() {
        return end_datetime;
    }
    public Event withEndDateTime(LocalDateTime newEndDateTime) {
        return new Event(id, venue_id, creator_id, organiser_id, name, description, begin_datetime, newEndDateTime,
                poster_filepath, capacity, status, visibility, ticket_price, published_at);
    }

    public String getPosterFilepath() {
        return poster_filepath;
    }
    public Event withPosterFilepath(String newPosterFilepath) {
        return new Event(id, venue_id, creator_id, organiser_id, name, description, begin_datetime, end_datetime,
                newPosterFilepath, capacity, status, visibility, ticket_price, published_at);
    }

    public int getCapacity() {
        return capacity;
    }
    public Event withCapacity(int newCapacity) {
        return new Event(id, venue_id, creator_id, organiser_id, name, description, begin_datetime, end_datetime,
                poster_filepath, newCapacity, status, visibility, ticket_price, published_at);
    }

    public EventStatus getStatus() {
        return status;
    }
    public Event withStatus(EventStatus newStatus) {
        return new Event(id, venue_id, creator_id, organiser_id, name, description, begin_datetime, end_datetime,
                poster_filepath, capacity, newStatus, visibility, ticket_price, published_at);
    }

    public EventVisibility getVisibility() {
        return visibility;
    }
    public Event withVisibility(EventVisibility newVisibility) {
        return new Event(id, venue_id, creator_id, organiser_id, name, description, begin_datetime, end_datetime,
                poster_filepath, capacity, status, newVisibility, ticket_price, published_at);
    }

    public BigDecimal getTicketPrice() {
        return ticket_price;
    }
    public Event withTicketPrice(BigDecimal newTicketPrice) {
        return new Event(id, venue_id, creator_id, organiser_id, name, description, begin_datetime, end_datetime,
                poster_filepath, capacity, status, visibility, newTicketPrice, published_at);
    }

    public LocalDateTime getPublishedAt() {
        return published_at;
    }
    public Event withPublishedAt(LocalDateTime newPublishedAt) {
        return new Event(id, venue_id, creator_id, organiser_id, name, description, begin_datetime, end_datetime,
                poster_filepath, capacity, status, visibility, ticket_price, newPublishedAt);
    }


    @Override
    public String toString() {
        return "Event{" +
                "id=" + id + "; " +
                "venue_id=" + venue_id + "; " +
                "creator_id=" + creator_id + "; " +
                "organiser_id=" + organiser_id + "; " +
                "name=" + name + "; " +
                "description=" + description + "; " +
                "begin_datetime=" + begin_datetime + "; " +
                "end_datetime=" + end_datetime + "; " +
                "poster_filepath=" + poster_filepath + "; " +
                "capacity=" + capacity + "; " +
                "status=" + status + "; " +
                "visibility=" + visibility + "; " +
                "ticket_price=" + ticket_price + "; " +
                "published_at=" + published_at + ";" +
                "}";
    }

    /** Compares event based on ID or name, venue_id and begin_datetime uniqueness. */
    @Override
    public boolean equals(Object other) {
        if (this == other) return true;
        if (other == null || getClass() != other.getClass()) return false;
        Event event = (Event) other;
        if (id != 0 && event.id != 0) return id == event.id;
        return Objects.equals(name, event.name) && venue_id == event.venue_id
                && Objects.equals(begin_datetime, event.begin_datetime);
    }

    @Override
    public int hashCode() {
        if (id != 0) return Objects.hash(id);
        return Objects.hash(name, venue_id, begin_datetime);
    }

}