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

    public ResourceService(SpaceRepository spaceRepository, EquipmentRepository equipmentRepository,
                           ServiceRepository serviceRepository, VenueRepository venueRepository) {
        this.transactionManager = TransactionManager.getInstance();
        this.spaceRepository = spaceRepository;
        this.equipmentRepository = equipmentRepository;
        this.serviceRepository = serviceRepository;
        this.venueRepository = venueRepository;
    }

    public List<Resource> getAllResources(){
        ArrayList<Resource> resources = new ArrayList<>();

        resources.addAll(getAllSpaces());
        resources.addAll(getAllServices());
        resources.addAll(getAllEquipments());

        return resources;
    }

    public List<Space> getAllSpaces(){
        return transactionManager.inReadOnly(conn ->
                spaceRepository.findAll(conn));
    }

    public List<Service> getAllServices(){
        return transactionManager.inReadOnly(conn ->
                serviceRepository.findAll(conn));
    }

    public List<Equipment> getAllEquipments(){
        return  transactionManager.inReadOnly(conn ->
                equipmentRepository.findAll(conn));
    }

    public List<Resource> searchResourceByName(String name){
        ArrayList<Resource> resources = new ArrayList<>();
        resources.addAll(searchSpaceByName(name));
        resources.addAll(searchServiceByName(name));
        resources.addAll(searchEquipmentByName(name));
        return resources;
    }

    public List<Space> searchSpaceByName(String name){
        return transactionManager.inReadOnly(conn->
                spaceRepository.searchByName(conn, name));
    }

    public List<Service> searchServiceByName(String name){
        return transactionManager.inReadOnly(conn->
                serviceRepository.searchByName(conn, name));
    }

    public List<Equipment> searchEquipmentByName(String name){
        return transactionManager.inReadOnly(
                conn->equipmentRepository.searchByName(conn, name)
        );
    }

    public Space getSpaceById(long id){
        return transactionManager.inReadOnly(conn ->
                spaceRepository.findById(conn,id)
                        .orElseThrow(() -> new NotFoundException("No space found with id "+id)));
    }

    public Service getServiceById(long id){
        return transactionManager.inReadOnly(conn ->
                serviceRepository.findById(conn,id)
                        .orElseThrow(() -> new NotFoundException("No service found with id "+id)));
    }

    public Equipment getEquipmentById(long id){
        return transactionManager.inReadOnly(conn ->
                equipmentRepository.findById(conn,id)
                        .orElseThrow(() -> new NotFoundException("No equipment found with id "+id)));
    }

    public List<Resource> getResourcesByVenue(long venueId){
        ArrayList<Resource> resources = new ArrayList<>();
        resources.addAll(getSpaceByVenue(venueId));
        resources.addAll(getEquipmentByVenue(venueId));
        return resources;
    }

    public List<Space> getSpaceByVenue(long venueId){
        return transactionManager.inReadOnly(conn->
                spaceRepository.findAllByVenueId(conn,venueId));
    }

    public List<Equipment> getEquipmentByVenue(long venueId){
        return transactionManager.inReadOnly(conn->
                equipmentRepository.findAllByVenueId(conn,venueId));
    }

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
    private void validate(Resource resource){
       validateVenueId(resource.getVenueId());
       validateName(resource.getName());
       if(resource instanceof Equipment){
           validateQuantity((Equipment) resource);
       }
    }

    private void validateVenueId(long venueId){
        if(venueId > 0) {
            transactionManager.inReadOnly(conn-> {
                    venueRepository.findById(conn,venueId)
                            .orElseThrow(() -> new ValidationException("No venue found with id "+venueId));
                    return null;
            });
        }
    }

    private void validateName(String name){
        if(name == null || name.isEmpty()) {
            throw new ValidationException("Name of resource is empty");
        }
    }

    private void validateQuantity(Equipment resource){
        if(resource.getTotalQuantity() <= 0) {
            throw new ValidationException("Quantity of resource is less than 0");
        }
    }

}
