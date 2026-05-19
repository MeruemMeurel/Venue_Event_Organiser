package Venue_Event_Manager.domain.model.feedback;

import Venue_Event_Manager.util.TestDataFactory;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class ReviewTest {

    @Nested
    @DisplayName("Tests for Constructors and Fallbacks")
    class ConstructorsAndDefaults {

        @Test
        @DisplayName("Default constructor should handle empty initialization and default comment fallback")
        void defaultConstructorShouldSetDefaults() {
            Review review = new Review();

            assertEquals(0, review.getId());
            assertEquals(0, review.getUserId());
            assertEquals(0, review.getRating());
            assertEquals("", review.getComment());
            assertNull(review.getCreatedAt());
        }
    }

    @Nested
    @DisplayName("Tests for Withers and State Transitions")
    class WithersAndState {

        @Test
        @DisplayName("Wither methods must produce a new immutable instance with updated values")
        void withersShouldBeCorrect() {
            Review original = TestDataFactory.createDefaultReview(1L, 2L);
            Review updated = original.withRating(4);

            assertNotSame(original, updated);
            assertEquals(4, updated.getRating());
            assertEquals(original.getComment(), updated.getComment());
        }
    }

    @Nested
    @DisplayName("Tests for Equals and HashCode")
    class EqualsAndHashCodeLogic {

        @Test
        @DisplayName("Reviews with same non-zero ID should be equal")
        void equalWithSameId() {
            Review rev1 = TestDataFactory.createDefaultReview(1L, 2L).withId(1234L);
            Review rev2 = TestDataFactory.createDefaultReview(3L, 4L).withId(1234L);

            assertEquals(rev1, rev2);
            assertEquals(rev1.hashCode(), rev2.hashCode());
        }

        @Test
        @DisplayName("Unsaved reviews (ID=0) match if they share user_id, event_id, and created_at")
        void equalUnsavedReviews() {
            LocalDateTime timestamp = LocalDateTime.now();

            //same identification attributes, but different ratings
            Review rev1 = TestDataFactory.createDefaultReview(5L, 6L).withCreatedAt(timestamp);
            Review rev2 = TestDataFactory.createDefaultReview(5L, 6L).withCreatedAt(timestamp).withRating(1);

            assertEquals(rev1, rev2);
            assertEquals(rev1.hashCode(), rev2.hashCode());
        }
    }
}