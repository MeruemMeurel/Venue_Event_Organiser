package Venue_Event_Manager.util;

import Venue_Event_Manager.domain.model.user.*;
import Venue_Event_Manager.domain.model.venue.*;
import Venue_Event_Manager.domain.model.resource.*;
import Venue_Event_Manager.domain.model.event.*;
import Venue_Event_Manager.domain.model.booking.*;
import Venue_Event_Manager.domain.model.request.*;
import Venue_Event_Manager.domain.model.feedback.*;

import java.time.LocalDate;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * A tool for creating mock domain models for testing purposes.
 * Eliminates code repetition in unit and integration tests.
 */
public class TestDataFactory {

    /** Utility class; instances are not required. */
    private TestDataFactory() {}

    /**
     * USER PACKET
     */
    /**
     * Creates a standard, active, non-admin, unsaved User with default mock data.
     * @param username username value
     * @return operation result
     */
    public static User createDefaultUser(String username) {
        return new User(
                0, //default ID for unsaved objects
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
     * Creates an admin unsaved User with default mock data.
     * @param username username value
     * @return operation result
     */
    public static User createAdminUser(String username) {
        return createDefaultUser(username).withIsAdmin(true);
    }

    /**
     * Creates a banned unsaved User with default mock data.
     * @param username username value
     * @return operation result
     */
    public static User createBannedUser(String username) {
        return createDefaultUser(username).withAccountStatus(AccountStatus.BANNED);
    }


    /**
     * VENUE PACKET
     */
    /**
     * Creates a standard Address record with full mock data.
     * @return operation result
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
     * Creates a standard unsaved Venue with default mock data.
     * @param name name value
     * @return operation result
     */
    public static Venue createDefaultVenue(String name) {
        return new Venue(
                0, //default ID for unsaved objects
                name,
                "Splendida location per eventi aziendali e privati.",
                createDefaultAddress()
        );
    }


    /**
     * RESOURCE PACKET
     */
    /**
     * Creates a standard unsaved Equipment without a specific Venue and default mock data.
     * @param name name value
     * @return operation result
     */
    public static Equipment createGenericEquipment(String name) {
        return new Equipment(
                0, //default ID for unsaved objects
                null, //generic equipment with no specific venue
                name,
                "Attrezzatura generica per eventi",
                10
        );
    }

    /**
     * Creates a standard unsaved Equipment with a specific Venue and default mock data.
     * @param name name value
     * @param venueId venueId value
     * @return operation result
     */
    public static Equipment createVenueEquipment(String name, long venueId) {
        return new Equipment(
                0, //default ID for unsaved objects
                venueId,
                name,
                "Attrezzatura specifica",
                5
        );
    }

    /**
     * Creates a standard unsaved Service with default mock data.
     * @param name name value
     * @return operation result
     */
    public static Service createDefaultService(String name) {
        return new Service(
                0, //default ID for unsaved objects
                name,
                "Servizio di supporto standard"
        );
    }

    /**
     * Creates a standard unsaved Space bound to a venue with default mock data.
     * @param name name value
     * @param venueId venueId value
     * @return operation result
     */
    public static Space createDefaultSpace(String name, long venueId) {
        return new Space(
                0, //default ID for unsaved objects
                venueId,
                name,
                "Spazio/Sala per eventi"
        );
    }


    /**
     * EVENT PACKET
     */
    /**
     * Creates a standard unsaved Event with default mock data.
     * @param name name value
     * @param venueId venueId value
     * @param creatorId creatorId value
     * @return operation result
     */
    public static Event createDefaultEvent(String name, long venueId, long creatorId) {
        return new Event(
                0, //default ID for unsaved objects
                venueId,
                creatorId,
                null, //organiser_id is optional
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
     * Creates a standard unsaved EventGuest bound to an event with default mock data.
     * @param firstname firstname value
     * @param lastname lastname value
     * @param eventId eventId value
     * @return operation result
     */
    public static EventGuest createDefaultGuest(String firstname, String lastname, long eventId) {
        return new EventGuest(
                0, //default ID for unsaved objects
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
     * Creates a standard unsaved Booking with default mock data.
     * @param userId userId value
     * @param eventId eventId value
     * @return operation result
     */
    public static Booking createDefaultBooking(long userId, long eventId) {
        return new Booking(
                0, //default ID for unsaved objects
                userId,
                eventId,
                LocalDateTime.of(2026, 5, 19, 12, 0),
                BookingStatus.PENDING_PAYMENT,
                new BigDecimal("31.00")
        );
    }

    /**
     * Creates a standard unsaved Ticket bound to a booking with default mock data.
     * @param bookingId bookingId value
     * @param firstname firstname value
     * @param lastname lastname value
     * @return operation result
     */
    public static Ticket createDefaultTicket(long bookingId, String firstname, String lastname) {
        return new Ticket(
                0, //default ID for unsaved objects
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
     * Creates a default unsaved EventRequest with default mock data.
     * @param requesterId requesterId value
     * @param venueId venueId value
     * @param name name value
     * @return operation result
     */
    public static EventRequest createDefaultRequest(long requesterId, long venueId, String name) {
        return new EventRequest(
                0, //default ID for unsaved objects
                requesterId,
                null, //handler_id is null at the start
                venueId,
                name,
                "Richiesta di prenotazione spazio per festa di laurea.",
                LocalDateTime.of(2026, 7, 10, 18, 0),
                LocalDateTime.of(2026, 7, 11, 0, 30),
                EventRequestStatus.PENDING,
                LocalDateTime.of(2026, 5, 19, 20, 0), //created_at standard value for tests
                null,
                new BigDecimal("150.00")
        );
    }


    /**
     * FEEDBACK PACKET
     */
    /**
     * Creates a standard unsaved Report with default mock data.
     * @param userId userId value
     * @param adminId adminId value
     * @param eventId eventId value
     * @return operation result
     */
    public static Report createDefaultReport(long userId, long adminId, Long eventId) {
        return new Report(
                0, //default ID for unsaved objects
                userId,
                adminId,
                eventId,
                ReportSeverity.MIDDLE,
                "Il comportamento di alcuni partecipanti ha violato i termini del servizio.",
                LocalDateTime.of(2026, 5, 20, 10, 0) //created_at standard value for tests
        );
    }

    /**
     * Creates a standard unsaved Review with default mock data.
     * @param userId userId value
     * @param eventId eventId value
     * @return operation result
     */
    public static Review createDefaultReview(long userId, long eventId) {
        return new Review(
                0, //default ID for unsaved objects
                userId,
                eventId,
                5,
                "Evento organizzato benissimo, acustica della sala impeccabile!",
                LocalDateTime.of(2026, 5, 20, 11, 30) // created_at standard value for tests
        );
    }
}
