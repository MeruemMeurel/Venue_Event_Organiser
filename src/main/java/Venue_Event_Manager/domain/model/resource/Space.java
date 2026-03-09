package Venue_Event_Manager.domain.model.resource;

import java.util.Objects;

/**
 * Domain entity representing a physical Space within a Venue.
 * Implemented as an immutable object, with multiple constructors to handle default and nullable fields.
 */
public class Space {

    //attributes
    private final long id;
    private final long venue_id; //reference to Venue
    private final String name;
    private final String description;


    //costructors
    /** Initializes an empty space with default and empty values. */
    public Space(){
        this(0, 0, "", "");
    }

    /** Master constructor for full initialization. */
    public Space(long id, long venue_id, String name, String description){
        this.id = id;
        this.venue_id = venue_id;
        this.name = name;
        this.description = description;
    }

    /** Constructor for unsaved space (ID defaults to 0). */
    public Space(long venue_id, String name, String description){
        this(0, venue_id, name, description);
    }


    //getters and whiters
    public long getId(){ return id; }
    public Space withId(long newId){
        return new Space(newId, venue_id, name, description);
    }

    public long getVenueId(){ return venue_id; }
    public Space withVenueId(long newVenueId){
        return new Space(id, newVenueId, name, description);
    }

    public String getName(){ return name; }
    public Space withName(String newName){
        return new Space(id, venue_id, newName, description);
    }

    public String getDescription(){ return description; }
    public Space withDescription(String newDescription){
        return new Space(id, venue_id, name, newDescription);
    }


    @Override
    public String toString() {
        return "Space{" +
                "id=" + id + "; " +
                "venue_id=" + venue_id + "; " +
                "name=" + name + "; " +
                "description=" + description + ";" +
                "}";
    }

    /** Compares space based on ID or name and venue_id uniqueness. */
    @Override
    public boolean equals(Object other){
        if (this == other) return true;
        if (other == null || getClass() != other.getClass()) return false;
        Space space = (Space) other;
        if (id != 0 && space.id != 0) return id == space.id;
        return venue_id == space.venue_id && Objects.equals(name, space.name);
    }

    @Override
    public int hashCode() {
        if (id != 0) return Objects.hash(id);
        return Objects.hash(venue_id, name);
    }
}