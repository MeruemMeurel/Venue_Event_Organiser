package venue.event.manager.service;

import venue.event.manager.domain.model.event.*;
import venue.event.manager.domain.model.user.User;
import venue.event.manager.exception.*;
import venue.event.manager.repository.*;
import venue.event.manager.util.TestDataFactory;
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
import static venue.event.manager.util.TestTransactionManagerFactory.create;

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
                .thenReturn(Optional.of(TestDataFactory.createAdminUser("creator").withId(1)));
        service = new EventService(create(), events, mock(EventRequestRepository.class), tickets, bookings, guests,
                venues, users);
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
        service.updateEvent(1, validEvent(EventStatus.CANCELLED).withPublishedAt(null).withName("Changed")
                .withCreatorId(99).withOrganiserId(98L));
        ArgumentCaptor<Event> captor = ArgumentCaptor.forClass(Event.class);
        verify(events).update(any(Connection.class), captor.capture());
        assertEquals(EventStatus.PUBLISHED, captor.getValue().getStatus());
        assertEquals(publishedAt, captor.getValue().getPublishedAt());
        assertEquals("Changed", captor.getValue().getName());
        assertEquals(1L, captor.getValue().getCreatorId());
        assertNull(captor.getValue().getOrganiserId());
    }

    @Test
    void publishShouldSetStatusAndCurrentPublicationTime() {
        when(events.findByIdForUpdate(any(Connection.class), eq(6L)))
                .thenReturn(Optional.of(validEvent(EventStatus.CONFIRMED)));
        LocalDateTime before = LocalDateTime.now();
        service.publishEvent(1, 6);
        ArgumentCaptor<LocalDateTime> time = ArgumentCaptor.forClass(LocalDateTime.class);
        verify(events).updateStatusAndPublishedAt(any(Connection.class), eq(6L), eq(EventStatus.PUBLISHED), time.capture());
        assertFalse(time.getValue().isBefore(before));
    }

    @Test
    void cancellationShouldCascadeBeforeUpdatingEventStatus() {
        when(events.findByIdForUpdate(any(Connection.class), eq(6L)))
                .thenReturn(Optional.of(validEvent(EventStatus.PUBLISHED)));
        service.cancelEvent(1, 6);
        var order = inOrder(bookings, guests, events);
        order.verify(bookings).cancelActiveByEventId(any(Connection.class), eq(6L));
        order.verify(guests).cancelActiveByEventId(any(Connection.class), eq(6L));
        order.verify(events).updateStatus(any(Connection.class), eq(6L), eq(EventStatus.CANCELLED));
    }

    @Test
    void invalidCancellationTransitionShouldNotCascade() {
        when(events.findByIdForUpdate(any(Connection.class), eq(6L)))
                .thenReturn(Optional.of(validEvent(EventStatus.CANCELLED)));
        assertThrows(ConflictException.class, () -> service.cancelEvent(1, 6));
        verifyNoInteractions(bookings, guests);
        verify(events, never()).updateStatus(any(), anyLong(), any());
    }

    @Test
    void capacityCannotDropBelowSoldTickets() {
        when(events.findByIdForUpdate(any(Connection.class), eq(6L))).thenReturn(Optional.of(validEvent(EventStatus.PUBLISHED)));
        when(tickets.countTicketsForEvent(any(Connection.class), eq(6L))).thenReturn(10);
        assertThrows(ForbiddenException.class, () -> service.changeCapacity(1, 6, 9));
        verify(events, never()).update(any(), any());
    }

    @Test
    void eventManagementOperationsShouldPreserveAndPersistRequestedChanges() {
        Event draft = validEvent(EventStatus.DRAFT);
        Event published = validEvent(EventStatus.PUBLISHED);
        when(events.findByIdForUpdate(any(Connection.class), eq(6L))).thenReturn(Optional.of(draft));
        service.confirmEvent(1, 6);
        verify(events).updateStatus(any(Connection.class), eq(6L), eq(EventStatus.CONFIRMED));

        when(events.findById(any(Connection.class), eq(6L))).thenReturn(Optional.of(published));
        LocalDateTime newBegin = LocalDateTime.now().plusDays(15);
        service.rescheduleEvent(1, 6, newBegin, newBegin.plusHours(4));
        service.changeCapacity(1, 6, 80);
        service.updatePoster(1, 6, "poster/new.png");
        service.setTicketPrice(1, 6, new BigDecimal("25.00"));
        service.removeOrganiser(1, 6);
        verify(events, times(5)).update(any(Connection.class), any(Event.class));

        when(users.findById(any(Connection.class), eq(4L)))
                .thenReturn(Optional.of(TestDataFactory.createDefaultUser("organiser").withId(4)));
        service.assignOrganiser(1, 6, 4);
        ArgumentCaptor<Event> captor = ArgumentCaptor.forClass(Event.class);
        verify(events, times(6)).update(any(Connection.class), captor.capture());
        assertEquals(4L, captor.getValue().getOrganiserId());

        service.changeVisibility(1, 6, EventVisibility.PRIVATE_GUEST_LIST);
        service.deleteEvent(1, 6);
        verify(events).updateVisibility(any(Connection.class), eq(6L), eq(EventVisibility.PRIVATE_GUEST_LIST));
        verify(events).deleteById(any(Connection.class), eq(6L));
    }

    @Test
    void assignedOrganiserShouldBeAllowedToManageItsEvent() {
        Event assignedEvent = validEvent(EventStatus.PUBLISHED).withOrganiserId(4L);
        when(events.findByIdForUpdate(any(Connection.class), eq(6L))).thenReturn(Optional.of(assignedEvent));
        when(users.findById(any(Connection.class), eq(4L)))
                .thenReturn(Optional.of(TestDataFactory.createDefaultUser("organiser").withId(4)));

        service.updatePoster(4, 6, "poster/organiser.png");

        verify(events).update(any(Connection.class), argThat(event ->
                "poster/organiser.png".equals(event.getPosterFilepath())));
    }

    @Test
    void unrelatedUserShouldNotBeAllowedToManageEvent() {
        Event assignedEvent = validEvent(EventStatus.PUBLISHED).withOrganiserId(4L);
        when(events.findByIdForUpdate(any(Connection.class), eq(6L))).thenReturn(Optional.of(assignedEvent));
        when(users.findById(any(Connection.class), eq(5L)))
                .thenReturn(Optional.of(TestDataFactory.createDefaultUser("other").withId(5)));

        assertThrows(ForbiddenException.class, () -> service.updatePoster(5, 6, "forbidden.png"));

        verify(events, never()).update(any(), any());
    }

    @Test
    void organiserShouldNotAssignAnotherOrganiser() {
        when(users.findById(any(Connection.class), eq(4L)))
                .thenReturn(Optional.of(TestDataFactory.createDefaultUser("organiser").withId(4)));

        assertThrows(ForbiddenException.class, () -> service.assignOrganiser(4, 6, 5));

        verify(events, never()).findByIdForUpdate(any(), anyLong());
        verify(events, never()).update(any(), any());
    }

    private Event validEvent(EventStatus status) {
        LocalDateTime begin = LocalDateTime.now().plusDays(8);
        return new Event(6, 2, 1, null, "Event", "Description", begin, begin.plusHours(3), null,
                100, status, EventVisibility.PUBLIC, BigDecimal.TEN, null);
    }
}
