package Venue_Event_Manager.domain.model.resource;

/**
 * Enum representing all resource types handled by ResourceService.
 */
public enum ResourceType {
    /** A reservable physical space. */
    SPACE,
    /** Equipment associated with a venue or shared globally. */
    EQUIPMENT,
    /** A service that can be requested for an event. */
    SERVICE
}
