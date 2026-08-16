package Venue_Event_Manager.service;

import Venue_Event_Manager.domain.model.event.*;
import Venue_Event_Manager.domain.model.request.*;
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

class RequestAndGuestWorkflowTest {
    private EventRequestRepository requests;
    private UserRepository users;
    private VenueRepository venues;
    private EventRequestService requestService;

    @BeforeEach
    void setUp() {
        requests = mock(EventRequestRepository.class);
        users = mock(UserRepository.class);
        venues = mock(VenueRepository.class);
        requestService = new EventRequestService(requests, users, venues);
    }

    @Test
    void createRequestShouldSupplyCreationTimeWhenMissing() {
        when(users.findById(any(Connection.class), eq(1L)))
                .thenReturn(Optional.of(TestDataFactory.createDefaultUser("requester").withId(1)));
        when(venues.findById(any(Connection.class), eq(2L)))
                .thenReturn(Optional.of(TestDataFactory.createDefaultVenue("Venue").withId(2)));
        when(requests.insert(any(Connection.class), any())).thenReturn(17L);
        EventRequest request = futureRequest().withCreatedAt(null);
        LocalDateTime before = LocalDateTime.now();
        assertEquals(17L, requestService.createRequest(request));
        ArgumentCaptor<EventRequest> captor = ArgumentCaptor.forClass(EventRequest.class);
        verify(requests).insert(any(Connection.class), captor.capture());
        assertFalse(captor.getValue().getCreatedAt().isBefore(before));
    }

    @Test
    void handlerAssignmentShouldRequireAnAdminAndPreserveOtherFields() {
        EventRequest pending = futureRequest().withId(4);
        User admin = TestDataFactory.createAdminUser("admin").withId(8);
        when(users.findById(any(Connection.class), eq(8L))).thenReturn(Optional.of(admin));
        when(requests.findById(any(Connection.class), eq(4L))).thenReturn(Optional.of(pending));
        requestService.assignHandler(4, 8);
        ArgumentCaptor<EventRequest> captor = ArgumentCaptor.forClass(EventRequest.class);
        verify(requests).update(any(Connection.class), captor.capture());
        assertEquals(8L, captor.getValue().getHandlerId());
        assertEquals(EventRequestStatus.PENDING, captor.getValue().getStatus());
    }

    @Test
    void nonAdminHandlerShouldNotUpdateRequest() {
        when(users.findById(any(Connection.class), anyLong()))
                .thenReturn(Optional.of(TestDataFactory.createDefaultUser("user").withId(8)));
        assertThrows(ValidationException.class, () -> requestService.assignHandler(4, 8));
        verify(requests, never()).update(any(), any());
    }

    @Test
    void acceptRejectAndCancelShouldClosePendingRequest() {
        EventRequest pending = futureRequest().withId(4).withHandlerId(8L);
        when(requests.findById(any(Connection.class), eq(4L))).thenReturn(Optional.of(pending));
        requestService.acceptRequest(4, new BigDecimal("99.90"));
        assertUpdatedRequest(EventRequestStatus.ACCEPTED, new BigDecimal("99.90"));

        reset(requests);
        when(requests.findById(any(Connection.class), eq(4L))).thenReturn(Optional.of(pending));
        requestService.rejectRequest(4);
        assertUpdatedRequest(EventRequestStatus.REJECTED, pending.getQuote());

        reset(requests);
        when(requests.findById(any(Connection.class), eq(4L))).thenReturn(Optional.of(pending));
        requestService.cancelRequest(4);
        assertUpdatedRequest(EventRequestStatus.CANCELLED, pending.getQuote());
    }

    @Test
    void acceptedRequestCannotBeClosedAgain() {
        when(requests.findById(any(Connection.class), anyLong()))
                .thenReturn(Optional.of(futureRequest().withId(4).withStatus(EventRequestStatus.ACCEPTED)));
        assertThrows(ConflictException.class, () -> requestService.rejectRequest(4));
        verify(requests, never()).update(any(), any());
    }

    @Test
    void privateEventInvitationShouldForceInvitedStatus() {
        EventGuestRepository guests = mock(EventGuestRepository.class);
        EventRepository events = mock(EventRepository.class);
        EventGuestService service = new EventGuestService(guests, events);
        Event event = privateEvent();
        when(events.findByIdForUpdate(any(Connection.class), eq(3L))).thenReturn(Optional.of(event));
        when(guests.insert(any(Connection.class), any())).thenReturn(12L);
        EventGuest input = TestDataFactory.createDefaultGuest("Mario", "Rossi", 3)
                .withStatus(EventGuestStatus.CONFIRMED);
        assertEquals(12L, service.inviteGuest(input));
        ArgumentCaptor<EventGuest> captor = ArgumentCaptor.forClass(EventGuest.class);
        verify(guests).insert(any(Connection.class), captor.capture());
        assertEquals(EventGuestStatus.INVITED, captor.getValue().getStatus());
    }

    @Test
    void publicOrMissingEventShouldNotInsertGuest() {
        EventGuestRepository guests = mock(EventGuestRepository.class);
        EventRepository events = mock(EventRepository.class);
        EventGuestService service = new EventGuestService(guests, events);
        EventGuest guest = TestDataFactory.createDefaultGuest("Mario", "Rossi", 3);
        when(events.findByIdForUpdate(any(Connection.class), anyLong())).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> service.inviteGuest(guest));
        when(events.findByIdForUpdate(any(Connection.class), anyLong()))
                .thenReturn(Optional.of(privateEvent().withVisibility(EventVisibility.PUBLIC)));
        assertThrows(ValidationException.class, () -> service.inviteGuest(guest));
        verify(guests, never()).insert(any(), any());
    }

    @Test
    void guestManagementShouldPreserveEventAndStatusAndUseLockedTransitions() {
        EventGuestRepository guests = mock(EventGuestRepository.class);
        EventRepository events = mock(EventRepository.class);
        EventGuestService service = new EventGuestService(guests, events);
        EventGuest stored = TestDataFactory.createDefaultGuest("Mario", "Rossi", 3).withId(7);
        when(guests.findByIdForUpdate(any(Connection.class), eq(7L))).thenReturn(Optional.of(stored));

        service.updateGuest(stored.withEventId(99).withStatus(EventGuestStatus.CONFIRMED).withNote("Updated"));
        ArgumentCaptor<EventGuest> captor = ArgumentCaptor.forClass(EventGuest.class);
        verify(guests).update(any(Connection.class), captor.capture());
        assertEquals(3L, captor.getValue().getEventId());
        assertEquals(EventGuestStatus.INVITED, captor.getValue().getStatus());

        service.confirmInvitation(7);
        verify(guests).updateEventGuestStatus(any(Connection.class), eq(7L), eq(EventGuestStatus.CONFIRMED));

        reset(guests);
        when(guests.findByIdForUpdate(any(Connection.class), eq(7L)))
                .thenReturn(Optional.of(stored.withStatus(EventGuestStatus.CONFIRMED)));
        service.cancelInvitation(7);
        verify(guests).updateEventGuestStatus(any(Connection.class), eq(7L), eq(EventGuestStatus.CANCELLED));

        when(guests.findById(any(Connection.class), eq(7L))).thenReturn(Optional.of(stored));
        service.removeGuest(7);
        verify(guests).deleteById(any(Connection.class), eq(7L));
    }

    private void assertUpdatedRequest(EventRequestStatus status, BigDecimal quote) {
        ArgumentCaptor<EventRequest> captor = ArgumentCaptor.forClass(EventRequest.class);
        verify(requests).update(any(Connection.class), captor.capture());
        assertEquals(status, captor.getValue().getStatus());
        assertEquals(quote, captor.getValue().getQuote());
        assertNotNull(captor.getValue().getClosedAt());
    }

    private EventRequest futureRequest() {
        LocalDateTime begin = LocalDateTime.now().plusDays(10);
        return TestDataFactory.createDefaultRequest(1, 2, "Request")
                .withBeginDateTime(begin).withEndDateTime(begin.plusHours(2))
                .withCreatedAt(LocalDateTime.now()).withQuote(null);
    }

    private Event privateEvent() {
        LocalDateTime begin = LocalDateTime.now().plusDays(5);
        return new Event(3, 2, null, "Private", "Description", begin, begin.plusHours(2), null,
                20, EventStatus.PUBLISHED, EventVisibility.PRIVATE_GUEST_LIST, BigDecimal.ZERO, LocalDateTime.now());
    }
}
