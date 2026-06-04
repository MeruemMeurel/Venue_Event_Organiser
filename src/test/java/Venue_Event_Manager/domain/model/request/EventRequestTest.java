package Venue_Event_Manager.domain.model.request;

import Venue_Event_Manager.util.TestDataFactory;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class EventRequestTest {

    @Nested
    @DisplayName("Tests for Constructors and Enums Fallbacks")
    class ConstructorsAndDefaults {

        @Test
        @DisplayName("Default constructor should set default IDs to 0 and state fields to null")
        void defaultConstructorShouldSetDefaults() {
            EventRequest request = new EventRequest();

            assertEquals(0, request.getId());
            assertEquals(0, request.getRequesterId());
            assertEquals(0, request.getVenueId());
            assertNull(request.getBeginDatetime());
            assertNull(request.getQuote());
        }

        @Test
        @DisplayName("Master constructor should fallback null status to PENDING")
        void masterConstructorShouldFallbackStatus() {
            EventRequest request = new EventRequest(1L, 2L, null, 3L, "Convegno", "Desc",
                    LocalDateTime.now(), LocalDateTime.now().plusHours(4), null,
                    LocalDateTime.now(), null, null);

            //verify that the ternary operator on the null status is working correctly.
            assertEquals(EventRequestStatus.PENDING, request.getStatus(),
                    "Null status must default to PENDING");
        }
    }

    @Nested
    @DisplayName("Tests for Immutability and Withers")
    class ImmutabilityAndWithers {

        @Test
        @DisplayName("Wither methods should return a new updated instance and leave original untouched")
        void withersShouldWorkCorrectly() {
            EventRequest original = TestDataFactory.createDefaultRequest(10L, 5L, "Festa");
            EventRequest updated = original.withStatus(EventRequestStatus.ACCEPTED).withHandlerId(1L);

            assertNotSame(original, updated);
            assertEquals(EventRequestStatus.ACCEPTED, updated.getStatus());
            assertEquals(1L, updated.getHandlerId());
            assertEquals(original.getName(), updated.getName(), "Unchanged fields must remain the same");
        }
    }

    @Nested
    @DisplayName("Tests for Equals and HashCode")
    class EqualsAndHashCodeLogic {

        @Test
        @DisplayName("EventRequests with same non-zero ID should be equal regardless of other fields")
        void equalWithSameId() {
            EventRequest req1 = TestDataFactory.createDefaultRequest(10L, 5L, "Festa A").withId(555L);
            EventRequest req2 = TestDataFactory.createDefaultRequest(11L, 6L, "Festa B").withId(555L);

            assertEquals(req1, req2);
            assertEquals(req1.hashCode(), req2.hashCode());
        }

        @Test
        @DisplayName("Unsaved requests (ID=0) are equal if they share business keys")
        void equalUnsavedRequests() {
            LocalDateTime now = LocalDateTime.of(2026, 5, 19, 20, 0);
            LocalDateTime begin = LocalDateTime.of(2026, 7, 10, 18, 0);

            EventRequest req1 = TestDataFactory.createDefaultRequest(2L, 3L, "Business Key Event")
                    .withCreatedAt(now).withBeginDateTime(begin);

            //same identification attributes, but different quotes and description
            EventRequest req2 = TestDataFactory.createDefaultRequest(2L, 3L, "Business Key Event")
                    .withCreatedAt(now).withBeginDateTime(begin)
                    .withQuote(new BigDecimal("999.00")).withDescription("Altra descrizione");

            assertEquals(req1, req2, "Unsaved requests must match by requester, venue, name, begin date, and creation date");
            assertEquals(req1.hashCode(), req2.hashCode());
        }
    }
}