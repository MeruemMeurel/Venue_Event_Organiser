package venue.event.manager.service;

import venue.event.manager.config.TransactionManager;
import venue.event.manager.domain.model.booking.Booking;
import venue.event.manager.domain.model.booking.BookingStatus;
import venue.event.manager.domain.model.booking.Ticket;
import venue.event.manager.domain.model.event.Event;
import venue.event.manager.domain.model.event.EventStatus;
import venue.event.manager.domain.model.event.EventVisibility;
import venue.event.manager.domain.model.user.AccountStatus;
import venue.event.manager.exception.ConflictException;
import venue.event.manager.exception.ForbiddenException;
import venue.event.manager.exception.NotFoundException;
import venue.event.manager.exception.ValidationException;
import venue.event.manager.repository.BookingRepository;
import venue.event.manager.repository.EventRepository;
import venue.event.manager.repository.TicketRepository;
import venue.event.manager.repository.UserRepository;

import java.math.BigDecimal;
import java.sql.Connection;
import java.time.LocalDateTime;
import java.util.List;

/** Coordinates booking, ticket and capacity business rules. */
public class BookingService {

    private final TransactionManager transactionManager;
    private final BookingRepository bookingRepository;
    private final TicketRepository ticketRepository;
    private final EventRepository eventRepository;
    private final UserRepository userRepository;

    /**
     * Initializes BookingService with all repositories needed to handle bookings and tickets.
     * @param bookingRepository repository used to access booking data
     * @param ticketRepository repository used to access ticket data
     * @param eventRepository repository used to access event data
     * @param userRepository repository used to access user data
     */
    public BookingService(BookingRepository bookingRepository, TicketRepository ticketRepository, EventRepository eventRepository,
                          UserRepository userRepository) {
        this(TransactionManager.getInstance(), bookingRepository, ticketRepository, eventRepository, userRepository);
    }

    /**
     * Initializes BookingService with an explicit transaction manager and its repositories.
     * This constructor allows callers such as unit tests to provide an isolated transaction boundary.
     * @param transactionManager transaction manager used to execute database work
     * @param bookingRepository repository used to access booking data
     * @param ticketRepository repository used to access ticket data
     * @param eventRepository repository used to access event data
     * @param userRepository repository used to access user data
     */
    public BookingService(TransactionManager transactionManager, BookingRepository bookingRepository,
                          TicketRepository ticketRepository, EventRepository eventRepository,
                          UserRepository userRepository) {
        this.transactionManager = transactionManager;
        this.bookingRepository = bookingRepository;
        this.ticketRepository = ticketRepository;
        this.eventRepository = eventRepository;
        this.userRepository = userRepository;
    }

    /**
     * Gets all bookings stored in database.
     * @return List of all bookings
     */
    public List<Booking> getAllBookings(){
        return transactionManager.inReadOnly(conn->
                bookingRepository.findAll(conn));
    }

    /**
     * Gets a booking from its id.
     * @param bookingId the id of the booking to find
     * @return Booking object if found
     * @throws NotFoundException if no booking is found with such id
     */
    public Booking getBooking(long bookingId){
        return transactionManager.inReadOnly(conn->
                bookingRepository.findById(conn,bookingId)
                        .orElseThrow(() -> new NotFoundException("No booking with such id exists")));
    }

    /**
     * Gets all bookings made by a specific user.
     * @param userId the id of the user
     * @return List of bookings made by the user
     */
    public List<Booking> getBookingsMadeByUser(long userId){
        return transactionManager.inReadOnly(conn->
                bookingRepository.findAllByUserId(conn,userId));
    }

    /**
     * Gets all bookings made for a specific event.
     * @param eventId the id of the event
     * @return List of bookings made for the event
     */
    public List<Booking> getBookingsForEvent(long eventId){
        return transactionManager.inReadOnly(conn->
                bookingRepository.findAllByEventId(conn,eventId));
    }

    /**
     * Gets all bookings with a specific status.
     * @param bookingStatus the status used to filter bookings
     * @return List of bookings with the given status
     */
    public List<Booking> getBookingsWithStatus(BookingStatus bookingStatus){
        return transactionManager.inReadOnly(conn->
                bookingRepository.findAllByStatus(conn,bookingStatus));
    }

    /**
     * Gets all confirmed bookings.
     * @return List of confirmed bookings
     */
    public List<Booking> getConfirmedBookings(){
        return getBookingsWithStatus(BookingStatus.CONFIRMED);
    }

    /**
     * Gets all bookings that are pending payment.
     * @return List of pending bookings
     */
    public List<Booking> getPendingBookings(){
        return getBookingsWithStatus(BookingStatus.PENDING_PAYMENT);
    }

    /**
     * Gets all cancelled bookings.
     * @return List of cancelled bookings
     */
    public List<Booking> getCancelledBookings(){
        return getBookingsWithStatus(BookingStatus.CANCELLED);
    }

    /**
     * Gets all bookings made by a user with a specific status.
     * @param userId the id of the user
     * @param bookingStatus the status used to filter bookings
     * @return List of bookings matching user and status
     */
    public List<Booking> getBookingsByUserWithStatus(long userId, BookingStatus bookingStatus){
        return transactionManager.inReadOnly(conn->
                bookingRepository.findAllByUserIdAndStatus(conn,userId,bookingStatus));
    }

    /**
     * Gets all confirmed bookings made by a specific user.
     * @param userId the id of the user
     * @return List of confirmed bookings made by the user
     */
    public List<Booking> getConfirmedBookingsMadeByUser(long userId){
        return getBookingsByUserWithStatus(userId,BookingStatus.CONFIRMED);
    }

    /**
     * Gets all pending bookings made by a specific user.
     * @param userId the id of the user
     * @return List of pending bookings made by the user
     */
    public List<Booking> getPendingBookingsMadeByUser(long userId){
        return getBookingsByUserWithStatus(userId,BookingStatus.PENDING_PAYMENT);
    }

    /**
     * Gets all cancelled bookings made by a specific user.
     * @param userId the id of the user
     * @return List of cancelled bookings made by the user
     */
    public List<Booking> getCancelledBookingsMadeByUser(long userId){
        return getBookingsByUserWithStatus(userId,BookingStatus.CANCELLED);
    }

    /**
     * Gets all bookings for an event with a specific status.
     * @param eventId the id of the event
     * @param bookingStatus the status used to filter bookings
     * @return List of bookings matching event and status
     */
    public List<Booking> getBookingsForEventWithStatus(long eventId, BookingStatus bookingStatus){
        return transactionManager.inReadOnly(conn->
                bookingRepository.findAllByEventIdAndStatus(conn,eventId,bookingStatus));
    }

    /**
     * Gets all cancelled bookings for a specific event.
     * @param eventId the id of the event
     * @return List of cancelled bookings for the event
     */
    public List<Booking> getCancelledBookingsForEvent(long eventId){
        return getBookingsForEventWithStatus(eventId, BookingStatus.CANCELLED);
    }

    /**
     * Gets all pending bookings for a specific event.
     * @param eventId the id of the event
     * @return List of pending bookings for the event
     */
    public List<Booking> getPendingBookingsForEvent(long eventId){
        return getBookingsForEventWithStatus(eventId,BookingStatus.PENDING_PAYMENT);
    }

    /**
     * Gets all confirmed bookings for a specific event.
     * @param eventId the id of the event
     * @return List of confirmed bookings for the event
     */
    public List<Booking> getConfirmedBookingsForEvent(long eventId){
        return getBookingsForEventWithStatus(eventId,BookingStatus.CONFIRMED);
    }

    /**
     * Gets all bookings made by a user for a specific event.
     * @param userId the id of the user
     * @param eventId the id of the event
     * @return List of bookings matching user and event
     */
    public List<Booking> getBookingsForEventMadeByUser(long userId, long eventId){
        return transactionManager.inReadOnly(conn->
                bookingRepository.findAllByUserIdAndEventId(conn,userId,eventId));
    }

    /**
     * Create a new booking, inserting all tickets to db and creating a new booking, checking for overbooking first
     * @param userId the id of the user booking the tickets
     * @param eventId the id of the event
     * @param tickets list of tickets to book, they must have firstname and lastname fields
     * @return Booking new booking
     * @throws NotFoundException if no event is found with such id
     * @throws ConflictException if overbooking is happening
     */
    public Booking book(long userId, long eventId, List<Ticket> tickets){

        validateManyTickets(tickets);

        return transactionManager.inTransaction(conn->{

            validateUserExists(conn,userId);

            Event event = eventRepository.findByIdForUpdate(conn,eventId)
                    .orElseThrow(() -> new NotFoundException("No event with such id exists"));

            validateEventToBook(event);

            int alreadyReserved = ticketRepository.countTicketsForEvent(conn,eventId);

            if(tickets.size() + alreadyReserved > event.getCapacity()) {
                throw new ConflictException("Not enough available places remaining for event: "+eventId);
            }

            BigDecimal ticketPrice = event.getTicketPrice() != null ? event.getTicketPrice() : BigDecimal.ZERO;

            Booking booking = new Booking(
                    userId,
                    eventId,
                    LocalDateTime.now(),
                    BookingStatus.PENDING_PAYMENT,
                    ticketPrice.multiply(BigDecimal.valueOf(tickets.size()))
            );

            booking = booking.withId(
                    bookingRepository.insert(conn, booking)
            );

            long bookingId = booking.getId();
            List<Ticket> ticketsToInsert = tickets.stream()
                    .map(ticket -> ticket
                            .withBookingId(bookingId)
                            .withStartsAt(event.getBeginDatetime()))
                    .toList();

            ticketRepository.insertMany(conn,ticketsToInsert);

            return booking;
        });
    }

    /**
     * Updates the status of a booking.
     * @param bookingId the id of the booking to update
     * @param bookingStatus the new status to set
     */
    private void updateStatus(long actorId, long bookingId, BookingStatus bookingStatus){
        transactionManager.inTransaction(conn->{
            Booking booking = requireBookingManager(conn,bookingId,actorId);

            validateBookingStatusTransition(booking.getStatus(),bookingStatus);
            bookingRepository.updateStatus(conn,bookingId,bookingStatus);
            return null;
        });
    }

    /**
     * Confirms a booking by changing its status to CONFIRMED.
     * @param actorId booking owner or administrator performing the operation
     * @param bookingId the id of the booking to confirm
     */
    public void confirmBooking(long actorId, long bookingId){
        updateStatus(actorId,bookingId,BookingStatus.CONFIRMED);
    }

    /**
     * Cancels a booking by changing its status to CANCELLED.
     * @param actorId booking owner or administrator performing the operation
     * @param bookingId the id of the booking to cancel
     */
    public void cancelBooking(long actorId, long bookingId){
        updateStatus(actorId,bookingId,BookingStatus.CANCELLED);
    }

    /**
     * Deletes a booking from database.
     * @param actorId booking owner or administrator performing the operation
     * @param bookingId the id of the booking to delete
     */
    public void deleteBooking(long actorId, long bookingId){
        transactionManager.inTransaction(conn->{
            requireBookingManager(conn,bookingId,actorId);
            bookingRepository.delete(conn,bookingId);
            return null;
        });
    }

    /**
     * Loads and locks a booking, then verifies that the actor owns it or is an administrator.
     * @param conn active transaction connection
     * @param bookingId booking to authorize
     * @param actorId user performing the operation
     * @return locked booking
     * @throws NotFoundException if the booking or actor does not exist
     * @throws ForbiddenException if the actor cannot manage the booking
     */
    private Booking requireBookingManager(Connection conn, long bookingId, long actorId){
        Booking booking = bookingRepository.findByIdForUpdate(conn,bookingId)
                .orElseThrow(() -> new NotFoundException("No booking with id " + bookingId + " exists"));
        var actor = userRepository.findById(conn,actorId)
                .orElseThrow(() -> new NotFoundException("No user found with id " + actorId));
        if(!actor.isAdmin() && booking.getUserId() != actorId) {
            throw new ForbiddenException("Only the booking owner or an admin can manage this booking");
        }
        return booking;
    }

    /**
     * Validates if an event can receive bookings.
     * @param event the event to validate
     * @throws ValidationException if event is null, ended or not public
     */
    public void validateEventToBook(Event event){
        if(event == null) throw new ValidationException("Event is null");
        if(event.getStatus() != EventStatus.PUBLISHED) throw new ValidationException("Event is not published");
        if(event.getVisibility() != EventVisibility.PUBLIC) throw new ValidationException("Event is not public");
        if(!event.getBeginDatetime().isAfter(LocalDateTime.now())) {
            throw new ValidationException("Event has already started");
        }
    }

    /**
     * Validates a list of tickets before inserting them in a booking.
     * @param tickets list of tickets to validate
     * @throws ValidationException if list is empty or one ticket is not valid
     */
    public void validateManyTickets(List<Ticket> tickets){
        if(tickets == null || tickets.isEmpty()) throw new ValidationException("No tickets found");
        for(Ticket ticket : tickets){
            validateTicketForInsert(ticket);
        }
    }

    /**
     * Validates a single ticket before insert.
     * @param ticket the ticket to validate
     * @throws ValidationException if ticket is null, already booked or has invalid guest data
     */
    public void validateTicketForInsert(Ticket ticket){

        if (ticket == null) throw new ValidationException("Ticket is null");
        if(ticket.getBookingId() != 0) throw new ValidationException("Ticket has already been booked");
        if(ticket.getFirstname() == null || ticket.getLastname() == null) throw new ValidationException("Firstname or Lastname is null");
        if(ticket.getFirstname().isEmpty() || ticket.getLastname().isEmpty()) throw new ValidationException("Firstname or Lastname is empty");
    }

    /**
     * Returns a list of all tickets connected to a specific booking
     * @param bookingId the id of the booking
     * @return List of tickets linked to the booking
     */
    public List<Ticket> getTicketsForBooking(long bookingId){
        return transactionManager.inReadOnly(conn->
                ticketRepository.findAllByBookingId(conn,bookingId));
    }

    /**
     * Returns all tickets connected to a specific event
     * @param eventId the id of the event
     * @return List of tickets linked to the event
     */
    public List<Ticket> getTicketsForEvent(long eventId){
        return transactionManager.inReadOnly(conn->
                ticketRepository.findAllByEventId(conn,eventId) );
    }

    /**
     * Calculates number of available places for a booking
     * @param eventId the id of the event
     * @return number of remaining places
     */
    public int getRemainingPlaces(long eventId){
        return transactionManager.inReadOnly(conn->
        {
            Event event = eventRepository.findById(conn, eventId)
                    .orElseThrow(() -> new NotFoundException("No event with id: "+eventId));

            int soldTickets = ticketRepository.countTicketsForEvent(conn, eventId);
            return event.getCapacity() - soldTickets;
        });
    }

    /**
     * Validates if a user exists before creating a booking.
     * @param conn the db connection
     * @param userId the id of the user to validate
     * @throws NotFoundException if no user is found with such id
     */
    private void validateUserExists(Connection conn, long userId){
        if(userRepository.findById(conn,userId)
                .orElseThrow(() -> new NotFoundException("No user found with id "+userId))
                .getAccountStatus() == AccountStatus.BANNED) {
            throw new ForbiddenException("Banned users cannot book events");
        }
    }

    /**
     * Validates the booking state machine.
     * @param currentStatus current persisted booking status
     * @param newStatus requested booking status
     * @throws ConflictException if the transition is duplicated or not allowed
     */
    static void validateBookingStatusTransition(BookingStatus currentStatus, BookingStatus newStatus){
        if(currentStatus == newStatus) {
            throw new ConflictException("Booking is already " + newStatus);
        }

        boolean allowed = (currentStatus == BookingStatus.PENDING_PAYMENT
                && (newStatus == BookingStatus.CONFIRMED || newStatus == BookingStatus.CANCELLED))
                || (currentStatus == BookingStatus.CONFIRMED && newStatus == BookingStatus.CANCELLED);

        if(!allowed) {
            throw new ConflictException("Cannot change booking status from " + currentStatus + " to " + newStatus);
        }
    }



}
