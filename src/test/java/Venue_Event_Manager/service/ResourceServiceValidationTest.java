package Venue_Event_Manager.service;

import Venue_Event_Manager.domain.model.resource.*;
import Venue_Event_Manager.domain.model.venue.Venue;
import Venue_Event_Manager.exception.ValidationException;
import Venue_Event_Manager.repository.*;
import Venue_Event_Manager.util.TestDataFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class ResourceServiceValidationTest {
    private ResourceService service;
    private VenueRepository venues;

    @BeforeEach void setUp() {
        venues = mock(VenueRepository.class);
        when(venues.findById(any(Connection.class), anyLong()))
                .thenReturn(Optional.of(TestDataFactory.createDefaultVenue("Venue").withId(1)));
        service = new ResourceService(mock(SpaceRepository.class), mock(EquipmentRepository.class),
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
