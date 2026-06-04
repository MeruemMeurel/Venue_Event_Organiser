package Venue_Event_Manager.service;

import Venue_Event_Manager.config.TransactionManager;
import Venue_Event_Manager.domain.model.event.Event;
import Venue_Event_Manager.domain.model.booking.Booking;
import Venue_Event_Manager.domain.model.booking.BookingStatus;
import Venue_Event_Manager.domain.model.feedback.Review;
import Venue_Event_Manager.exception.ValidationException;
import Venue_Event_Manager.exception.NotFoundException;
import Venue_Event_Manager.repository.ReviewRepository;
import Venue_Event_Manager.repository.EventRepository;
import Venue_Event_Manager.repository.BookingRepository;
import Venue_Event_Manager.exception.ConflictException;
import Venue_Event_Manager.exception.ForbiddenException;
import java.time.LocalDateTime;
import java.util.List;

public class ReviewService {
    private TransactionManager transactionManager;
    private ReviewRepository reviewRepository;
    private EventRepository eventRepository;
    private BookingRepository bookingRepository;

    public ReviewService(ReviewRepository reviewRepository, EventRepository eventRepository, BookingRepository bookingRepository) {
        this.transactionManager = TransactionManager.getInstance();
        this.reviewRepository = reviewRepository;
        this.eventRepository = eventRepository;
        this.bookingRepository = bookingRepository;
    }

    /**
     * Retrieves all reviews in the system.
     *
     * @return a list of all reviews
     */
    public List<Review> getAllReviews(){
        return transactionManager.inReadOnly(conn ->
                reviewRepository.findAll(conn) );
    }

    /**
     * Retrieves a specific review by its ID.
     *
     * @param reviewId the ID of the review to retrieve
     * @return the review matching the given ID
     * @throws NotFoundException if no review is found with the given ID
     */
    public Review getReview(long reviewId){
        return transactionManager.inReadOnly(conn ->
                reviewRepository.findById(conn, reviewId))
                    .orElseThrow(() -> new NotFoundException("Review with id "
                            + reviewId + " not found" ));
    }

    /**
     * Retrieves all reviews written by a specific user.
     *
     * @param userId the ID of the user
     * @return a list of reviews written by the user
     */
    public List<Review> getReviewsByUser(long userId){
        return transactionManager.inReadOnly(conn ->
                reviewRepository.findAllByUserId(conn, userId));
    }

    /**
     * Retrieves all reviews written for a specific event.
     *
     * @param eventId the ID of the event
     * @return a list of reviews for the event
     */
    public List<Review> getReviewsForEvent(long eventId){
        return transactionManager.inReadOnly(conn ->
                reviewRepository.findAllByEventId(conn, eventId));
    }

    /**
     * Retrieves all reviews matching a specific rating score.
     *
     * @param rating the rating value (1 to 5)
     * @return a list of reviews with the matching rating
     */
    public List<Review> getReviewsWithRating(int rating){
        return transactionManager.inReadOnly(conn ->
                reviewRepository.findAllByRating(conn, rating));
    }

    /**
     * Retrieves a single review written by a specific user for a specific event.
     *
     * @param userId  the ID of the user
     * @param eventId the ID of the event
     * @return the matching review
     * @throws NotFoundException if no review is found for the user and event
     */
    public Review getReviewByUserAndEvent (long userId, long eventId){
        return transactionManager.inReadOnly(conn ->
                reviewRepository.findByUserIdAndEventId(conn, userId, eventId))
                    .orElseThrow(() -> new NotFoundException("Review from user with id " + userId +
                            " for event " + eventId + " not found" ));
    }

    /**
     * Calculates the average rating given by a user.
     *
     * @param userId the ID of the user
     * @return the user's average rating
     */
    public double getAverageRatingByUser(long userId){
       return transactionManager.inReadOnly(conn ->
                reviewRepository.getAverageRatingByUser(conn, userId));
    }

    /**
     * Calculates the average rating received by an event.
     *
     * @param eventId the ID of the event
     * @return the event's average rating
     */
    public double getAverageRatingByEvent(long eventId){
        return transactionManager.inReadOnly(conn ->
                reviewRepository.getAverageRatingByEvent(conn, eventId));
    }

    /**
     * Deletes a review from the system.
     *
     * @param reviewId the ID of the review to delete
     * @throws NotFoundException if the review is not found
     */
    public void deleteReview(long reviewId){
        transactionManager.inTransaction(conn -> {
            reviewRepository.findById(conn, reviewId)
                        .orElseThrow(() -> new NotFoundException("Review with id " + reviewId
                                + " not found" ));

                    reviewRepository.deleteById(conn, reviewId);

                    return null;
                });
    }

    /**
     * Helper method to validate if an event has ended.
     *
     * @param event the event to check
     * @throws ValidationException if the event is still ongoing
     */
    private void validateEventEnded(Event event){
        if(event.getEndDatetime().isAfter(LocalDateTime.now())){
            throw new ValidationException("Cannot review an event that hasn't ended yet.");
        }
    }

    /**
     * Adds a new review to an event, enforcing validation constraints and business rules.
     *
     * @param review the review details to insert
     * @return the generated ID of the new review
     * @throws ValidationException if the rating is invalid or the event has not ended yet
     * @throws NotFoundException   if the referenced event does not exist
     * @throws ForbiddenException  if the user does not have a confirmed booking for the event
     * @throws ConflictException   if the user has already reviewed this event
     */
    public long addReview(Review review){
        return transactionManager.inTransaction(conn -> {
            //check if rating for the event are ok
            if (review.getRating() < 1 || review.getRating() > 5) {
                throw new ValidationException("Review rating must be between 1 and 5");
            }
            //check if there's an event with such id
            Event event = eventRepository.findById(conn, review.getEventId())
                    .orElseThrow(() -> new NotFoundException("Event with id "
                            + review.getEventId() + " not found"));
            //check if event has ended
            validateEventEnded(event);
            //get all the bookings with corresponding event and user id
            List<Booking> bookings=bookingRepository.findAllByUserIdAndEventId(conn,
                    review.getUserId(), review.getEventId());
            //check if atleast one is confirmed, if not throw an exception
            boolean hasConfirmedBooking = false;
            for(Booking booking : bookings){
               if (booking.getStatus() == BookingStatus.CONFIRMED){
                   hasConfirmedBooking = true;
                   break;
               }
            }
            if(!hasConfirmedBooking){
                throw new ForbiddenException("User did not attend the event.");
            }
            //check if user has already reviewed the event
            if (reviewRepository.findByUserIdAndEventId(conn, review.getUserId(), review.getEventId()).isPresent()){
                throw new ConflictException("User has already review this event.");
            }
            Review finalReview = review.withCreatedAt(LocalDateTime.now());
            return reviewRepository.insert(conn, finalReview);
        });
    }
}
