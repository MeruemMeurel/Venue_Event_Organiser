package Venue_Event_Manager.repository.jdbc;

import Venue_Event_Manager.domain.model.booking.*;
import Venue_Event_Manager.domain.model.event.*;
import Venue_Event_Manager.domain.model.feedback.Review;
import Venue_Event_Manager.domain.model.request.EventRequest;
import Venue_Event_Manager.domain.model.request.EventRequestStatus;
import Venue_Event_Manager.domain.model.user.*;
import Venue_Event_Manager.domain.model.venue.*;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.sql.*;
import java.time.*;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

class PostgresTransactionAndConcurrencyIntegrationTest {
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
    void rollbackShouldRemoveAllWritesFromTheTransaction() throws SQLException {
        String email = "rollback_" + System.nanoTime() + "@example.test";
        long id;
        try (Connection connection = connection(false)) {
            id = new PgUserRepository().insert(connection,
                    new User("rollback_user", "Test", "User", LocalDate.of(1990, 1, 1), email,
                            null, false, AccountStatus.ACTIVE), "encoded");
            assertTrue(new PgUserRepository().findById(connection, id).isPresent());
            connection.rollback();
        }
        try (Connection verification = connection(true)) {
            assertTrue(new PgUserRepository().findById(verification, id).isEmpty());
            assertTrue(new PgUserRepository().findByEmail(verification, email).isEmpty());
        }
    }

    @Test
    void ticketBatchShouldBeAtomicWhenOneRowViolatesForeignKey() throws SQLException {
        Fixture fixture = createFixture();
        try (Connection connection = connection(false)) {
            PgTicketRepository repository = new PgTicketRepository();
            List<Ticket> batch = List.of(
                    new Ticket(fixture.bookingId, "Valid", "Guest", fixture.startsAt),
                    new Ticket(Long.MAX_VALUE, "Invalid", "Guest", fixture.startsAt));
            assertThrows(DaoException.class, () -> repository.insertMany(connection, batch));
            connection.rollback();
            assertTrue(repository.findAllByBookingId(connection, fixture.bookingId).isEmpty());
        } finally {
            deleteFixture(fixture);
        }
    }

    @Test
    void lockedBookingShouldRejectConcurrentForUpdateInsteadOfReadingStaleState() throws SQLException {
        Fixture fixture = createFixture();
        try (Connection first = connection(false); Connection second = connection(false)) {
            PgBookingRepository repository = new PgBookingRepository();
            assertTrue(repository.findByIdForUpdate(first, fixture.bookingId).isPresent());
            try (Statement statement = second.createStatement()) {
                statement.execute("SET LOCAL lock_timeout = '250ms'");
            }
            assertThrows(DaoException.class, () -> repository.findByIdForUpdate(second, fixture.bookingId));
            first.rollback();
            second.rollback();
        } finally {
            deleteFixture(fixture);
        }
    }

    @Test
    void eventLockShouldSerializeCapacityChangesAndBookings() throws SQLException {
        Fixture fixture = createFixture();
        try (Connection first = connection(false); Connection second = connection(false)) {
            PgEventRepository repository = new PgEventRepository();
            assertTrue(repository.findByIdForUpdate(first, fixture.eventId).isPresent());
            setShortLockTimeout(second);

            assertThrows(DaoException.class, () -> repository.findByIdForUpdate(second, fixture.eventId));

            first.rollback();
            second.rollback();
        } finally {
            deleteFixture(fixture);
        }
    }

    @Test
    void eventRequestLockShouldSerializeCompetingTransitions() throws SQLException {
        Fixture fixture = createFixture();
        try (Connection first = connection(false); Connection second = connection(false)) {
            PgEventRequestRepository repository = new PgEventRequestRepository();
            assertTrue(repository.findByIdForUpdate(first, fixture.requestId).isPresent());
            setShortLockTimeout(second);

            assertThrows(DaoException.class, () -> repository.findByIdForUpdate(second, fixture.requestId));

            first.rollback();
            second.rollback();
        } finally {
            deleteFixture(fixture);
        }
    }

    @Test
    void concurrentDuplicateReviewsShouldBeRejectedByTheDatabase() throws SQLException {
        Fixture fixture = createFixture();
        PgReviewRepository repository = new PgReviewRepository();
        try (Connection first = connection(false); Connection second = connection(false)) {
            repository.insert(first, new Review(fixture.userId, fixture.eventId, 5,
                    "First review", LocalDateTime.now()));
            setShortLockTimeout(second);

            assertThrows(DaoException.class, () -> repository.insert(second,
                    new Review(fixture.userId, fixture.eventId, 4,
                            "Concurrent duplicate", LocalDateTime.now())));

            second.rollback();
            first.commit();
        }

        try (Connection verification = connection(false)) {
            assertThrows(DaoException.class, () -> repository.insert(verification,
                    new Review(fixture.userId, fixture.eventId, 3,
                            "Committed duplicate", LocalDateTime.now())));
            verification.rollback();
        } finally {
            deleteFixture(fixture);
        }
    }

    @Test
    void updatingOrDeletingMissingRowsShouldFailExplicitly() throws SQLException {
        try (Connection connection = connection(false)) {
            PgTicketRepository repository = new PgTicketRepository();
            Ticket missing = new Ticket(Long.MAX_VALUE, 1, "Nobody", "Missing", LocalDateTime.now());
            assertThrows(DaoException.class, () -> repository.update(connection, missing));
            assertThrows(DaoException.class, () -> repository.deleteById(connection, Long.MAX_VALUE));
            connection.rollback();
        }
    }

    private Fixture createFixture() throws SQLException {
        try (Connection connection = connection(false)) {
            String suffix = Long.toString(System.nanoTime());
            long userId = new PgUserRepository().insert(connection,
                    new User("lock_" + suffix, "Test", "User", LocalDate.of(1990, 1, 1),
                            "lock_" + suffix + "@example.test", null, false, AccountStatus.ACTIVE), "encoded");
            long venueId = new PgVenueRepository().insert(connection,
                    new Venue("Lock venue " + suffix, null,
                            new Address("Street", "1", "Florence", "50100", "Italy", null)));
            LocalDateTime startsAt = LocalDateTime.now().plusDays(20).withNano(0);
            long eventId = new PgEventRepository().insert(connection,
                    new Event(venueId, userId, null, "Lock event " + suffix, null, startsAt,
                            startsAt.plusHours(2), null, 10, EventStatus.PUBLISHED,
                            EventVisibility.PUBLIC, BigDecimal.TEN, LocalDateTime.now()));
            long bookingId = new PgBookingRepository().insert(connection,
                    new Booking(userId, eventId, LocalDateTime.now(), BookingStatus.PENDING_PAYMENT, BigDecimal.TEN));
            long requestId = new PgEventRequestRepository().insert(connection,
                    new EventRequest(userId, null, venueId, "Lock request " + suffix, null, startsAt,
                            startsAt.plusHours(2), EventRequestStatus.PENDING, LocalDateTime.now(), null, null));
            connection.commit();
            return new Fixture(userId, venueId, eventId, bookingId, requestId, startsAt);
        }
    }

    private void deleteFixture(Fixture fixture) throws SQLException {
        try (Connection connection = connection(true); PreparedStatement booking = connection.prepareStatement(
                "DELETE FROM booking WHERE id = ?"); PreparedStatement event = connection.prepareStatement(
                "DELETE FROM event WHERE id = ?"); PreparedStatement venue = connection.prepareStatement(
                "DELETE FROM venue WHERE id = ?"); PreparedStatement user = connection.prepareStatement(
                "DELETE FROM \"USER\" WHERE id = ?")) {
            booking.setLong(1, fixture.bookingId);
            booking.executeUpdate();
            event.setLong(1, fixture.eventId);
            event.executeUpdate();
            venue.setLong(1, fixture.venueId);
            venue.executeUpdate();
            user.setLong(1, fixture.userId);
            user.executeUpdate();
        }
    }

    private Connection connection(boolean autoCommit) throws SQLException {
        Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
        connection.setAutoCommit(autoCommit);
        return connection;
    }

    private void setShortLockTimeout(Connection connection) throws SQLException {
        try (Statement statement = connection.createStatement()) {
            statement.execute("SET LOCAL lock_timeout = '250ms'");
        }
    }

    private record Fixture(long userId, long venueId, long eventId, long bookingId, long requestId,
                           LocalDateTime startsAt) {}
}
