package venue.event.manager.util;

import venue.event.manager.domain.model.user.*;
import venue.event.manager.domain.model.venue.*;
import venue.event.manager.domain.model.resource.*;
import venue.event.manager.domain.model.event.*;
import venue.event.manager.domain.model.booking.*;
import venue.event.manager.domain.model.request.*;
import venue.event.manager.domain.model.feedback.*;

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
     * @param username value to use
     * @return resulting value
     */
    public static User createAdminUser(String username) {
        return createDefaultUser(username).withIsAdmin(true);
    }

    /**
     * Creates a banned unsaved User with default mock data.
     * @param username value to use
     * @return resulting value
     */
    public static User createBannedUser(String username) {
        return createDefaultUser(username).withAccountStatus(AccountStatus.BANNED);
    }


    /**
     * VENUE PACKET
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
     * @param name value to use
     * @return resulting value
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
     * @param name value to use
     * @param venueId value to use
     * @return resulting value
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
     * @param name value to use
     * @return resulting value
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
     * @param name value to use
     * @param venueId value to use
     * @return resulting value
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
     * @param firstname value to use
     * @param lastname value to use
     * @param eventId value to use
     * @return resulting value
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
     * @param bookingId value to use
     * @param firstname value to use
     * @param lastname value to use
     * @return resulting value
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
     * @param userId value to use
     * @param eventId value to use
     * @return resulting value
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
