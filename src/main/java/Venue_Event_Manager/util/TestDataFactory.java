package Venue_Event_Manager.util;

import Venue_Event_Manager.domain.model.user.*;
import Venue_Event_Manager.domain.model.venue.*;
import Venue_Event_Manager.domain.model.resource.*;
import Venue_Event_Manager.domain.model.event.*;
import Venue_Event_Manager.domain.model.booking.*;
import Venue_Event_Manager.domain.model.request.*;

import java.time.LocalDate;
import java.math.BigDecimal;
import java.time.LocalDateTime;

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
     * Creates a standard Equipment without a specific Venue and default mock data.
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
     * Creates a standard Equipment with a specific Venue and default mock data.
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
     * Creates a standard Service with default mock data.
     */
    public static Service createDefaultService(String name) {
        return new Service(
                0, // default ID for unsaved objects
                name,
                "Servizio di supporto standard"
        );
    }

    /**
     * Creates a standard Space bound to a venue with default mock data.
     */
    public static Space createDefaultSpace(String name, long venueId) {
        return new Space(
                0, // default ID for unsaved objects
                venueId,
                name,
                "Spazio/Sala per eventi"
        );
    }


    /**
     * EVENT PACKET
     */
    /**
     * Creates a standard Event with default mock data.
     */
    public static Event createDefaultEvent(String name, long venueId, long creatorId) {
        return new Event(
                0, // default ID for unsaved objects
                venueId,
                creatorId,
                null, // organiser_id is optional
                name,
                "Concerto live o conferenza aziendale di test.",
                LocalDateTime.of(2026, 6, 1, 20, 0),
                LocalDateTime.of(2026, 6, 1, 23, 30),
                "covers/test_poster.png",
                500,
                EventStatus.CONFIRMED,
                EventVisibility.PUBLIC,
                new BigDecimal("15.50"),
                null
        );
    }

    /**
     * Creates a standard EventGuest bound to an event with default mock data.
     */
    public static EventGuest createDefaultGuest(String firstname, String lastname, long eventId) {
        return new EventGuest(
                0, // default ID for unsaved objects
                eventId,
                firstname,
                lastname,
                java.time.LocalDate.of(2000, 1, 1),
                EventGuestStatus.INVITED,
                "Nota di prova"
        );
    }


    /**
     * BOOKING PACKET
     */
    /**
     * Creates a standard Booking with default mock data.
     */
    public static Booking createDefaultBooking(long userId, long eventId) {
        return new Booking(
                0,
                userId,
                eventId,
                LocalDateTime.of(2026, 5, 19, 12, 0),
                BookingStatus.PENDING_PAYMENT,
                new BigDecimal("31.00")
        );
    }

    /**
     * Creates a standard Ticket bound to a booking with default mock data.
     */
    public static Ticket createDefaultTicket(long bookingId, String firstname, String lastname) {
        return new Ticket(
                0,
                bookingId,
                firstname,
                lastname,
                LocalDateTime.of(2026, 6, 1, 20, 0)
        );
    }


    /**
     * REQUEST PACKET
     */
    /**
     * Creates a default EventRequest with default mock data.
     */
    public static EventRequest createDefaultRequest(long requesterId, long venueId, String name) {
        return new EventRequest(
                0,
                requesterId,
                null, //handler_id is null at the start
                venueId,
                name,
                "Richiesta di prenotazione spazio per festa di laurea.",
                LocalDateTime.of(2026, 7, 10, 18, 0),
                LocalDateTime.of(2026, 7, 11, 0, 30),
                EventRequestStatus.PENDING,
                LocalDateTime.of(2026, 5, 19, 20, 0), //created_at standard for tests
                null,
                new BigDecimal("150.00")
        );
    }
}