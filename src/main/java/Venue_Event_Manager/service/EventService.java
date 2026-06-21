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
    private final EventRequestRepository eventRequestRepository;
    private final VenueRepository venueRepository;
    private final UserRepository userRepository;

    /**
     * Initializes EventService with all repositories needed to handle events.
     * @param eventRepository repository used to access event data
     * @param eventRequestRepository repository used to access event request data
     * @param venueRepository repository used to access venue data
     * @param userRepository repository used to access user data
     */
    public EventService(EventRepository eventRepository, EventRequestRepository eventRequestRepository, VenueRepository venueRepository, UserRepository userRepository) {
        transactionManager = TransactionManager.getInstance();
        this.eventRepository = eventRepository;
        this.eventRequestRepository = eventRequestRepository;
        this.venueRepository = venueRepository;
        this.userRepository = userRepository;
    }

    /**
     * Gets all events stored in database.
     * @return List of all events
     */
    public List<Event> getAllEvents(){
        return transactionManager.inReadOnly(conn->
                eventRepository.findAll(conn));
    }

    /**
     * Gets an event from its id.
     * @param eventId the id of the event to find
     * @return Event object if found
     * @throws NotFoundException if no event is found with such id
     */
    public Event getEvent(long eventId){
        return transactionManager.inReadOnly(conn->
                eventRepository.findById(conn,eventId)
                        .orElseThrow(() -> new NotFoundException("No Event found with id " + eventId)));
    }

    /**
     * Gets all events linked to a venue.
     * @param venueId the id of the venue
     * @return List of events linked to the venue
     */
    public List<Event> getEventsByVenueId(long venueId){
        return transactionManager.inReadOnly(conn->
                eventRepository.findAllByVenueId(conn,venueId));
    }

    /**
     * Gets all events created by a user.
     * @param creatorId the id of the creator
     * @return List of events created by the user
     */
    public List<Event> getEventsByCreator(long creatorId){
        return transactionManager.inReadOnly(conn->
                eventRepository.findAllByCreatorId(conn,creatorId));
    }

    /**
     * Gets all events assigned to an organiser.
     * @param organiserId the id of the organiser
     * @return List of events assigned to the organiser
     */
    public List<Event> getEventsByOrganiser(long organiserId){
        return transactionManager.inReadOnly(conn->
                eventRepository.findAllByOrganiserId(conn,organiserId));
    }

    /**
     * Gets all events with a specific status.
     * @param eventStatus the status used to filter events
     * @return List of events with the given status
     */
    public List<Event> getEventsWithStatus(EventStatus eventStatus){
        return transactionManager.inReadOnly(conn->
                eventRepository.findAllByStatus(conn,eventStatus));
    }

    /**
     * Gets all events with a specific visibility.
     * @param eventVisibility the visibility used to filter events
     * @return List of events with the given visibility
     */
    public List<Event> getEventsWithVisibility(EventVisibility eventVisibility){
        return transactionManager.inReadOnly(conn->
                eventRepository.findAllVisibility(conn,eventVisibility));
    }

    /**
     * Gets all events starting exactly at a specific date and time.
     * @param start the start date and time
     * @return List of events starting at the given date and time
     */
    public List<Event> getEventsStartingAt(LocalDateTime start){
        return transactionManager.inReadOnly(conn->
                eventRepository.findAllByStartDate(conn,start));
    }

    /**
     * Gets all events ending exactly at a specific date and time.
     * @param end the end date and time
     * @return List of events ending at the given date and time
     */
    public List<Event> getEventsEndingAt(LocalDateTime end){
        return transactionManager.inReadOnly(conn->
                eventRepository.findAllByEndDate(conn,end));
    }

    /**
     * Gets all events starting after a specific date and time.
     * @param dateTime the threshold date and time
     * @return List of events starting after the threshold
     */
    public List<Event> getEventsAfter(LocalDateTime dateTime){
        return transactionManager.inReadOnly(conn->
                eventRepository.findAllAfter(conn,dateTime));
    }

    /**
     * Gets all events starting before a specific date and time.
     * @param dateTime the threshold date and time
     * @return List of events starting before the threshold
     */
    public List<Event> getEventsBefore(LocalDateTime dateTime){
        return transactionManager.inReadOnly(conn->
                eventRepository.findAllBefore(conn,dateTime));
    }

    /**
     * Gets all events between two dates.
     * @param start the beginning of the time range
     * @param end the end of the time range
     * @return List of events included in the time range
     */
    public List<Event> getEventsBetween(LocalDateTime start, LocalDateTime end){
        return transactionManager.inReadOnly(conn->
                eventRepository.findAllBetween(conn,start,end));
    }

    /**
     * Gets average review rating for an event.
     * @param eventId the id of the event
     * @return average review rating
     * @throws NotFoundException if no review average is found for the event
     */
    public Double getAverageReview(long eventId){
        return transactionManager.inReadOnly(conn->
                eventRepository.getAverageReview(conn,eventId)
                        .orElseThrow(() -> new NotFoundException("No Event found with id " + eventId)));
    }

    //CRUD
    /**
     * Inserts a new event in database.
     * @param event the event to insert
     * @return generated id of the new event
     * @throws ValidationException if event data are not valid
     */
    public long createEvent(Event event){
        return transactionManager.inTransaction(conn->{
            validate(event);
            return eventRepository.insert(conn,event);
        });
    }

    /**
     * Updates an existing event in database.
     * @param event the event object with updated data
     * @throws ValidationException if event data are not valid
     */
    public void updateEvent(Event event){
        transactionManager.inTransaction(conn->{
            validate(event);
            eventRepository.update(conn,event);
            return null;
        });
    }

    /**
     * Deletes an event from database.
     * @param eventId the id of the event to delete
     */
    public void deleteEvent(long eventId){
        transactionManager.inTransaction(conn->{
            eventRepository.deleteById(conn,eventId);
            return null;
        });
    }

    /**
     * Changes visibility of an event.
     * @param eventId the id of the event to update
     * @param eventVisibility the new visibility to set
     */
    public void changeVisibility(long eventId, EventVisibility eventVisibility){
        transactionManager.inTransaction(conn->{
            eventRepository.updateVisibility(conn,eventId,eventVisibility);
            return null;
        });
    }

    /**
     * Changes status of an event.
     * @param event the event to update
     * @param eventStatus the new status to set
     */
    private void changeStatus(Event event, EventStatus eventStatus){
        transactionManager.inTransaction(conn->{
            eventRepository.updateStatus(conn,event.getId(),eventStatus);
            return null;
        });
    }

    /**
     * Publishes an event.
     * @param eventId the id of the event to publish
     * @throws NotFoundException if no event is found with such id
     * @throws ForbiddenException if event cannot be published
     */
    public void publishEvent(long eventId){
        transactionManager.inTransaction(conn->{
            Event event = eventRepository.findById(conn,eventId)
                    .orElseThrow(() -> new NotFoundException("No Event found with id " + eventId));
            if(event.getStatus() == EventStatus.PUBLISHED) throw new ForbiddenException("Event is already published");
            if(event.getStatus() == EventStatus.CANCELLED) throw new ForbiddenException("Event is already cancelled");
            //TODO handle Not published status yet to add
            if(event.getPublishedAt() == null) eventRepository.updateStatusAndPublishedAt(conn,event.getId(),EventStatus.PUBLISHED,LocalDateTime.now());
            else eventRepository.updateStatus(conn,event.getId(), EventStatus.PUBLISHED);
            return null;
        });
    }

    /**
     * Confirms an event.
     * @param eventId the id of the event to confirm
     * @throws NotFoundException if no event is found with such id
     */
    public void confirmEvent(long eventId){
        transactionManager.inTransaction(conn->{
            Event event = eventRepository.findById(conn,eventId)
                    .orElseThrow(() -> new NotFoundException("No Event found with id " + eventId));
            eventRepository.updateStatus(conn,event.getId(),EventStatus.CONFIRMED);
            return null;
        });
    }

    /**
     * Cancels an event.
     * @param eventId the id of the event to cancel
     * @throws NotFoundException if no event is found with such id
     */
    public void cancelEvent(long eventId){
        transactionManager.inTransaction(conn->{
            Event event = eventRepository.findById(conn,eventId)
                    .orElseThrow(() -> new NotFoundException("No Event found with id " + eventId));
            eventRepository.updateStatus(conn,event.getId(),EventStatus.CANCELLED);
            return null;
        });
    }


    /**
     * Reschedules an event.
     * @param eventId the id of the event to reschedule
     * @param new_begin the new beginning date and time
     * @param new_end the new ending date and time
     * @throws NotFoundException if no event is found with such id
     * @throws ForbiddenException if event has already finished
     * @throws ValidationException if new dates are not valid
     */
    public void rescheduleEvent(long eventId, LocalDateTime new_begin, LocalDateTime new_end){
        transactionManager.inTransaction(conn->{
            Event old_event = eventRepository.findById(conn,eventId)
                    .orElseThrow(() -> new NotFoundException("No Event found with id " + eventId));
            if (old_event.getEndDatetime().isBefore(LocalDateTime.now())) throw new ForbiddenException("Can't reschedule finished event");
            Event new_event = old_event.withBeginDateTime(new_begin).withEndDateTime(new_end);
            validate(new_event);
            eventRepository.update(conn,new_event);
            return null;
        });
    }

    //TODO can't change capacity to be lower than current sold tickets
    /**
     * Changes capacity of an event.
     * @param eventId the id of the event to update
     * @param capacity the new capacity to set
     * @throws NotFoundException if no event is found with such id
     * @throws ValidationException if capacity is not valid
     */
    public void changeCapacity(long eventId, int capacity){
        transactionManager.inTransaction(conn->{
            Event new_event = eventRepository.findById(conn,eventId)
                    .orElseThrow(() -> new NotFoundException("No Event found with id " + eventId))
                    .withCapacity(capacity);
            validate(new_event);
            eventRepository.update(conn,new_event);
            return null;
        });
    }

    /**
     * Assigns an organiser to an event.
     * @param eventId the id of the event to update
     * @param organiserId the id of the organiser to assign
     * @throws NotFoundException if event or organiser are not found
     * @throws ValidationException if organiser is not valid
     */
    public void assignOrganiser(long eventId, long organiserId){
        transactionManager.inTransaction(conn->{
            validateOrganiserId(conn,organiserId);

            Event new_event = eventRepository.findById(conn,eventId)
                    .orElseThrow(() -> new NotFoundException("No Event found with id " + eventId))
                    .withOrganiserId(organiserId);
            validate(new_event);
            eventRepository.update(conn,new_event);
            return null;
        });
    }

    /**
     * Removes organiser from an event.
     * @param eventId the id of the event to update
     * @throws NotFoundException if no event is found with such id
     */
    public void removeOrganiser(long eventId){
        transactionManager.inTransaction(conn->{
            Event new_event = eventRepository.findById(conn,eventId)
                    .orElseThrow(() -> new NotFoundException("No Event found with id " + eventId))
                    .withOrganiserId(null);
            validate(new_event);
            eventRepository.update(conn,new_event);
            return null;
        });
    }

    /**
     * Updates poster filepath of an event.
     * @param eventId the id of the event to update
     * @param filepath the new poster filepath
     * @throws NotFoundException if no event is found with such id
     * @throws ValidationException if updated event is not valid
     */
    public void updatePoster(long eventId, String filepath){
        transactionManager.inTransaction(conn->{
            Event new_event = eventRepository.findById(conn,eventId)
                    .orElseThrow(() -> new NotFoundException("No Event found with id " + eventId))
                    .withPosterFilepath(filepath);
            validate(new_event);
            eventRepository.update(conn,new_event);
            return null;
        });
    }

    /**
     * Sets ticket price of an event.
     * @param eventId the id of the event to update
     * @param ticketPrice the new ticket price
     * @throws NotFoundException if no event is found with such id
     * @throws ValidationException if ticket price is not valid
     */
    public void setTicketPrice(long eventId, BigDecimal ticketPrice){
        transactionManager.inTransaction(conn->{
            Event new_event = eventRepository.findById(conn,eventId)
                    .orElseThrow(() -> new NotFoundException("No Event found with id " + eventId))
                    .withTicketPrice(ticketPrice);
            validate(new_event);
            eventRepository.update(conn,new_event);
            return null;
        });
    }






    //Validation
    /**
     * Validates all event fields before insert or update.
     * @param event the event to validate
     * @throws ValidationException if one or more fields are not valid
     */
    private void validate(Event event){
        validateName(event.getName());
        validateBeginAndEndDate(event.getBeginDatetime(), event.getEndDatetime());
        if(event.getCapacity() <= 0) throw new ValidationException("Capacity must be greater than 0");
        if(event.getTicketPrice() != null && event.getTicketPrice().compareTo(BigDecimal.ZERO) < 0)
            throw new ValidationException("Ticket price must be greater than 0");
        validateVenueId(event.getVenueId());
        validateCreatorId(event.getCreatorId());
        if(event.getOrganiserId() != null) validateOrganiserId(event.getOrganiserId());
        //TODO Validate in case of private event
        //TODO discuss with group the full event state machine and allowed transitions before completing status logic
    }

    /**
     * Validates event name.
     * @param name the name to validate
     * @throws ValidationException if name is empty
     */
    private void validateName(String name){
        if(name == null || name.isEmpty()) {
            throw new ValidationException("Name of resource is empty");
        }
    }

    /**
     * Validates begin and end dates of an event.
     * @param begin the beginning date and time
     * @param end the ending date and time
     * @throws ValidationException if dates are empty or begin is after end
     */
    private void validateBeginAndEndDate(LocalDateTime begin, LocalDateTime end){
        if(begin == null || end == null){
            throw new ValidationException("Begin or end date is empty");
        }else if(begin.isAfter(end)){
            throw new ValidationException("Begin date is after end date");
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
     * Validates if a creator user exists.
     * @param creatorId the id of the creator to validate
     * @throws ValidationException if no user is found with such id
     */
    private void validateCreatorId(long creatorId){
        if(creatorId > 0) {
            transactionManager.inReadOnly(conn-> {
                userRepository.findById(conn,creatorId)
                        .orElseThrow(() -> new ValidationException("No user found with id "+creatorId));
                return null;
            });
        }
    }

    /**
     * Validates if an organiser exists and is not an admin.
     * @param organiserId the id of the organiser to validate
     * @throws ValidationException if organiser id is not valid
     */
    private void validateOrganiserId(long organiserId){
        transactionManager.inReadOnly(conn-> {
            validateOrganiserId(conn,organiserId);
            return null;
        });
    }

    /**
     * Validates if an organiser exists and is not an admin using an existing connection.
     * @param conn the db connection
     * @param organiserId the id of the organiser to validate
     * @throws ValidationException if organiser id is not valid
     */
    private void validateOrganiserId(Connection conn, long organiserId){
        if(organiserId <= 0) throw new ValidationException("Organiser id is not valid");

        userRepository.findById(conn,organiserId)
                .map(user -> {
                    if(user.isAdmin()) throw new ValidationException("Organiser cannot be an admin");
                    return user;
                })
                .orElseThrow(() -> new ValidationException("No organiser found with id "+organiserId));
    }




}
