package Venue_Event_Manager.service;

import Venue_Event_Manager.domain.model.booking.*;
import Venue_Event_Manager.domain.model.event.*;
import Venue_Event_Manager.domain.model.request.*;
import Venue_Event_Manager.domain.model.user.*;
import Venue_Event_Manager.domain.model.venue.Venue;
import Venue_Event_Manager.exception.*;
import Venue_Event_Manager.repository.*;
import Venue_Event_Manager.util.TestDataFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.math.BigDecimal;
import java.sql.Connection;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class CoreServiceWorkflowTest {
    private BookingRepository bookings;
    private TicketRepository tickets;
    private EventRepository events;
    private UserRepository users;
    private BookingService bookingService;

    @BeforeEach
    void setUp() {
        bookings = mock(BookingRepository.class);
        tickets = mock(TicketRepository.class);
        events = mock(EventRepository.class);
        users = mock(UserRepository.class);
        bookingService = new BookingService(bookings, tickets, events, users);
    }

    @Test
    void bookingShouldCalculateTotalAndPersistAssignedTickets() {
        Event event = bookableEvent(5, new BigDecimal("12.50"));
        when(users.findById(any(Connection.class), eq(7L)))
                .thenReturn(Optional.of(TestDataFactory.createDefaultUser("buyer").withId(7)));
        when(events.findByIdForUpdate(any(Connection.class), eq(5L))).thenReturn(Optional.of(event));
        when(tickets.countTicketsForEvent(any(Connection.class), eq(5L))).thenReturn(2);
        when(bookings.insert(any(Connection.class), any(Booking.class))).thenReturn(41L);

        Booking result = bookingService.book(7, 5,
                List.of(new Ticket("Mario", "Rossi"), new Ticket("Anna", "Bianchi")));

        assertAll(
                () -> assertEquals(41L, result.getId()),
                () -> assertEquals(BookingStatus.PENDING_PAYMENT, result.getStatus()),
                () -> assertEquals(new BigDecimal("25.00"), result.getTotalPrice())
        );
        ArgumentCaptor<List<Ticket>> captor = ArgumentCaptor.forClass(List.class);
        verify(tickets).insertMany(any(Connection.class), captor.capture());
        assertTrue(captor.getValue().stream().allMatch(t -> t.getBookingId() == 41L));
        assertTrue(captor.getValue().stream().allMatch(t -> event.getBeginDatetime().equals(t.getStartsAt())));
    }

    @Test
    void freeEventShouldProduceZeroPriceBooking() {
        Event event = bookableEvent(5, null);
        when(users.findById(any(Connection.class), anyLong()))
                .thenReturn(Optional.of(TestDataFactory.createDefaultUser("buyer").withId(7)));
        when(events.findByIdForUpdate(any(Connection.class), anyLong())).thenReturn(Optional.of(event));
        when(bookings.insert(any(Connection.class), any())).thenReturn(2L);
        Booking result = bookingService.book(7, 5, List.of(new Ticket("A", "B")));
        assertEquals(BigDecimal.ZERO, result.getTotalPrice());
    }

    @Test
    void overbookingShouldNotWriteBookingOrTickets() {
        when(users.findById(any(Connection.class), anyLong()))
                .thenReturn(Optional.of(TestDataFactory.createDefaultUser("buyer").withId(7)));
        when(events.findByIdForUpdate(any(Connection.class), anyLong()))
                .thenReturn(Optional.of(bookableEvent(5, BigDecimal.TEN).withCapacity(2)));
        when(tickets.countTicketsForEvent(any(Connection.class), anyLong())).thenReturn(2);
        assertThrows(ConflictException.class,
                () -> bookingService.book(7, 5, List.of(new Ticket("A", "B"))));
        verify(bookings, never()).insert(any(), any());
        verify(tickets, never()).insertMany(any(), anyList());
    }

    @Test
    void missingOrBannedUserShouldNotLockEvent() {
        when(users.findById(any(Connection.class), anyLong())).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class,
                () -> bookingService.book(7, 5, List.of(new Ticket("A", "B"))));
        verify(events, never()).findByIdForUpdate(any(), anyLong());

        reset(users, events);
        when(users.findById(any(Connection.class), anyLong()))
                .thenReturn(Optional.of(TestDataFactory.createBannedUser("banned").withId(7)));
        assertThrows(ForbiddenException.class,
                () -> bookingService.book(7, 5, List.of(new Ticket("A", "B"))));
        verify(events, never()).findByIdForUpdate(any(), anyLong());
    }

    @Test
    void confirmingAndCancellingShouldUseLockedStateAndExpectedStatus() {
        Booking pending = TestDataFactory.createDefaultBooking(7, 5).withId(9);
        when(bookings.findByIdForUpdate(any(Connection.class), eq(9L))).thenReturn(Optional.of(pending));
        bookingService.confirmBooking(9);
        verify(bookings).updateStatus(any(Connection.class), eq(9L), eq(BookingStatus.CONFIRMED));

        reset(bookings);
        when(bookings.findByIdForUpdate(any(Connection.class), eq(9L)))
                .thenReturn(Optional.of(pending.withStatus(BookingStatus.CONFIRMED)));
        bookingService.cancelBooking(9);
        verify(bookings).updateStatus(any(Connection.class), eq(9L), eq(BookingStatus.CANCELLED));
    }

    @Test
    void missingBookingShouldNotBeUpdated() {
        when(bookings.findByIdForUpdate(any(Connection.class), anyLong())).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> bookingService.confirmBooking(99));
        verify(bookings, never()).updateStatus(any(), anyLong(), any());
    }

    private Event bookableEvent(long id, BigDecimal price) {
        LocalDateTime begin = LocalDateTime.now().plusDays(5);
        return new Event(id, 1, null, "Bookable", "Description", begin, begin.plusHours(2), null,
                100, EventStatus.PUBLISHED, EventVisibility.PUBLIC, price, LocalDateTime.now());
    }
}
