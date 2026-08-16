package venue.event.manager.domain.model.venue;

import venue.event.manager.util.TestDataFactory;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class VenueTest {

    @Nested
    @DisplayName("Tests for Constructors and Defaults")
    class ConstructorsAndDefaults {

        @Test
        @DisplayName("Default constructor should initialize empty venue with defaults")
        void defaultConstructorShouldSetDefaults() {
            Venue venue = new Venue();

            assertEquals(0, venue.getId());
            assertEquals("", venue.getName());
            assertEquals("", venue.getDescription());
            assertNull(venue.getAddress());
        }

        @Test
        @DisplayName("Master constructor should map all parameters correctly")
        void masterConstructorShouldMapAllFields() {
            Address address = TestDataFactory.createDefaultAddress();
            Venue venue = new Venue(5L, "Palazzo Reale", "Descrizione", address);

            assertEquals(5L, venue.getId());
            assertEquals("Palazzo Reale", venue.getName());
            assertEquals("Descrizione", venue.getDescription());
            assertEquals(address, venue.getAddress());
        }

        @Test
        @DisplayName("Unsaved venue constructor should default ID to 0")
        void unsavedVenueConstructorShouldSetIdToZero() {
            Address address = TestDataFactory.createDefaultAddress();
            Venue venue = new Venue("Alcatraz", "Discoteca", address);

            assertEquals(0, venue.getId());
        }
    }

    @Nested
    @DisplayName("Tests for Immutability and Withers")
    class ImmutabilityAndWithers {

        @Test
        @DisplayName("Wither methods should return a new instance and preserve other fields")
        void witherShouldReturnNewInstanceWithUpdatedField() {
            Venue original = TestDataFactory.createDefaultVenue("Spazio Cobianchi");

            Venue updated = original.withId(42L);

            assertNotSame(original, updated, "Wither must return a new instance");
            assertEquals(42L, updated.getId());
            assertEquals(original.getName(), updated.getName());

            Venue updatedName = original.withName("Nuovo Nome");
            assertEquals("Nuovo Nome", updatedName.getName());
            assertEquals(original.getId(), updatedName.getId());
        }
    }

    @Nested
    @DisplayName("Tests for Equals and HashCode")
    class EqualsAndHashCodeLogic {

        @Test
        @DisplayName("Venues with same non-zero ID should be equal regardless of address details")
        void equalWithSameId() {
            Venue venue1 = TestDataFactory.createDefaultVenue("Venue A").withId(100L);
            //create a second object with the same ID but a slightly modified address using wither methods
            Address differentAddress = TestDataFactory.createDefaultAddress().withStreet("Via Diverse");
            Venue venue2 = TestDataFactory.createDefaultVenue("Venue B").withId(100L).withAddress(differentAddress);

            assertEquals(venue1, venue2, "Should be equal by ID");
            assertEquals(venue1.hashCode(), venue2.hashCode(), "HashCode must match when equals is true");
        }

        @Test
        @DisplayName("Unsaved venues (ID=0) should be equal if they share the exact same address")
        void equalUnsavedVenuesByAddress() {
            Venue venue1 = TestDataFactory.createDefaultVenue("Location Uno");
            Venue venue2 = TestDataFactory.createDefaultVenue("Location Due");
            //both have ID=0 and the exact same factory base address

            assertEquals(venue1, venue2, "Unsaved venues at the same physical address should be logically equal");
            assertEquals(venue1.hashCode(), venue2.hashCode());
        }

        @Test
        @DisplayName("Venues with different non-zero IDs should NOT be equal")
        void notEqualWithDifferentIds() {
            Venue venue1 = TestDataFactory.createDefaultVenue("Identica").withId(1L);
            Venue venue2 = TestDataFactory.createDefaultVenue("Identica").withId(2L);

            assertNotEquals(venue1, venue2);
        }
    }
}
