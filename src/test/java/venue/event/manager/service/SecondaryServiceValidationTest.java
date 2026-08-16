package venue.event.manager.service;

import venue.event.manager.domain.model.feedback.Report;
import venue.event.manager.domain.model.feedback.Review;
import venue.event.manager.domain.model.request.EventRequest;
import venue.event.manager.domain.model.user.User;
import venue.event.manager.domain.model.venue.Venue;
import venue.event.manager.exception.ValidationException;
import venue.event.manager.repository.*;
import venue.event.manager.util.TestDataFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.sql.Connection;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class SecondaryServiceValidationTest {

    private EventRequestService requests;
    private ReviewService reviews;
    private ReportService reports;

    @BeforeEach
    void setUp() {
        UserRepository users = mock(UserRepository.class);
        VenueRepository venues = mock(VenueRepository.class);
        User requester = TestDataFactory.createDefaultUser("requester").withId(1);
        when(users.findById(any(Connection.class), eq(1L))).thenReturn(Optional.of(requester));
        when(venues.findById(any(Connection.class), anyLong()))
                .thenReturn(Optional.of(TestDataFactory.createDefaultVenue("Venue").withId(1)));
        requests = new EventRequestService(mock(EventRequestRepository.class), users, venues);
        reviews = new ReviewService(mock(ReviewRepository.class), mock(EventRepository.class),
                mock(BookingRepository.class));
        reports = new ReportService(mock(ReportRepository.class), mock(EventRepository.class), users);
    }

    @Test void nullRequestShouldBeRejected() { assertThrows(ValidationException.class, () -> requests.createRequest(null)); }
    @Test void invalidRequesterShouldBeRejected() { assertThrows(ValidationException.class, () -> requests.createRequest(validRequest().withRequesterId(0))); }
    @Test void invalidVenueShouldBeRejected() { assertThrows(ValidationException.class, () -> requests.createRequest(validRequest().withVenueId(0))); }
    @Test void blankRequestNameShouldBeRejected() { assertThrows(ValidationException.class, () -> requests.createRequest(validRequest().withName(" "))); }
    @Test void longRequestDescriptionShouldBeRejected() { assertThrows(ValidationException.class, () -> requests.createRequest(validRequest().withDescription("x".repeat(1001)))); }
    @Test void invertedRequestDatesShouldBeRejected() {
        EventRequest request = validRequest();
        assertThrows(ValidationException.class, () -> requests.createRequest(request.withEndDateTime(request.getBeginDatetime())));
    }
    @Test void negativeQuoteShouldBeRejected() { assertThrows(ValidationException.class, () -> requests.createRequest(validRequest().withQuote(new BigDecimal("-1")))); }
    @Test void closureBeforeCreationShouldBeRejected() {
        EventRequest request = validRequest();
        assertThrows(ValidationException.class, () -> requests.createRequest(request.withClosedAt(request.getCreatedAt().minusDays(1))));
    }

    @Test void nullReviewUpdateShouldBeRejected() { assertThrows(ValidationException.class, () -> reviews.updateReview(null)); }
    @Test void invalidReviewIdShouldBeRejected() { assertThrows(ValidationException.class, () -> reviews.updateReview(validReview().withId(0))); }
    @Test void lowRatingShouldBeRejected() { assertThrows(ValidationException.class, () -> reviews.updateReview(validReview().withRating(0))); }
    @Test void highRatingShouldBeRejected() { assertThrows(ValidationException.class, () -> reviews.updateReview(validReview().withRating(6))); }
    @Test void longReviewCommentShouldBeRejected() { assertThrows(ValidationException.class, () -> reviews.updateReview(validReview().withComment("x".repeat(1001)))); }
    @Test void missingReviewCreationDateShouldBeRejected() { assertThrows(ValidationException.class, () -> reviews.updateReview(validReview().withCreatedAt(null))); }

    @Test void nullReportUpdateShouldBeRejected() { assertThrows(ValidationException.class, () -> reports.updateReport(null)); }
    @Test void invalidReportIdShouldBeRejected() { assertThrows(ValidationException.class, () -> reports.updateReport(validReport().withId(0))); }
    @Test void longReportCommentShouldBeRejected() { assertThrows(ValidationException.class, () -> reports.updateReport(validReport().withComment("x".repeat(1001)))); }
    @Test void missingReportCreationDateShouldBeRejected() { assertThrows(ValidationException.class, () -> reports.updateReport(validReport().withCreatedAt(null))); }

    private EventRequest validRequest() {
        LocalDateTime begin = LocalDateTime.now().plusDays(5);
        return TestDataFactory.createDefaultRequest(1, 1, "Request").withBeginDateTime(begin)
                .withEndDateTime(begin.plusHours(2)).withCreatedAt(LocalDateTime.now());
    }
    private Review validReview() { return TestDataFactory.createDefaultReview(1, 1).withId(1); }
    private Report validReport() { return TestDataFactory.createDefaultReport(1, 2, 1L).withId(1); }
}
