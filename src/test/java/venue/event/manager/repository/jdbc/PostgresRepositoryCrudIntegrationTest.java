package venue.event.manager.repository.jdbc;

import venue.event.manager.domain.model.booking.Booking;
import venue.event.manager.domain.model.booking.BookingStatus;
import venue.event.manager.domain.model.booking.Ticket;
import venue.event.manager.domain.model.event.Event;
import venue.event.manager.domain.model.event.EventStatus;
import venue.event.manager.domain.model.event.EventVisibility;
import venue.event.manager.domain.model.feedback.Review;
import venue.event.manager.domain.model.feedback.Report;
import venue.event.manager.domain.model.feedback.ReportSeverity;
import venue.event.manager.domain.model.event.EventGuest;
import venue.event.manager.domain.model.event.EventGuestStatus;
import venue.event.manager.domain.model.request.EventRequest;
import venue.event.manager.domain.model.request.EventRequestStatus;
import venue.event.manager.domain.model.resource.Equipment;
import venue.event.manager.domain.model.resource.Service;
import venue.event.manager.domain.model.resource.Space;
import venue.event.manager.domain.model.user.AccountStatus;
import venue.event.manager.domain.model.user.User;
import venue.event.manager.domain.model.venue.Address;
import venue.event.manager.domain.model.venue.Venue;
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
        PgReportRepository reports = new PgReportRepository();
        PgEventRequestRepository requests = new PgEventRequestRepository();
        PgEventGuestRepository guests = new PgEventGuestRepository();

        String suffix = Long.toString(System.nanoTime());
        long userId = users.insert(connection, new User("crud_" + suffix, "Test", "User",
                LocalDate.of(1990, 1, 1), "crud_" + suffix + "@example.test", null, false,
                AccountStatus.ACTIVE), "encoded-password");
        long adminId = users.insert(connection, new User("admin_" + suffix, "Admin", "User",
                LocalDate.of(1985, 1, 1), "admin_" + suffix + "@example.test", null, true,
                AccountStatus.ACTIVE), "admin-password");
        assertTrue(users.findAll(connection).stream().anyMatch(u -> u.getId() == userId));
        assertTrue(users.findByUsername(connection, "crud_" + suffix).isPresent());
        assertTrue(users.findByEmail(connection, "crud_" + suffix + "@example.test").isPresent());
        assertTrue(users.findAllByIsAdmin(connection, true).stream().anyMatch(u -> u.getId() == adminId));
        assertTrue(users.findAllByAccountStatus(connection, AccountStatus.ACTIVE).stream()
                .anyMatch(u -> u.getId() == userId));
        assertEquals("encoded-password", users.getPasswordById(connection, userId).orElseThrow());
        users.updatePassword(connection, userId, "updated-password");
        assertEquals("updated-password", users.getPasswordById(connection, userId).orElseThrow());

        Venue venue = new Venue("CRUD venue " + suffix, "Initial description",
                new Address("Street", "1", "Florence", "50100", "Italy", null));
        long venueId = venues.insert(connection, venue);
        assertEquals(venue.getName(), venues.findById(connection, venueId).orElseThrow().getName());
        assertTrue(venues.findByName(connection, "CRUD venue").stream().anyMatch(v -> v.getId() == venueId));
        assertTrue(venues.findByCity(connection, "Florence").stream().anyMatch(v -> v.getId() == venueId));
        venues.update(connection, venue.withId(venueId).withDescription("Updated description"));
        assertEquals("Updated description", venues.findById(connection, venueId).orElseThrow().getDescription());
        assertTrue(venues.findAll(connection).stream().anyMatch(v -> v.getId() == venueId));
        assertTrue(venues.findByCountry(connection, "Italy").stream().anyMatch(v -> v.getId() == venueId));

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
        assertTrue(events.findAll(connection).stream().anyMatch(e -> e.getId() == eventId));
        assertTrue(events.findAllByVenueId(connection, venueId).stream().anyMatch(e -> e.getId() == eventId));
        assertTrue(events.findAllByCreatorId(connection, userId).stream().anyMatch(e -> e.getId() == eventId));
        assertTrue(events.findAllByStatus(connection, EventStatus.PUBLISHED).stream().anyMatch(e -> e.getId() == eventId));
        assertTrue(events.findAllVisibility(connection, EventVisibility.PUBLIC).stream().anyMatch(e -> e.getId() == eventId));
        assertTrue(events.findAllByStartDate(connection, begin).stream().anyMatch(e -> e.getId() == eventId));
        assertTrue(events.findAllByEndDate(connection, begin.plusHours(2)).stream().anyMatch(e -> e.getId() == eventId));
        assertTrue(events.findAllAfter(connection, begin.minusMinutes(1)).stream().anyMatch(e -> e.getId() == eventId));
        assertTrue(events.findAllBefore(connection, begin.plusMinutes(1)).stream().anyMatch(e -> e.getId() == eventId));
        assertTrue(events.findAllBetween(connection, begin.minusMinutes(1), begin.plusHours(3)).stream()
                .anyMatch(e -> e.getId() == eventId));

        EventRequest request = new EventRequest(userId, adminId, venueId, "CRUD request " + suffix,
                null, begin, begin.plusHours(2), EventRequestStatus.PENDING, LocalDateTime.now(), null, null);
        long requestId = requests.insert(connection, request);
        assertTrue(requests.findAll(connection).stream().anyMatch(r -> r.getId() == requestId));
        assertTrue(requests.findAllByRequesterId(connection, userId).stream().anyMatch(r -> r.getId() == requestId));
        assertTrue(requests.findAllByHandlerId(connection, adminId).stream().anyMatch(r -> r.getId() == requestId));
        assertTrue(requests.findAllByVenueId(connection, venueId).stream().anyMatch(r -> r.getId() == requestId));
        assertTrue(requests.findAllByStatus(connection, EventRequestStatus.PENDING).stream()
                .anyMatch(r -> r.getId() == requestId));
        assertTrue(requests.findAllByStartDate(connection, begin).stream().anyMatch(r -> r.getId() == requestId));
        assertTrue(requests.findAllByEndDate(connection, begin.plusHours(2)).stream()
                .anyMatch(r -> r.getId() == requestId));
        assertTrue(requests.findAllAfter(connection, begin.minusMinutes(1)).stream()
                .anyMatch(r -> r.getId() == requestId));
        assertTrue(requests.findAllBefore(connection, begin.plusMinutes(1)).stream()
                .anyMatch(r -> r.getId() == requestId));
        assertTrue(requests.findAllBetween(connection, begin.minusMinutes(1), begin.plusHours(3)).stream()
                .anyMatch(r -> r.getId() == requestId));
        requests.updateStatus(connection, requestId, EventRequestStatus.ACCEPTED);
        EventRequest accepted = requests.findById(connection, requestId).orElseThrow();
        assertEquals(EventRequestStatus.ACCEPTED, accepted.getStatus());
        requests.update(connection, accepted.withName("Updated request"));
        assertTrue(spaces.findAvailableSpaces(connection, venueId, begin, begin.plusHours(1)).stream()
                .anyMatch(s -> s.getId() == spaceId));
        assertTrue(services.findAvailableServicesForEvent(connection, eventId).stream()
                .anyMatch(s -> s.getId() == serviceId));

        Booking booking = new Booking(userId, eventId, LocalDateTime.now(), BookingStatus.CONFIRMED,
                new BigDecimal("10.00"));
        long bookingId = bookings.insert(connection, booking);
        assertTrue(bookings.findAll(connection).stream().anyMatch(b -> b.getId() == bookingId));
        assertTrue(bookings.findByIdForUpdate(connection, bookingId).isPresent());
        assertTrue(bookings.findAllByUserId(connection, userId).stream().anyMatch(b -> b.getId() == bookingId));
        assertTrue(bookings.findAllByEventId(connection, eventId).stream().anyMatch(b -> b.getId() == bookingId));
        assertTrue(bookings.findAllByStatus(connection, BookingStatus.CONFIRMED).stream()
                .anyMatch(b -> b.getId() == bookingId));
        assertTrue(bookings.findAllByUserIdAndStatus(connection, userId, BookingStatus.CONFIRMED).stream()
                .anyMatch(b -> b.getId() == bookingId));
        assertTrue(bookings.findAllByEventIdAndStatus(connection, eventId, BookingStatus.CONFIRMED).stream()
                .anyMatch(b -> b.getId() == bookingId));
        assertTrue(bookings.findAllByUserIdAndEventId(connection, userId, eventId).stream()
                .anyMatch(b -> b.getId() == bookingId));
        Ticket ticket = new Ticket(bookingId, "Mario", "Rossi", begin);
        long ticketId = tickets.insert(connection, ticket);
        assertEquals(1, tickets.countTicketsForEvent(connection, eventId));
        assertTrue(tickets.findAllByBookingId(connection, bookingId).stream().anyMatch(t -> t.getId() == ticketId));
        tickets.update(connection, ticket.withId(ticketId).withFirstname("Luigi"));
        assertEquals("Luigi", tickets.findById(connection, ticketId).orElseThrow().getFirstname());
        assertTrue(tickets.findAll(connection).stream().anyMatch(t -> t.getId() == ticketId));
        assertTrue(tickets.findAllByEventId(connection, eventId).stream().anyMatch(t -> t.getId() == ticketId));

        EventGuest guest = new EventGuest(eventId, "Guest", "One", null, EventGuestStatus.INVITED, null);
        long guestId = guests.insert(connection, guest);
        assertTrue(guests.findAll(connection).stream().anyMatch(g -> g.getId() == guestId));
        assertTrue(guests.findByIdForUpdate(connection, guestId).isPresent());
        assertTrue(guests.findAllByEventId(connection, eventId).stream().anyMatch(g -> g.getId() == guestId));
        assertTrue(guests.findAllByStatus(connection, EventGuestStatus.INVITED).stream()
                .anyMatch(g -> g.getId() == guestId));
        assertTrue(guests.findAllByEventIdAndStatus(connection, eventId, EventGuestStatus.INVITED).stream()
                .anyMatch(g -> g.getId() == guestId));
        guests.updateEventGuestStatus(connection, guestId, EventGuestStatus.CONFIRMED);
        EventGuest confirmedGuest = guests.findById(connection, guestId).orElseThrow();
        guests.update(connection, confirmedGuest.withNote("Updated"));

        Review review = new Review(userId, eventId, 4, "Initial", LocalDateTime.now());
        long reviewId = reviews.insert(connection, review);
        assertTrue(reviews.findAllByRating(connection, 4).stream().anyMatch(r -> r.getId() == reviewId));
        assertTrue(reviews.findByUserIdAndEventId(connection, userId, eventId).isPresent());
        assertEquals(4.0, reviews.getAverageRatingByEvent(connection, eventId));
        reviews.update(connection, review.withId(reviewId).withComment("Updated"));
        assertEquals("Updated", reviews.findById(connection, reviewId).orElseThrow().getComment());
        assertTrue(reviews.findAll(connection).stream().anyMatch(r -> r.getId() == reviewId));
        assertTrue(reviews.findAllByUserId(connection, userId).stream().anyMatch(r -> r.getId() == reviewId));
        assertTrue(reviews.findAllByEventId(connection, eventId).stream().anyMatch(r -> r.getId() == reviewId));
        assertEquals(4.0, reviews.getAverageRatingGivenByUser(connection, userId));

        Report report = new Report(userId, adminId, eventId, ReportSeverity.MIDDLE, null, LocalDateTime.now());
        long reportId = reports.insert(connection, report);
        assertTrue(reports.findAll(connection).stream().anyMatch(r -> r.getId() == reportId));
        assertTrue(reports.findAllByUserId(connection, userId).stream().anyMatch(r -> r.getId() == reportId));
        assertTrue(reports.findAllByAdminId(connection, adminId).stream().anyMatch(r -> r.getId() == reportId));
        assertTrue(reports.findAllByEventId(connection, eventId).stream().anyMatch(r -> r.getId() == reportId));
        assertTrue(reports.findAllBySeverity(connection, ReportSeverity.MIDDLE).stream()
                .anyMatch(r -> r.getId() == reportId));
        assertTrue(reports.findByUserIdAndEventId(connection, userId, eventId).isPresent());
        assertTrue(reports.findAllByAdminIdAndEventId(connection, adminId, eventId).stream()
                .anyMatch(r -> r.getId() == reportId));
        reports.update(connection, report.withId(reportId).withComment("Updated report"));
        assertEquals("Updated report", reports.findById(connection, reportId).orElseThrow().getComment());

        reviews.deleteById(connection, reviewId);
        reports.deleteById(connection, reportId);
        guests.deleteById(connection, guestId);
        requests.deleteById(connection, requestId);
        tickets.deleteById(connection, ticketId);
        equipment.deleteById(connection, equipmentId);
        services.deleteById(connection, serviceId);
        spaces.deleteById(connection, spaceId);
        assertTrue(reviews.findById(connection, reviewId).isEmpty());
        assertTrue(reports.findById(connection, reportId).isEmpty());
        assertTrue(guests.findById(connection, guestId).isEmpty());
        assertTrue(requests.findById(connection, requestId).isEmpty());
        assertTrue(tickets.findById(connection, ticketId).isEmpty());
        assertTrue(equipment.findById(connection, equipmentId).isEmpty());
        assertTrue(services.findById(connection, serviceId).isEmpty());
        assertTrue(spaces.findById(connection, spaceId).isEmpty());
    }
}
