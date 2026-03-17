package Venue_Event_Manager.service;

import Venue_Event_Manager.config.TransactionManager;
import Venue_Event_Manager.domain.model.booking.Booking;
import Venue_Event_Manager.domain.model.booking.BookingStatus;
import Venue_Event_Manager.exception.NotFoundException;
import Venue_Event_Manager.repository.BookingRepository;
import Venue_Event_Manager.repository.EquipmentRepository;

import java.awt.print.Book;
import java.util.List;

public class BookingService {

    private TransactionManager transactionManager;
    private BookingRepository bookingRepository;

    public BookingService(BookingRepository bookingRepository) {
        this.transactionManager = TransactionManager.getInstance();
        this.bookingRepository = bookingRepository;
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
        return getBookingsWithStatus(BookingStatus.CONFIRMED);
    }

    public List<Booking> getPendingBookingsMadeByUser(long userId){
        return getBookingsWithStatus(BookingStatus.PENDING_PAYMENT);
    }

    public List<Booking> getCancelledBookingsMadeByUser(long userId){
        return getBookingsWithStatus(BookingStatus.CANCELLED);
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

    //TODO book with overbooking


    public void updateStatus(long bookingId, BookingStatus bookingStatus){
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
