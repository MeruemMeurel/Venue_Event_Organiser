package Venue_Event_Manager.domain.model.venue;

public class Venue {

    private final long id;
    private final String name;
    private final String description;
    private final Address address;

    public Venue(){
        this(0,"","",null);
    }
    public Venue(long id, String name, String description, Address address){
        this.id = id;
        this.name = name;
        this.description = description;
        this.address = address;
    }

}
