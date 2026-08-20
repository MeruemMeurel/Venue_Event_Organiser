package venue.event.manager.service;

import venue.event.manager.domain.model.event.Event;
import venue.event.manager.domain.model.event.EventStatus;
import venue.event.manager.domain.model.user.User;
import venue.event.manager.domain.model.venue.Venue;
import venue.event.manager.exception.ForbiddenException;
import venue.event.manager.exception.ValidationException;
import venue.event.manager.repository.*;
import venue.event.manager.util.TestDataFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.sql.Connection;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.*;
import static venue.event.manager.util.TestTransactionManagerFactory.create;

class EventServiceValidationTest {

    private EventRepository events;
    private VenueRepository venues;
    private UserRepository users;
    private EventService service;

    @BeforeEach
    void setUp() {
        events = mock(EventRepository.class);
        venues = mock(VenueRepository.class);
        users = mock(UserRepository.class);
        when(venues.findById(any(Connection.class), anyLong()))
                .thenReturn(Optional.of(TestDataFactory.createDefaultVenue("Venue").withId(1)));
        when(users.findById(any(Connection.class), anyLong()))
                .thenReturn(Optional.of(TestDataFactory.createAdminUser("creator").withId(1)));
        when(events.insert(any(Connection.class), any(Event.class))).thenReturn(42L);
        service = new EventService(create(), events, mock(EventRequestRepository.class), mock(TicketRepository.class),
                mock(BookingRepository.class), mock(EventGuestRepository.class), venues, users);
    }

    @Test
    void createShouldPersistEventAsDraftWithoutPublicationDate() {
        assertEquals(42L, service.createEvent(validEvent().withStatus(EventStatus.PUBLISHED)
                .withPublishedAt(LocalDateTime.now())));
        verify(events).insert(any(Connection.class), argThat(event ->
                event.getStatus() == EventStatus.DRAFT && event.getPublishedAt() == null));
    }

    @Test void nullEventShouldBeRejected() {
        assertThrows(ValidationException.class, () -> service.createEvent(null));
    }

    @Test void emptyNameShouldBeRejected() {
        assertThrows(ValidationException.class, () -> service.createEvent(validEvent().withName("")));
    }

    @Test void missingStartShouldBeRejected() {
        assertThrows(ValidationException.class, () -> service.createEvent(validEvent().withBeginDateTime(null)));
    }

    @Test void endBeforeStartShouldBeRejected() {
        Event event = validEvent();
        assertThrows(ValidationException.class,
                () -> service.createEvent(event.withEndDateTime(event.getBeginDatetime().minusMinutes(1))));
    }

    @Test void zeroCapacityShouldBeRejected() {
        assertThrows(ValidationException.class, () -> service.createEvent(validEvent().withCapacity(0)));
    }

    @Test void negativePriceShouldBeRejected() {
        assertThrows(ValidationException.class,
                () -> service.createEvent(validEvent().withTicketPrice(new BigDecimal("-0.01"))));
    }

    @Test void missingVenueShouldBeRejected() {
        when(venues.findById(any(Connection.class), anyLong())).thenReturn(Optional.empty());
        assertThrows(ValidationException.class, () -> service.createEvent(validEvent()));
    }

    @Test void missingCreatorShouldBeRejected() {
        when(users.findById(any(Connection.class), anyLong())).thenReturn(Optional.empty());
        assertThrows(ValidationException.class, () -> service.createEvent(validEvent()));
    }

    @Test void nonAdminCreatorShouldBeRejected() {
        when(users.findById(any(Connection.class), eq(1L)))
                .thenReturn(Optional.of(TestDataFactory.createDefaultUser("creator").withId(1)));
        assertThrows(ForbiddenException.class, () -> service.createEvent(validEvent()));
        verify(events, never()).insert(any(), any());
    }

    @Test void adminOrganiserShouldBeRejected() {
        User admin = TestDataFactory.createAdminUser("admin").withId(2);
        when(users.findById(any(Connection.class), eq(2L))).thenReturn(Optional.of(admin));
        assertThrows(ValidationException.class, () -> service.createEvent(validEvent().withOrganiserId(2L)));
    }

    private Event validEvent() {
        LocalDateTime begin = LocalDateTime.now().plusDays(5);
        return new Event(1, 1, null, "Valid event", "Description", begin, begin.plusHours(2), null,
                100, EventStatus.CONFIRMED, null, BigDecimal.TEN, null);
    }
}
