package Venue_Event_Manager.domain.model.booking;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;
import static Venue_Event_Manager.domain.model.booking.BookingStatus.*;

/**
 * Domain entity representing a Booking made by a User for a specific Event.
 * Implemented as an immutable object, with multiple constructors to handle default and nullable fields.
 */
public class Booking {

    //attributes
    private final long id;
    private final long user_id; //reference to User
    private final long event_id; //reference to Event
    private final LocalDateTime created_at; // Null only for transient entities; assigned before persistence.
    private final BookingStatus status;
    private final BigDecimal total_price;


    //constructors
    /** Initializes an empty booking with default and empty values. */
    public Booking(){ this(0, 0, 0, null, null, null); }

    /** Master constructor for full initialization.
     * @param id persistent identifier
     * @param user_id booking owner identifier
     * @param event_id booked event identifier
     * @param created_at creation timestamp
     * @param status booking status, defaulting to {@link BookingStatus#PENDING_PAYMENT} when {@code null}
     * @param total_price total booking price */
    public Booking(long id, long user_id, long event_id, LocalDateTime created_at, BookingStatus status,
                   BigDecimal total_price){
        this.id = id;
        this.user_id = user_id;
        this.event_id = event_id;
        this.created_at = created_at;
        this.status = status != null ? status : PENDING_PAYMENT;
        this.total_price = total_price;
    }

    /** Constructor for an unsaved booking whose identifier defaults to zero.
     * @param user_id booking owner identifier
     * @param event_id booked event identifier
     * @param created_at creation timestamp
     * @param status booking status
     * @param total_price total booking price */
    public Booking(long user_id, long event_id, LocalDateTime created_at, BookingStatus status, BigDecimal total_price){
        this(0, user_id, event_id, created_at, status, total_price);
    }


    //getters and withers
    /** Gets the persistent identifier.
     * @return persistent identifier, or zero when unsaved */
    /**
     * Performs the {@code getId} operation.
     * @return operation result
     */
    public long getId() { return id; }
    /** Returns a copy with a different identifier.
     * @param newId replacement identifier
     * @return a copy with the supplied identifier */
    /**
     * Performs the {@code withId} operation.
     * @param newId newId value
     * @return operation result
     */
    public Booking withId(long newId){
        return new Booking(newId, user_id, event_id, created_at, status, total_price);
    }

    /** Gets the booking owner identifier.
     * @return booking owner identifier */
    /**
     * Performs the {@code getUserId} operation.
     * @return operation result
     */
    public long getUserId(){ return user_id; }
    /** Returns a copy with a different owner.
     * @param newUserId replacement owner identifier
     * @return a copy with the supplied owner */
    /**
     * Performs the {@code withUserId} operation.
     * @param newUserId newUserId value
     * @return operation result
     */
    public Booking withUserId(long newUserId){
        return new Booking(id, newUserId, event_id, created_at, status, total_price);
    }

    /** Gets the booked event identifier.
     * @return booked event identifier */
    /**
     * Performs the {@code getEventId} operation.
     * @return operation result
     */
    public long getEventId(){ return event_id; }
    /** Returns a copy for a different event.
     * @param newEventId replacement event identifier
     * @return a copy with the supplied event */
    /**
     * Performs the {@code withEventId} operation.
     * @param newEventId newEventId value
     * @return operation result
     */
    public Booking withEventId(long newEventId){
        return new Booking(id, user_id, newEventId, created_at, status, total_price);
    }

    /** Gets the creation timestamp.
     * @return creation timestamp */
    /**
     * Performs the {@code getCreatedAt} operation.
     * @return operation result
     */
    public LocalDateTime getCreatedAt(){ return created_at; }
    /** Returns a copy with a different creation timestamp.
     * @param newCreatedAt replacement timestamp
     * @return a copy with the supplied timestamp */
    /**
     * Performs the {@code withCreatedAt} operation.
     * @param newCreatedAt newCreatedAt value
     * @return operation result
     */
    public Booking withCreatedAt(LocalDateTime newCreatedAt){
        return new Booking(id, user_id, event_id, newCreatedAt, status, total_price);
    }

    /** Gets the current booking status.
     * @return current booking status */
    /**
     * Performs the {@code getStatus} operation.
     * @return operation result
     */
    public BookingStatus getStatus(){ return status; }
    /** Returns a copy with a different status.
     * @param newStatus replacement status
     * @return a copy with the supplied status */
    /**
     * Performs the {@code withStatus} operation.
     * @param newStatus newStatus value
     * @return operation result
     */
    public Booking withStatus(BookingStatus newStatus){
        return new Booking(id, user_id, event_id, created_at, newStatus, total_price);
    }

    /** Gets the total booking price.
     * @return total booking price */
    /**
     * Performs the {@code getTotalPrice} operation.
     * @return operation result
     */
    public BigDecimal getTotalPrice(){ return total_price; }
    /** Returns a copy with a different total price.
     * @param newTotalPrice replacement price
     * @return a copy with the supplied price */
    /**
     * Performs the {@code withTotalPrice} operation.
     * @param newTotalPrice newTotalPrice value
     * @return operation result
     */
    public Booking withTotalPrice(BigDecimal newTotalPrice){
        return new Booking(id, user_id, event_id, created_at, status, newTotalPrice);
    }


    @Override
    /**
     * Performs the {@code toString} operation.
     * @return operation result
     */
    public String toString(){
        return "Booking{" +
                "id=" + id + "; " +
                "user_id=" + user_id + "; " +
                "event_id=" + event_id + "; " +
                "created_at=" + created_at + "; " +
                "status=" + status + "; " +
                "total_price=" + total_price + ";" +
                "}";
    }

    /** Compares booking based on ID or user_id, event_id and created_at uniqueness */
    @Override
    /**
     * Performs the {@code equals} operation.
     * @param other other value
     * @return operation result
     */
    public boolean equals(Object other){
        if (this == other) return true;
        if (other == null || getClass() != other.getClass()) return false;
        Booking booking = (Booking) other;
        if (id != 0 && booking.id != 0){
            return Objects.equals(id, booking.id);
        }
        return Objects.equals(user_id, booking.user_id) && Objects.equals(event_id, booking.event_id) &&
                Objects.equals(created_at, booking.created_at);
    }

    @Override
    /**
     * Performs the {@code hashCode} operation.
     * @return operation result
     */
    public int hashCode(){
        if(id != 0){
            return Objects.hash(id);
        }
        return Objects.hash(user_id, event_id, created_at);
    }

}
