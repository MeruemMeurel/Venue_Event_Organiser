package venue.event.manager.domain.model.resource;

import venue.event.manager.util.TestDataFactory;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class EquipmentTest {

    @Nested
    @DisplayName("Tests for Constructors, Defaults and Validations")
    class ConstructorsAndValidations {

        @Test
        @DisplayName("Default constructor should set default values and null venue")
        void defaultConstructorShouldSetDefaults() {
            Equipment eq = new Equipment();
            assertEquals(0, eq.getId());
            assertNull(eq.getVenueId());
            assertEquals("", eq.getName());
            assertEquals("", eq.getDescription());
            assertEquals(0, eq.getTotalQuantity());
        }

        @Test
        @DisplayName("Master constructor should throw NullPointerException if name is null")
        void masterConstructorShouldValidationName() {
            assertThrows(NullPointerException.class, () -> {
                new Equipment(1L, 2L, null, "Desc", 10);
            }, "Should throw NPE when resource name is null due to Objects.requireNonNull");
        }
    }

    @Nested
    @DisplayName("Tests for Withers and Immutability")
    class WithersAndImmutability {

        @Test
        @DisplayName("Wither methods should return a new instance and update the correct field")
        void withersShouldWorkCorrectly() {
            Equipment original = TestDataFactory.createGenericEquipment("Proiettore 4K");
            Equipment updated = original.withTotalQuantity(25);

            assertNotSame(original, updated);
            assertEquals(25, updated.getTotalQuantity());
            assertEquals(original.getName(), updated.getName());
        }
    }

    @Nested
    @DisplayName("Tests for Equals and HashCode")
    class EqualsAndHashCodeLogic {

        @Test
        @DisplayName("Equipments with same non-zero ID should be equal even if details differ")
        void equalWithSameId() {
            Equipment eq1 = TestDataFactory.createGenericEquipment("EQ A").withId(50L);
            Equipment eq2 = TestDataFactory.createGenericEquipment("EQ B").withId(50L);

            assertEquals(eq1, eq2);
            assertEquals(eq1.hashCode(), eq2.hashCode());
        }

        @Test
        @DisplayName("Unsaved equipments (ID=0) should be equal if they share same venue_id and name")
        void equalUnsavedEquipments() {
            Equipment eq1 = TestDataFactory.createVenueEquipment("Mixer", 10L);
            Equipment eq2 = TestDataFactory.createVenueEquipment("Mixer", 10L);

            assertEquals(eq1, eq2);
            assertEquals(eq1.hashCode(), eq2.hashCode());
        }
    }
}
