package Venue_Event_Manager.domain.model.user;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.time.LocalDate;

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

}