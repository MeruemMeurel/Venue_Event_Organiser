package venue.event.manager.domain.model.resource;

import venue.event.manager.util.TestDataFactory;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SpaceTest {

    @Nested
    @DisplayName("Tests for Constructors and Hierarchy Mapping")
    class ConstructorsAndMapping {

        @Test
        @DisplayName("Master constructor should accurately map superclass fields")
        void masterConstructorShouldMapHierarchy() {
            Space space = new Space(15L, 200L, "Sala Conferenze", "Grande sala multimediale");

            assertEquals(15L, space.getId());
            assertEquals(200L, space.getVenueId());
            assertEquals("Sala Conferenze", space.getName());
            assertEquals("Grande sala multimediale", space.getDescription());
        }
    }

    @Nested
    @DisplayName("Tests for Equals and HashCode")
    class EqualsAndHashCodeLogic {

        @Test
        @DisplayName("Spaces with same non-zero ID should match")
        void equalWithSameId() {
            Space space1 = TestDataFactory.createDefaultSpace("Sala Red", 1L).withId(9L);
            Space space2 = TestDataFactory.createDefaultSpace("Sala Blue", 2L).withId(9L);

            assertEquals(space1, space2);
            assertEquals(space1.hashCode(), space2.hashCode());
        }

        @Test
        @DisplayName("Unsaved spaces (ID=0) are equal if they belong to the same venue and have the same name")
        void equalUnsavedSpaces() {
            Space space1 = TestDataFactory.createDefaultSpace("Main Stage", 55L);
            Space space2 = TestDataFactory.createDefaultSpace("Main Stage", 55L);

            assertEquals(space1, space2);
            assertEquals(space1.hashCode(), space2.hashCode());
        }
    }
}
