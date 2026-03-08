package Venue_Event_Manager.domain.model.resource;

import java.util.Objects;

/**
 * Domain entity representing Equipment.
 * Can be generic (venue_id is null) or specific to a Venue.
 */
public class Equipment {

    //attributes
    private final long id;
    private final Long venue_id; //reference to Venue, NULLABLE
    private final String name;
    private final String description;
    private final int total_quantity;


    //costructors
    /** Initializes an empty equipment with default and empty values. */
    public Equipment(){
        this(0, 0, "", "", 0);
    }

    /** Master constructor for full initialization. */
    public Equipment(long id, Long venue_id, String name, String description, int total_quantity){
        this.id = id;
        this.venue_id = venue_id;
        this.name = name;
        this.description = description;
        this.total_quantity = total_quantity;
    }

    /** Constructor for unsaved equipment (ID defaults to 0). */
    public Equipment(long venue_id, String name, String description, int total_quantity){
        this(0, venue_id, name, description, total_quantity);
    }


    //getters and whiters
    public long getId(){ return id; }
    public Equipment withId(long newId){
        return new Equipment(newId, venue_id, name, description, total_quantity);
    }

    public long getVenueId(){ return venue_id; }
    public Equipment withVenueId(long newVenueId){
        return new Equipment(id, newVenueId, name, description, total_quantity);
    }

    public String getName(){ return name; }
    public Equipment withName(String newName){
        return new Equipment(id, venue_id, newName, description, total_quantity);
    }

    public String getDescription(){ return description; }
    public Equipment withDescription(String newDescription){
        return new Equipment(id, venue_id, name, newDescription, total_quantity);
    }

    public int getTotal_quantity(){ return total_quantity; }
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

    /** Compares equipment based on ID or name and venue_id uniqueness. */
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