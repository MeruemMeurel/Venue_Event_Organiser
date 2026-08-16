package venue.event.manager.domain.model.booking;

import venue.event.manager.util.TestDataFactory;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class BookingTest {

    @Nested
    @DisplayName("Tests for Constructors and Enums Fallbacks")
    class ConstructorsAndDefaults {

        @Test
        @DisplayName("Default constructor should set numerical fields to 0 and dates/objects to null")
        void defaultConstructorShouldSetDefaults() {
            Booking booking = new Booking();

            assertEquals(0, booking.getId());
            assertEquals(0, booking.getUserId());
            assertEquals(0, booking.getEventId());
            assertNull(booking.getCreatedAt());
            assertNull(booking.getTotalPrice());
        }

        @Test
        @DisplayName("Master constructor should fallback null status to PENDING_PAYMENT")
        void masterConstructorShouldFallbackStatus() {
            Booking booking = new Booking(1L, 10L, 20L, LocalDateTime.now(), null, new BigDecimal("10.00"));

            assertEquals(BookingStatus.PENDING_PAYMENT, booking.getStatus(),
                    "Null status must default to PENDING_PAYMENT");
        }
    }

    @Nested
    @DisplayName("Tests for Immutability and Withers")
    class ImmutabilityAndWithers {

        @Test
        @DisplayName("Wither methods should create a new modified instance while keeping others identical")
        void withersShouldPreserveState() {
            Booking original = TestDataFactory.createDefaultBooking(5L, 12L);
            Booking updated = original.withStatus(BookingStatus.CONFIRMED);

            assertNotSame(original, updated, "Wither must return a new instance");
            assertEquals(BookingStatus.CONFIRMED, updated.getStatus());
            assertEquals(original.getUserId(), updated.getUserId());
        }
    }

    @Nested
    @DisplayName("Tests for Equals and HashCode")
    class EqualsAndHashCodeLogic {

        @Test
        @DisplayName("Bookings with same non-zero ID must be equal")
        void equalWithSameId() {
            //different user and event, but same ID
            Booking b1 = TestDataFactory.createDefaultBooking(1L, 2L).withId(999L);
            Booking b2 = TestDataFactory.createDefaultBooking(3L, 4L).withId(999L);

            assertEquals(b1, b2);
            assertEquals(b1.hashCode(), b2.hashCode());
        }

        @Test
        @DisplayName("Unsaved bookings (ID=0) are equal if they match user_id, event_id, and created_at")
        void equalUnsavedBookings() {
            LocalDateTime now = LocalDateTime.now();
            Booking b1 = TestDataFactory.createDefaultBooking(1L, 2L).withCreatedAt(now);
            Booking b2 = TestDataFactory.createDefaultBooking(1L, 2L).withCreatedAt(now).withTotalPrice(new BigDecimal("0.0"));

            assertEquals(b1, b2);
            assertEquals(b1.hashCode(), b2.hashCode());
        }
    }
}
