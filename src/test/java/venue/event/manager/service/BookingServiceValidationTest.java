package venue.event.manager.service;

import venue.event.manager.domain.model.booking.Ticket;
import venue.event.manager.domain.model.event.Event;
import venue.event.manager.domain.model.event.EventStatus;
import venue.event.manager.domain.model.event.EventVisibility;
import venue.event.manager.exception.ValidationException;
import venue.event.manager.repository.BookingRepository;
import venue.event.manager.repository.EventRepository;
import venue.event.manager.repository.TicketRepository;
import venue.event.manager.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;

class BookingServiceValidationTest {

    private BookingService service;

    @BeforeEach
    void setUp() {
        service = new BookingService(mock(BookingRepository.class), mock(TicketRepository.class),
                mock(EventRepository.class), mock(UserRepository.class));
    }

    @Test
    void publishedPublicFutureEventShouldBeBookable() {
        assertDoesNotThrow(() -> service.validateEventToBook(bookableEvent()));
    }

    @Test
    void nullEventShouldBeRejected() {
        assertThrows(ValidationException.class, () -> service.validateEventToBook(null));
    }

    @Test
    void draftEventShouldBeRejected() {
        assertThrows(ValidationException.class,
                () -> service.validateEventToBook(bookableEvent().withStatus(EventStatus.DRAFT)));
    }

    @Test
    void confirmedEventShouldBeRejected() {
        assertThrows(ValidationException.class,
                () -> service.validateEventToBook(bookableEvent().withStatus(EventStatus.CONFIRMED)));
    }

    @Test
    void cancelledEventShouldBeRejected() {
        assertThrows(ValidationException.class,
                () -> service.validateEventToBook(bookableEvent().withStatus(EventStatus.CANCELLED)));
    }

    @Test
    void privateEventShouldBeRejected() {
        assertThrows(ValidationException.class, () -> service.validateEventToBook(
                bookableEvent().withVisibility(EventVisibility.PRIVATE_GUEST_LIST)));
    }

    @Test
    void startedEventShouldBeRejected() {
        assertThrows(ValidationException.class, () -> service.validateEventToBook(
                bookableEvent().withBeginDateTime(LocalDateTime.now().minusMinutes(1))));
    }

    @Test
    void validTicketShouldBeAccepted() {
        assertDoesNotThrow(() -> service.validateTicketForInsert(new Ticket("Mario", "Rossi")));
    }

    @Test
    void nullTicketShouldBeRejected() {
        assertThrows(ValidationException.class, () -> service.validateTicketForInsert(null));
    }

    @Test
    void ticketAlreadyAssignedToBookingShouldBeRejected() {
        assertThrows(ValidationException.class,
                () -> service.validateTicketForInsert(new Ticket(10, "Mario", "Rossi", null)));
    }

    @Test
    void ticketWithNullNameShouldBeRejected() {
        assertThrows(ValidationException.class,
                () -> service.validateTicketForInsert(new Ticket(null, "Rossi")));
    }

    @Test
    void ticketWithEmptyNameShouldBeRejected() {
        assertThrows(ValidationException.class,
                () -> service.validateTicketForInsert(new Ticket("", "Rossi")));
    }

    @Test
    void nullTicketListShouldBeRejected() {
        assertThrows(ValidationException.class, () -> service.validateManyTickets(null));
    }

    @Test
    void emptyTicketListShouldBeRejected() {
        assertThrows(ValidationException.class, () -> service.validateManyTickets(List.of()));
    }

    @Test
    void listContainingInvalidTicketShouldBeRejected() {
        assertThrows(ValidationException.class,
                () -> service.validateManyTickets(List.of(new Ticket("Mario", "Rossi"), new Ticket("", "Bianchi"))));
    }

    private Event bookableEvent() {
        LocalDateTime begin = LocalDateTime.now().plusDays(2);
        return new Event(1, 1, null, "Future event", "Description", begin, begin.plusHours(2), null,
                100, EventStatus.PUBLISHED, EventVisibility.PUBLIC, BigDecimal.TEN, LocalDateTime.now());
    }
}
