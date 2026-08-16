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

    /** Master constructor for full initialization.
     *
     * @param id persistent identifier
     * @param name service name
     * @param description service description */
    public Service(long id, String name, String description){
        super(id, null, name, description);
    }

    /** Creates an unsaved service.
     *
     * @param name service name
     * @param description service description */
    public Service(String name, String description){
        super(null, name, description);
    }


    //whiters
    /**
     * Performs the {@code withId} operation.
     * @param newId newId value
     * @return operation result
     */
    public Service withId(long newId){
        return new Service(newId, name, description);
    }

    /**
     * Performs the {@code withName} operation.
     * @param newName newName value
     * @return operation result
     */
    public Service withName(String newName){
        return new Service(id, newName, description);
    }

    /**
     * Performs the {@code withDescription} operation.
     * @param newDescription newDescription value
     * @return operation result
     */
    public Service withDescription(String newDescription){
        return new Service(id, name, newDescription);
    }


    @Override
    /**
     * Performs the {@code toString} operation.
     * @return operation result
     */
    public String toString() {
        return "Service{" +
                "id=" + id + "; " +
                "name=" + name + "; " +
                "description=" + description + ";" +
                "}";
    }

    /** Compares service based on ID or name uniqueness. */
    @Override
    /**
     * Performs the {@code equals} operation.
     * @param other other value
     * @return operation result
     */
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
    /**
     * Performs the {@code hashCode} operation.
     * @return operation result
     */
    public int hashCode() {
        if (id != 0){
            return Objects.hash(id);
        }
        return Objects.hash(name);
    }
}
