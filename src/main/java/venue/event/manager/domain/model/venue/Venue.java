package venue.event.manager.domain.model.venue;

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
     * Returns the id.
     * @return id
     */
    public long getId() { return id; }
    /**
     * Returns a copy with an updated id.
     * @param newId replacement id
     * @return copy with the updated id
     */
    public Venue withId(long newId){
        return new Venue(newId, name, description, address);
    }

    /**
     * Returns the name.
     * @return name
     */
    public String getName(){ return name; }
    /**
     * Returns a copy with an updated name.
     * @param newName replacement name
     * @return copy with the updated name
     */
    public Venue withName(String newName){
        return new Venue(id, newName, description, address);
    }

    /**
     * Returns the description.
     * @return description
     */
    public String getDescription(){ return description; }
    /**
     * Returns a copy with an updated description.
     * @param newDescription replacement description
     * @return copy with the updated description
     */
    public Venue withDescription(String newDescription){
        return new Venue(id, name, newDescription, address);
    }

    /**
     * Returns the address.
     * @return address
     */
    public Address getAddress(){ return address; }
    /**
     * Returns a copy with an updated address.
     * @param newAddress replacement address
     * @return copy with the updated address
     */
    public Venue withAddress(Address newAddress){
        return new Venue(id, name, description, newAddress);
    }


    @Override
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
    public int hashCode(){
        if(id != 0){
            return Objects.hash(id);
        }
        return Objects.hash(address);
    }
}
