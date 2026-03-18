package Venue_Event_Manager.service;

import Venue_Event_Manager.config.TransactionManager;
import Venue_Event_Manager.domain.model.booking.Booking;
import Venue_Event_Manager.domain.model.booking.BookingStatus;
import Venue_Event_Manager.domain.model.booking.Ticket;
import Venue_Event_Manager.domain.model.event.Event;
import Venue_Event_Manager.exception.ConflictException;
import Venue_Event_Manager.exception.ForbiddenException;
import Venue_Event_Manager.exception.NotFoundException;
import Venue_Event_Manager.repository.BookingRepository;
import Venue_Event_Manager.repository.EquipmentRepository;
import Venue_Event_Manager.repository.EventRepository;
import Venue_Event_Manager.repository.TicketRepository;

import java.awt.print.Book;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class BookingService {

    private TransactionManager transactionManager;
    private BookingRepository bookingRepository;
    private TicketRepository ticketRepository;
    private EventRepository eventRepository;

    public BookingService(BookingRepository bookingRepository, TicketRepository ticketRepository, EventRepository eventRepository) {
        this.transactionManager = TransactionManager.getInstance();
        this.bookingRepository = bookingRepository;
        this.ticketRepository = ticketRepository;
        this.eventRepository = eventRepository;
    }

    public List<Booking> getAllBookings(){
        return transactionManager.inReadOnly(conn->
                bookingRepository.findAll(conn));
    }

    public Booking getBooking(long bookingId){
        return transactionManager.inTransaction(conn->
                bookingRepository.findById(conn,bookingId)
                        .orElseThrow(() -> new NotFoundException("No booking with such id exists")));
    }

    public List<Booking> getBookingsMadeByUser(long userId){
        return transactionManager.inReadOnly(conn->
                bookingRepository.findAllByUserId(conn,userId));
    }

    public List<Booking> getBookingsForEvent(long eventId){
        return transactionManager.inReadOnly(conn->
                bookingRepository.findAllByEventId(conn,eventId));
    }

    public List<Booking> getBookingsWithStatus(BookingStatus bookingStatus){
        return transactionManager.inReadOnly(conn->
                bookingRepository.findAllByStatus(conn,bookingStatus));
    }

    public List<Booking> getConfirmedBookings(){
        return getBookingsWithStatus(BookingStatus.CONFIRMED);
    }

    public List<Booking> getPendingBookings(){
        return getBookingsWithStatus(BookingStatus.PENDING_PAYMENT);
    }

    public List<Booking> getCancelledBookings(){
        return getBookingsWithStatus(BookingStatus.CANCELLED);
    }

    public List<Booking> getBookingsByUserWithStatus(long userId, BookingStatus bookingStatus){
        return transactionManager.inReadOnly(conn->
                bookingRepository.findAllByUserIdAndStatus(conn,userId,bookingStatus));
    }

    public List<Booking> getConfirmedBookingsMadeByUser(long userId){
        return getBookingsByUserWithStatus(userId,BookingStatus.CONFIRMED);
    }

    public List<Booking> getPendingBookingsMadeByUser(long userId){
        return getBookingsByUserWithStatus(userId,BookingStatus.PENDING_PAYMENT);
    }

    public List<Booking> getCancelledBookingsMadeByUser(long userId){
        return getBookingsByUserWithStatus(userId,BookingStatus.CANCELLED);
    }

    public List<Booking> getBookingsForEventWithStatus(long eventId, BookingStatus bookingStatus){
        return transactionManager.inReadOnly(conn->
                bookingRepository.findAllByEventIdAndStatus(conn,eventId,bookingStatus));
    }

    public List<Booking> getCancelledBookingsForEvent(long eventId){
        return getBookingsForEventWithStatus(eventId, BookingStatus.CANCELLED);
    }

    public List<Booking> getPendingBookingsForEvent(long eventId){
        return getBookingsForEventWithStatus(eventId,BookingStatus.PENDING_PAYMENT);
    }

    public List<Booking> getConfirmedBookingsForEvent(long eventId){
        return getBookingsForEventWithStatus(eventId,BookingStatus.CONFIRMED);
    }

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
        return transactionManager.inTransaction(conn->{

            Event event = eventRepository.findByIdForUpdate(conn,eventId)
                    .orElseThrow(() -> new NotFoundException("No event with such id exists"));

            int alreadyReserved = ticketRepository.countTicketsForEvent(conn,eventId);

            if(tickets.size() + alreadyReserved > event.getCapacity()) {
                throw new ConflictException("Not enough available places remaining for event: "+eventId);
            }

            Booking booking = new Booking(
                    userId,
                    eventId,
                    LocalDateTime.now(),
                    BookingStatus.PENDING_PAYMENT,
                    event.getTicketPrice().multiply(BigDecimal.valueOf(tickets.size()))
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

    private void updateStatus(long bookingId, BookingStatus bookingStatus){
        transactionManager.inTransaction(conn->{
            bookingRepository.updateStatus(conn,bookingId,bookingStatus);
            return null;
        });
    }

    public void confirmBooking(long bookingId){
        updateStatus(bookingId,BookingStatus.CONFIRMED);
    }
    public void cancelBooking(long bookingId){
        updateStatus(bookingId,BookingStatus.CANCELLED);
    }

    public void deleteBooking(long bookingId){
        transactionManager.inTransaction(conn->{
            bookingRepository.delete(conn,bookingId);
            return null;
        });
    }



}
