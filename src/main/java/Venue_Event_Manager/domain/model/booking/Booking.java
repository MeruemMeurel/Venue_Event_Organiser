package Venue_Event_Manager.domain.model.booking;

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
    private final LocalDateTime created_at; // TODO gestire valori null?
    private final BookingStatus status;
    private final double total_price;


    //constructors
    /** Initializes an empty booking with default and empty values. */
    public Booking(){ this(0, 0, 0, null, null, 0); }

    /** Master constructor for full initialization. */
    public Booking(long id, long user_id, long event_id, LocalDateTime created_at, BookingStatus status,
                   double total_price){
        this.id = id;
        this.user_id = user_id;
        this.event_id = event_id;
        this.created_at = created_at;
        this.status = status != null ? status : PENDING_PAYMENT;
        this.total_price = total_price;
    }

    /** Constructor for unsaved booking (ID defaults to 0). */
    public Booking(long user_id, long event_id, LocalDateTime created_at, BookingStatus status, double total_price){
        this(0, user_id, event_id, created_at, status, total_price);
    }


    //getters and withers
    public long getId() { return id; }
    public Booking withId(long newId){
        return new Booking(newId, user_id, event_id, created_at, status, total_price);
    }

    public long getUserId(){ return user_id; }
    public Booking withUserId(long newUserId){
        return new Booking(id, newUserId, event_id, created_at, status, total_price);
    }

    public long getEventId(){ return event_id; }
    public Booking withEventId(long newEventId){
        return new Booking(id, user_id, newEventId, created_at, status, total_price);
    }

    public LocalDateTime getCreatedAt(){ return created_at; }
    public Booking withCreatedAt(LocalDateTime newCreatedAt){
        return new Booking(id, user_id, event_id, newCreatedAt, status, total_price);
    }

    public BookingStatus getStatus(){ return status; }
    public Booking withStatus(BookingStatus newStatus){
        return new Booking(id, user_id, event_id, created_at, newStatus, total_price);
    }

    public double getTotalPrice(){ return total_price; }
    public Booking withTotalPrice(double newTotalPrice){
        return new Booking(id, user_id, event_id, created_at, status, newTotalPrice);
    }


    @Override
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
    public boolean equals(Object other){
        if (this == other) return true;
        if (other == null || getClass() != other.getClass()) return false;
        Booking booking = (Booking) other;
        if (id != 0 && booking.id != 0) return id == booking.id;
        return user_id == booking.user_id && event_id == booking.event_id &&
                Objects.equals(created_at, booking.created_at);
    }

    @Override
    public int hashCode(){
        if(id != 0) return Objects.hash(id);
        return Objects.hash(user_id, event_id, created_at);
    }

}