package venue.event.manager.service;

import venue.event.manager.domain.model.booking.*;
import venue.event.manager.domain.model.event.*;
import venue.event.manager.domain.model.feedback.*;
import venue.event.manager.domain.model.resource.*;
import venue.event.manager.domain.model.user.User;
import venue.event.manager.domain.model.venue.Venue;
import venue.event.manager.exception.*;
import venue.event.manager.repository.*;
import venue.event.manager.util.TestDataFactory;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.math.BigDecimal;
import java.sql.Connection;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static venue.event.manager.util.TestTransactionManagerFactory.create;

class SecondaryServiceWorkflowTest {

    @Test
    void attendedUserShouldCreateReviewWithServerCreationTime() {
        ReviewRepository reviews = mock(ReviewRepository.class);
        EventRepository events = mock(EventRepository.class);
        BookingRepository bookings = mock(BookingRepository.class);
        ReviewService service = new ReviewService(create(), reviews, events, bookings);
        when(events.findById(any(Connection.class), eq(2L))).thenReturn(Optional.of(endedEvent()));
        when(bookings.findAllByUserIdAndEventId(any(Connection.class), eq(1L), eq(2L)))
                .thenReturn(List.of(TestDataFactory.createDefaultBooking(1, 2)
                        .withStatus(BookingStatus.CONFIRMED)));
        when(reviews.findByUserIdAndEventId(any(Connection.class), eq(1L), eq(2L)))
                .thenReturn(Optional.empty());
        when(reviews.insert(any(Connection.class), any())).thenReturn(10L);

        LocalDateTime before = LocalDateTime.now();
        assertEquals(10L, service.addReview(1, TestDataFactory.createDefaultReview(1, 2).withCreatedAt(null)));
        ArgumentCaptor<Review> captor = ArgumentCaptor.forClass(Review.class);
        verify(reviews).insert(any(Connection.class), captor.capture());
        assertFalse(captor.getValue().getCreatedAt().isBefore(before));
    }

    @Test
    void reviewShouldRequireAttendanceAndUniqueness() {
        ReviewRepository reviews = mock(ReviewRepository.class);
        EventRepository events = mock(EventRepository.class);
        BookingRepository bookings = mock(BookingRepository.class);
        ReviewService service = new ReviewService(create(), reviews, events, bookings);
        when(events.findById(any(Connection.class), anyLong())).thenReturn(Optional.of(endedEvent()));
        when(bookings.findAllByUserIdAndEventId(any(Connection.class), anyLong(), anyLong()))
                .thenReturn(List.of(TestDataFactory.createDefaultBooking(1, 2)
                        .withStatus(BookingStatus.CANCELLED)));
        assertThrows(ForbiddenException.class,
                () -> service.addReview(1, TestDataFactory.createDefaultReview(1, 2)));
        verify(reviews, never()).insert(any(), any());

        when(bookings.findAllByUserIdAndEventId(any(Connection.class), anyLong(), anyLong()))
                .thenReturn(List.of(TestDataFactory.createDefaultBooking(1, 2)
                        .withStatus(BookingStatus.CONFIRMED)));
        when(reviews.findByUserIdAndEventId(any(Connection.class), anyLong(), anyLong()))
                .thenReturn(Optional.of(TestDataFactory.createDefaultReview(1, 2).withId(3)));
        assertThrows(ConflictException.class,
                () -> service.addReview(1, TestDataFactory.createDefaultReview(1, 2)));
        verify(reviews, never()).insert(any(), any());
    }

    @Test
    void reviewUpdateShouldProtectOwnershipFields() {
        ReviewRepository reviews = mock(ReviewRepository.class);
        ReviewService service = new ReviewService(create(), reviews, mock(EventRepository.class),
                mock(BookingRepository.class));
        Review stored = TestDataFactory.createDefaultReview(1, 2).withId(3);
        when(reviews.findById(any(Connection.class), eq(3L))).thenReturn(Optional.of(stored));
        assertThrows(ValidationException.class, () -> service.updateReview(1, stored.withUserId(9)));
        verify(reviews, never()).update(any(), any());
        service.updateReview(1, stored.withComment("Updated"));
        verify(reviews).update(any(Connection.class), eq(stored.withComment("Updated")));
    }

    @Test
    void onlyAuthorShouldCreateUpdateOrDeleteReview() {
        ReviewRepository reviews = mock(ReviewRepository.class);
        ReviewService service = new ReviewService(create(), reviews, mock(EventRepository.class),
                mock(BookingRepository.class));
        Review stored = TestDataFactory.createDefaultReview(1, 2).withId(3);

        assertThrows(ForbiddenException.class, () -> service.addReview(9, stored.withId(0)));

        when(reviews.findById(any(Connection.class), eq(3L))).thenReturn(Optional.of(stored));
        assertThrows(ForbiddenException.class, () -> service.updateReview(9, stored.withComment("Forbidden")));
        assertThrows(ForbiddenException.class, () -> service.deleteReview(9, 3));
        verify(reviews, never()).update(any(), any());
        verify(reviews, never()).deleteById(any(), anyLong());

        service.deleteReview(1, 3);
        verify(reviews).deleteById(any(Connection.class), eq(3L));
    }

    @Test
    void adminShouldCreateReportWithServerCreationTime() {
        ReportRepository reports = mock(ReportRepository.class);
        EventRepository events = mock(EventRepository.class);
        UserRepository users = mock(UserRepository.class);
        ReportService service = new ReportService(create(), reports, events, users);
        when(users.findById(any(Connection.class), eq(2L)))
                .thenReturn(Optional.of(TestDataFactory.createDefaultUser("target").withId(2)));
        when(users.findById(any(Connection.class), eq(8L)))
                .thenReturn(Optional.of(TestDataFactory.createAdminUser("admin").withId(8)));
        when(events.findById(any(Connection.class), eq(3L))).thenReturn(Optional.of(endedEvent().withId(3)));
        when(reports.insert(any(Connection.class), any())).thenReturn(14L);
        LocalDateTime before = LocalDateTime.now();
        assertEquals(14L, service.addReport(8, TestDataFactory.createDefaultReport(2, 8, 3L).withCreatedAt(null)));
        ArgumentCaptor<Report> captor = ArgumentCaptor.forClass(Report.class);
        verify(reports).insert(any(Connection.class), captor.capture());
        assertFalse(captor.getValue().getCreatedAt().isBefore(before));
    }

    @Test
    void reportShouldRequireAdminAndNonAdminTarget() {
        ReportRepository reports = mock(ReportRepository.class);
        UserRepository users = mock(UserRepository.class);
        ReportService service = new ReportService(create(), reports, mock(EventRepository.class), users);
        when(users.findById(any(Connection.class), eq(8L)))
                .thenReturn(Optional.of(TestDataFactory.createDefaultUser("not_admin").withId(8)));
        assertThrows(ForbiddenException.class,
                () -> service.addReport(8, TestDataFactory.createDefaultReport(2, 8, null)));
        verify(reports, never()).insert(any(), any());

        when(users.findById(any(Connection.class), eq(8L)))
                .thenReturn(Optional.of(TestDataFactory.createAdminUser("admin").withId(8)));
        when(users.findById(any(Connection.class), eq(2L)))
                .thenReturn(Optional.of(TestDataFactory.createAdminUser("target_admin").withId(2)));
        assertThrows(ValidationException.class,
                () -> service.addReport(8, TestDataFactory.createDefaultReport(2, 8, null)));
        verify(reports, never()).insert(any(), any());
    }

    @Test
    void reportOperationsShouldRequireAuthenticatedAdminIdentity() {
        ReportRepository reports = mock(ReportRepository.class);
        UserRepository users = mock(UserRepository.class);
        ReportService service = new ReportService(create(), reports, mock(EventRepository.class), users);
        when(users.findById(any(Connection.class), eq(7L)))
                .thenReturn(Optional.of(TestDataFactory.createDefaultUser("ordinary").withId(7)));
        when(users.findById(any(Connection.class), eq(9L)))
                .thenReturn(Optional.of(TestDataFactory.createAdminUser("other_admin").withId(9)));

        assertThrows(ForbiddenException.class, () -> service.getAllReports(7));
        Report stored = TestDataFactory.createDefaultReport(2, 8, null).withId(3);
        assertThrows(ForbiddenException.class, () -> service.updateReport(7, stored));
        assertThrows(ForbiddenException.class, () -> service.deleteReport(7, 3));
        assertThrows(ForbiddenException.class,
                () -> service.addReport(9, TestDataFactory.createDefaultReport(2, 8, null)));
        verify(reports, never()).findAll(any());
        verify(reports, never()).insert(any(), any());
    }

    @Test
    void resourceServiceShouldDispatchCrudByConcreteType() {
        SpaceRepository spaces = mock(SpaceRepository.class);
        EquipmentRepository equipment = mock(EquipmentRepository.class);
        ServiceRepository services = mock(ServiceRepository.class);
        VenueRepository venues = mock(VenueRepository.class);
        when(venues.findById(any(Connection.class), eq(1L)))
                .thenReturn(Optional.of(TestDataFactory.createDefaultVenue("Venue").withId(1)));
        ResourceService service = new ResourceService(create(), spaces, equipment, services, venues,
                mock(EventRepository.class));
        Space space = TestDataFactory.createDefaultSpace("Hall", 1);
        Equipment item = TestDataFactory.createVenueEquipment("Projector", 1);
        venue.event.manager.domain.model.resource.Service support = TestDataFactory.createDefaultService("Support");
        when(spaces.insert(any(Connection.class), eq(space))).thenReturn(1L);
        when(equipment.insert(any(Connection.class), eq(item))).thenReturn(2L);
        when(services.insert(any(Connection.class), eq(support))).thenReturn(3L);
        assertAll(
                () -> assertEquals(1L, service.create(space)),
                () -> assertEquals(2L, service.create(item)),
                () -> assertEquals(3L, service.create(support))
        );
        verify(spaces).insert(any(Connection.class), eq(space));
        verify(equipment).insert(any(Connection.class), eq(item));
        verify(services).insert(any(Connection.class), eq(support));

        Space storedSpace = space.withId(11);
        Equipment storedItem = item.withId(12);
        venue.event.manager.domain.model.resource.Service storedSupport = support.withId(13);
        service.update(storedSpace);
        service.update(storedItem);
        service.update(storedSupport);
        verify(spaces).update(any(Connection.class), eq(storedSpace));
        verify(equipment).update(any(Connection.class), eq(storedItem));
        verify(services).update(any(Connection.class), eq(storedSupport));

        service.delete(storedSpace);
        service.delete(ResourceType.EQUIPMENT, 12);
        service.delete(ResourceType.SERVICE, 13);
        verify(spaces).deleteById(any(Connection.class), eq(11L));
        verify(equipment).deleteById(any(Connection.class), eq(12L));
        verify(services).deleteById(any(Connection.class), eq(13L));
    }

    @Test
    void availableResourcesShouldAggregateAllRepositoryResults() {
        SpaceRepository spaces = mock(SpaceRepository.class);
        EquipmentRepository equipment = mock(EquipmentRepository.class);
        ServiceRepository services = mock(ServiceRepository.class);
        EventRepository events = mock(EventRepository.class);
        ResourceService service = new ResourceService(create(), spaces, equipment, services,
                mock(VenueRepository.class), events);
        Event event = endedEvent().withId(2);
        Space space = TestDataFactory.createDefaultSpace("Hall", event.getVenueId());
        Equipment item = TestDataFactory.createVenueEquipment("Projector", event.getVenueId());
        venue.event.manager.domain.model.resource.Service support = TestDataFactory.createDefaultService("Support");
        when(events.findById(any(Connection.class), eq(2L))).thenReturn(Optional.of(event));
        when(spaces.findAvailableSpaces(any(Connection.class), eq(event.getVenueId()), any(), any()))
                .thenReturn(List.of(space));
        when(equipment.findAvailableEquipment(any(Connection.class), eq(event.getVenueId()), any(), any()))
                .thenReturn(List.of(item));
        when(services.findAvailableServicesForEvent(any(Connection.class), eq(2L))).thenReturn(List.of(support));
        assertEquals(List.of(space, item, support), service.getAvailableResourcesForEvent(2));
    }

    @Test
    void venueServiceShouldForwardValidatedCrudOperations() {
        VenueRepository venues = mock(VenueRepository.class);
        VenueService service = new VenueService(create(), venues);
        Venue newVenue = TestDataFactory.createDefaultVenue("New venue");
        Venue stored = newVenue.withId(5);
        when(venues.insert(any(Connection.class), eq(newVenue))).thenReturn(5L);
        assertEquals(5L, service.createVenue(newVenue));
        service.updateVenue(stored);
        service.deleteVenue(5);
        verify(venues).update(any(Connection.class), eq(stored));
        verify(venues).deleteById(any(Connection.class), eq(5L));
    }

    private Event endedEvent() {
        LocalDateTime end = LocalDateTime.now().minusDays(1);
        return new Event(2, 1, null, "Ended", "Description", end.minusHours(2), end, null,
                50, EventStatus.PUBLISHED, EventVisibility.PUBLIC, BigDecimal.TEN, LocalDateTime.now().minusDays(10));
    }
}
