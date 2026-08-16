package venue.event.manager.service;

import venue.event.manager.domain.model.booking.BookingStatus;
import venue.event.manager.domain.model.event.EventGuestStatus;
import venue.event.manager.domain.model.event.EventStatus;
import venue.event.manager.exception.ConflictException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

class StatusTransitionTest {

    @Test
    void bookingAllowsExpectedTransitions() {
        assertDoesNotThrow(() -> BookingService.validateBookingStatusTransition(
                BookingStatus.PENDING_PAYMENT,BookingStatus.CONFIRMED));
        assertDoesNotThrow(() -> BookingService.validateBookingStatusTransition(
                BookingStatus.PENDING_PAYMENT,BookingStatus.CANCELLED));
        assertDoesNotThrow(() -> BookingService.validateBookingStatusTransition(
                BookingStatus.CONFIRMED,BookingStatus.CANCELLED));
    }

    @Test
    void bookingRejectsReactivationAndDuplicateTransitions() {
        assertThrows(ConflictException.class, () -> BookingService.validateBookingStatusTransition(
                BookingStatus.CANCELLED,BookingStatus.CONFIRMED));
        assertThrows(ConflictException.class, () -> BookingService.validateBookingStatusTransition(
                BookingStatus.CONFIRMED,BookingStatus.CONFIRMED));
    }

    @Test
    void eventAllowsExpectedTransitions() {
        assertDoesNotThrow(() -> EventService.validateEventStatusTransition(
                EventStatus.DRAFT,EventStatus.CONFIRMED));
        assertDoesNotThrow(() -> EventService.validateEventStatusTransition(
                EventStatus.DRAFT,EventStatus.CANCELLED));
        assertDoesNotThrow(() -> EventService.validateEventStatusTransition(
                EventStatus.CONFIRMED,EventStatus.PUBLISHED));
        assertDoesNotThrow(() -> EventService.validateEventStatusTransition(
                EventStatus.CONFIRMED,EventStatus.CANCELLED));
        assertDoesNotThrow(() -> EventService.validateEventStatusTransition(
                EventStatus.PUBLISHED,EventStatus.CANCELLED));
    }

    @Test
    void eventRejectsBackwardAndDuplicateTransitions() {
        assertThrows(ConflictException.class, () -> EventService.validateEventStatusTransition(
                EventStatus.PUBLISHED,EventStatus.CONFIRMED));
        assertThrows(ConflictException.class, () -> EventService.validateEventStatusTransition(
                EventStatus.CANCELLED,EventStatus.PUBLISHED));
        assertThrows(ConflictException.class, () -> EventService.validateEventStatusTransition(
                EventStatus.DRAFT,EventStatus.DRAFT));
    }

    @Test
    void guestAllowsExpectedTransitions() {
        assertDoesNotThrow(() -> EventGuestService.validateGuestStatusTransition(
                EventGuestStatus.INVITED,EventGuestStatus.CONFIRMED));
        assertDoesNotThrow(() -> EventGuestService.validateGuestStatusTransition(
                EventGuestStatus.INVITED,EventGuestStatus.CANCELLED));
        assertDoesNotThrow(() -> EventGuestService.validateGuestStatusTransition(
                EventGuestStatus.CONFIRMED,EventGuestStatus.CANCELLED));
    }

    @Test
    void guestRejectsReactivationAndDuplicateTransitions() {
        assertThrows(ConflictException.class, () -> EventGuestService.validateGuestStatusTransition(
                EventGuestStatus.CANCELLED,EventGuestStatus.CONFIRMED));
        assertThrows(ConflictException.class, () -> EventGuestService.validateGuestStatusTransition(
                EventGuestStatus.INVITED,EventGuestStatus.INVITED));
    }
}
