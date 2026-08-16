package venue.event.manager.domain.model.feedback;

import venue.event.manager.util.TestDataFactory;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class ReportTest {

    @Nested
    @DisplayName("Tests for Constructors and Fallbacks")
    class ConstructorsAndDefaults {

        @Test
        @DisplayName("Default constructor should fall back to default comment and MIDDLE severity")
        void defaultConstructorShouldSetDefaults() {
            Report report = new Report();

            assertEquals(0, report.getId());
            assertEquals(0, report.getUserId());
            assertEquals("", report.getComment(), "Null comment should fallback to empty string");
            assertEquals(ReportSeverity.MIDDLE, report.getSeverity(), "Null severity should fallback to MIDDLE");
            assertNull(report.getCreatedAt());
        }

        @Test
        @DisplayName("Master constructor should gracefully handle null comment and severity")
        void masterConstructorShouldHandleNulls() {
            Report report = new Report(1L, 10L, 20L, null, null, null, null);

            assertEquals("", report.getComment());
            assertEquals(ReportSeverity.MIDDLE, report.getSeverity());
        }
    }

    @Nested
    @DisplayName("Tests for Immutability and Withers")
    class ImmutabilityAndWithers {

        @Test
        @DisplayName("Wither methods should return a new instance and modify exclusively the targeted field")
        void withersShouldWork() {
            Report original = TestDataFactory.createDefaultReport(2L, 3L, 4L);
            Report updated = original.withSeverity(ReportSeverity.HIGH);

            assertNotSame(original, updated);
            assertEquals(ReportSeverity.HIGH, updated.getSeverity());
            assertEquals(original.getComment(), updated.getComment());
        }
    }

    @Nested
    @DisplayName("Tests for Equals and HashCode")
    class EqualsAndHashCodeLogic {

        @Test
        @DisplayName("Reports with same non-zero ID should match")
        void equalWithSameId() {
            Report r1 = TestDataFactory.createDefaultReport(1L, 2L, 3L).withId(88L);
            Report r2 = TestDataFactory.createDefaultReport(5L, 6L, 7L).withId(88L);

            assertEquals(r1, r2);
            assertEquals(r1.hashCode(), r2.hashCode());
        }

        @Test
        @DisplayName("Unsaved reports (ID=0) match if they share user_id, admin_id, and created_at")
        void equalUnsavedReports() {
            LocalDateTime timestamp = LocalDateTime.now();
            Report r1 = TestDataFactory.createDefaultReport(10L, 20L, 30L).withCreatedAt(timestamp);
            Report r2 = TestDataFactory.createDefaultReport(10L, 20L, 30L).withCreatedAt(timestamp).withComment("Diverso");

            assertEquals(r1, r2);
            assertEquals(r1.hashCode(), r2.hashCode());
        }
    }
}
