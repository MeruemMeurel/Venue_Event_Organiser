package venue.event.manager.service;

import venue.event.manager.domain.model.resource.*;
import venue.event.manager.domain.model.venue.Venue;
import venue.event.manager.exception.ValidationException;
import venue.event.manager.repository.*;
import venue.event.manager.util.TestDataFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static venue.event.manager.util.TestTransactionManagerFactory.create;

class ResourceServiceValidationTest {
    private ResourceService service;
    private VenueRepository venues;

    @BeforeEach void setUp() {
        venues = mock(VenueRepository.class);
        when(venues.findById(any(Connection.class), anyLong()))
                .thenReturn(Optional.of(TestDataFactory.createDefaultVenue("Venue").withId(1)));
        service = new ResourceService(create(), mock(SpaceRepository.class), mock(EquipmentRepository.class),
                mock(ServiceRepository.class), venues, mock(EventRepository.class));
    }

    @Test void nullResourceShouldBeRejected() { assertThrows(ValidationException.class, () -> service.create(null)); }
    @Test void blankResourceNameShouldBeRejected() { assertThrows(ValidationException.class, () -> service.create(new Service(" ", "Description"))); }
    @Test void spaceWithoutVenueShouldBeRejected() { assertThrows(ValidationException.class, () -> service.create(new Space(0, "Space", "Description"))); }
    @Test void missingVenueShouldBeRejected() {
        when(venues.findById(any(Connection.class), anyLong())).thenReturn(Optional.empty());
        assertThrows(ValidationException.class, () -> service.create(new Space(1, "Space", "Description")));
    }
    @Test void zeroEquipmentQuantityShouldBeRejected() { assertThrows(ValidationException.class, () -> service.create(new Equipment(null, "Equipment", "Description", 0))); }
    @Test void serviceWithVenueShouldBeRejected() { assertThrows(ValidationException.class, () -> service.create(new Service(0, "Service", "Description") {
        @Override public Long getVenueId() { return 1L; }
    })); }
    @Test void nullResourceTypeShouldBeRejected() { assertThrows(ValidationException.class, () -> service.delete(null, 1)); }
    @Test void invalidDeleteIdShouldBeRejected() { assertThrows(ValidationException.class, () -> service.delete(ResourceType.SPACE, 0)); }
}
