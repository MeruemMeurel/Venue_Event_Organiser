package Venue_Event_Manager.repository.jdbc;

import Venue_Event_Manager.domain.model.booking.Booking;
import Venue_Event_Manager.domain.model.booking.BookingStatus;
import Venue_Event_Manager.domain.model.booking.Ticket;
import Venue_Event_Manager.domain.model.event.Event;
import Venue_Event_Manager.domain.model.event.EventStatus;
import Venue_Event_Manager.domain.model.event.EventVisibility;
import Venue_Event_Manager.domain.model.feedback.Review;
import Venue_Event_Manager.domain.model.resource.Equipment;
import Venue_Event_Manager.domain.model.resource.Service;
import Venue_Event_Manager.domain.model.resource.Space;
import Venue_Event_Manager.domain.model.user.AccountStatus;
import Venue_Event_Manager.domain.model.user.User;
import Venue_Event_Manager.domain.model.venue.Address;
import Venue_Event_Manager.domain.model.venue.Venue;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

class PostgresRepositoryCrudIntegrationTest {

    private static final String URL = "jdbc:postgresql://localhost:5433/event_manager_db";
    private static final String USER = "admin";
    private static final String PASSWORD = "change_me";

    @BeforeAll
    static void requireDatabase() {
        try (Connection ignored = DriverManager.getConnection(URL, USER, PASSWORD)) {
            // Database available.
        } catch (SQLException exception) {
            assumeTrue(false, "Local PostgreSQL is unavailable: " + exception.getMessage());
        }
    }

    @Test
    void repositoriesShouldSupportCoreCrudAndQueries() throws SQLException {
        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD)) {
            connection.setAutoCommit(false);
            try {
                exerciseRepositories(connection);
            } finally {
                connection.rollback();
            }
        }
    }

    private void exerciseRepositories(Connection connection) {
        PgUserRepository users = new PgUserRepository();
        PgVenueRepository venues = new PgVenueRepository();
        PgEventRepository events = new PgEventRepository();
        PgBookingRepository bookings = new PgBookingRepository();
        PgSpaceRepository spaces = new PgSpaceRepository();
        PgEquipmentRepository equipment = new PgEquipmentRepository();
        PgServiceRepository services = new PgServiceRepository();
        PgTicketRepository tickets = new PgTicketRepository();
        PgReviewRepository reviews = new PgReviewRepository();

        String suffix = Long.toString(System.nanoTime());
        long userId = users.insert(connection, new User("crud_" + suffix, "Test", "User",
                LocalDate.of(1990, 1, 1), "crud_" + suffix + "@example.test", null, false,
                AccountStatus.ACTIVE), "encoded-password");

        Venue venue = new Venue("CRUD venue " + suffix, "Initial description",
                new Address("Street", "1", "Florence", "50100", "Italy", null));
        long venueId = venues.insert(connection, venue);
        assertEquals(venue.getName(), venues.findById(connection, venueId).orElseThrow().getName());
        assertTrue(venues.findByName(connection, "CRUD venue").stream().anyMatch(v -> v.getId() == venueId));
        assertTrue(venues.findByCity(connection, "Florence").stream().anyMatch(v -> v.getId() == venueId));
        venues.update(connection, venue.withId(venueId).withDescription("Updated description"));
        assertEquals("Updated description", venues.findById(connection, venueId).orElseThrow().getDescription());

        Space space = new Space(venueId, "CRUD space " + suffix, "Initial");
        long spaceId = spaces.insert(connection, space);
        assertTrue(spaces.findAllByVenueId(connection, venueId).stream().anyMatch(s -> s.getId() == spaceId));
        assertTrue(spaces.searchByName(connection, "CRUD space").stream().anyMatch(s -> s.getId() == spaceId));
        spaces.update(connection, space.withId(spaceId).withDescription("Updated"));
        assertEquals("Updated", spaces.findById(connection, spaceId).orElseThrow().getDescription());

        Equipment item = new Equipment(venueId, "CRUD equipment " + suffix, "Initial", 3);
        long equipmentId = equipment.insert(connection, item);
        assertTrue(equipment.findAllByVenueId(connection, venueId).stream().anyMatch(e -> e.getId() == equipmentId));
        equipment.update(connection, item.withId(equipmentId).withTotalQuantity(8));
        assertEquals(8, equipment.findById(connection, equipmentId).orElseThrow().getTotalQuantity());

        Service service = new Service("CRUD service " + suffix, "Initial");
        long serviceId = services.insert(connection, service);
        assertTrue(services.searchByName(connection, "CRUD service").stream().anyMatch(s -> s.getId() == serviceId));
        services.update(connection, service.withId(serviceId).withDescription("Updated"));
        assertEquals("Updated", services.findById(connection, serviceId).orElseThrow().getDescription());

        LocalDateTime begin = LocalDateTime.now().plusDays(10).withNano(0);
        Event event = new Event(venueId, userId, null, "CRUD event " + suffix, "Description", begin,
                begin.plusHours(2), null, 20, EventStatus.PUBLISHED, EventVisibility.PUBLIC,
                new BigDecimal("10.00"), LocalDateTime.now());
        long eventId = events.insert(connection, event);
        assertTrue(spaces.findAvailableSpaces(connection, venueId, begin, begin.plusHours(1)).stream()
                .anyMatch(s -> s.getId() == spaceId));
        assertTrue(services.findAvailableServicesForEvent(connection, eventId).stream()
                .anyMatch(s -> s.getId() == serviceId));

        Booking booking = new Booking(userId, eventId, LocalDateTime.now(), BookingStatus.CONFIRMED,
                new BigDecimal("10.00"));
        long bookingId = bookings.insert(connection, booking);
        Ticket ticket = new Ticket(bookingId, "Mario", "Rossi", begin);
        long ticketId = tickets.insert(connection, ticket);
        assertEquals(1, tickets.countTicketsForEvent(connection, eventId));
        assertTrue(tickets.findAllByBookingId(connection, bookingId).stream().anyMatch(t -> t.getId() == ticketId));
        tickets.update(connection, ticket.withId(ticketId).withFirstname("Luigi"));
        assertEquals("Luigi", tickets.findById(connection, ticketId).orElseThrow().getFirstname());

        Review review = new Review(userId, eventId, 4, "Initial", LocalDateTime.now());
        long reviewId = reviews.insert(connection, review);
        assertTrue(reviews.findAllByRating(connection, 4).stream().anyMatch(r -> r.getId() == reviewId));
        assertTrue(reviews.findByUserIdAndEventId(connection, userId, eventId).isPresent());
        assertEquals(4.0, reviews.getAverageRatingByEvent(connection, eventId));
        reviews.update(connection, review.withId(reviewId).withComment("Updated"));
        assertEquals("Updated", reviews.findById(connection, reviewId).orElseThrow().getComment());

        reviews.deleteById(connection, reviewId);
        tickets.deleteById(connection, ticketId);
        equipment.deleteById(connection, equipmentId);
        services.deleteById(connection, serviceId);
        spaces.deleteById(connection, spaceId);
        assertTrue(reviews.findById(connection, reviewId).isEmpty());
        assertTrue(tickets.findById(connection, ticketId).isEmpty());
        assertTrue(equipment.findById(connection, equipmentId).isEmpty());
        assertTrue(services.findById(connection, serviceId).isEmpty());
        assertTrue(spaces.findById(connection, spaceId).isEmpty());
    }
}
