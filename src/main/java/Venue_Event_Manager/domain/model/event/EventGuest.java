package Venue_Event_Manager.domain.model.event;

import java.time.LocalDateTime;
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
    private final LocalDateTime birthday; //NULLABLE
    private final EventGuestStatus status;
    private final String note; //NULLABLE


    //constructors
    /** Initializes an empty event guest with default and empty values. */
    public EventGuest(){
        this(0, 0, "", "", null, null, "");
    }

    /** Master constructor for full initialization. */
    public EventGuest(long id, long event_id, String firstname, String lastname, LocalDateTime birthday,
                      EventGuestStatus status, String note){
        this.id = id;
        this.event_id = event_id;
        this.firstname = firstname;
        this.lastname = lastname;
        this.birthday = birthday;
        this.status = status != null ? status : INVITED;
        this.note = note;
    }

    /** Constructor for unsaved event guest (ID defaults to 0). */
    public EventGuest(long event_id, String firstname, String lastname, LocalDateTime birthday, EventGuestStatus status,
                      String note){
        this(0, event_id, firstname, lastname, birthday, status, note);
    }


    //getters and withers
    public long getId(){ return id; }
    public EventGuest withId(long newId){
        return new EventGuest(newId, event_id, firstname, lastname, birthday, status, note);
    }

    public long getEventId(){ return event_id; }
    public EventGuest withEventId(long newEventId){
        return new EventGuest(id, newEventId, firstname, lastname, birthday, status, note);
    }

    public String getFirstname(){ return firstname; }
    public EventGuest withFirstname(String newFirstname){
        return new EventGuest(id, event_id, newFirstname, lastname, birthday, status, note);
    }

    public String getLastname(){ return lastname; }
    public EventGuest withLastname(String newLastname){
        return new EventGuest(id, event_id, firstname, newLastname, birthday, status, note);
    }

    public LocalDateTime getBirthday(){ return birthday; }
    public EventGuest withBirthday(LocalDateTime newBirthday){
        return new EventGuest(id, event_id, firstname, lastname, newBirthday, status, note);
    }

    public EventGuestStatus getStatus(){ return status; }
    public EventGuest withStatus(EventGuestStatus newStatus){
        return new EventGuest(id, event_id, firstname, lastname, birthday, newStatus, note);
    }

    public String getNote(){ return note; }
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
        if (id != 0 && eventGuest.id != 0) return id == eventGuest.id;
        return event_id == eventGuest.event_id && Objects.equals(firstname, eventGuest.firstname) &&
                Objects.equals(lastname, eventGuest.lastname);
    }

    @Override
    public int hashCode() {
        if (id != 0) return Objects.hash(id);
        return Objects.hash(event_id, firstname, lastname);
    }

}