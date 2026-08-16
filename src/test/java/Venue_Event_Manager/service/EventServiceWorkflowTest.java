package Venue_Event_Manager.service;

import Venue_Event_Manager.domain.model.event.*;
import Venue_Event_Manager.domain.model.user.User;
import Venue_Event_Manager.exception.*;
import Venue_Event_Manager.repository.*;
import Venue_Event_Manager.util.TestDataFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.math.BigDecimal;
import java.sql.Connection;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class EventServiceWorkflowTest {
    private EventRepository events;
    private BookingRepository bookings;
    private EventGuestRepository guests;
    private VenueRepository venues;
    private UserRepository users;
    private TicketRepository tickets;
    private EventService service;

    @BeforeEach
    void setUp() {
        events = mock(EventRepository.class);
        bookings = mock(BookingRepository.class);
        guests = mock(EventGuestRepository.class);
        venues = mock(VenueRepository.class);
        users = mock(UserRepository.class);
        tickets = mock(TicketRepository.class);
        when(venues.findById(any(Connection.class), eq(2L)))
                .thenReturn(Optional.of(TestDataFactory.createDefaultVenue("Venue").withId(2)));
        when(users.findById(any(Connection.class), eq(1L)))
                .thenReturn(Optional.of(TestDataFactory.createDefaultUser("creator").withId(1)));
        service = new EventService(events, mock(EventRequestRepository.class), tickets, bookings, guests, venues, users);
    }

    @Test
    void createShouldAlwaysPersistDraftWithoutPublicationDate() {
        when(events.insert(any(Connection.class), any())).thenReturn(11L);
        Event input = validEvent(EventStatus.PUBLISHED).withPublishedAt(LocalDateTime.now());
        assertEquals(11L, service.createEvent(input));
        ArgumentCaptor<Event> captor = ArgumentCaptor.forClass(Event.class);
        verify(events).insert(any(Connection.class), captor.capture());
        assertEquals(EventStatus.DRAFT, captor.getValue().getStatus());
        assertNull(captor.getValue().getPublishedAt());
    }

    @Test
    void updateShouldPreservePersistedLifecycleFields() {
        LocalDateTime publishedAt = LocalDateTime.now().minusDays(1);
        Event stored = validEvent(EventStatus.PUBLISHED).withPublishedAt(publishedAt);
        when(events.findByIdForUpdate(any(Connection.class), eq(6L))).thenReturn(Optional.of(stored));
        service.updateEvent(validEvent(EventStatus.CANCELLED).withPublishedAt(null).withName("Changed"));
        ArgumentCaptor<Event> captor = ArgumentCaptor.forClass(Event.class);
        verify(events).update(any(Connection.class), captor.capture());
        assertEquals(EventStatus.PUBLISHED, captor.getValue().getStatus());
        assertEquals(publishedAt, captor.getValue().getPublishedAt());
        assertEquals("Changed", captor.getValue().getName());
    }

    @Test
    void publishShouldSetStatusAndCurrentPublicationTime() {
        when(events.findByIdForUpdate(any(Connection.class), eq(6L)))
                .thenReturn(Optional.of(validEvent(EventStatus.CONFIRMED)));
        LocalDateTime before = LocalDateTime.now();
        service.publishEvent(6);
        ArgumentCaptor<LocalDateTime> time = ArgumentCaptor.forClass(LocalDateTime.class);
        verify(events).updateStatusAndPublishedAt(any(Connection.class), eq(6L), eq(EventStatus.PUBLISHED), time.capture());
        assertFalse(time.getValue().isBefore(before));
    }

    @Test
    void cancellationShouldCascadeBeforeUpdatingEventStatus() {
        when(events.findByIdForUpdate(any(Connection.class), eq(6L)))
                .thenReturn(Optional.of(validEvent(EventStatus.PUBLISHED)));
        service.cancelEvent(6);
        var order = inOrder(bookings, guests, events);
        order.verify(bookings).cancelActiveByEventId(any(Connection.class), eq(6L));
        order.verify(guests).cancelActiveByEventId(any(Connection.class), eq(6L));
        order.verify(events).updateStatus(any(Connection.class), eq(6L), eq(EventStatus.CANCELLED));
    }

    @Test
    void invalidCancellationTransitionShouldNotCascade() {
        when(events.findByIdForUpdate(any(Connection.class), eq(6L)))
                .thenReturn(Optional.of(validEvent(EventStatus.CANCELLED)));
        assertThrows(ConflictException.class, () -> service.cancelEvent(6));
        verifyNoInteractions(bookings, guests);
        verify(events, never()).updateStatus(any(), anyLong(), any());
    }

    @Test
    void capacityCannotDropBelowSoldTickets() {
        when(events.findById(any(Connection.class), eq(6L))).thenReturn(Optional.of(validEvent(EventStatus.PUBLISHED)));
        when(tickets.countTicketsForEvent(any(Connection.class), eq(6L))).thenReturn(10);
        assertThrows(ForbiddenException.class, () -> service.changeCapacity(6, 9));
        verify(events, never()).update(any(), any());
    }

    private Event validEvent(EventStatus status) {
        LocalDateTime begin = LocalDateTime.now().plusDays(8);
        return new Event(6, 2, 1, null, "Event", "Description", begin, begin.plusHours(3), null,
                100, status, EventVisibility.PUBLIC, BigDecimal.TEN, null);
    }
}
