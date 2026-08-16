package Venue_Event_Manager.domain.model.event;

import java.time.LocalDate;
import java.util.Objects;
import static Venue_Event_Manager.domain.model.event.EventGuestStatus.*;

/**
 * Domain entity representing a Guest assigned to a specific Event.
 * Implemented as an immutable object, with multiple constructors to handle default and nullable fields.
 */
public class EventGuest {

    //attributes
    private final long id;
    private final long event_id; //reference to Event
    private final String firstname;
    private final String lastname;
    private final LocalDate birthday; //NULLABLE
    private final EventGuestStatus status;
    private final String note; //NULLABLE


    //constructors
    /** Initializes an empty event guest with default and empty values. */
    public EventGuest(){
        this(0, 0, "", "", null, null, "");
    }

    /** Master constructor for full initialization.
     * @param id persistent identifier
     * @param event_id associated event identifier
     * @param firstname guest first name
     * @param lastname guest last name
     * @param birthday optional guest birth date
     * @param status guest status, defaulting to {@link EventGuestStatus#INVITED} when {@code null}
     * @param note optional note */
    public EventGuest(long id, long event_id, String firstname, String lastname, LocalDate birthday,
                      EventGuestStatus status, String note){
        this.id = id;
        this.event_id = event_id;
        this.firstname = firstname;
        this.lastname = lastname;
        this.birthday = birthday;
        this.status = status != null ? status : INVITED;
        this.note = note;
    }

    /** Constructor for an unsaved guest whose identifier defaults to zero.
     * @param event_id associated event identifier
     * @param firstname guest first name
     * @param lastname guest last name
     * @param birthday optional guest birth date
     * @param status guest status
     * @param note optional note */
    public EventGuest(long event_id, String firstname, String lastname, LocalDate birthday, EventGuestStatus status,
                      String note){
        this(0, event_id, firstname, lastname, birthday, status, note);
    }


    //getters and withers
    /** Gets the persistent identifier.
     * @return persistent identifier, or zero when unsaved */
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
    public EventGuest withId(long newId){
        return new EventGuest(newId, event_id, firstname, lastname, birthday, status, note);
    }

    /** Gets the associated event identifier.
     * @return associated event identifier */
    /**
     * Performs the {@code getEventId} operation.
     * @return operation result
     */
    public long getEventId(){ return event_id; }
    /** Returns a copy associated with a different event.
     * @param newEventId replacement event identifier
     * @return a copy associated with the supplied event */
    /**
     * Performs the {@code withEventId} operation.
     * @param newEventId newEventId value
     * @return operation result
     */
    public EventGuest withEventId(long newEventId){
        return new EventGuest(id, newEventId, firstname, lastname, birthday, status, note);
    }

    /** Gets the guest first name.
     * @return guest first name */
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
    public EventGuest withFirstname(String newFirstname){
        return new EventGuest(id, event_id, newFirstname, lastname, birthday, status, note);
    }

    /** Gets the guest last name.
     * @return guest last name */
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
    public EventGuest withLastname(String newLastname){
        return new EventGuest(id, event_id, firstname, newLastname, birthday, status, note);
    }

    /** Gets the guest birth date.
     * @return guest birth date, or {@code null} */
    /**
     * Performs the {@code getBirthday} operation.
     * @return operation result
     */
    public LocalDate getBirthday(){ return birthday; }
    /** Returns a copy with a different birth date.
     * @param newBirthday replacement birth date
     * @return a copy with the supplied birth date */
    /**
     * Performs the {@code withBirthday} operation.
     * @param newBirthday newBirthday value
     * @return operation result
     */
    public EventGuest withBirthday(LocalDate newBirthday){
        return new EventGuest(id, event_id, firstname, lastname, newBirthday, status, note);
    }

    /** Gets the current guest status.
     * @return current guest status */
    /**
     * Performs the {@code getStatus} operation.
     * @return operation result
     */
    public EventGuestStatus getStatus(){ return status; }
    /** Returns a copy with a different status.
     * @param newStatus replacement status
     * @return a copy with the supplied status */
    /**
     * Performs the {@code withStatus} operation.
     * @param newStatus newStatus value
     * @return operation result
     */
    public EventGuest withStatus(EventGuestStatus newStatus){
        return new EventGuest(id, event_id, firstname, lastname, birthday, newStatus, note);
    }

    /** Gets the optional guest note.
     * @return optional guest note */
    /**
     * Performs the {@code getNote} operation.
     * @return operation result
     */
    public String getNote(){ return note; }
    /** Returns a copy with a different note.
     * @param newNote replacement note
     * @return a copy with the supplied note */
    /**
     * Performs the {@code withNote} operation.
     * @param newNote newNote value
     * @return operation result
     */
    public EventGuest withNote(String newNote){
        return new EventGuest(id, event_id, firstname, lastname, birthday, status, newNote);
    }


    @Override
    /**
     * Performs the {@code toString} operation.
     * @return operation result
     */
    public String toString() {
        return "EventGuest{" +
                "id=" + id + "; " +
                "event_id=" + event_id + "; " +
                "firstname=" + firstname + "; " +
                "lastname=" + lastname + "; " +
                "birthday=" + birthday + "; " +
                "status=" + status + "; " +
                "note=" + note + ";" +
                "}";
    }

    /** Compares event guest based on ID or firstname, lastname and event_id uniqueness */
    @Override
    /**
     * Performs the {@code equals} operation.
     * @param other other value
     * @return operation result
     */
    public boolean equals(Object other) {
        if (this == other) return true;
        if (other == null || getClass() != other.getClass()) return false;
        EventGuest eventGuest = (EventGuest) other;
        if (id != 0 && eventGuest.id != 0){
            return Objects.equals(id, eventGuest.id);
        }
        return Objects.equals(event_id, eventGuest.event_id) && Objects.equals(firstname, eventGuest.firstname) &&
                Objects.equals(lastname, eventGuest.lastname);
    }

    @Override
    /**
     * Performs the {@code hashCode} operation.
     * @return operation result
     */
    public int hashCode() {
        if (id != 0){
            return Objects.hash(id);
        }
        return Objects.hash(event_id, firstname, lastname);
    }

}
