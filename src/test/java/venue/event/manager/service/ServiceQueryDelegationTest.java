package venue.event.manager.service;

import venue.event.manager.domain.model.booking.*;
import venue.event.manager.domain.model.event.*;
import venue.event.manager.domain.model.feedback.*;
import venue.event.manager.domain.model.request.*;
import venue.event.manager.domain.model.resource.*;
import venue.event.manager.domain.model.user.*;
import venue.event.manager.domain.model.venue.*;
import venue.event.manager.repository.*;
import venue.event.manager.util.TestDataFactory;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static venue.event.manager.util.TestTransactionManagerFactory.create;

class ServiceQueryDelegationTest {

    @Test
    void bookingQueriesShouldDelegateFiltersAndReturnRepositoryData() {
        BookingRepository bookings = mock(BookingRepository.class);
        TicketRepository tickets = mock(TicketRepository.class);
        EventRepository events = mock(EventRepository.class);
        BookingService service = new BookingService(create(), bookings, tickets, events,
                mock(UserRepository.class));
        Booking booking = TestDataFactory.createDefaultBooking(1, 2).withId(3);
        Ticket ticket = TestDataFactory.createDefaultTicket(3, "Mario", "Rossi").withId(4);
        when(bookings.findAll(any())).thenReturn(List.of(booking));
        when(bookings.findById(any(), eq(3L))).thenReturn(Optional.of(booking));
        when(events.findById(any(), eq(2L))).thenReturn(Optional.of(futureEvent()));
        when(tickets.countTicketsForEvent(any(), eq(2L))).thenReturn(4);
        when(tickets.findAllByBookingId(any(), eq(3L))).thenReturn(List.of(ticket));
        when(tickets.findAllByEventId(any(), eq(2L))).thenReturn(List.of(ticket));

        assertEquals(List.of(booking), service.getAllBookings());
        assertEquals(booking, service.getBooking(3));
        service.getBookingsMadeByUser(1);
        service.getBookingsForEvent(2);
        service.getConfirmedBookings();
        service.getPendingBookings();
        service.getCancelledBookings();
        service.getConfirmedBookingsMadeByUser(1);
        service.getPendingBookingsMadeByUser(1);
        service.getCancelledBookingsMadeByUser(1);
        service.getConfirmedBookingsForEvent(2);
        service.getPendingBookingsForEvent(2);
        service.getCancelledBookingsForEvent(2);
        service.getBookingsForEventMadeByUser(1, 2);
        assertEquals(List.of(ticket), service.getTicketsForBooking(3));
        assertEquals(List.of(ticket), service.getTicketsForEvent(2));
        assertEquals(96, service.getRemainingPlaces(2));

        verify(bookings).findAllByUserIdAndEventId(any(), eq(1L), eq(2L));
        verify(bookings, times(3)).findAllByStatus(any(), any(BookingStatus.class));
        verify(bookings, times(3)).findAllByUserIdAndStatus(any(), eq(1L), any(BookingStatus.class));
        verify(bookings, times(3)).findAllByEventIdAndStatus(any(), eq(2L), any(BookingStatus.class));
    }

    @Test
    void eventQueriesShouldDelegateEverySupportedFilter() {
        EventRepository events = mock(EventRepository.class);
        EventService service = new EventService(create(), events, mock(EventRequestRepository.class), mock(TicketRepository.class),
                mock(BookingRepository.class), mock(EventGuestRepository.class), mock(VenueRepository.class),
                mock(UserRepository.class));
        Event event = futureEvent();
        LocalDateTime start = event.getBeginDatetime();
        LocalDateTime end = event.getEndDatetime();
        when(events.findAll(any())).thenReturn(List.of(event));
        when(events.findById(any(), eq(2L))).thenReturn(Optional.of(event));
        when(events.getAverageReview(any(), eq(2L))).thenReturn(Optional.of(4.5));

        assertEquals(List.of(event), service.getAllEvents());
        assertEquals(event, service.getEvent(2));
        service.getEventsByVenueId(1);
        service.getEventsByCreator(1);
        service.getEventsByOrganiser(3);
        service.getEventsWithStatus(EventStatus.PUBLISHED);
        service.getEventsWithVisibility(EventVisibility.PUBLIC);
        service.getEventsStartingAt(start);
        service.getEventsEndingAt(end);
        service.getEventsAfter(start);
        service.getEventsBefore(end);
        service.getEventsBetween(start, end);
        assertEquals(4.5, service.getAverageReview(2));

        verify(events).findAllBetween(any(), eq(start), eq(end));
        verify(events).findAllVisibility(any(), eq(EventVisibility.PUBLIC));
    }

    @Test
    void requestQueriesShouldDelegateEverySupportedFilter() {
        EventRequestRepository requests = mock(EventRequestRepository.class);
        EventRequestService service = new EventRequestService(create(), requests, mock(UserRepository.class),
                mock(VenueRepository.class));
        EventRequest request = TestDataFactory.createDefaultRequest(1, 2, "Request").withId(3);
        LocalDateTime start = request.getBeginDatetime();
        LocalDateTime end = request.getEndDatetime();
        when(requests.findAll(any())).thenReturn(List.of(request));
        when(requests.findById(any(), eq(3L))).thenReturn(Optional.of(request));

        assertEquals(List.of(request), service.getAllRequests());
        assertEquals(request, service.getRequest(3));
        service.getRequestsByRequester(1);
        service.getRequestsByHandler(4);
        service.getRequestsByVenue(2);
        service.getRequestsByStatus(EventRequestStatus.PENDING);
        service.getRequestsStartingAt(start);
        service.getRequestsEndingAt(end);
        service.getRequestsAfter(start);
        service.getRequestsBefore(end);
        service.getRequestsBetween(start, end);

        verify(requests).findAllBetween(any(), eq(start), eq(end));
        verify(requests).findAllByStatus(any(), eq(EventRequestStatus.PENDING));
    }

    @Test
    void resourceQueriesShouldCombineAndDispatchRepositoryResults() {
        SpaceRepository spaces = mock(SpaceRepository.class);
        EquipmentRepository equipment = mock(EquipmentRepository.class);
        ServiceRepository services = mock(ServiceRepository.class);
        VenueRepository venues = mock(VenueRepository.class);
        ResourceService service = new ResourceService(create(), spaces, equipment, services, venues,
                mock(EventRepository.class));
        Space space = TestDataFactory.createDefaultSpace("Hall", 1).withId(2);
        Equipment item = TestDataFactory.createVenueEquipment("Projector", 1).withId(3);
        venue.event.manager.domain.model.resource.Service support = TestDataFactory.createDefaultService("Support").withId(4);
        when(venues.findById(any(), eq(1L))).thenReturn(Optional.of(TestDataFactory.createDefaultVenue("Venue").withId(1)));
        when(spaces.findAll(any())).thenReturn(List.of(space));
        when(equipment.findAll(any())).thenReturn(List.of(item));
        when(services.findAll(any())).thenReturn(List.of(support));
        when(spaces.searchByName(any(), eq("x"))).thenReturn(List.of(space));
        when(equipment.searchByName(any(), eq("x"))).thenReturn(List.of(item));
        when(services.searchByName(any(), eq("x"))).thenReturn(List.of(support));
        when(spaces.findById(any(), eq(2L))).thenReturn(Optional.of(space));
        when(equipment.findById(any(), eq(3L))).thenReturn(Optional.of(item));
        when(services.findById(any(), eq(4L))).thenReturn(Optional.of(support));
        when(spaces.findAllByVenueId(any(), eq(1L))).thenReturn(List.of(space));
        when(equipment.findAllByVenueId(any(), eq(1L))).thenReturn(List.of(item));

        assertEquals(List.of(space, support, item), service.getAllResources());
        assertEquals(List.of(space, support, item), service.searchResourceByName("x"));
        assertEquals(space, service.getResourceById(ResourceType.SPACE, 2));
        assertEquals(item, service.getResourceById(ResourceType.EQUIPMENT, 3));
        assertEquals(support, service.getResourceById(ResourceType.SERVICE, 4));
        assertEquals(List.of(space, item), service.getResourcesByVenue(1));
        assertEquals(List.of(space), service.getSpaceByVenue(1));
        assertEquals(List.of(item), service.getEquipmentByVenue(1));
    }

    @Test
    void feedbackQueriesShouldDelegateAndReturnAggregates() {
        ReviewRepository reviews = mock(ReviewRepository.class);
        ReviewService reviewService = new ReviewService(create(), reviews, mock(EventRepository.class),
                mock(BookingRepository.class));
        Review review = TestDataFactory.createDefaultReview(1, 2).withId(3);
        when(reviews.findAll(any())).thenReturn(List.of(review));
        when(reviews.findById(any(), eq(3L))).thenReturn(Optional.of(review));
        when(reviews.findByUserIdAndEventId(any(), eq(1L), eq(2L))).thenReturn(Optional.of(review));
        when(reviews.getAverageRatingGivenByUser(any(), eq(1L))).thenReturn(4.0);
        when(reviews.getAverageRatingByEvent(any(), eq(2L))).thenReturn(4.5);
        assertEquals(List.of(review), reviewService.getAllReviews());
        assertEquals(review, reviewService.getReview(3));
        reviewService.getReviewsByUser(1);
        reviewService.getReviewsForEvent(2);
        reviewService.getReviewsWithRating(5);
        assertEquals(review, reviewService.getReviewByUserAndEvent(1, 2));
        assertEquals(4.0, reviewService.getAverageRatingGivenByUser(1));
        assertEquals(4.5, reviewService.getAverageRatingByEvent(2));

        ReportRepository reports = mock(ReportRepository.class);
        ReportService reportService = new ReportService(create(), reports, mock(EventRepository.class),
                mock(UserRepository.class));
        Report report = TestDataFactory.createDefaultReport(1, 8, 2L).withId(4);
        when(reports.findAll(any())).thenReturn(List.of(report));
        when(reports.findById(any(), eq(4L))).thenReturn(Optional.of(report));
        when(reports.findByUserIdAndEventId(any(), eq(1L), eq(2L))).thenReturn(Optional.of(report));
        assertEquals(List.of(report), reportService.getAllReports());
        assertEquals(report, reportService.getReport(4));
        reportService.getReportsByUser(1);
        reportService.getReportsByAdmin(8);
        reportService.getReportsForEvent(2);
        reportService.getReportsBySeverity(ReportSeverity.MIDDLE);
        assertEquals(report, reportService.getReportByUserAndEvent(1, 2));
        reportService.getReportsByAdminAndEvent(8, 2);
    }

    @Test
    void userVenueAndGuestQueriesShouldDelegateCorrectly() {
        UserRepository users = mock(UserRepository.class);
        var transactionManager = create();
        UserService userService = new UserService(transactionManager, users,
                new AuthService(transactionManager, users, new PasswordHasher()));
        User user = TestDataFactory.createDefaultUser("user").withId(1);
        when(users.findAll(any())).thenReturn(List.of(user));
        when(users.findById(any(), eq(1L))).thenReturn(Optional.of(user));
        when(users.findByEmail(any(), eq(user.getEmail()))).thenReturn(Optional.of(user));
        when(users.findByUsername(any(), eq(user.getUsername()))).thenReturn(Optional.of(user));
        when(users.findByPhone(any(), eq(user.getPhone()))).thenReturn(Optional.of(user));
        when(users.getAverageRatingGivenByUser(any(), eq(1L))).thenReturn(Optional.of(4.0));
        assertEquals(List.of(user), userService.findAll());
        assertEquals(user, userService.getById(1));
        assertEquals(user, userService.getByEmail(user.getEmail()));
        assertEquals(user, userService.getByUsername(user.getUsername()));
        assertEquals(user, userService.getByPhone(user.getPhone()));
        userService.getAdmins();
        userService.getBannedUsers();
        userService.getAccountsWithStatus(AccountStatus.ACTIVE);
        assertEquals(Optional.of(4.0), userService.getAverageRatingGivenByUser(1));

        VenueRepository venues = mock(VenueRepository.class);
        VenueService venueService = new VenueService(create(), venues);
        Venue venue = TestDataFactory.createDefaultVenue("Venue").withId(2);
        when(venues.findAll(any())).thenReturn(List.of(venue));
        when(venues.findById(any(), eq(2L))).thenReturn(Optional.of(venue));
        assertEquals(List.of(venue), venueService.getAllVenues());
        assertEquals(venue, venueService.getVenue(2));
        venueService.searchVenueByName("Venue");
        venueService.getVenuesByCity("Prato");
        venueService.getVenuesByCountry("Italia");
        LocalDateTime begin = LocalDateTime.now().plusDays(1);
        venueService.getVenuesWithAvailableSpaces(begin, begin.plusHours(2));

        EventGuestRepository guests = mock(EventGuestRepository.class);
        EventRepository events = mock(EventRepository.class);
        EventGuestService guestService = new EventGuestService(create(), guests, events);
        EventGuest guest = TestDataFactory.createDefaultGuest("Mario", "Rossi", 2).withId(5);
        when(guests.findById(any(), eq(5L))).thenReturn(Optional.of(guest));
        when(events.findById(any(), eq(2L))).thenReturn(Optional.of(futureEvent()));
        assertEquals(guest, guestService.getGuestById(5));
        guestService.getGuestsForEvent(2);
        guestService.getGuestsForEventWithStatus(2, EventGuestStatus.INVITED);
        verify(guests).findAllByEventIdAndStatus(any(), eq(2L), eq(EventGuestStatus.INVITED));
    }

    private static Event futureEvent() {
        LocalDateTime begin = LocalDateTime.now().plusDays(5);
        return new Event(2, 1, null, "Future", "Description", begin, begin.plusHours(2), null,
                100, EventStatus.PUBLISHED, EventVisibility.PUBLIC, java.math.BigDecimal.TEN, LocalDateTime.now());
    }
}
