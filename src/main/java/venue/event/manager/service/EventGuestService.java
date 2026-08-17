package venue.event.manager.service;

import venue.event.manager.config.TransactionManager;
import venue.event.manager.domain.model.event.Event;
import venue.event.manager.domain.model.event.EventGuest;
import venue.event.manager.domain.model.event.EventGuestStatus;
import venue.event.manager.domain.model.event.EventStatus;
import venue.event.manager.domain.model.event.EventVisibility;
import venue.event.manager.exception.ConflictException;
import venue.event.manager.exception.NotFoundException;
import venue.event.manager.exception.ValidationException;
import venue.event.manager.repository.EventGuestRepository;
import venue.event.manager.repository.EventRepository;

import java.util.List;

/** Coordinates invitations and lifecycle transitions for private-event guests. */
public class EventGuestService {

    private final TransactionManager transactionManager;
    private final EventGuestRepository eventGuestRepository;
    private final EventRepository eventRepository;

    /**
     * Initializes EventGuestService with repositories needed to handle guests.
     * @param eventGuestRepository repository used to access event guest data
     * @param eventRepository repository used to access event data for validation
     */
    public EventGuestService(EventGuestRepository eventGuestRepository, EventRepository eventRepository) {
        this(TransactionManager.getInstance(), eventGuestRepository, eventRepository);
    }

    /**
     * Initializes the service with an explicit transaction manager.
     * @param transactionManager transaction manager used to execute database work
     * @param eventGuestRepository repository used to access guest data
     * @param eventRepository repository used to access event data
     */
    public EventGuestService(TransactionManager transactionManager, EventGuestRepository eventGuestRepository,
                             EventRepository eventRepository) {
        this.transactionManager = transactionManager;
        this.eventGuestRepository = eventGuestRepository;
        this.eventRepository = eventRepository;
    }

    /**
     * Gets an event guest by their ID.
     * @param guestId the id of the guest
     * @return EventGuest object
     * @throws NotFoundException if no guest is found
     */
    public EventGuest getGuestById(long guestId) {
        return transactionManager.inReadOnly(conn ->
                eventGuestRepository.findById(conn, guestId)
                        .orElseThrow(() -> new NotFoundException("No guest found with id " + guestId))
        );
    }

    /**
     * Gets all guests for a specific event.
     * @param eventId the id of the event
     * @return List of EventGuest
     */
    public List<EventGuest> getGuestsForEvent(long eventId) {
        validateEventExists(eventId);
        return transactionManager.inReadOnly(conn ->
                eventGuestRepository.findAllByEventId(conn, eventId)
        );
    }

    /**
     * Gets all guests for an event filtered by their RSVP status.
     * @param eventId the id of the event
     * @param status the RSVP status
     * @return List of EventGuest
     */
    public List<EventGuest> getGuestsForEventWithStatus(long eventId, EventGuestStatus status) {
        validateEventExists(eventId);
        return transactionManager.inReadOnly(conn ->
                eventGuestRepository.findAllByEventIdAndStatus(conn, eventId, status)
        );
    }

    /**
     * Invites a new guest to an event. Defaults their status to INVITED.
     * @param guest the guest to invite
     * @return the generated ID of the new guest
     */
    public long inviteGuest(EventGuest guest) {
        validateGuest(guest);

        return transactionManager.inTransaction(conn -> {
            Event event = eventRepository.findByIdForUpdate(conn,guest.getEventId())
                    .orElseThrow(() -> new NotFoundException("No event found with id " + guest.getEventId()));
            validateEventAllowsInvitations(event);

            EventGuest newGuest = guest.withStatus(EventGuestStatus.INVITED);
            return eventGuestRepository.insert(conn, newGuest);
        });
    }

    /**
     * Updates a guest's information (name, birthday, note).
     * @param guest the guest object with updated data
     */
    public void updateGuest(EventGuest guest) {
        validateGuest(guest);

        transactionManager.inTransaction(conn -> {
            EventGuest storedGuest = eventGuestRepository.findByIdForUpdate(conn, guest.getId())
                    .orElseThrow(() -> new NotFoundException("No guest found with id " + guest.getId()));

            EventGuest guestToUpdate = guest
                    .withEventId(storedGuest.getEventId())
                    .withStatus(storedGuest.getStatus());

            eventGuestRepository.update(conn, guestToUpdate);
            return null;
        });
    }

    /**
     * Confirms an invitation on behalf of a guest.
     * @param guestId the id of the guest
     */
    public void confirmInvitation(long guestId) {
        updateGuestStatus(guestId, EventGuestStatus.CONFIRMED);
    }

    /**
     * Cancels an invitation on behalf of a guest.
     * @param guestId the id of the guest
     */
    public void cancelInvitation(long guestId) {
        updateGuestStatus(guestId, EventGuestStatus.CANCELLED);
    }

    /**
     * Helper method to update a guest's status safely.
     */
    private void updateGuestStatus(long guestId, EventGuestStatus status) {
        transactionManager.inTransaction(conn -> {
            EventGuest guest = eventGuestRepository.findByIdForUpdate(conn, guestId)
                    .orElseThrow(() -> new NotFoundException("No guest found with id " + guestId));

            validateGuestStatusTransition(guest.getStatus(),status);
            eventGuestRepository.updateEventGuestStatus(conn, guestId, status);
            return null;
        });
    }

    /**
     * Removes a guest entirely from an event's guest list.
     * @param guestId the id of the guest to remove
     */
    public void removeGuest(long guestId) {
        transactionManager.inTransaction(conn -> {
            eventGuestRepository.findById(conn, guestId)
                    .orElseThrow(() -> new NotFoundException("No guest found with id " + guestId));

            eventGuestRepository.deleteById(conn, guestId);
            return null;
        });
    }

    // --- Validations ---

    private void validateGuest(EventGuest guest) {
        if (guest == null) {
            throw new ValidationException("Guest cannot be null");
        }
        if (guest.getFirstname() == null || guest.getFirstname().isBlank()) {
            throw new ValidationException("Guest firstname is required");
        }
        if (guest.getLastname() == null || guest.getLastname().isBlank()) {
            throw new ValidationException("Guest lastname is required");
        }
        if (guest.getEventId() <= 0) {
            throw new ValidationException("Guest must be linked to a valid event ID");
        }
    }

    private void validateEventExists(long eventId) {
        transactionManager.inReadOnly(conn -> {
            eventRepository.findById(conn, eventId)
                    .orElseThrow(() -> new NotFoundException("No event found with id " + eventId));
            return null;
        });
    }

    /**
     * Validates the guest invitation state machine.
     * @param currentStatus current persisted invitation status
     * @param newStatus requested invitation status
     * @throws ConflictException if the transition is duplicated or not allowed
     */
    static void validateGuestStatusTransition(EventGuestStatus currentStatus, EventGuestStatus newStatus) {
        if(currentStatus == newStatus) {
            throw new ConflictException("Guest invitation is already " + newStatus);
        }

        boolean allowed = (currentStatus == EventGuestStatus.INVITED
                && (newStatus == EventGuestStatus.CONFIRMED || newStatus == EventGuestStatus.CANCELLED))
                || (currentStatus == EventGuestStatus.CONFIRMED && newStatus == EventGuestStatus.CANCELLED);

        if(!allowed) {
            throw new ConflictException("Cannot change guest status from " + currentStatus + " to " + newStatus);
        }
    }

    /**
     * Validates that an event can receive guest-list invitations.
     * @param event event for which the invitation is being created
     * @throws ValidationException if the event does not use a private guest list
     * @throws ConflictException if the event has been cancelled
     */
    private void validateEventAllowsInvitations(Event event) {
        if(event.getVisibility() != EventVisibility.PRIVATE_GUEST_LIST) {
            throw new ValidationException("Guests can only be invited to private guest-list events");
        }
        if(event.getStatus() == EventStatus.CANCELLED) {
            throw new ConflictException("Guests cannot be invited to a cancelled event");
        }
    }
}
