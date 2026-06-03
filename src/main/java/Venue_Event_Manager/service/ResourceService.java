package Venue_Event_Manager.service;

import Venue_Event_Manager.config.TransactionManager;
import Venue_Event_Manager.domain.model.resource.Equipment;
import Venue_Event_Manager.domain.model.resource.Resource;
import Venue_Event_Manager.domain.model.resource.Service;
import Venue_Event_Manager.domain.model.resource.Space;
import Venue_Event_Manager.exception.NotFoundException;
import Venue_Event_Manager.exception.ValidationException;
import Venue_Event_Manager.repository.EquipmentRepository;
import Venue_Event_Manager.repository.ServiceRepository;
import Venue_Event_Manager.repository.SpaceRepository;
import Venue_Event_Manager.repository.VenueRepository;

import java.util.ArrayList;
import java.util.List;

public class ResourceService {

    private final TransactionManager transactionManager;
    private final SpaceRepository spaceRepository;
    private final EquipmentRepository equipmentRepository;
    private final ServiceRepository serviceRepository;
    private final VenueRepository venueRepository;

    /**
     * Initializes ResourceService with all repositories needed to handle resources.
     * @param spaceRepository repository used to access space data
     * @param equipmentRepository repository used to access equipment data
     * @param serviceRepository repository used to access service data
     * @param venueRepository repository used to access venue data
     */
    public ResourceService(SpaceRepository spaceRepository, EquipmentRepository equipmentRepository,
                           ServiceRepository serviceRepository, VenueRepository venueRepository) {
        this.transactionManager = TransactionManager.getInstance();
        this.spaceRepository = spaceRepository;
        this.equipmentRepository = equipmentRepository;
        this.serviceRepository = serviceRepository;
        this.venueRepository = venueRepository;
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
        return transactionManager.inReadOnly(conn->
                spaceRepository.searchByName(conn, name));
    }

    /**
     * Searches services by name.
     * @param name the name or part of name to search
     * @return List of services matching the given name
     */
    public List<Service> searchServiceByName(String name){
        return transactionManager.inReadOnly(conn->
                serviceRepository.searchByName(conn, name));
    }

    /**
     * Searches equipments by name.
     * @param name the name or part of name to search
     * @return List of equipments matching the given name
     */
    public List<Equipment> searchEquipmentByName(String name){
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
     * Gets an equipment from its id.
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
     * Gets all venue resources linked to a specific venue.
     * @param venueId the id of the venue
     * @return List of resources linked to the venue
     */
    public List<Resource> getResourcesByVenue(long venueId){
        ArrayList<Resource> resources = new ArrayList<>();
        resources.addAll(getSpaceByVenue(venueId));
        resources.addAll(getEquipmentByVenue(venueId));
        return resources;
    }

    /**
     * Gets all spaces linked to a specific venue.
     * @param venueId the id of the venue
     * @return List of spaces linked to the venue
     */
    public List<Space> getSpaceByVenue(long venueId){
        return transactionManager.inReadOnly(conn->
                spaceRepository.findAllByVenueId(conn,venueId));
    }

    /**
     * Gets all equipments linked to a specific venue.
     * @param venueId the id of the venue
     * @return List of equipments linked to the venue
     */
    public List<Equipment> getEquipmentByVenue(long venueId){
        return transactionManager.inReadOnly(conn->
                equipmentRepository.findAllByVenueId(conn,venueId));
    }

    /**
     * Inserts a new resource in database.
     * @param resource the resource to insert
     */
    public void create(Resource resource){
        transactionManager.inTransaction(conn->{
            if(resource instanceof Space){
                spaceRepository.insert(conn,(Space)resource);
            }else if(resource instanceof Service){
                serviceRepository.insert(conn,(Service)resource);
            }else if(resource instanceof Equipment){
                equipmentRepository.insert(conn,(Equipment)resource);
            }
            return null;
        });
    }

    /**
     * Updates an existing resource in database.
     * @param resource the resource object with updated data
     */
    public void update(Resource resource){
        transactionManager.inTransaction(conn->{
            if(resource instanceof Space){
                spaceRepository.update(conn,(Space) resource);
            }else if(resource instanceof Service){
                serviceRepository.update(conn,(Service) resource);
            }else if(resource instanceof Equipment){
                equipmentRepository.update(conn,(Equipment) resource);
            }
            return null;
        });
    }

    /**
     * Deletes a resource from database.
     * @param resource the resource to delete
     */
    public void delete(Resource resource){
        transactionManager.inTransaction(conn->{
            if(resource instanceof Space){
                spaceRepository.deleteById(conn,((Space)resource).getId());
            }else if(resource instanceof Service){
                serviceRepository.deleteById(conn,((Service)resource).getId());
            }else if(resource instanceof Equipment){
                equipmentRepository.deleteById(conn,((Equipment)resource).getId());
            }
            return null;
        });
    }

    //validation
    /**
     * Validates all common resource fields before insert or update.
     * @param resource the resource to validate
     * @throws ValidationException if one or more fields are not valid
     */
    private void validate(Resource resource){
       validateVenueId(resource.getVenueId());
       validateName(resource.getName());
       if(resource instanceof Equipment){
           validateQuantity((Equipment) resource);
       }
    }

    /**
     * Validates if a venue exists.
     * @param venueId the id of the venue to validate
     * @throws ValidationException if no venue is found with such id
     */
    private void validateVenueId(long venueId){
        if(venueId > 0) {
            transactionManager.inReadOnly(conn-> {
                    venueRepository.findById(conn,venueId)
                            .orElseThrow(() -> new ValidationException("No venue found with id "+venueId));
                    return null;
            });
        }
    }

    /**
     * Validates resource name.
     * @param name the name to validate
     * @throws ValidationException if name is empty
     */
    private void validateName(String name){
        if(name == null || name.isEmpty()) {
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

}
