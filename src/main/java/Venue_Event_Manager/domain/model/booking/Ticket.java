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
    private final LocalDateTime birthday; //NULLABLE


    //constructors
    /** Initializes an empty ticket with default and empty values. */
    public Ticket(){ this(0, 0, "", "", null); }

    /** Master constructor for full initialization. */
    public Ticket(long id, long booking_id, String firstname, String lastname, LocalDateTime birthday){
        this.id = id;
        this.booking_id = booking_id;
        this.firstname = firstname;
        this.lastname = lastname;
        this.birthday = birthday;
    }

    /** Constructor for unsaved ticket (ID defaults to 0). */
    public Ticket(long booking_id, String firstname, String lastname, LocalDateTime birthday){
        this(0, booking_id, firstname, lastname, birthday);
    }


    //getters and withers
    public long getId(){ return id; }
    public Ticket withId(long newId){
        return new Ticket(newId, booking_id, firstname, lastname, birthday);
    }

    public long getBookingId(){ return booking_id; }
    public Ticket withBookingId(long newBookingId){
        return new Ticket(id, newBookingId, firstname, lastname, birthday);
    }

    public String getFirstname(){ return firstname; }
    public Ticket withFirstname(String newFirstname){
        return new Ticket(id, booking_id, newFirstname, lastname, birthday);
    }

    public String getLastname(){ return lastname; }
    public Ticket withLastname(String newLastname){
        return new Ticket(id, booking_id, firstname, newLastname, birthday);
    }

    public LocalDateTime getBirthday(){ return birthday; }
    public Ticket withBirthday(LocalDateTime newBirthday){
        return new Ticket(id, booking_id, firstname, lastname, newBirthday);
    }


    @Override
    public String toString(){
        return "Ticket{" +
                "id=" + id + "; " +
                "booking_id=" + booking_id + "; " +
                "firstname=" + firstname + "; " +
                "lastname=" + lastname + "; " +
                "birthday=" + birthday + ";" +
                "}";
    }

    /** Compares ticket based on ID or booking_id, firstname and lastname uniqueness */
    @Override
    public boolean equals(Object other){
        if (this == other) return true;
        if (other == null || getClass() != other.getClass()) return false;
        Ticket ticket = (Ticket) other;
        if (id != 0 && ticket.id != 0) { return id == ticket.id; }
        return booking_id == ticket.booking_id && Objects.equals(firstname, ticket.firstname) &&
                Objects.equals(lastname, ticket.lastname);
    }

    @Override
    public int hashCode(){
        if(id != 0) return Objects.hash(id);
        return Objects.hash(booking_id, firstname, lastname);
    }

}