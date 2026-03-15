package Venue_Event_Manager.domain.model.booking;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Domain entity representing a Ticket associated with a Booking.
 * Implemented as an immutable object, with multiple constructors to handle default and nullable fields.
 */
public class Ticket {

    //attributes
    private final long id;
    private final long booking_id; //reference to Booking
    private final String firstname; //NULLABLE
    private final String lastname; //NULLABLE
    private final LocalDateTime starts_at; //NULLABLE


    //constructors
    /** Initializes an empty ticket with default and empty values. */
    public Ticket(){ this(0, 0, "", "", null); }

    /** Master constructor for full initialization. */
    public Ticket(long id, long booking_id, String firstname, String lastname, LocalDateTime starts_at){
        this.id = id;
        this.booking_id = booking_id;
        this.firstname = firstname;
        this.lastname = lastname;
        this.starts_at = starts_at;
    }

    /** Constructor for unsaved ticket (ID defaults to 0). */
    public Ticket(long booking_id, String firstname, String lastname, LocalDateTime startsAt){
        this(0, booking_id, firstname, lastname, startsAt);
    }


    //getters and withers
    public long getId(){ return id; }
    public Ticket withId(long newId){
        return new Ticket(newId, booking_id, firstname, lastname, starts_at);
    }

    public long getBookingId(){ return booking_id; }
    public Ticket withBookingId(long newBookingId){
        return new Ticket(id, newBookingId, firstname, lastname, starts_at);
    }

    public String getFirstname(){ return firstname; }
    public Ticket withFirstname(String newFirstname){
        return new Ticket(id, booking_id, newFirstname, lastname, starts_at);
    }

    public String getLastname(){ return lastname; }
    public Ticket withLastname(String newLastname){
        return new Ticket(id, booking_id, firstname, newLastname, starts_at);
    }

    public LocalDateTime getStartsAt(){ return starts_at; }
    public Ticket withBirthday(LocalDateTime newStartsAt){
        return new Ticket(id, booking_id, firstname, lastname, newStartsAt);
    }


    @Override
    public String toString(){
        return "Ticket{" +
                "id=" + id + "; " +
                "booking_id=" + booking_id + "; " +
                "firstname=" + firstname + "; " +
                "lastname=" + lastname + "; " +
                "startsAt=" + starts_at + ";" +
                "}";
    }

    /** Compares ticket based on ID or booking_id, firstname and lastname uniqueness */
    @Override
    public boolean equals(Object other){
        if (this == other) return true;
        if (other == null || getClass() != other.getClass()) return false;
        Ticket ticket = (Ticket) other;
        if (id != 0 && ticket.id != 0) return id == ticket.id;
        return booking_id == ticket.booking_id && Objects.equals(firstname, ticket.firstname) &&
                Objects.equals(lastname, ticket.lastname);
    }

    @Override
    public int hashCode(){
        if(id != 0) return Objects.hash(id);
        return Objects.hash(booking_id, firstname, lastname);
    }

}