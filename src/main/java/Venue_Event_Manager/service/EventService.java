package Venue_Event_Manager.service;

import Venue_Event_Manager.config.TransactionManager;
import Venue_Event_Manager.domain.model.event.Event;
import Venue_Event_Manager.domain.model.event.EventStatus;
import Venue_Event_Manager.domain.model.event.EventVisibility;
import Venue_Event_Manager.repository.EventGuestRepository;
import Venue_Event_Manager.repository.EventRepository;
import Venue_Event_Manager.repository.EventRequestRepository;
import Venue_Event_Manager.exception.*;

import java.sql.Connection;
import java.time.LocalDateTime;
import java.util.List;

public class EventService {
    private final TransactionManager transactionManager;
    private final EventRepository eventRepository;
    private final EventGuestRepository eventGuestRepository;
    private final EventRequestRepository eventRequestRepository;

    public EventService(EventRepository eventRepository, EventGuestRepository eventGuestRepository, EventRequestRepository eventRequestRepository) {
        transactionManager = TransactionManager.getInstance();
        this.eventRepository = eventRepository;
        this.eventGuestRepository = eventGuestRepository;
        this.eventRequestRepository = eventRequestRepository;
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






}
