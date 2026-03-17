package Venue_Event_Manager.domain.model.resource;

import java.util.Objects;

/**
 * Domain entity representing Equipment.
 * Can be generic (venue_id is null) or specific to a Venue.
 */
public class Equipment extends Resource {

    //attributes
    private final int total_quantity;


    //costructors
    /** Initializes an empty equipment with default and empty values. */
    public Equipment(){
        super();
        this.total_quantity = 0;
    }

    /** Master constructor for full initialization. */
    public Equipment(long id, Long venue_id, String name, String description, int total_quantity){
        super(id, venue_id, name, description);
        this.total_quantity = total_quantity;
    }

    /** Constructor for unsaved equipment (ID defaults to 0). */
    public Equipment(long venue_id, String name, String description, int total_quantity){
        super(venue_id, name, description);
        this.total_quantity = total_quantity;
    }


    //getters and whiters
    public Equipment withId(long newId){
        return new Equipment(newId, venue_id, name, description, total_quantity);
    }

    public Equipment withVenueId(long newVenueId){
        return new Equipment(id, newVenueId, name, description, total_quantity);
    }

    public Equipment withName(String newName){
        return new Equipment(id, venue_id, newName, description, total_quantity);
    }

    public Equipment withDescription(String newDescription){
        return new Equipment(id, venue_id, name, newDescription, total_quantity);
    }

    public int getTotalQuantity(){ return total_quantity; }
    public Equipment withTotalQuantity(int newTotalQuantity){
        return new Equipment(id, venue_id, name, description, newTotalQuantity);
    }


    @Override
    public String toString() {
        return "Equipment{" +
                "id=" + id + "; " +
                "venue_id=" + venue_id + "; " +
                "name=" + name + "; " +
                "description=" + description + "; " +
                "total_quantity=" + total_quantity + ";" +
                "}";
    }

    /** Compares equipment based on ID or venue_id and name uniqueness */
    @Override
    public boolean equals(Object other){
        if (this == other) return true;
        if (other == null || getClass() != other.getClass()) return false;
        Equipment equipment = (Equipment) other;
        if (id != 0 && equipment.id != 0) return id == equipment.id;
        return venue_id == equipment.venue_id && Objects.equals(name, equipment.name);
    }

    @Override
    public int hashCode() {
        if (id != 0) return Objects.hash(id);
        return Objects.hash(venue_id, name);
    }
}