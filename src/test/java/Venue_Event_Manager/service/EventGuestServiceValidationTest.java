package Venue_Event_Manager.service;

import Venue_Event_Manager.domain.model.event.EventGuest;
import Venue_Event_Manager.exception.ValidationException;
import Venue_Event_Manager.repository.EventGuestRepository;
import Venue_Event_Manager.repository.EventRepository;
import Venue_Event_Manager.util.TestDataFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;

class EventGuestServiceValidationTest {

    private EventGuestService service;

    @BeforeEach
    void setUp() {
        service = new EventGuestService(mock(EventGuestRepository.class), mock(EventRepository.class));
    }

    @Test void nullGuestShouldBeRejected() {
        assertThrows(ValidationException.class, () -> service.inviteGuest(null));
    }

    @Test void nullFirstnameShouldBeRejected() {
        assertThrows(ValidationException.class,
                () -> service.inviteGuest(validGuest().withFirstname(null)));
    }

    @Test void blankFirstnameShouldBeRejected() {
        assertThrows(ValidationException.class,
                () -> service.inviteGuest(validGuest().withFirstname(" ")));
    }

    @Test void nullLastnameShouldBeRejected() {
        assertThrows(ValidationException.class,
                () -> service.inviteGuest(validGuest().withLastname(null)));
    }

    @Test void blankLastnameShouldBeRejected() {
        assertThrows(ValidationException.class,
                () -> service.inviteGuest(validGuest().withLastname(" ")));
    }

    @Test void missingEventIdShouldBeRejected() {
        assertThrows(ValidationException.class,
                () -> service.inviteGuest(validGuest().withEventId(0)));
    }

    private EventGuest validGuest() {
        return TestDataFactory.createDefaultGuest("Mario", "Rossi", 1);
    }
}
