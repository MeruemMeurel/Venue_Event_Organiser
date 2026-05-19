package Venue_Event_Manager.domain.model.event;

import Venue_Event_Manager.util.TestDataFactory;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class EventTest {

    @Nested
    @DisplayName("Tests for Constructors and Enums Defaults")
    class ConstructorsAndDefaults {

        @Test
        @DisplayName("Default constructor should initialize empty event with empty state and nulls")
        void defaultConstructorShouldSetDefaults() {
            Event event = new Event();

            assertEquals(0, event.getId());
            assertEquals(0, event.getVenueId());
            assertEquals("", event.getName());
            assertNull(event.getBeginDatetime());
            assertEquals(EventStatus.CONFIRMED, event.getStatus());
        }

        @Test
        @DisplayName("Master constructor should gracefully default null status and visibility")
        void masterConstructorShouldFallbackEnums() {
            Event event = new Event(1L, 10L, 5L, null, "Tech Summit", "Desc",
                    LocalDateTime.now(), LocalDateTime.now().plusHours(2),
                    null, 100, null, null, null, null);

            assertEquals(EventStatus.CONFIRMED, event.getStatus(), "Null status must default to CONFIRMED");
            assertEquals(EventVisibility.PUBLIC, event.getVisibility(), "Null visibility must default to PUBLIC");
        }
    }

    @Nested
    @DisplayName("Tests for Immutability and Withers")
    class ImmutabilityAndWithers {

        @Test
        @DisplayName("Wither methods should maintain immutability and isolate fields")
        void withersShouldWork() {
            Event original = TestDataFactory.createDefaultEvent("Rock Fest", 1L, 2L);
            Event updated = original.withCapacity(1200);

            assertNotSame(original, updated);
            assertEquals(1200, updated.getCapacity());
            assertEquals(original.getName(), updated.getName());

            Event updatedPrice = original.withTicketPrice(new BigDecimal("29.99"));
            assertEquals(new BigDecimal("29.99"), updatedPrice.getTicketPrice());
        }
    }

    @Nested
    @DisplayName("Tests for Equals and HashCode")
    class EqualsAndHashCodeLogic {

        @Test
        @DisplayName("Events with same non-zero ID match regardless of business key updates")
        void equalWithSameId() {
            Event event1 = TestDataFactory.createDefaultEvent("Show A", 1L, 2L).withId(77L);
            Event event2 = TestDataFactory.createDefaultEvent("Show B", 5L, 6L).withId(77L);

            assertEquals(event1, event2);
            assertEquals(event1.hashCode(), event2.hashCode());
        }

        @Test
        @DisplayName("Unsaved events (ID=0) are equal if they match name, venue_id, and begin_datetime")
        void equalUnsavedEvents() {
            //same attributes but different creatorID
            Event event1 = TestDataFactory.createDefaultEvent("Party", 10L, 2L);
            Event event2 = TestDataFactory.createDefaultEvent("Party", 10L, 5L);

            assertEquals(event1, event2);
            assertEquals(event1.hashCode(), event2.hashCode());
        }
    }
}