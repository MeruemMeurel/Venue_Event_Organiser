package venue.event.manager.domain.model.resource;

import java.util.Objects;

/**
 * Domain entity representing a physical Space within a Venue.
 * Implemented as an immutable object, with multiple constructors to handle default and nullable fields.
 */
public class Space extends Resource {

    //costructors
    /** Initializes an empty space with default and empty values. */
    public Space(){
        super();
    }

    /** Master constructor for full initialization.
     *
     * @param id persistent identifier
     * @param venue_id hosting venue identifier
     *
     * @param name space name
     * @param description space description */
    public Space(long id, long venue_id, String name, String description){
        super(id, venue_id, name, description);
    }

    /** Creates an unsaved space.
     *
     * @param venue_id hosting venue identifier
     * @param name space name
     * @param description space description */
    public Space(long venue_id, String name, String description){
        super(venue_id, name, description);
    }


    //whiters
    /**
     * Returns a copy with an updated id.
     * @param newId replacement id
     * @return copy with the updated id
     */
    public Space withId(long newId){
        return new Space(newId, venue_id, name, description);
    }

    /**
     * Returns a copy with an updated venue id.
     * @param newVenueId replacement venue id
     * @return copy with the updated venue id
     */
    public Space withVenueId(Long newVenueId){
        return new Space(id, newVenueId, name, description);
    }

    /**
     * Returns a copy with an updated name.
     * @param newName replacement name
     * @return copy with the updated name
     */
    public Space withName(String newName){
        return new Space(id, venue_id, newName, description);
    }

    /**
     * Returns a copy with an updated description.
     * @param newDescription replacement description
     * @return copy with the updated description
     */
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
        if (id != 0 && space.id != 0){
            return Objects.equals(id, space.id);
        }
        return Objects.equals(venue_id, space.venue_id) && Objects.equals(name, space.name);
    }

    @Override
    public int hashCode() {
        if (id != 0){
            return Objects.hash(id);
        }
        return Objects.hash(venue_id, name);
    }
}
