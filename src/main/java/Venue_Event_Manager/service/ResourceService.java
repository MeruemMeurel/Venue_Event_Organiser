package Venue_Event_Manager.service;

import Venue_Event_Manager.config.TransactionManager;
import Venue_Event_Manager.domain.model.event.Event;
import Venue_Event_Manager.domain.model.resource.Equipment;
import Venue_Event_Manager.domain.model.resource.Resource;
import Venue_Event_Manager.domain.model.resource.ResourceType;
import Venue_Event_Manager.domain.model.resource.Service;
import Venue_Event_Manager.domain.model.resource.Space;
import Venue_Event_Manager.exception.NotFoundException;
import Venue_Event_Manager.exception.ValidationException;
import Venue_Event_Manager.repository.EquipmentRepository;
import Venue_Event_Manager.repository.EventRepository;
import Venue_Event_Manager.repository.ServiceRepository;
import Venue_Event_Manager.repository.SpaceRepository;
import Venue_Event_Manager.repository.VenueRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ResourceService {

    private final TransactionManager transactionManager;
    private final SpaceRepository spaceRepository;
    private final EquipmentRepository equipmentRepository;
    private final ServiceRepository serviceRepository;
    private final VenueRepository venueRepository;
    private final EventRepository eventRepository;

    /**
     * Initializes ResourceService with all repositories needed to handle resources.
     * @param spaceRepository repository used to access space data
     * @param equipmentRepository repository used to access equipment data
     * @param serviceRepository repository used to access service data
     * @param venueRepository repository used to access venue data
     */
    public ResourceService(SpaceRepository spaceRepository, EquipmentRepository equipmentRepository,
                           ServiceRepository serviceRepository, VenueRepository venueRepository, EventRepository eventRepository) {
        this.transactionManager = TransactionManager.getInstance();
        this.spaceRepository = spaceRepository;
        this.equipmentRepository = equipmentRepository;
        this.serviceRepository = serviceRepository;
        this.venueRepository = venueRepository;
        this.eventRepository = eventRepository;
    }

    /**
     * Gets all resources stored in database.
     * @return List of all resources
     */
    public List<Resource> getAllResources(){
        ArrayList<Resource> resources = new ArrayList<>();

        resources.addAll(getAllSpaces());
        resources.addAll(getAllServices());
        resources.addAll(getAllEquipments());

        return resources;
    }

    /**
     * Gets all spaces stored in database.
     * @return List of all spaces
     */
    public List<Space> getAllSpaces(){
        return transactionManager.inReadOnly(conn ->
                spaceRepository.findAll(conn));
    }

    /**
     * Gets all services stored in database.
     * @return List of all services
     */
    public List<Service> getAllServices(){
        return transactionManager.inReadOnly(conn ->
                serviceRepository.findAll(conn));
    }

    /**
     * Gets all equipments stored in database.
     * @return List of all equipments
     */
    public List<Equipment> getAllEquipments(){
        return  transactionManager.inReadOnly(conn ->
                equipmentRepository.findAll(conn));
    }

    /**
     * Searches all resource types by name.
     * @param name the name or part of name to search
     * @return List of resources matching the given name
     */
    public List<Resource> searchResourceByName(String name){
        validateName(name);

        ArrayList<Resource> resources = new ArrayList<>();
        resources.addAll(searchSpaceByName(name));
        resources.addAll(searchServiceByName(name));
        resources.addAll(searchEquipmentByName(name));
        return resources;
    }

    /**
     * Searches spaces by name.
     * @param name the name or part of name to search
     * @return List of spaces matching the given name
     */
    public List<Space> searchSpaceByName(String name){
        validateName(name);

        return transactionManager.inReadOnly(conn->
                spaceRepository.searchByName(conn, name));
    }

    /**
     * Searches services by name.
     * @param name the name or part of name to search
     * @return List of services matching the given name
     */
    public List<Service> searchServiceByName(String name){
        validateName(name);

        return transactionManager.inReadOnly(conn->
                serviceRepository.searchByName(conn, name));
    }

    /**
     * Searches equipments by name.
     * @param name the name or part of name to search
     * @return List of equipments matching the given name
     */
    public List<Equipment> searchEquipmentByName(String name){
        validateName(name);

        return transactionManager.inReadOnly(
                conn->equipmentRepository.searchByName(conn, name)
        );
    }

    /**
     * Gets a space from its id.
     * @param id the id of the space to find
     * @return Space object if found
     * @throws NotFoundException if no space is found with such id
     */
    public Space getSpaceById(long id){
        return transactionManager.inReadOnly(conn ->
                spaceRepository.findById(conn,id)
                        .orElseThrow(() -> new NotFoundException("No space found with id "+id)));
    }

    /**
     * Gets a service from its id.
     * @param id the id of the service to find
     * @return Service object if found
     * @throws NotFoundException if no service is found with such id
     */
    public Service getServiceById(long id){
        return transactionManager.inReadOnly(conn ->
                serviceRepository.findById(conn,id)
                        .orElseThrow(() -> new NotFoundException("No service found with id "+id)));
    }

    /**
     * Gets equipment from its id.
     * @param id the id of the equipment to find
     * @return Equipment object if found
     * @throws NotFoundException if no equipment is found with such id
     */
    public Equipment getEquipmentById(long id){
        return transactionManager.inReadOnly(conn ->
                equipmentRepository.findById(conn,id)
                        .orElseThrow(() -> new NotFoundException("No equipment found with id "+id)));
    }

    /**
     * Gets a resource from its type and id.
     * @param resourceType the type of resource to find
     * @param id the id of the resource to find
     * @return Resource object if found
     * @throws ValidationException if resource type is not valid
     * @throws NotFoundException if no resource is found with such type and id
     */
    public Resource getResourceById(ResourceType resourceType, long id){
        validateResourceType(resourceType);

        switch (resourceType) {
            case SPACE:
                return getSpaceById(id);
            case EQUIPMENT:
                return getEquipmentById(id);
            case SERVICE:
                return getServiceById(id);
            default:
                throw new ValidationException("Unsupported resource type");
        }
    }

    /**
     * Gets all venue resources linked to a specific venue.
     * Services are not included because they are not linked to venues.
     * @param venueId the id of the venue
     * @return List of venue resources linked to the venue
     */
    public List<Resource> getResourcesByVenue(long venueId){
        validateVenueId(venueId);

        return transactionManager.inReadOnly(conn-> {
            ArrayList<Resource> resources = new ArrayList<>();
            resources.addAll(spaceRepository.findAllByVenueId(conn,venueId));
            resources.addAll(equipmentRepository.findAllByVenueId(conn,venueId));
            return resources;
        });
    }

    /**
     * Gets all spaces linked to a specific venue.
     * @param venueId the id of the venue
     * @return List of spaces linked to the venue
     */
    public List<Space> getSpaceByVenue(long venueId){
        validateVenueId(venueId);

        return transactionManager.inReadOnly(conn->
                spaceRepository.findAllByVenueId(conn,venueId));
    }

    /**
     * Gets all equipments linked to a specific venue.
     * @param venueId the id of the venue
     * @return List of equipments linked to the venue
     */
    public List<Equipment> getEquipmentByVenue(long venueId){
        validateVenueId(venueId);

        return transactionManager.inReadOnly(conn->
                equipmentRepository.findAllByVenueId(conn,venueId));
    }

    /**
     * Inserts a new resource in database.
     * @param resource the resource to insert
     * @return generated id of the new resource
     * @throws ValidationException if resource data or resource type are not valid
     */
    public long create(Resource resource){
        validate(resource);

        return transactionManager.inTransaction(conn->{
            if(resource instanceof Space){
                return spaceRepository.insert(conn,(Space)resource);
            }else if(resource instanceof Service){
                return serviceRepository.insert(conn,(Service)resource);
            }else if(resource instanceof Equipment){
                return equipmentRepository.insert(conn,(Equipment)resource);
            }
            throw new ValidationException("Unsupported resource type");
        });
    }

    /**
     * Updates an existing resource in database.
     * @param resource the resource object with updated data
     * @throws ValidationException if resource data or resource type are not valid
     */
    public void update(Resource resource){
        validate(resource);

        transactionManager.inTransaction(conn->{
            if(resource instanceof Space){
                spaceRepository.update(conn,(Space) resource);
            }else if(resource instanceof Service){
                serviceRepository.update(conn,(Service) resource);
            }else if(resource instanceof Equipment){
                equipmentRepository.update(conn,(Equipment) resource);
            }else{
                throw new ValidationException("Unsupported resource type");
            }
            return null;
        });
    }

    /**
     * Deletes a resource from database.
     * @param resource the resource to delete
     * @throws ValidationException if resource type or id are not valid
     */
    public void delete(Resource resource){
        validateResourceForDelete(resource);

        transactionManager.inTransaction(conn->{
            if(resource instanceof Space){
                spaceRepository.deleteById(conn,((Space)resource).getId());
            }else if(resource instanceof Service){
                serviceRepository.deleteById(conn,((Service)resource).getId());
            }else if(resource instanceof Equipment){
                equipmentRepository.deleteById(conn,((Equipment)resource).getId());
            }else{
                throw new ValidationException("Unsupported resource type");
            }
            return null;
        });
    }

    /**
     * Deletes a resource from database using type and id.
     * @param resourceType the type of resource to delete
     * @param id the id of the resource to delete
     * @throws ValidationException if resource type or id are not valid
     */
    public void delete(ResourceType resourceType, long id){
        validateResourceType(resourceType);
        validateId(id);

        transactionManager.inTransaction(conn->{
            switch (resourceType) {
                case SPACE:
                    spaceRepository.deleteById(conn,id);
                    break;
                case EQUIPMENT:
                    equipmentRepository.deleteById(conn,id);
                    break;
                case SERVICE:
                    serviceRepository.deleteById(conn,id);
                    break;
                default:
                    throw new ValidationException("Unsupported resource type");
            }
            return null;
        });
    }

    /**
     * Gets all resources available for a specific event.
     * @param eventId the id of the event
     * @return List of resources available for the event
     */
    public List<Resource> getAvailableResourcesForEvent(long eventId){
        return transactionManager.inReadOnly(conn -> {
            Event event = eventRepository.findById(conn,eventId)
                    .orElseThrow(() -> new ValidationException("No event found with id " + eventId));

            List<Resource> availableResources = new ArrayList<>();

            availableResources.addAll(
                    spaceRepository.findAvailableSpaces(conn,event.getVenueId(),
                            event.getBeginDatetime(), event.getEndDatetime()));
            availableResources.addAll(
                    equipmentRepository.findAvailableEquipment(conn, event.getVenueId(),
                            event.getBeginDatetime(), event.getEndDatetime()));
            availableResources.addAll(
                    serviceRepository.findAvailableServicesForEvent(conn, eventId));

            return availableResources;
        });
    }

    /**
     * Gets all spaces available in a venue during a time interval
     * @param venueId the id of the venue
     * @param begin the beginning of the time interval
     * @param end the end of the time interval
     * @return List of available spaces
     */
    public List<Space> getAvailableSpaces(long venueId, LocalDateTime begin, LocalDateTime end){
        return transactionManager.inReadOnly(conn ->
                spaceRepository.findAvailableSpaces(conn,venueId, begin, end));
    }

    /**
     * Gets all equipments available in a venue during a time interval.
     * @param venueId the id of the venue
     * @param begin the beginning of the time interval
     * @param end the end of the time interval
     * @return List of available equipments
     */
    public List<Equipment> getAvailableEquipment(long venueId, LocalDateTime begin, LocalDateTime end){
        return transactionManager.inReadOnly(conn ->
                equipmentRepository.findAvailableEquipment(conn,venueId, begin, end));
    }

    /**
     * Gets all services available for an event.
     * Services are not linked to venues, so availability must be defined by event requirements.
     * @param eventId the id of the event
     * @return List of available services
     */
    public List<Service> getAvailableServicesForEvent(long eventId){
        return transactionManager.inReadOnly(conn ->
                serviceRepository.findAvailableServicesForEvent(conn, eventId)
        );
    }

    //validation
    /**
     * Validates all common resource fields before insert or update.
     * @param resource the resource to validate
     * @throws ValidationException if one or more fields are not valid
     */
    private void validate(Resource resource){
        validateResourceNotNull(resource);
        validateName(resource.getName());

        if(resource instanceof Space){
            validateVenueId(resource.getVenueId());
        }else if(resource instanceof Equipment){
            validateNullableVenueId(resource.getVenueId());
            validateQuantity((Equipment) resource);
        }else if(resource instanceof Service){
            validateServiceVenueId(resource.getVenueId());
        }else{
            throw new ValidationException("Unsupported resource type");
        }
    }

    /**
     * Validates if a venue exists.
     * @param venueId the id of the venue to validate
     * @throws ValidationException if no venue is found with such id
     */
    private void validateVenueId(Long venueId){
        if(venueId == null || venueId <= 0) {
            throw new ValidationException("Venue id is not valid");
        }

        transactionManager.inReadOnly(conn-> {
                venueRepository.findById(conn,venueId)
                        .orElseThrow(() -> new ValidationException("No venue found with id "+venueId));
                return null;
        });
    }

    /**
     * Validates if a nullable venue exists when it is provided.
     * @param venueId the nullable id of the venue to validate
     * @throws ValidationException if venue id is invalid or no venue is found with such id
     */
    private void validateNullableVenueId(Long venueId){
        if(venueId != null){
            validateVenueId(venueId);
        }
    }

    /**
     * Validates resource name.
     * @param name the name to validate
     * @throws ValidationException if name is empty
     */
    private void validateName(String name){
        if(name == null || name.isBlank()) {
            throw new ValidationException("Name of resource is empty");
        }
    }

    /**
     * Validates equipment quantity.
     * @param resource the equipment to validate
     * @throws ValidationException if quantity is less or equal to 0
     */
    private void validateQuantity(Equipment resource){
        if(resource.getTotalQuantity() <= 0) {
            throw new ValidationException("Quantity of resource is less than 0");
        }
    }

    /**
     * Validates that a resource is not null.
     * @param resource the resource to validate
     * @throws ValidationException if resource is null
     */
    private void validateResourceNotNull(Resource resource){
        if(resource == null){
            throw new ValidationException("Resource is null");
        }
    }

    /**
     * Validates that a service is not linked to a venue.
     * @param venueId the venue id stored in the service resource
     * @throws ValidationException if service has a venue id
     */
    private void validateServiceVenueId(Long venueId){
        if(venueId != null){
            throw new ValidationException("Service cannot be linked to a venue");
        }
    }

    /**
     * Validates resource type.
     * @param resourceType the resource type to validate
     * @throws ValidationException if resource type is null
     */
    private void validateResourceType(ResourceType resourceType){
        if(resourceType == null){
            throw new ValidationException("Resource type is null");
        }
    }

    /**
     * Validates resource id.
     * @param id the id to validate
     * @throws ValidationException if id is not valid
     */
    private void validateId(long id){
        if(id <= 0){
            throw new ValidationException("Resource id is not valid");
        }
    }

    /**
     * Validates a resource before delete.
     * @param resource the resource to validate
     * @throws ValidationException if resource is null, has invalid id or unsupported type
     */
    private void validateResourceForDelete(Resource resource){
        validateResourceNotNull(resource);
        validateId(resource.getId());

        if(!(resource instanceof Space) && !(resource instanceof Equipment) && !(resource instanceof Service)){
            throw new ValidationException("Unsupported resource type");
        }
    }

}
