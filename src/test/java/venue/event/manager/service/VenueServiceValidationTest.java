package venue.event.manager.service;

import venue.event.manager.domain.model.venue.Address;
import venue.event.manager.domain.model.venue.Venue;
import venue.event.manager.exception.ValidationException;
import venue.event.manager.repository.VenueRepository;
import venue.event.manager.util.TestDataFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static venue.event.manager.util.TestTransactionManagerFactory.create;

class VenueServiceValidationTest {

    private VenueService service;

    @BeforeEach
    void setUp() {
        service = new VenueService(create(), mock(VenueRepository.class));
    }

    @Test void nullVenueShouldBeRejected() {
        assertThrows(ValidationException.class, () -> service.createVenue(null));
    }

    @Test void blankNameShouldBeRejected() {
        assertThrows(ValidationException.class,
                () -> service.createVenue(validVenue().withName(" ")));
    }

    @Test void oneCharacterNameShouldBeRejected() {
        assertThrows(ValidationException.class,
                () -> service.createVenue(validVenue().withName("A")));
    }

    @Test void overlyLongDescriptionShouldBeRejected() {
        assertThrows(ValidationException.class,
                () -> service.createVenue(validVenue().withDescription("x".repeat(1001))));
    }

    @Test void missingAddressShouldBeRejected() {
        assertThrows(ValidationException.class,
                () -> service.createVenue(validVenue().withAddress(null)));
    }

    @Test void blankStreetShouldBeRejected() {
        assertThrows(ValidationException.class, () -> service.createVenue(
                validVenue().withAddress(validVenue().getAddress().withStreet(" "))));
    }

    @Test void blankCityShouldBeRejected() {
        assertThrows(ValidationException.class, () -> service.createVenue(
                validVenue().withAddress(validVenue().getAddress().withCity(null))));
    }

    @Test void invalidUpdateIdShouldBeRejected() {
        assertThrows(ValidationException.class, () -> service.updateVenue(validVenue()));
    }

    @Test void invalidDeleteIdShouldBeRejected() {
        assertThrows(ValidationException.class, () -> service.deleteVenue(0));
    }

    @Test void blankSearchTermShouldBeRejected() {
        assertThrows(ValidationException.class, () -> service.searchVenueByName(" "));
    }

    @Test void missingAvailabilityDateShouldBeRejected() {
        assertThrows(ValidationException.class,
                () -> service.getVenuesWithAvailableSpaces(null, LocalDateTime.now()));
    }

    @Test void invertedAvailabilityRangeShouldBeRejected() {
        LocalDateTime begin = LocalDateTime.now().plusHours(2);
        assertThrows(ValidationException.class,
                () -> service.getVenuesWithAvailableSpaces(begin, begin.minusHours(1)));
    }

    @Test void equalAvailabilityBoundsShouldBeRejected() {
        LocalDateTime instant = LocalDateTime.now();
        assertThrows(ValidationException.class,
                () -> service.getVenuesWithAvailableSpaces(instant, instant));
    }

    private Venue validVenue() {
        Address address = TestDataFactory.createDefaultAddress();
        return new Venue("Valid venue", "Description", address);
    }
}
