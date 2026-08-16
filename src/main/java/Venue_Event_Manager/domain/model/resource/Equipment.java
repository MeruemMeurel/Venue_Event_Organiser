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

    /**
     * Master constructor for full initialization.
     * @param id persistent identifier
     * @param venue_id associated venue identifier, or {@code null} for generic equipment
     * @param name equipment name
     * @param description equipment description
     * @param total_quantity total available quantity
     */
    public Equipment(long id, Long venue_id, String name, String description, int total_quantity){
        super(id, venue_id, name, description);
        this.total_quantity = total_quantity;
    }

    /**
     * Creates unsaved equipment whose identifier defaults to zero.
     * @param venue_id associated venue identifier, or {@code null} for generic equipment
     * @param name equipment name
     * @param description equipment description
     * @param total_quantity total available quantity
     */
    public Equipment(Long venue_id, String name, String description, int total_quantity){
        super(venue_id, name, description);
        this.total_quantity = total_quantity;
    }


    //getters and whiters
    /** Returns a copy with a different identifier.
     * @param newId replacement identifier
     * @return a copy with the supplied identifier */
    /**
     * Performs the {@code withId} operation.
     * @param newId newId value
     * @return operation result
     */
    public Equipment withId(long newId){
        return new Equipment(newId, venue_id, name, description, total_quantity);
    }

    /** Returns a copy associated with a different venue.
     * @param newVenueId replacement venue identifier
     * @return a copy with the supplied venue */
    /**
     * Performs the {@code withVenueId} operation.
     * @param newVenueId newVenueId value
     * @return operation result
     */
    public Equipment withVenueId(long newVenueId){
        return new Equipment(id, newVenueId, name, description, total_quantity);
    }

    /** Returns a copy with a different name.
     * @param newName replacement name
     * @return a copy with the supplied name */
    /**
     * Performs the {@code withName} operation.
     * @param newName newName value
     * @return operation result
     */
    public Equipment withName(String newName){
        return new Equipment(id, venue_id, newName, description, total_quantity);
    }

    /** Returns a copy with a different description.
     * @param newDescription replacement description
     * @return a copy with the supplied description */
    /**
     * Performs the {@code withDescription} operation.
     * @param newDescription newDescription value
     * @return operation result
     */
    public Equipment withDescription(String newDescription){
        return new Equipment(id, venue_id, name, newDescription, total_quantity);
    }

    /** Returns the total number of available units.
     * @return total available quantity */
    /**
     * Performs the {@code getTotalQuantity} operation.
     * @return operation result
     */
    public int getTotalQuantity(){ return total_quantity; }
    /** Returns a copy with a different total quantity.
     * @param newTotalQuantity replacement quantity
     * @return a copy with the supplied quantity */
    /**
     * Performs the {@code withTotalQuantity} operation.
     * @param newTotalQuantity newTotalQuantity value
     * @return operation result
     */
    public Equipment withTotalQuantity(int newTotalQuantity){
        return new Equipment(id, venue_id, name, description, newTotalQuantity);
    }


    @Override
    /**
     * Performs the {@code toString} operation.
     * @return operation result
     */
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
    /**
     * Performs the {@code equals} operation.
     * @param other other value
     * @return operation result
     */
    public boolean equals(Object other){
        if (this == other) return true;
        if (other == null || getClass() != other.getClass()) return false;
        Equipment equipment = (Equipment) other;
        if (id != 0 && equipment.id != 0){
            return Objects.equals(id, equipment.id);
        }
        return Objects.equals(venue_id, equipment.venue_id) && Objects.equals(name, equipment.name);
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
        return Objects.hash(venue_id, name);
    }
}
