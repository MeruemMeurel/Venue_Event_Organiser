package Venue_Event_Manager.domain.model.user;

import Venue_Event_Manager.util.TestDataFactory;
import java.time.LocalDate;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class UserTest {

    @Nested
    @DisplayName("Tests for Constructors and Defaults")

    class ConstructorsAndDefaults {

        @Test
        @DisplayName("Default constructor should initialize empty user with sensible defaults")
        void defaultConstructorShouldSetDefaults() {
            User user = new User();

            assertEquals(0, user.getId());
            assertEquals("", user.getUsername());
            assertEquals("", user.getFirstname());
            assertEquals("", user.getLastname());
            assertNull(user.getBirthday());
            assertEquals("", user.getEmail());
            assertEquals("", user.getPhone());
            assertFalse(user.isAdmin());
            assertEquals(AccountStatus.ACTIVE, user.getAccountStatus());
        }

        @Test
        @DisplayName("Master constructor should handle null values for isAdmin and accountStatus gracefully")
        void masterConstructorShouldHandleNulls() {
            User user = new User(1L, "username", "Mario", "Rossi",
                    LocalDate.of(2000, 1, 1), "mario@test.com",
                    "123456", null, null);

            assertFalse(user.isAdmin(), "Null isAdmin should default to false");
            assertEquals(AccountStatus.ACTIVE, user.getAccountStatus(), "Null accountStatus should default to ACTIVE");
        }

        @Test
        @DisplayName("Unsaved user constructor should default ID to 0")
        void unsavedUserConstructorShouldSetIdToZero() {
            User user = new User("username", "Mario", "Rossi",
                    LocalDate.of(2000, 1, 1), "mario@test.com",
                    "123456", false, AccountStatus.ACTIVE);

            assertEquals(0, user.getId());
        }
    }

    @Nested
    @DisplayName("Tests for Immutability and Withers")
    class ImmutabilityAndWithers {

        @Test
        @DisplayName("Wither methods should return a new instance and keep other fields intact")
        void witherShouldReturnNewInstanceWithUpdatedField() {
            User original = TestDataFactory.createDefaultUser("mario88");

            User updated = original.withId(99L);

            assertNotSame(original, updated, "Wither must return a new object instance (immutability)");
            assertEquals(99L, updated.getId(), "The target field must be updated");
            assertEquals(original.getUsername(), updated.getUsername(), "Other fields must remain identical");

            User updatedEmail = original.withEmail("new@test.com");
            assertEquals("new@test.com", updatedEmail.getEmail());
            assertEquals(original.getId(), updatedEmail.getId());
        }
    }

    @Nested
    @DisplayName("Tests for Equals and HashCode")
    class EqualsAndHashCodeLogic {

        @Test
        @DisplayName("Users with same non-zero ID should be equal regardless of other fields")
        void equalWithSameId() {
            //different username, same ID
            User user1 = TestDataFactory.createDefaultUser("user1").withId(10L);
            User user2 = TestDataFactory.createDefaultUser("user2").withId(10L);

            assertEquals(user1, user2);
            assertEquals(user1.hashCode(), user2.hashCode());
        }

        @Test
        @DisplayName("Unsaved users (ID=0) should be equal if they share username or email")
        void equalUnsavedUsers() {
            User user1 = TestDataFactory.createDefaultUser("comune");
            User user2 = TestDataFactory.createDefaultUser("comune");

            assertEquals(user1, user2);
            assertEquals(user1.hashCode(), user2.hashCode());
        }

        @Test
        @DisplayName("Users with different non-zero IDs should NOT be equal even with same username/email")
        void notEqualWithDifferentIds() {
            //same attributes, but different ID
            User user1 = TestDataFactory.createDefaultUser("uguale").withId(1L);
            User user2 = TestDataFactory.createDefaultUser("uguale").withId(2L);

            assertNotEquals(user1, user2, "Different database IDs means different users");
        }
    }

}