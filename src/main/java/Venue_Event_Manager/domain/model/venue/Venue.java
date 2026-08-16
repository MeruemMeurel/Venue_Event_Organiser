package Venue_Event_Manager.domain.model.venue;

import java.util.Objects;

/**
 * Domain entity representing a physical Venue.
 * Implemented as an immutable object, with multiple constructors to handle default and nullable fields.
 */
public class Venue {

    //attributes
    private final long id;
    private final String name;
    private final String description;
    private final Address address;


    //costructors
    /** Initializes an empty venue with default values. */
    public Venue(){ this(0,"","",null); }

    /** Master constructor for full initialization.
     *
     * @param id persistent identifier
     * @param name venue name
     * @param description venue description
     *
     * @param address venue address */
    public Venue(long id, String name, String description, Address address){
        this.id = id;
        this.name = name;
        this.description = description;
        this.address = address;
    }

    /** Creates an unsaved venue.
     *
     * @param name venue name
     * @param description venue description
     * @param address venue address */
    public Venue(String name, String description, Address address){
        this(0, name, description, address);
    }


    //getters and withers
    /**
     * Performs the {@code getId} operation.
     * @return operation result
     */
    public long getId() { return id; }
    /**
     * Performs the {@code withId} operation.
     * @param newId newId value
     * @return operation result
     */
    public Venue withId(long newId){
        return new Venue(newId, name, description, address);
    }

    /**
     * Performs the {@code getName} operation.
     * @return operation result
     */
    public String getName(){ return name; }
    /**
     * Performs the {@code withName} operation.
     * @param newName newName value
     * @return operation result
     */
    public Venue withName(String newName){
        return new Venue(id, newName, description, address);
    }

    /**
     * Performs the {@code getDescription} operation.
     * @return operation result
     */
    public String getDescription(){ return description; }
    /**
     * Performs the {@code withDescription} operation.
     * @param newDescription newDescription value
     * @return operation result
     */
    public Venue withDescription(String newDescription){
        return new Venue(id, name, newDescription, address);
    }

    /**
     * Performs the {@code getAddress} operation.
     * @return operation result
     */
    public Address getAddress(){ return address; }
    /**
     * Performs the {@code withAddress} operation.
     * @param newAddress newAddress value
     * @return operation result
     */
    public Venue withAddress(Address newAddress){
        return new Venue(id, name, description, newAddress);
    }


    @Override
    /**
     * Performs the {@code toString} operation.
     * @return operation result
     */
    public String toString(){
        return "Venue{" +
                "id=" + id + "; " +
                "name=" + name + "; " +
                "description=" + description + "; " +
                address.toString() + ";" +
                "}" ;
    }

    /** Compares venues based on ID or physical address uniqueness. */
    @Override
    /**
     * Performs the {@code equals} operation.
     * @param other other value
     * @return operation result
     */
    public boolean equals(Object other){
        if (this == other) return true;
        if (other == null || getClass() != other.getClass()) return false;
        Venue venue = (Venue) other;
        if (id != 0 && venue.id != 0) {
            return Objects.equals(id, venue.id);
        }
        return Objects.equals(address, venue.address);
    }

    @Override
    /**
     * Performs the {@code hashCode} operation.
     * @return operation result
     */
    public int hashCode(){
        if(id != 0){
            return Objects.hash(id);
        }
        return Objects.hash(address);
    }
}
