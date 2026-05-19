package Venue_Event_Manager.util;

import Venue_Event_Manager.domain.model.user.User;
import Venue_Event_Manager.domain.model.user.AccountStatus;

import java.time.LocalDate;

/**
 * A tool for creating mock domain models for testing purposes.
 * Eliminates code repetition in unit and integration tests.
 */
public class TestDataFactory {

    /**
     * USER
     */
    /**
     * Creates a standard, active, non-admin user with default mock data.
     */
    public static User createDefaultUser(String username) {
        return new User(
                0, // default ID for unsaved objects
                username,
                "Mario",
                "Rossi",
                LocalDate.of(2000, 1, 1),
                username + "@example.com",
                "+391234567890",
                false,
                AccountStatus.ACTIVE
        );
    }

    /**
     * Creates an admin user.
     */
    public static User createAdminUser(String username) {
        return createDefaultUser(username).withIsAdmin(true);
    }

    /**
     * Creates a banned user.
     */
    public static User createBannedUser(String username) {
        return createDefaultUser(username).withAccountStatus(AccountStatus.BANNED);
    }

}