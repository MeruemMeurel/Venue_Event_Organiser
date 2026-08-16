package venue.event.manager.domain.model.event;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;
import static venue.event.manager.domain.model.event.EventStatus.*;
import static venue.event.manager.domain.model.event.EventVisibility.*;

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

    /** Master constructor for full initialization.
     * @param id persistent identifier
     * @param venue_id hosting venue identifier
     * @param creator_id administrator who created the event
     * @param organiser_id optional organiser identifier
     * @param name event name
     * @param description event description
     * @param begin_datetime event start
     * @param end_datetime event end
     * @param poster_filepath optional poster path
     * @param capacity maximum attendance
     * @param status event status, defaulting to {@link EventStatus#DRAFT} when {@code null}
     * @param visibility event visibility
     * @param ticket_price optional ticket price
     * @param published_at optional publication timestamp */
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
        this.status = status != null ? status : DRAFT;
        this.visibility = visibility != null ? visibility : PUBLIC;
        this.ticket_price = ticket_price;
        this.published_at = published_at;
    }

    /** Constructor for an unsaved event whose identifier defaults to zero.
     * @param venue_id hosting venue identifier
     * @param creator_id administrator who created the event
     * @param organiser_id optional organiser identifier
     * @param name event name
     * @param description event description
     * @param begin_datetime event start
     * @param end_datetime event end
     * @param poster_filepath optional poster path
     * @param capacity maximum attendance
     * @param status event status
     * @param visibility event visibility
     * @param ticket_price optional ticket price
     * @param published_at optional publication timestamp */
    public Event(long venue_id, long creator_id, Long organiser_id, String name, String description,
                 LocalDateTime begin_datetime, LocalDateTime end_datetime, String poster_filepath, int capacity,
                 EventStatus status, EventVisibility visibility, BigDecimal ticket_price, LocalDateTime published_at) {
        this(0, venue_id, creator_id, organiser_id, name, description, begin_datetime, end_datetime, poster_filepath,
                capacity, status, visibility, ticket_price, published_at);
    }

    //getters and withers
    /** Gets the persistent identifier.
     * @return event identifier, or zero when unsaved
     */
    public long getId() {
        return id;
    }
    /** Returns a copy with another identifier.
     * @param newId replacement identifier
     * @return updated copy
     */
    public Event withId(long newId) {
        return new Event(newId, venue_id, creator_id, organiser_id, name, description, begin_datetime, end_datetime,
                poster_filepath, capacity, status, visibility, ticket_price, published_at);
    }

    /** Gets the venue identifier.
     * @return associated venue identifier
     */
    public long getVenueId() {
        return venue_id;
    }
    /** Returns a copy assigned to another venue.
     * @param newVenueId replacement venue identifier
     * @return updated copy
     */
    public Event withVenueId(long newVenueId) {
        return new Event(id, newVenueId, creator_id, organiser_id, name, description, begin_datetime, end_datetime,
                poster_filepath, capacity, status, visibility, ticket_price, published_at);
    }

    /** Gets the creator identifier.
     * @return creator identifier
     */
    public long getCreatorId() {
        return creator_id;
    }
    /** Returns a copy with another creator.
     * @param newCreatorId replacement creator identifier
     * @return updated copy
     */
    public Event withCreatorId(long newCreatorId) {
        return new Event(id, venue_id, newCreatorId, organiser_id, name, description, begin_datetime, end_datetime,
                poster_filepath, capacity, status, visibility, ticket_price, published_at);
    }

    /** Gets the optional organiser identifier.
     * @return organiser identifier, or {@code null}
     */
    public Long getOrganiserId() {
        return organiser_id;
    }
    /** Returns a copy with another organiser.
     * @param newOrganiserId replacement organiser, possibly {@code null}
     * @return updated copy
     */
    public Event withOrganiserId(Long newOrganiserId) {
        return new Event(id, venue_id, creator_id, newOrganiserId, name, description, begin_datetime, end_datetime,
                poster_filepath, capacity, status, visibility, ticket_price, published_at);
    }

    /** Gets the event name.
     * @return event name
     */
    public String getName() {
        return name;
    }
    /** Returns a copy with another name.
     * @param newName replacement name
     * @return updated copy
     */
    public Event withName(String newName) {
        return new Event(id, venue_id, creator_id, organiser_id, newName, description, begin_datetime, end_datetime,
                poster_filepath, capacity, status, visibility, ticket_price, published_at);
    }

    /** Gets the event description.
     * @return event description
     */
    public String getDescription() {
        return description;
    }
    /** Returns a copy with another description.
     * @param newDescription replacement description
     * @return updated copy
     */
    public Event withDescription(String newDescription) {
        return new Event(id, venue_id, creator_id, organiser_id, name, newDescription, begin_datetime, end_datetime,
                poster_filepath, capacity, status, visibility, ticket_price, published_at);
    }

    /** Gets the beginning date and time.
     * @return event beginning
     */
    public LocalDateTime getBeginDatetime() {
        return begin_datetime;
    }
    /** Returns a copy with another beginning.
     * @param newBeginDateTime replacement beginning
     * @return updated copy
     */
    public Event withBeginDateTime(LocalDateTime newBeginDateTime) {
        return new Event(id, venue_id, creator_id, organiser_id, name, description, newBeginDateTime, end_datetime,
                poster_filepath, capacity, status, visibility, ticket_price, published_at);
    }

    /** Gets the ending date and time.
     * @return event ending
     */
    public LocalDateTime getEndDatetime() {
        return end_datetime;
    }
    /** Returns a copy with another ending.
     * @param newEndDateTime replacement ending
     * @return updated copy
     */
    public Event withEndDateTime(LocalDateTime newEndDateTime) {
        return new Event(id, venue_id, creator_id, organiser_id, name, description, begin_datetime, newEndDateTime,
                poster_filepath, capacity, status, visibility, ticket_price, published_at);
    }

    /** Gets the optional poster path.
     * @return poster file path, or {@code null}
     */
    public String getPosterFilepath() {
        return poster_filepath;
    }
    /** Returns a copy with another poster path.
     * @param newPosterFilepath replacement path
     * @return updated copy
     */
    public Event withPosterFilepath(String newPosterFilepath) {
        return new Event(id, venue_id, creator_id, organiser_id, name, description, begin_datetime, end_datetime,
                newPosterFilepath, capacity, status, visibility, ticket_price, published_at);
    }

    /** Gets the attendee capacity.
     * @return event capacity
     */
    public int getCapacity() {
        return capacity;
    }
    /** Returns a copy with another capacity.
     * @param newCapacity replacement capacity
     * @return updated copy
     */
    public Event withCapacity(int newCapacity) {
        return new Event(id, venue_id, creator_id, organiser_id, name, description, begin_datetime, end_datetime,
                poster_filepath, newCapacity, status, visibility, ticket_price, published_at);
    }

    /** Gets the lifecycle status.
     * @return event status
     */
    public EventStatus getStatus() {
        return status;
    }
    /** Returns a copy with another status.
     * @param newStatus replacement status
     * @return updated copy
     */
    public Event withStatus(EventStatus newStatus) {
        return new Event(id, venue_id, creator_id, organiser_id, name, description, begin_datetime, end_datetime,
                poster_filepath, capacity, newStatus, visibility, ticket_price, published_at);
    }

    /** Gets the visibility policy.
     * @return event visibility
     */
    public EventVisibility getVisibility() {
        return visibility;
    }
    /** Returns a copy with another visibility.
     * @param newVisibility replacement visibility
     * @return updated copy
     */
    public Event withVisibility(EventVisibility newVisibility) {
        return new Event(id, venue_id, creator_id, organiser_id, name, description, begin_datetime, end_datetime,
                poster_filepath, capacity, status, newVisibility, ticket_price, published_at);
    }

    /** Gets the ticket price.
     * @return ticket price, or {@code null}
     */
    public BigDecimal getTicketPrice() {
        return ticket_price;
    }
    /** Returns a copy with another ticket price.
     * @param newTicketPrice replacement price
     * @return updated copy
     */
    public Event withTicketPrice(BigDecimal newTicketPrice) {
        return new Event(id, venue_id, creator_id, organiser_id, name, description, begin_datetime, end_datetime,
                poster_filepath, capacity, status, visibility, newTicketPrice, published_at);
    }

    /** Gets the publication timestamp.
     * @return publication timestamp, or {@code null}
     */
    public LocalDateTime getPublishedAt() {
        return published_at;
    }
    /** Returns a copy with another publication timestamp.
     * @param newPublishedAt replacement timestamp
     * @return updated copy
     */
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
        if (id != 0 && event.id != 0){
            return Objects.equals(id, event.id);
        }
        return Objects.equals(name, event.name) && Objects.equals(venue_id, event.venue_id) &&
                Objects.equals(begin_datetime, event.begin_datetime);
    }

    @Override
    public int hashCode() {
        if (id != 0){
            return Objects.hash(id);
        }
        return Objects.hash(name, venue_id, begin_datetime);
    }

}
