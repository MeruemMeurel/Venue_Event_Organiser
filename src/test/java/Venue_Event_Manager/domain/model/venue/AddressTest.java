package Venue_Event_Manager.domain.model.venue;

import Venue_Event_Manager.util.TestDataFactory;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static org.junit.jupiter.api.Assertions.*;

class AddressTest {

    @Nested
    @DisplayName("Tests for Constructors and Defaults")
    class ConstructorsAndDefaults {

        @Test
        @DisplayName("Default constructor should initialize all fields to null")
        void defaultConstructorShouldSetNulls() {
            Address address = new Address();

            assertNull(address.street());
            assertNull(address.street_number());
            assertNull(address.city());
            assertNull(address.postal_code());
            assertNull(address.country());
            assertNull(address.additional_info());
        }

        @Test
        @DisplayName("Overloaded constructor should set additional_info to null by default")
        void overloadedConstructorShouldDefaultAdditionalInfoToNull() {
            Address address = new Address("Via Astori", "5", "Firenze", "50134", "Italia");

            assertEquals("Via Astori", address.street());
            assertEquals("5", address.street_number());
            assertNull(address.additional_info());
        }
    }

    @Nested
    @DisplayName("Tests for Withers and Immutability")
    class WithersAndImmutability {

        @Test
        @DisplayName("Wither methods should return a new record instance with updated values")
        void withersShouldWorkCorrectly() {
            Address original = TestDataFactory.createDefaultAddress();

            Address updatedCity = original.withCity("Roma");

            assertNotSame(original, updatedCity, "Withers on records must return a new instance");
            assertEquals("Roma", updatedCity.city());
            assertEquals(original.street(), updatedCity.street(), "Other fields must remain untouched");

            Address updatedInfo = original.withAdditionalInfo("Piano 3");
            assertEquals("Piano 3", updatedInfo.additional_info());
        }
    }
}