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
    private final LocalDateTime starts_at;


    //constructors
    /** Initializes an empty ticket with default and empty values. */
    public Ticket(){ this(0, 0, "", "", null); }

    /** Master constructor for full initialization.
     * @param id persistent identifier
     * @param booking_id associated booking identifier
     * @param firstname attendee first name
     * @param lastname attendee last name
     * @param starts_at ticket validity start */
    public Ticket(long id, long booking_id, String firstname, String lastname, LocalDateTime starts_at){
        this.id = id;
        this.booking_id = booking_id;
        this.firstname = firstname;
        this.lastname = lastname;
        this.starts_at = starts_at;
    }

    /** Constructor for an unsaved ticket whose identifier defaults to zero.
     * @param booking_id associated booking identifier
     * @param firstname attendee first name
     * @param lastname attendee last name
     * @param startsAt ticket validity start */
    public Ticket(long booking_id, String firstname, String lastname, LocalDateTime startsAt){
        this(0, booking_id, firstname, lastname, startsAt);
    }

    /** Constructor for a ticket not yet associated with a booking.
     * @param firstname attendee first name
     * @param lastname attendee last name */
    public Ticket(String firstname, String lastname) {
        this.id=0;
        this.booking_id=0;
        this.firstname = firstname;
        this.lastname = lastname;
        this.starts_at = LocalDateTime.of(1900,1,1,1,1);
    }

    //getters and withers
    /** @return persistent identifier, or zero when unsaved */
    /**
     * Performs the {@code getId} operation.
     * @return operation result
     */
    public long getId(){ return id; }
    /** Returns a copy with a different identifier.
     * @param newId replacement identifier
     * @return a copy with the supplied identifier */
    /**
     * Performs the {@code withId} operation.
     * @param newId newId value
     * @return operation result
     */
    public Ticket withId(long newId){
        return new Ticket(newId, booking_id, firstname, lastname, starts_at);
    }

    /** @return associated booking identifier */
    /**
     * Performs the {@code getBookingId} operation.
     * @return operation result
     */
    public long getBookingId(){ return booking_id; }
    /** Returns a copy associated with a different booking.
     * @param newBookingId replacement booking identifier
     * @return a copy associated with the supplied booking */
    /**
     * Performs the {@code withBookingId} operation.
     * @param newBookingId newBookingId value
     * @return operation result
     */
    public Ticket withBookingId(long newBookingId){
        return new Ticket(id, newBookingId, firstname, lastname, starts_at);
    }

    /** @return attendee first name */
    /**
     * Performs the {@code getFirstname} operation.
     * @return operation result
     */
    public String getFirstname(){ return firstname; }
    /** Returns a copy with a different first name.
     * @param newFirstname replacement first name
     * @return a copy with the supplied first name */
    /**
     * Performs the {@code withFirstname} operation.
     * @param newFirstname newFirstname value
     * @return operation result
     */
    public Ticket withFirstname(String newFirstname){
        return new Ticket(id, booking_id, newFirstname, lastname, starts_at);
    }

    /** @return attendee last name */
    /**
     * Performs the {@code getLastname} operation.
     * @return operation result
     */
    public String getLastname(){ return lastname; }
    /** Returns a copy with a different last name.
     * @param newLastname replacement last name
     * @return a copy with the supplied last name */
    /**
     * Performs the {@code withLastname} operation.
     * @param newLastname newLastname value
     * @return operation result
     */
    public Ticket withLastname(String newLastname){
        return new Ticket(id, booking_id, firstname, newLastname, starts_at);
    }

    /** @return ticket validity start */
    /**
     * Performs the {@code getStartsAt} operation.
     * @return operation result
     */
    public LocalDateTime getStartsAt(){ return starts_at; }
    /** Returns a copy with a different validity start.
     * @param newStartsAt replacement validity start
     * @return a copy with the supplied validity start */
    /**
     * Performs the {@code withStartsAt} operation.
     * @param newStartsAt newStartsAt value
     * @return operation result
     */
    public Ticket withStartsAt(LocalDateTime newStartsAt){
        return new Ticket(id, booking_id, firstname, lastname, newStartsAt);
    }


    @Override
    /**
     * Performs the {@code toString} operation.
     * @return operation result
     */
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
    /**
     * Performs the {@code equals} operation.
     * @param other other value
     * @return operation result
     */
    public boolean equals(Object other){
        if (this == other) return true;
        if (other == null || getClass() != other.getClass()) return false;
        Ticket ticket = (Ticket) other;
        if (id != 0 && ticket.id != 0){
            return Objects.equals(id, ticket.id);
        }
        return Objects.equals(booking_id, ticket.booking_id) && Objects.equals(firstname, ticket.firstname) &&
                Objects.equals(lastname, ticket.lastname);
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
        return Objects.hash(booking_id, firstname, lastname);
    }

}
