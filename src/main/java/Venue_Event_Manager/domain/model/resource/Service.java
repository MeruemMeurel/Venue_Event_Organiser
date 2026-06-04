package Venue_Event_Manager.domain.model.resource;

import java.util.Objects;

/**
 * Domain entity representing a Service available for events.
 */
public class Service extends Resource {

    //costructors
    /** Initializes an empty service with default and empty values. */
    public Service(){
        super();
    }

    /** Master constructor for full initialization. */
    public Service(long id, String name, String description){
        super(id, null, name, description);
    }

    /** Constructor for unsaved service (ID defaults to 0). */
    public Service(String name, String description){
        super(null, name, description);
    }


    //whiters
    public Service withId(long newId){
        return new Service(newId, name, description);
    }

    public Service withName(String newName){
        return new Service(id, newName, description);
    }

    public Service withDescription(String newDescription){
        return new Service(id, name, newDescription);
    }


    @Override
    public String toString() {
        return "Service{" +
                "id=" + id + "; " +
                "name=" + name + "; " +
                "description=" + description + ";" +
                "}";
    }

    /** Compares service based on ID or name uniqueness. */
    @Override
    public boolean equals(Object other){
        if (this == other) return true;
        if (other == null || getClass() != other.getClass()) return false;
        Service service = (Service) other;
        if (id != 0 && service.id != 0){
            return Objects.equals(id, service.id);
        }
        return Objects.equals(name, service.name);
    }

    @Override
    public int hashCode() {
        if (id != 0){
            return Objects.hash(id);
        }
        return Objects.hash(name);
    }
}