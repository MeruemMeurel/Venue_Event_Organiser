package Venue_Event_Manager.util;

import Venue_Event_Manager.domain.model.user.User;
import Venue_Event_Manager.domain.model.user.AccountStatus;

import Venue_Event_Manager.domain.model.venue.Address;
import Venue_Event_Manager.domain.model.venue.Venue;

import Venue_Event_Manager.domain.model.resource.Equipment;
import Venue_Event_Manager.domain.model.resource.Service;
import Venue_Event_Manager.domain.model.resource.Space;

import java.time.LocalDate;

/**
 * A tool for creating mock domain models for testing purposes.
 * Eliminates code repetition in unit and integration tests.
 */
public class TestDataFactory {

    /**
     * USER PACKET
     */
    /**
     * Creates a standard, active, non-admin User with default mock data.
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
     * Creates an admin User with default mock data.
     */
    public static User createAdminUser(String username) {
        return createDefaultUser(username).withIsAdmin(true);
    }

    /**
     * Creates a banned User with default mock data.
     */
    public static User createBannedUser(String username) {
        return createDefaultUser(username).withAccountStatus(AccountStatus.BANNED);
    }


    /**
     * VENUE PACKET
     */
    /**
     * Creates a standard Address record with full mock data.
     */
    public static Address createDefaultAddress() {
        return new Address(
                "Via Roma",
                "10",
                "Prato",
                "59100",
                "Italia",
                "Interno 2"
        );
    }

    /**
     * Creates a standard Venue with default mock data.
     */
    public static Venue createDefaultVenue(String name) {
        return new Venue(
                0, // default ID for unsaved objects
                name,
                "Splendida location per eventi aziendali e privati.",
                createDefaultAddress()
        );
    }


    /**
     * RESOURCE PACKET
     */
    /**
     * Creates a standard Equipment instance without a specific Venue and default mock data.
     */
    public static Equipment createGenericEquipment(String name) {
        return new Equipment(
                0, // default ID for unsaved objects
                null, // generic equipment with no specific venue
                name,
                "Attrezzatura generica per eventi",
                10
        );
    }

    /**
     * Creates a standard Equipment instance with a specific Venue and default mock data.
     */
    public static Equipment createVenueEquipment(String name, long venueId) {
        return new Equipment(
                0, // default ID for unsaved objects
                venueId,
                name,
                "Attrezzatura specifica",
                5
        );
    }

    /**
     * Creates a standard Service instance with default mock data.
     */
    public static Service createDefaultService(String name) {
        return new Service(
                0, // default ID for unsaved objects
                name,
                "Servizio di supporto standard"
        );
    }

    /**
     * Creates a standard Space instance bound to a venue with default mock data.
     */
    public static Space createDefaultSpace(String name, long venueId) {
        return new Space(
                0, // default ID for unsaved objects
                venueId,
                name,
                "Spazio/Sala per eventi"
        );
    }
}