package Venue_Event_Manager.service;

import Venue_Event_Manager.config.TransactionManager;
import Venue_Event_Manager.domain.model.resource.Equipment;
import Venue_Event_Manager.domain.model.resource.Resource;
import Venue_Event_Manager.domain.model.resource.Service;
import Venue_Event_Manager.domain.model.resource.Space;
import Venue_Event_Manager.exception.NotFoundException;
import Venue_Event_Manager.repository.EquipmentRepository;
import Venue_Event_Manager.repository.ServiceRepository;
import Venue_Event_Manager.repository.SpaceRepository;

import java.util.ArrayList;
import java.util.List;

public class ResourceService {

    private final TransactionManager transactionManager;
    private final SpaceRepository spaceRepository;
    private final EquipmentRepository equipmentRepository;
    private final ServiceRepository serviceRepository;

    public ResourceService(SpaceRepository spaceRepository, EquipmentRepository equipmentRepository, ServiceRepository serviceRepository) {
        this.transactionManager = TransactionManager.getInstance();
        this.spaceRepository = spaceRepository;
        this.equipmentRepository = equipmentRepository;
        this.serviceRepository = serviceRepository;
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

}
