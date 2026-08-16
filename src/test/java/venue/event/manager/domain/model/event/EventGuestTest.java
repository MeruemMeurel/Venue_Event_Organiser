package venue.event.manager.domain.model.event;

import venue.event.manager.util.TestDataFactory;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class EventGuestTest {

    @Nested
    @DisplayName("Tests for Constructors and Fallbacks")
    class ConstructorsAndFallbacks {

        @Test
        @DisplayName("Master constructor should handle null status defaulting to INVITED")
        void masterConstructorShouldFallbackToInvited() {
            EventGuest guest = new EventGuest(1L, 100L, "Luca", "Verdi",
                    LocalDate.of(1995, 5, 5), null, "VIP");

            assertEquals(EventGuestStatus.INVITED, guest.getStatus(), "Null status must default to INVITED");
        }
    }

    @Nested
    @DisplayName("Tests for Equals and HashCode")
    class EqualsAndHashCodeLogic {

        @Test
        @DisplayName("Guests with same non-zero ID should be equal")
        void equalWithSameId() {
            EventGuest g1 = TestDataFactory.createDefaultGuest("Mario", "Rossi", 1L).withId(5L);
            EventGuest g2 = TestDataFactory.createDefaultGuest("Luigi", "Bianchi", 2L).withId(5L);

            assertEquals(g1, g2);
            assertEquals(g1.hashCode(), g2.hashCode());
        }

        @Test
        @DisplayName("Unsaved guests (ID=0) match if they share firstname, lastname, and event_id")
        void equalUnsavedGuests() {
            EventGuest g1 = TestDataFactory.createDefaultGuest("Anna", "Neri", 50L);
            EventGuest g2 = TestDataFactory.createDefaultGuest("Anna", "Neri", 50L).withNote("Nota differente");

            assertEquals(g1, g2);
            assertEquals(g1.hashCode(), g2.hashCode());
        }
    }
}
