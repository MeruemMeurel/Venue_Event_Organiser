package Venue_Event_Manager.service;

import Venue_Event_Manager.config.TransactionManager;
import Venue_Event_Manager.domain.model.booking.Booking;
import Venue_Event_Manager.domain.model.booking.BookingStatus;
import Venue_Event_Manager.domain.model.booking.Ticket;
import Venue_Event_Manager.domain.model.event.Event;
import Venue_Event_Manager.domain.model.event.EventVisibility;
import Venue_Event_Manager.exception.ConflictException;
import Venue_Event_Manager.exception.ForbiddenException;
import Venue_Event_Manager.exception.NotFoundException;
import Venue_Event_Manager.exception.ValidationException;
import Venue_Event_Manager.repository.BookingRepository;
import Venue_Event_Manager.repository.EquipmentRepository;
import Venue_Event_Manager.repository.EventRepository;
import Venue_Event_Manager.repository.TicketRepository;
import Venue_Event_Manager.repository.UserRepository;

import java.awt.print.Book;
import java.math.BigDecimal;
import java.sql.Connection;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class BookingService {

    private TransactionManager transactionManager;
    private BookingRepository bookingRepository;
    private TicketRepository ticketRepository;
    private EventRepository eventRepository;
    private UserRepository userRepository;

    /**
     * Initializes BookingService with all repositories needed to handle bookings and tickets.
     * @param bookingRepository repository used to access booking data
     * @param ticketRepository repository used to access ticket data
     * @param eventRepository repository used to access event data
     * @param userRepository repository used to access user data
     */
    public BookingService(BookingRepository bookingRepository, TicketRepository ticketRepository, EventRepository eventRepository,
                          UserRepository userRepository) {
        this.transactionManager = TransactionManager.getInstance();
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
        return transactionManager.inTransaction(conn->
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

            for(int i=0;i<tickets.size();i++) {

                tickets.set(i, tickets.get(i)
                        .withBookingId(booking.getId())
                        .withStartsAt(event.getBeginDatetime())
                );

            }

            ticketRepository.insertMany(conn,tickets);

            return booking;
        });
    }

    /**
     * Updates the status of a booking.
     * @param bookingId the id of the booking to update
     * @param bookingStatus the new status to set
     */
    private void updateStatus(long bookingId, BookingStatus bookingStatus){
        transactionManager.inTransaction(conn->{
            bookingRepository.updateStatus(conn,bookingId,bookingStatus);
            return null;
        });
    }

    /**
     * Confirms a booking by changing its status to CONFIRMED.
     * @param bookingId the id of the booking to confirm
     */
    public void confirmBooking(long bookingId){
        //TODO discuss with group which booking status transitions are allowed before confirming a booking
        updateStatus(bookingId,BookingStatus.CONFIRMED);
    }

    /**
     * Cancels a booking by changing its status to CANCELLED.
     * @param bookingId the id of the booking to cancel
     */
    public void cancelBooking(long bookingId){
        //TODO discuss with group which booking status transitions are allowed before cancelling a booking
        updateStatus(bookingId,BookingStatus.CANCELLED);
    }

    /**
     * Deletes a booking from database.
     * @param bookingId the id of the booking to delete
     */
    public void deleteBooking(long bookingId){
        transactionManager.inTransaction(conn->{
            bookingRepository.delete(conn,bookingId);
            return null;
        });
    }

    /**
     * Validates if an event can receive bookings.
     * @param event the event to validate
     * @throws ValidationException if event is null, ended or not public
     */
    public void validateEventToBook(Event event){
        if(event == null) throw new ValidationException("Event is null");
        if(event.getEndDatetime().isBefore(LocalDateTime.now())) throw new ValidationException("Event has ended");
        if(!(event.getVisibility() == EventVisibility.PUBLIC))  throw new ValidationException("Event is not public");
        //TODO discuss with group which event statuses allow public booking
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
     * @param bookingId the id of the booking
     * @return List of tickets linked to the booking
     */
    public List<Ticket> getTicketsForBooking(long bookingId){
        return transactionManager.inTransaction(conn->
                ticketRepository.findAllByBookingId(conn,bookingId));
    }

    /**
     * @param eventId the id of the event
     * @return List of tickets linked to the event
     */
    public List<Ticket> getTicketsForEvent(long eventId){
        return transactionManager.inTransaction(conn->
                ticketRepository.findAllByEventId(conn,eventId) );
    }

    /**
     * @param eventId the id of the event
     * @return number of remaining places
     */
    public int getRemainingPlaces(long eventId){
        return transactionManager.inTransaction(conn->
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
        userRepository.findById(conn,userId)
                .orElseThrow(() -> new NotFoundException("No user found with id "+userId));
    }



}
