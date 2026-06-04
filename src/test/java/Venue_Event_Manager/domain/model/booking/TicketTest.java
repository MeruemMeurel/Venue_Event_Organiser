package Venue_Event_Manager.domain.model.booking;

import Venue_Event_Manager.util.TestDataFactory;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class TicketTest {

    @Nested
    @DisplayName("Tests for Constructors and Defaults")
    class ConstructorsAndDefaults {

        @Test
        @DisplayName("Default constructor should set empty strings and null date")
        void defaultConstructorShouldSetDefaults() {
            Ticket ticket = new Ticket();

            assertEquals(0, ticket.getId());
            assertEquals(0, ticket.getBookingId());
            assertEquals("", ticket.getFirstname());
            assertEquals("", ticket.getLastname());
            assertNull(ticket.getStartsAt());
        }
    }

    @Nested
    @DisplayName("Tests for Withers and State Modification")
    class WithersAndState {

        @Test
        @DisplayName("withBirthday (wither for starts_at) should update correctly and preserve instance immutability")
        void witherStartsAtShouldWork() {
            Ticket original = TestDataFactory.createDefaultTicket(1L, "Marco", "C");
            LocalDateTime newStart = LocalDateTime.of(2026, 7, 7, 18, 0);

            Ticket updated = original.withStartsAt(newStart);

            assertNotSame(original, updated);
            assertEquals(newStart, updated.getStartsAt());
            assertEquals(original.getFirstname(), updated.getFirstname());
        }
    }

    @Nested
    @DisplayName("Tests for Equals and HashCode")
    class EqualsAndHashCodeLogic {

        @Test
        @DisplayName("Tickets with same non-zero ID must match")
        void equalWithSameId() {
            Ticket t1 = TestDataFactory.createDefaultTicket(1L, "A", "B").withId(44L);
            Ticket t2 = TestDataFactory.createDefaultTicket(2L, "C", "D").withId(44L);

            assertEquals(t1, t2);
            assertEquals(t1.hashCode(), t2.hashCode());
        }

        @Test
        @DisplayName("Unsaved tickets (ID=0) are equal if they share booking_id, firstname, and lastname")
        void equalUnsavedTickets() {
            Ticket t1 = TestDataFactory.createDefaultTicket(10L, "Giovanni", "Rossi");
            Ticket t2 = TestDataFactory.createDefaultTicket(10L, "Giovanni", "Rossi").withStartsAt(LocalDateTime.now());

            assertEquals(t1, t2);
            assertEquals(t1.hashCode(), t2.hashCode());
        }
    }
}