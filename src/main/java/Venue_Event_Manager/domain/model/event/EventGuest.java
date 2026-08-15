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
    /** @return persistent identifier, or zero when unsaved */
    public long getId(){ return id; }
    /** Returns a copy with a different identifier.
     * @param newId replacement identifier
     * @return a copy with the supplied identifier */
    public EventGuest withId(long newId){
        return new EventGuest(newId, event_id, firstname, lastname, birthday, status, note);
    }

    /** @return associated event identifier */
    public long getEventId(){ return event_id; }
    /** Returns a copy associated with a different event.
     * @param newEventId replacement event identifier
     * @return a copy associated with the supplied event */
    public EventGuest withEventId(long newEventId){
        return new EventGuest(id, newEventId, firstname, lastname, birthday, status, note);
    }

    /** @return guest first name */
    public String getFirstname(){ return firstname; }
    /** Returns a copy with a different first name.
     * @param newFirstname replacement first name
     * @return a copy with the supplied first name */
    public EventGuest withFirstname(String newFirstname){
        return new EventGuest(id, event_id, newFirstname, lastname, birthday, status, note);
    }

    /** @return guest last name */
    public String getLastname(){ return lastname; }
    /** Returns a copy with a different last name.
     * @param newLastname replacement last name
     * @return a copy with the supplied last name */
    public EventGuest withLastname(String newLastname){
        return new EventGuest(id, event_id, firstname, newLastname, birthday, status, note);
    }

    /** @return guest birth date, or {@code null} */
    public LocalDate getBirthday(){ return birthday; }
    /** Returns a copy with a different birth date.
     * @param newBirthday replacement birth date
     * @return a copy with the supplied birth date */
    public EventGuest withBirthday(LocalDate newBirthday){
        return new EventGuest(id, event_id, firstname, lastname, newBirthday, status, note);
    }

    /** @return current guest status */
    public EventGuestStatus getStatus(){ return status; }
    /** Returns a copy with a different status.
     * @param newStatus replacement status
     * @return a copy with the supplied status */
    public EventGuest withStatus(EventGuestStatus newStatus){
        return new EventGuest(id, event_id, firstname, lastname, birthday, newStatus, note);
    }

    /** @return optional guest note */
    public String getNote(){ return note; }
    /** Returns a copy with a different note.
     * @param newNote replacement note
     * @return a copy with the supplied note */
    public EventGuest withNote(String newNote){
        return new EventGuest(id, event_id, firstname, lastname, birthday, status, newNote);
    }


    @Override
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
    public int hashCode() {
        if (id != 0){
            return Objects.hash(id);
        }
        return Objects.hash(event_id, firstname, lastname);
    }

}
