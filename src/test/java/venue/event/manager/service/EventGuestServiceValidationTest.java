package venue.event.manager.service;

import venue.event.manager.domain.model.event.EventGuest;
import venue.event.manager.exception.ValidationException;
import venue.event.manager.repository.EventGuestRepository;
import venue.event.manager.repository.EventRepository;
import venue.event.manager.repository.UserRepository;
import venue.event.manager.util.TestDataFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static venue.event.manager.util.TestTransactionManagerFactory.create;

class EventGuestServiceValidationTest {

    private EventGuestService service;

    @BeforeEach
    void setUp() {
        service = new EventGuestService(create(), mock(EventGuestRepository.class), mock(EventRepository.class),
                mock(UserRepository.class));
    }

    @Test void nullGuestShouldBeRejected() {
        assertThrows(ValidationException.class, () -> service.inviteGuest(1, null));
    }

    @Test void nullFirstnameShouldBeRejected() {
        assertThrows(ValidationException.class,
                () -> service.inviteGuest(1, validGuest().withFirstname(null)));
    }

    @Test void blankFirstnameShouldBeRejected() {
        assertThrows(ValidationException.class,
                () -> service.inviteGuest(1, validGuest().withFirstname(" ")));
    }

    @Test void nullLastnameShouldBeRejected() {
        assertThrows(ValidationException.class,
                () -> service.inviteGuest(1, validGuest().withLastname(null)));
    }

    @Test void blankLastnameShouldBeRejected() {
        assertThrows(ValidationException.class,
                () -> service.inviteGuest(1, validGuest().withLastname(" ")));
    }

    @Test void missingEventIdShouldBeRejected() {
        assertThrows(ValidationException.class,
                () -> service.inviteGuest(1, validGuest().withEventId(0)));
    }

    private EventGuest validGuest() {
        return TestDataFactory.createDefaultGuest("Mario", "Rossi", 1);
    }
}
