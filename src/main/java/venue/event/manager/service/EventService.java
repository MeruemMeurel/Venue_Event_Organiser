package venue.event.manager.service;

import venue.event.manager.config.TransactionManager;
import venue.event.manager.domain.model.event.Event;
import venue.event.manager.domain.model.event.EventStatus;
import venue.event.manager.domain.model.event.EventVisibility;
import venue.event.manager.domain.model.user.User;
import venue.event.manager.repository.*;
import venue.event.manager.exception.ConflictException;
import venue.event.manager.exception.ForbiddenException;
import venue.event.manager.exception.NotFoundException;
import venue.event.manager.exception.ValidationException;

import java.math.BigDecimal;
import java.sql.Connection;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

/** Coordinates event creation, updates and lifecycle transitions. */
public class EventService {
    private final TransactionManager transactionManager;
    private final EventRepository eventRepository;
    private final EventRequestRepository eventRequestRepository;
    private final TicketRepository ticketRepository;
    private final BookingRepository bookingRepository;
    private final EventGuestRepository eventGuestRepository;
    private final VenueRepository venueRepository;
    private final UserRepository userRepository;

    /**
     * Initializes EventService with all repositories needed to handle events.
     * @param eventRepository repository used to access event data
     * @param eventRequestRepository repository used to access event request data
     * @param ticketRepository repository used to access ticket data
     * @param bookingRepository repository used to access booking data
     * @param eventGuestRepository repository used to access event guest data
     * @param venueRepository repository used to access venue data
     * @param userRepository repository used to access user data
     */
    public EventService(EventRepository eventRepository, EventRequestRepository eventRequestRepository,
                        TicketRepository ticketRepository, BookingRepository bookingRepository,
                        EventGuestRepository eventGuestRepository, VenueRepository venueRepository,
                        UserRepository userRepository) {
        this(TransactionManager.getInstance(), eventRepository, eventRequestRepository, ticketRepository,
                bookingRepository, eventGuestRepository, venueRepository, userRepository);
    }

    /**
     * Initializes the service with an explicit transaction manager.
     * @param transactionManager transaction manager used to execute database work
     * @param eventRepository repository used to access event data
     * @param eventRequestRepository repository used to access request data
     * @param ticketRepository repository used to access ticket data
     * @param bookingRepository repository used to access booking data
     * @param eventGuestRepository repository used to access guest data
     * @param venueRepository repository used to access venue data
     * @param userRepository repository used to access user data
     */
    public EventService(TransactionManager transactionManager, EventRepository eventRepository,
                        EventRequestRepository eventRequestRepository, TicketRepository ticketRepository,
                        BookingRepository bookingRepository, EventGuestRepository eventGuestRepository,
                        VenueRepository venueRepository, UserRepository userRepository) {
        this.transactionManager = transactionManager;
        this.eventRepository = eventRepository;
        this.eventRequestRepository = eventRequestRepository;
        this.ticketRepository = ticketRepository;
        this.bookingRepository = bookingRepository;
        this.eventGuestRepository = eventGuestRepository;
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
            validate(conn,event);
            Event draftEvent = event
                    .withStatus(EventStatus.DRAFT)
                    .withPublishedAt(null);
            return eventRepository.insert(conn,draftEvent);
        });
    }

    /**
     * Updates an existing event in database.
     * @param actorId id of the administrator or assigned organiser performing the update
     * @param event the event object with updated data
     * @throws ValidationException if event data are not valid
     * @throws ForbiddenException if the actor is neither an administrator nor the assigned organiser
     */
    public void updateEvent(long actorId, Event event){
        transactionManager.inTransaction(conn->{
            if(event == null) throw new ValidationException("Event cannot be null");

            Event storedEvent = requireEventManager(conn,event.getId(),actorId);

            Event eventToUpdate = event
                    .withCreatorId(storedEvent.getCreatorId())
                    .withOrganiserId(storedEvent.getOrganiserId())
                    .withStatus(storedEvent.getStatus())
                    .withPublishedAt(storedEvent.getPublishedAt());

            validate(conn,eventToUpdate);
            eventRepository.update(conn,eventToUpdate);
            return null;
        });
    }

    /**
     * Deletes an event from database.
     * @param actorId id of the administrator or assigned organiser performing the deletion
     * @param eventId the id of the event to delete
     * @throws ForbiddenException if the actor is neither an administrator nor the assigned organiser
     */
    public void deleteEvent(long actorId, long eventId){
        transactionManager.inTransaction(conn->{
            requireEventManager(conn,eventId,actorId);
            eventRepository.deleteById(conn,eventId);
            return null;
        });
    }

    /**
     * Changes visibility of an event.
     * @param actorId id of the administrator or assigned organiser performing the update
     * @param eventId the id of the event to update
     * @param eventVisibility the new visibility to set
     * @throws ForbiddenException if the actor is neither an administrator nor the assigned organiser
     */
    public void changeVisibility(long actorId, long eventId, EventVisibility eventVisibility){
        transactionManager.inTransaction(conn->{
            requireEventManager(conn,eventId,actorId);
            eventRepository.updateVisibility(conn,eventId,eventVisibility);
            return null;
        });
    }

    /**
     * Publishes an event.
     * @param actorId id of the administrator or assigned organiser performing the operation
     * @param eventId the id of the event to publish
     * @throws NotFoundException if no event is found with such id
     * @throws ConflictException if the event is not confirmed
     * @throws ValidationException if event data do not allow publication
     * @throws ForbiddenException if the actor is neither an administrator nor the assigned organiser
     */
    public void publishEvent(long actorId, long eventId){
        transactionManager.inTransaction(conn->{
            Event event = requireEventManager(conn,eventId,actorId);

            validateEventStatusTransition(event.getStatus(),EventStatus.PUBLISHED);
            validateForPublication(conn,event);
            eventRepository.updateStatusAndPublishedAt(conn,event.getId(),EventStatus.PUBLISHED,LocalDateTime.now());
            return null;
        });
    }

    /**
     * Confirms an event.
     * @param actorId id of the administrator or assigned organiser performing the operation
     * @param eventId the id of the event to confirm
     * @throws NotFoundException if no event is found with such id
     * @throws ForbiddenException if the actor is neither an administrator nor the assigned organiser
     */
    public void confirmEvent(long actorId, long eventId){
        transactionManager.inTransaction(conn->{
            Event event = requireEventManager(conn,eventId,actorId);

            validateEventStatusTransition(event.getStatus(),EventStatus.CONFIRMED);
            eventRepository.updateStatus(conn,event.getId(),EventStatus.CONFIRMED);
            return null;
        });
    }

    /**
     * Cancels an event.
     * @param actorId id of the administrator or assigned organiser performing the operation
     * @param eventId the id of the event to cancel
     * @throws NotFoundException if no event is found with such id
     * @throws ForbiddenException if the actor is neither an administrator nor the assigned organiser
     */
    public void cancelEvent(long actorId, long eventId){
        transactionManager.inTransaction(conn->{
            Event event = requireEventManager(conn,eventId,actorId);

            validateEventStatusTransition(event.getStatus(),EventStatus.CANCELLED);
            bookingRepository.cancelActiveByEventId(conn,eventId);
            eventGuestRepository.cancelActiveByEventId(conn,eventId);
            eventRepository.updateStatus(conn,event.getId(),EventStatus.CANCELLED);
            return null;
        });
    }


    /**
     * Reschedules an event.
     * @param actorId id of the administrator or assigned organiser performing the update
     * @param eventId the id of the event to reschedule
     * @param newBegin the new beginning date and time
     * @param newEnd the new ending date and time
     * @throws NotFoundException if no event is found with such id
     * @throws ForbiddenException if event has already finished
     * @throws ValidationException if new dates are not valid
     */
    public void rescheduleEvent(long actorId, long eventId, LocalDateTime newBegin, LocalDateTime newEnd){
        transactionManager.inTransaction(conn->{
            Event storedEvent = requireEventManager(conn,eventId,actorId);
            if (storedEvent.getEndDatetime().isBefore(LocalDateTime.now())) throw new ForbiddenException("Can't reschedule finished event");
            Event updatedEvent = storedEvent.withBeginDateTime(newBegin).withEndDateTime(newEnd);
            validate(conn,updatedEvent);
            eventRepository.update(conn,updatedEvent);
            return null;
        });
    }

    /**
     * Changes capacity of an event.
     * @param actorId id of the administrator or assigned organiser performing the update
     * @param eventId the id of the event to update
     * @param capacity the new capacity to set
     * @throws NotFoundException if no event is found with such id
     * @throws ValidationException if capacity is not valid
     */
    public void changeCapacity(long actorId, long eventId, int capacity){
        transactionManager.inTransaction(conn->{
            Event event = requireEventManager(conn,eventId,actorId);

            if(capacity < ticketRepository.countTicketsForEvent(conn,eventId))
                throw new  ForbiddenException("Capacity is less than number of tickets");

            Event updatedEvent = event.withCapacity(capacity);

            validate(conn,updatedEvent);
            eventRepository.update(conn,updatedEvent);
            return null;
        });
    }

    /**
     * Assigns an organiser to an event.
     * @param actorId id of the administrator assigning the organiser
     * @param eventId the id of the event to update
     * @param organiserId the id of the organiser to assign
     * @throws NotFoundException if event or organiser are not found
     * @throws ValidationException if organiser is not valid
     * @throws ForbiddenException if the actor is not an administrator
     */
    public void assignOrganiser(long actorId, long eventId, long organiserId){
        transactionManager.inTransaction(conn->{
            requireAdmin(conn,actorId);
            validateOrganiserId(conn,organiserId);

            Event updatedEvent = eventRepository.findByIdForUpdate(conn,eventId)
                    .orElseThrow(() -> new NotFoundException("No Event found with id " + eventId))
                    .withOrganiserId(organiserId);
            validate(conn,updatedEvent);
            eventRepository.update(conn,updatedEvent);
            return null;
        });
    }

    /**
     * Removes organiser from an event.
     * @param actorId id of the administrator removing the organiser
     * @param eventId the id of the event to update
     * @throws NotFoundException if no event is found with such id
     * @throws ForbiddenException if the actor is not an administrator
     */
    public void removeOrganiser(long actorId, long eventId){
        transactionManager.inTransaction(conn->{
            requireAdmin(conn,actorId);
            Event updatedEvent = eventRepository.findByIdForUpdate(conn,eventId)
                    .orElseThrow(() -> new NotFoundException("No Event found with id " + eventId))
                    .withOrganiserId(null);
            validate(conn,updatedEvent);
            eventRepository.update(conn,updatedEvent);
            return null;
        });
    }

    /**
     * Updates poster filepath of an event.
     * @param actorId id of the administrator or assigned organiser performing the update
     * @param eventId the id of the event to update
     * @param filepath the new poster filepath
     * @throws NotFoundException if no event is found with such id
     * @throws ValidationException if updated event is not valid
     */
    public void updatePoster(long actorId, long eventId, String filepath){
        transactionManager.inTransaction(conn->{
            Event updatedEvent = requireEventManager(conn,eventId,actorId).withPosterFilepath(filepath);
            validate(conn,updatedEvent);
            eventRepository.update(conn,updatedEvent);
            return null;
        });
    }

    /**
     * Sets ticket price of an event.
     * @param actorId id of the administrator or assigned organiser performing the update
     * @param eventId the id of the event to update
     * @param ticketPrice the new ticket price
     * @throws NotFoundException if no event is found with such id
     * @throws ValidationException if ticket price is not valid
     */
    public void setTicketPrice(long actorId, long eventId, BigDecimal ticketPrice){
        transactionManager.inTransaction(conn->{
            Event updatedEvent = requireEventManager(conn,eventId,actorId).withTicketPrice(ticketPrice);
            validate(conn,updatedEvent);
            eventRepository.update(conn,updatedEvent);
            return null;
        });
    }






    //Validation
    /**
     * Validates all event fields before insert or update.
     * @param conn active transaction connection used for relational checks
     * @param event the event to validate
     * @throws ValidationException if one or more fields are not valid
     */
    private void validate(Connection conn, Event event){
        if(event == null) throw new ValidationException("Event cannot be null");
        validateName(event.getName());
        validateBeginAndEndDate(event.getBeginDatetime(), event.getEndDatetime());
        if(event.getCapacity() <= 0) throw new ValidationException("Capacity must be greater than 0");
        if(event.getTicketPrice() != null && event.getTicketPrice().compareTo(BigDecimal.ZERO) < 0)
            throw new ValidationException("Ticket price cannot be negative");
        validateVenueId(conn,event.getVenueId());
        validateCreatorId(conn,event.getCreatorId());
        if(event.getOrganiserId() != null) validateOrganiserId(conn,event.getOrganiserId());
    }

    /**
     * Validates all requirements that must hold when an event becomes visible.
     * @param conn active database connection used to verify the venue
     * @param event event being prepared for publication
     * @throws ValidationException if the event has started, has invalid capacity or references a missing venue
     */
    private void validateForPublication(Connection conn, Event event){
        if(!event.getBeginDatetime().isAfter(LocalDateTime.now())) {
            throw new ValidationException("Cannot publish an event that has already started");
        }
        if(event.getCapacity() <= 0) {
            throw new ValidationException("Cannot publish an event without a positive capacity");
        }
        venueRepository.findById(conn,event.getVenueId())
                .orElseThrow(() -> new ValidationException("No venue found with id " + event.getVenueId()));
    }

    /**
     * Validates the event state machine.
     * @param currentStatus current persisted event status
     * @param newStatus requested event status
     * @throws ConflictException if the transition is duplicated or not allowed
     */
    static void validateEventStatusTransition(EventStatus currentStatus, EventStatus newStatus){
        if(currentStatus == newStatus) {
            throw new ConflictException("Event is already " + newStatus);
        }

        boolean allowed = (currentStatus == EventStatus.DRAFT
                && (newStatus == EventStatus.CONFIRMED || newStatus == EventStatus.CANCELLED))
                || (currentStatus == EventStatus.CONFIRMED
                && (newStatus == EventStatus.PUBLISHED || newStatus == EventStatus.CANCELLED))
                || (currentStatus == EventStatus.PUBLISHED && newStatus == EventStatus.CANCELLED);

        if(!allowed) {
            throw new ConflictException("Cannot change event status from " + currentStatus + " to " + newStatus);
        }
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
        }else if(!begin.isBefore(end)){
            throw new ValidationException("Begin date must be before end date");
        }
    }

    /**
     * Validates if a venue exists.
     * @param conn active transaction connection
     * @param venueId the id of the venue to validate
     * @throws ValidationException if no venue is found with such id
     */
    private void validateVenueId(Connection conn, long venueId){
        if(venueId <= 0) throw new ValidationException("Venue id is not valid");
        venueRepository.findById(conn,venueId)
                .orElseThrow(() -> new ValidationException("No venue found with id "+venueId));
    }

    /**
     * Validates if a creator user exists.
     * @param conn active transaction connection
     * @param creatorId the id of the creator to validate
     * @throws ValidationException if no user is found with such id
     */
    private void validateCreatorId(Connection conn, long creatorId){
        if(creatorId <= 0) throw new ValidationException("Creator id is not valid");
        User creator = userRepository.findById(conn,creatorId)
                .orElseThrow(() -> new ValidationException("No user found with id "+creatorId));
        if(!creator.isAdmin()) throw new ForbiddenException("Only admins can create events");
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

    /**
     * Loads and locks an event, then verifies that the actor can manage it.
     * @param conn active transaction connection
     * @param eventId event to load and authorize
     * @param actorId user attempting the operation
     * @return locked event when the actor is an administrator or its assigned organiser
     * @throws NotFoundException if the event or actor does not exist
     * @throws ForbiddenException if the actor cannot manage the event
     */
    private Event requireEventManager(Connection conn, long eventId, long actorId){
        Event event = eventRepository.findByIdForUpdate(conn,eventId)
                .orElseThrow(() -> new NotFoundException("No Event found with id " + eventId));
        User actor = userRepository.findById(conn,actorId)
                .orElseThrow(() -> new NotFoundException("No user found with id " + actorId));

        if(!actor.isAdmin() && !Objects.equals(event.getOrganiserId(),actorId)) {
            throw new ForbiddenException("Only admins or the assigned organiser can manage this event");
        }
        return event;
    }

    /**
     * Verifies that the actor exists and has administrator privileges.
     * @param conn active transaction connection
     * @param actorId user attempting the operation
     * @throws NotFoundException if the actor does not exist
     * @throws ForbiddenException if the actor is not an administrator
     */
    private void requireAdmin(Connection conn, long actorId){
        User actor = userRepository.findById(conn,actorId)
                .orElseThrow(() -> new NotFoundException("No user found with id " + actorId));
        if(!actor.isAdmin()) throw new ForbiddenException("Only admins can assign or remove event organisers");
    }




}
