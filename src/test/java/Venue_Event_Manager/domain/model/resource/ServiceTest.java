package Venue_Event_Manager.domain.model.resource;

import Venue_Event_Manager.util.TestDataFactory;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ServiceTest {

    @Nested
    @DisplayName("Tests for Constructors and Constraints")
    class ConstructorsAndConstraints {

        @Test
        @DisplayName("Default constructor should set venue_id to null")
        void defaultConstructorShouldSetVenueToNull() {
            Service service = new Service();
            assertNull(service.getVenueId());
            assertEquals("", service.getName());
        }

        @Test
        @DisplayName("Overloaded and Master constructor must always force venue_id to null")
        void serviceVenueIdMustAlwaysBeNull() {
            Service service1 = new Service("Catering", "Servizio cibo");
            Service service2 = new Service(1L, "Cleaning", "Pulizie");

            assertNull(service1.getVenueId(), "Service venue_id must be null for unsaved");
            assertNull(service2.getVenueId(), "Service venue_id must be null for master constructor");
        }
    }

    @Nested
    @DisplayName("Tests for Equals and HashCode")
    class EqualsAndHashCodeLogic {

        @Test
        @DisplayName("Unsaved services (ID=0) should be equal if they share the same name")
        void equalUnsavedServicesByName() {
            Service s1 = TestDataFactory.createDefaultService("Security");
            Service s2 = TestDataFactory.createDefaultService("Security").withDescription("Diverso commento");

            assertEquals(s1, s2, "Unsaved services should match by business key (name)");
            assertEquals(s1.hashCode(), s2.hashCode());
        }
    }
}