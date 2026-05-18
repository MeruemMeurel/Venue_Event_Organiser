package Venue_Event_Manager.service;

import Venue_Event_Manager.config.TransactionManager;
import Venue_Event_Manager.domain.model.event.Event;
import Venue_Event_Manager.domain.model.event.EventStatus;
import Venue_Event_Manager.domain.model.event.EventVisibility;
import Venue_Event_Manager.repository.*;
import Venue_Event_Manager.exception.*;

import java.math.BigDecimal;
import java.sql.Connection;
import java.time.LocalDateTime;
import java.util.List;

public class EventService {
    private final TransactionManager transactionManager;
    private final EventRepository eventRepository;
    private final EventGuestRepository eventGuestRepository;
    private final EventRequestRepository eventRequestRepository;
    private final VenueRepository venueRepository;
    private final UserRepository userRepository;

    public EventService(EventRepository eventRepository, EventGuestRepository eventGuestRepository, EventRequestRepository eventRequestRepository, VenueRepository venueRepository, UserRepository userRepository) {
        transactionManager = TransactionManager.getInstance();
        this.eventRepository = eventRepository;
        this.eventGuestRepository = eventGuestRepository;
        this.eventRequestRepository = eventRequestRepository;
        this.venueRepository = venueRepository;
        this.userRepository = userRepository;
    }

    public List<Event> getAllEvents(){
        return transactionManager.inReadOnly(conn->
                eventRepository.findAll(conn));
    }

    public Event getEvent(long eventId){
        return transactionManager.inReadOnly(conn->
                eventRepository.findById(conn,eventId)
                        .orElseThrow(() -> new NotFoundException("No Event found with id " + eventId)));
    }

    public List<Event> getEventsByVenueId(long venueId){
        return transactionManager.inReadOnly(conn->
                eventRepository.findAllByVenueId(conn,venueId));
    }

    public List<Event> getEventsByCreator(long creatorId){
        return transactionManager.inReadOnly(conn->
                eventRepository.findAllByCreatorId(conn,creatorId));
    }

    public List<Event> getEventsByOrganiser(long organiserId){
        return transactionManager.inReadOnly(conn->
                eventRepository.findAllByOrganiserId(conn,organiserId));
    }

    public List<Event> getEventsWithStatus(EventStatus eventStatus){
        return transactionManager.inReadOnly(conn->
                eventRepository.findAllByStatus(conn,eventStatus));
    }

    public List<Event> getEventsWithVisibility(EventVisibility eventVisibility){
        return transactionManager.inReadOnly(conn->
                eventRepository.findAllVisibility(conn,eventVisibility));
    }

    public List<Event> getEventsStartingAt(LocalDateTime start){
        return transactionManager.inReadOnly(conn->
                eventRepository.findAllByStartDate(conn,start));
    }

    public List<Event> getEventsEndingAt(LocalDateTime end){
        return transactionManager.inReadOnly(conn->
                eventRepository.findAllByEndDate(conn,end));
    }

    public List<Event> getEventsAfter(LocalDateTime dateTime){
        return transactionManager.inReadOnly(conn->
                eventRepository.findAllAfter(conn,dateTime));
    }

    public List<Event> getEventsBefore(LocalDateTime dateTime){
        return transactionManager.inReadOnly(conn->
                eventRepository.findAllBefore(conn,dateTime));
    }

    public List<Event> getEventsBetween(LocalDateTime start, LocalDateTime end){
        return transactionManager.inReadOnly(conn->
                eventRepository.findAllBetween(conn,start,end));
    }

    public Double getAverageReview(long eventId){
        return transactionManager.inReadOnly(conn->
                eventRepository.getAverageReview(conn,eventId)
                        .orElseThrow(() -> new NotFoundException("No Event found with id " + eventId)));
    }

    //CRUD
    public void createEvent(Event event){
        transactionManager.inTransaction(conn->{
            validate(event);
            eventRepository.insert(conn,event);
            return null;
        });
    }

    public void updateEvent(Event event){
        transactionManager.inTransaction(conn->{
            validate(event);
            eventRepository.update(conn,event);
            return null;
        });
    }

    public void deleteEvent(Event event){
        transactionManager.inTransaction(conn->{
            eventRepository.deleteById(conn,event.getId());
            return null;
        });
    }

    public void changeVisibility(Event event, EventVisibility eventVisibility){
        transactionManager.inTransaction(conn->{
            eventRepository.updateVisibility(conn,event.getId(),eventVisibility);
            return null;
        });
    }

    private void changeStatus(Event event, EventStatus eventStatus){
        transactionManager.inTransaction(conn->{
            eventRepository.updateStatus(conn,event.getId(),eventStatus);
            return null;
        });
    }

    public void publishEvent(Event event){
        transactionManager.inTransaction(conn->{
            if(event.getPublishedAt() == null) eventRepository.updateStatusAndPublishedAt(conn,event.getId(),EventStatus.PUBLISHED,LocalDateTime.now());
            else eventRepository.updateStatus(conn,event.getId(), EventStatus.PUBLISHED);
            return null;
        });
    }

    public void confirmEvent(Event event){
        transactionManager.inTransaction(conn->{
            eventRepository.updateStatus(conn,event.getId(),EventStatus.CONFIRMED);
            return null;
        });
    }

    public void cancelEvent(Event event){
        transactionManager.inTransaction(conn->{
            eventRepository.updateStatus(conn,event.getId(),EventStatus.CANCELLED);
            return null;
        });
    }






    //Validation
    private void validate(Event event){
        validateName(event.getName());
        validateBeginAndEndDate(event.getBeginDatetime(), event.getEndDatetime());
        if(event.getCapacity() <= 0) throw new ValidationException("Capacity must be greater than 0");
        if(event.getTicketPrice() != null && event.getTicketPrice().compareTo(BigDecimal.ZERO) < 0)
            throw new ValidationException("Ticket price must be greater than 0");
        validateVenueId(event.getVenueId());
        validateVenueId(event.getVenueId());
        //TODO Validate in case of private event
    }

    private void validateName(String name){
        if(name == null || name.isEmpty()) {
            throw new ValidationException("Name of resource is empty");
        }
    }

    private void validateBeginAndEndDate(LocalDateTime begin, LocalDateTime end){
        if(begin == null || end == null){
            throw new ValidationException("Begin or end date is empty");
        }else if(begin.isAfter(end)){
            throw new ValidationException("Begin date is after end date");
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

    private void validateCreatorId(long creatorId){
        if(creatorId > 0) {
            transactionManager.inReadOnly(conn-> {
                userRepository.findById(conn,creatorId)
                        .orElseThrow(() -> new ValidationException("No user found with id "+creatorId));
                return null;
            });
        }
    }




}
