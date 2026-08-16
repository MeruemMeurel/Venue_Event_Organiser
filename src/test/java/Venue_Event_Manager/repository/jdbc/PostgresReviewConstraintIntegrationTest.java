package Venue_Event_Manager.repository.jdbc;

import Venue_Event_Manager.domain.model.event.Event;
import Venue_Event_Manager.domain.model.event.EventStatus;
import Venue_Event_Manager.domain.model.event.EventVisibility;
import Venue_Event_Manager.domain.model.feedback.Review;
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

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

class PostgresReviewConstraintIntegrationTest {
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
    void sameUserCannotReviewTheSameEventTwice() throws SQLException {
        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD)) {
            connection.setAutoCommit(false);
            try {
                String suffix = Long.toString(System.nanoTime());
                long userId = new PgUserRepository().insert(connection,
                        new User("review_" + suffix, "Review", "User", LocalDate.of(1990, 1, 1),
                                "review_" + suffix + "@example.test", null, false,
                                AccountStatus.ACTIVE), "encoded");
                long venueId = new PgVenueRepository().insert(connection,
                        new Venue("Review venue " + suffix, null,
                                new Address("Street", "1", "Florence", "50100", "Italy", null)));
                LocalDateTime start = LocalDateTime.now().minusDays(2).withNano(0);
                long eventId = new PgEventRepository().insert(connection,
                        new Event(venueId, userId, null, "Review event " + suffix, null, start,
                                start.plusHours(2), null, 10, EventStatus.PUBLISHED,
                                EventVisibility.PUBLIC, BigDecimal.TEN, start.minusDays(1)));

                PgReviewRepository reviews = new PgReviewRepository();
                reviews.insert(connection, new Review(userId, eventId, 5, "First", LocalDateTime.now()));

                assertThrows(DaoException.class, () -> reviews.insert(connection,
                        new Review(userId, eventId, 4, "Duplicate", LocalDateTime.now())));
            } finally {
                connection.rollback();
            }
        }
    }
}
