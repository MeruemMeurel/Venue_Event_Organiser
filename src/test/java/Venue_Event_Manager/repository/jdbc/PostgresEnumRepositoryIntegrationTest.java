package Venue_Event_Manager.repository.jdbc;

import Venue_Event_Manager.domain.model.booking.Booking;
import Venue_Event_Manager.domain.model.booking.BookingStatus;
import Venue_Event_Manager.domain.model.event.Event;
import Venue_Event_Manager.domain.model.event.EventGuest;
import Venue_Event_Manager.domain.model.event.EventGuestStatus;
import Venue_Event_Manager.domain.model.event.EventStatus;
import Venue_Event_Manager.domain.model.event.EventVisibility;
import Venue_Event_Manager.domain.model.feedback.Report;
import Venue_Event_Manager.domain.model.feedback.ReportSeverity;
import Venue_Event_Manager.domain.model.request.EventRequest;
import Venue_Event_Manager.domain.model.request.EventRequestStatus;
import Venue_Event_Manager.domain.model.user.AccountStatus;
import Venue_Event_Manager.domain.model.user.User;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

class PostgresEnumRepositoryIntegrationTest {

    private static final String JDBC_URL = "jdbc:postgresql://localhost:5433/event_manager_db";
    private static final String DB_USER = "admin";
    private static final String DB_PASSWORD = "change_me";

    @BeforeAll
    static void requireLocalDatabase() {
        try (Connection ignored = DriverManager.getConnection(JDBC_URL, DB_USER, DB_PASSWORD)) {
            // Database is available: integration tests can run.
        } catch (SQLException exception) {
            assumeTrue(false, "Local PostgreSQL is unavailable: " + exception.getMessage());
        }
    }

    @Test
    void repositoriesShouldPersistFilterAndUpdatePostgresEnums() throws SQLException {
        try (Connection connection = DriverManager.getConnection(JDBC_URL, DB_USER, DB_PASSWORD)) {
            connection.setAutoCommit(false);
            try {
                exerciseEnumQueries(connection);
            } finally {
                connection.rollback();
            }
        }
    }

    private void exerciseEnumQueries(Connection connection) throws SQLException {
        PgUserRepository users = new PgUserRepository();
        PgEventRepository events = new PgEventRepository();
        PgBookingRepository bookings = new PgBookingRepository();
        PgEventGuestRepository guests = new PgEventGuestRepository();
        PgEventRequestRepository requests = new PgEventRequestRepository();
        PgReportRepository reports = new PgReportRepository();

        String suffix = Long.toString(System.nanoTime());
        long userId = users.insert(connection, newUser("enum_user_" + suffix, false), "encoded-password");
        long adminId = users.insert(connection, newUser("enum_admin_" + suffix, true), "encoded-password");
        long venueId = insertVenue(connection, suffix);

        users.updateAccountStatus(connection, userId, AccountStatus.BANNED);
        assertContainsId(users.findAllByAccountStatus(connection, AccountStatus.BANNED), userId);

        LocalDateTime beginsAt = LocalDateTime.now().plusDays(10).withNano(0);
        Event event = new Event(venueId, adminId, null, "Enum event " + suffix, "Integration test",
                beginsAt, beginsAt.plusHours(2), null, 20, EventStatus.CONFIRMED,
                EventVisibility.PUBLIC, new BigDecimal("12.50"), null);
        long eventId = events.insert(connection, event);
        assertContainsId(events.findAllByStatus(connection, EventStatus.CONFIRMED), eventId);
        assertContainsId(events.findAllVisibility(connection, EventVisibility.PUBLIC), eventId);
        events.update(connection, event.withId(eventId).withStatus(EventStatus.PUBLISHED)
                .withVisibility(EventVisibility.PRIVATE_GUEST_LIST).withPublishedAt(LocalDateTime.now()));
        events.updateStatus(connection, eventId, EventStatus.CANCELLED);
        events.updateVisibility(connection, eventId, EventVisibility.PUBLIC);
        assertEquals(EventStatus.CANCELLED, events.findById(connection, eventId).orElseThrow().getStatus());

        Booking booking = new Booking(userId, eventId, LocalDateTime.now(), BookingStatus.PENDING_PAYMENT,
                new BigDecimal("12.50"));
        long bookingId = bookings.insert(connection, booking);
        assertContainsId(bookings.findAllByStatus(connection, BookingStatus.PENDING_PAYMENT), bookingId);
        bookings.update(connection, booking.withId(bookingId).withStatus(BookingStatus.CONFIRMED));
        bookings.updateStatus(connection, bookingId, BookingStatus.CANCELLED);
        assertEquals(BookingStatus.CANCELLED, bookings.findById(connection, bookingId).orElseThrow().getStatus());

        EventGuest guest = new EventGuest(eventId, "Enum", "Guest", LocalDate.of(1990, 1, 1),
                EventGuestStatus.INVITED, "Integration test");
        long guestId = guests.insert(connection, guest);
        assertContainsId(guests.findAllByStatus(connection, EventGuestStatus.INVITED), guestId);
        guests.update(connection, guest.withId(guestId).withStatus(EventGuestStatus.CONFIRMED));
        guests.updateEventGuestStatus(connection, guestId, EventGuestStatus.CANCELLED);
        assertEquals(EventGuestStatus.CANCELLED, guests.findById(connection, guestId).orElseThrow().getStatus());

        EventRequest request = new EventRequest(userId, adminId, venueId, "Enum request " + suffix,
                "Integration test", beginsAt.plusDays(1), beginsAt.plusDays(1).plusHours(2),
                EventRequestStatus.PENDING, LocalDateTime.now(), null, null);
        long requestId = requests.insert(connection, request);
        assertContainsId(requests.findAllByStatus(connection, EventRequestStatus.PENDING), requestId);
        requests.update(connection, request.withId(requestId).withStatus(EventRequestStatus.ACCEPTED));
        requests.updateStatus(connection, requestId, EventRequestStatus.REJECTED);
        assertEquals(EventRequestStatus.REJECTED,
                requests.findById(connection, requestId).orElseThrow().getStatus());

        Report report = new Report(userId, adminId, eventId, ReportSeverity.LOW, "Integration test",
                LocalDateTime.now());
        long reportId = reports.insert(connection, report);
        assertContainsId(reports.findAllBySeverity(connection, ReportSeverity.LOW), reportId);
        reports.update(connection, report.withId(reportId).withSeverity(ReportSeverity.HIGH));
        assertEquals(ReportSeverity.HIGH, reports.findById(connection, reportId).orElseThrow().getSeverity());
    }

    private User newUser(String username, boolean admin) {
        return new User(username, "Enum", "Test", LocalDate.of(1990, 1, 1),
                username + "@example.test", null, admin, AccountStatus.ACTIVE);
    }

    private long insertVenue(Connection connection, String suffix) throws SQLException {
        String sql = "INSERT INTO venue (name, description, street, street_number, city, postal_code, country) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?) RETURNING id";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, "Enum venue " + suffix);
            statement.setString(2, "Integration test");
            statement.setString(3, "Test street");
            statement.setString(4, "1");
            statement.setString(5, "Florence");
            statement.setString(6, "50100");
            statement.setString(7, "Italy");
            try (var resultSet = statement.executeQuery()) {
                assertTrue(resultSet.next());
                return resultSet.getLong(1);
            }
        }
    }

    private <T> void assertContainsId(Iterable<T> entities, long expectedId) {
        boolean found = false;
        for (T entity : entities) {
            long id;
            if (entity instanceof User user) id = user.getId();
            else if (entity instanceof Event event) id = event.getId();
            else if (entity instanceof Booking booking) id = booking.getId();
            else if (entity instanceof EventGuest guest) id = guest.getId();
            else if (entity instanceof EventRequest request) id = request.getId();
            else if (entity instanceof Report report) id = report.getId();
            else throw new IllegalArgumentException("Unsupported entity type");
            if (id == expectedId) {
                found = true;
                break;
            }
        }
        assertTrue(found, "Expected result to contain entity with id " + expectedId);
    }
}
