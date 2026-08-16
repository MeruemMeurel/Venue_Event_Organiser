package venue.event.manager.domain.model.resource;

import java.util.Objects;

/**
 * Abstract base class representing a generic resource (Space, Service, or Equipment)
 */
public abstract class Resource {

    //attributes
    /** Persistent resource identifier. */
    protected final long id;
    /** Associated venue identifier, when the resource type supports one. */
    protected final Long venue_id;
    /** Human-readable resource name. */
    protected final String name;
    /** Optional resource description. */
    protected final String description;


    //constructors
    /** Initializes an empty resource with default and empty values. */
    public Resource(){
        this(0, null, "", "");
    }

    /**
     * Master constructor for full initialization.
     * @param id persistent identifier
     * @param venue_id associated venue identifier, or {@code null}
     * @param name resource name
     * @param description resource description
     */
    protected Resource(long id, Long venue_id, String name, String description) {
        this.id = id;
        this.venue_id = venue_id;
        this.name = Objects.requireNonNull(name, "Resource name cannot be null");
        this.description = description;
    }

    /**
     * Constructor for an unsaved resource whose identifier defaults to zero.
     * @param venue_id associated venue identifier, or {@code null}
     * @param name resource name
     * @param description resource description
     */
    public Resource(Long venue_id, String name, String description){
        this(0, venue_id, name, description);
    }


    //getters
    /** Gets the persistent identifier.
     * @return persistent identifier, or zero for an unsaved resource */
    public long getId() {
        return id;
    }
    /** Gets the associated venue identifier.
     * @return associated venue identifier, or {@code null} */
    public Long getVenueId() {
        return venue_id;
    }
    /** Gets the resource name.
     * @return resource name */
    public String getName() {
        return name;
    }
    /** Gets the resource description.
     * @return resource description */
    public String getDescription() {
        return description;
    }


    @Override
    public String toString() {
        return "Resource{" +
                "class="  + this.getClass() + "; " +
                "id=" + id + "; " +
                "venue_id=" + venue_id + "; " +
                "name=" + name + "; " +
                "description=" + description + ";" +
                "}";
    }

    /** Compares equipment based on ID uniqueness */
    @Override
    public boolean equals(Object other) {
        if (this == other) return true;
        if (other == null || getClass() != other.getClass()) return false;
        Resource resource = (Resource) other;
        if (id == 0 && resource.id == 0){
            return false;
        }
        return Objects.equals(id, resource.id);
    }

    @Override
    public int hashCode() {
        if (id == 0){
            return super.hashCode();
        }
        return Objects.hash(id);
    }
}
