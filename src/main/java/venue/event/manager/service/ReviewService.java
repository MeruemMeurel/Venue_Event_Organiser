package venue.event.manager.service;

import venue.event.manager.config.TransactionManager;
import venue.event.manager.domain.model.booking.Booking;
import venue.event.manager.domain.model.booking.BookingStatus;
import venue.event.manager.domain.model.event.Event;
import venue.event.manager.domain.model.feedback.Review;
import venue.event.manager.exception.ConflictException;
import venue.event.manager.exception.ForbiddenException;
import venue.event.manager.exception.NotFoundException;
import venue.event.manager.exception.ValidationException;
import venue.event.manager.repository.BookingRepository;
import venue.event.manager.repository.EventRepository;
import venue.event.manager.repository.ReviewRepository;

import java.sql.Connection;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

/** Coordinates review eligibility, validation and persistence. */
public class ReviewService {

    private final TransactionManager transactionManager;
    private final ReviewRepository reviewRepository;
    private final EventRepository eventRepository;
    private final BookingRepository bookingRepository;

    /**
     * Initializes ReviewService with all repositories needed to handle reviews.
     * @param reviewRepository repository used to access review data
     * @param eventRepository repository used to access event data
     * @param bookingRepository repository used to access booking data
     */
    public ReviewService(ReviewRepository reviewRepository, EventRepository eventRepository, BookingRepository bookingRepository) {
        this.transactionManager = TransactionManager.getInstance();
        this.reviewRepository = reviewRepository;
        this.eventRepository = eventRepository;
        this.bookingRepository = bookingRepository;
    }

    /**
     * Gets all reviews stored in database.
     * @return List of all reviews
     */
    public List<Review> getAllReviews(){
        return transactionManager.inReadOnly(conn ->
                reviewRepository.findAll(conn));
    }

    /**
     * Gets a review from its id.
     * @param reviewId the id of the review to find
     * @return Review object if found
     * @throws NotFoundException if no review is found with such id
     */
    public Review getReview(long reviewId){
        return transactionManager.inReadOnly(conn ->
                reviewRepository.findById(conn,reviewId)
                        .orElseThrow(() -> new NotFoundException("Review with id " + reviewId + " not found")));
    }

    /**
     * Gets all reviews written by a specific user.
     * @param userId the id of the user
     * @return List of reviews written by the user
     */
    public List<Review> getReviewsByUser(long userId){
        return transactionManager.inReadOnly(conn ->
                reviewRepository.findAllByUserId(conn,userId));
    }

    /**
     * Gets all reviews written for a specific event.
     * @param eventId the id of the event
     * @return List of reviews written for the event
     */
    public List<Review> getReviewsForEvent(long eventId){
        return transactionManager.inReadOnly(conn ->
                reviewRepository.findAllByEventId(conn,eventId));
    }

    /**
     * Gets all reviews with a specific rating.
     * @param rating the rating used to filter reviews
     * @return List of reviews with the given rating
     * @throws ValidationException if rating is not valid
     */
    public List<Review> getReviewsWithRating(int rating){
        validateRating(rating);

        return transactionManager.inReadOnly(conn ->
                reviewRepository.findAllByRating(conn,rating));
    }

    /**
     * Gets a review written by a user for a specific event.
     * @param userId the id of the user
     * @param eventId the id of the event
     * @return Review object if found
     * @throws NotFoundException if no review is found for such user and event
     */
    public Review getReviewByUserAndEvent(long userId, long eventId){
        return transactionManager.inReadOnly(conn ->
                reviewRepository.findByUserIdAndEventId(conn,userId,eventId)
                        .orElseThrow(() -> new NotFoundException("Review from user with id " + userId +
                                " for event " + eventId + " not found")));
    }

    /**
     * Gets average rating given by a user.
     * @param userId the id of the user
     * @return average rating given by the user
     */
    public double getAverageRatingGivenByUser(long userId){
        return transactionManager.inReadOnly(conn ->
                reviewRepository.getAverageRatingGivenByUser(conn,userId));
    }

    /**
     * Gets average rating received by an event.
     * @param eventId the id of the event
     * @return average rating received by the event
     */
    public double getAverageRatingByEvent(long eventId){
        return transactionManager.inReadOnly(conn ->
                reviewRepository.getAverageRatingByEvent(conn,eventId));
    }

    /**
     * Inserts a new review in database.
     * @param review the review to insert
     * @return generated id of the new review
     * @throws ValidationException if review data are not valid
     * @throws NotFoundException if event does not exist
     * @throws ForbiddenException if user did not attend the event
     * @throws ConflictException if user has already reviewed the event
     */
    public long addReview(Review review){
        validateReviewNotNull(review);

        return transactionManager.inTransaction(conn -> {
            validateForInsert(conn,review);
            Review finalReview = review.withCreatedAt(LocalDateTime.now());
            return reviewRepository.insert(conn,finalReview);
        });
    }

    /**
     * Updates an existing review in database.
     * @param review the review object with updated data
     * @throws ValidationException if review data or id are not valid
     */
    public void updateReview(Review review){
        validateForUpdate(review);

        transactionManager.inTransaction(conn -> {
            Review storedReview = reviewRepository.findById(conn,review.getId())
                    .orElseThrow(() -> new NotFoundException("Review with id " + review.getId() + " not found"));

            if(storedReview.getUserId() != review.getUserId()
                    || storedReview.getEventId() != review.getEventId()
                    || !Objects.equals(storedReview.getCreatedAt(),review.getCreatedAt())) {
                throw new ValidationException("Review user, event and creation date cannot be changed");
            }

            reviewRepository.update(conn,review);
            return null;
        });
    }

    /**
     * Deletes a review from database.
     * @param reviewId the id of the review to delete
     * @throws NotFoundException if no review is found with such id
     */
    public void deleteReview(long reviewId){
        transactionManager.inTransaction(conn -> {
            reviewRepository.findById(conn,reviewId)
                    .orElseThrow(() -> new NotFoundException("Review with id " + reviewId + " not found"));
            reviewRepository.deleteById(conn,reviewId);
            return null;
        });
    }

    /**
     * Validates review data before insert.
     * @param conn the db connection
     * @param review the review to validate
     * @throws ValidationException if one or more fields are not valid
     */
    private void validateForInsert(Connection conn, Review review){
        validateRating(review.getRating());
        validateComment(review.getComment());
        Event event = validateEventExists(conn,review.getEventId());
        validateEventEnded(event);
        validateUserAttendedEvent(conn,review.getUserId(),review.getEventId());
        validateUserHasNotReviewedEvent(conn,review.getUserId(),review.getEventId());
    }

    /**
     * Validates review data before update.
     * @param review the review to validate
     * @throws ValidationException if one or more fields are not valid
     */
    private void validateForUpdate(Review review){
        validateReviewNotNull(review);
        validateId(review.getId(),"Review id");
        validateId(review.getUserId(),"User id");
        validateId(review.getEventId(),"Event id");
        validateRating(review.getRating());
        validateComment(review.getComment());
        validateCreatedAt(review.getCreatedAt());
    }

    /**
     * Validates that review is not null.
     * @param review the review to validate
     * @throws ValidationException if review is null
     */
    private void validateReviewNotNull(Review review){
        if(review == null) throw new ValidationException("Review cannot be null");
    }

    /**
     * Validates positive id.
     * @param id the id to validate
     * @param label the name of the id field
     * @throws ValidationException if id is not valid
     */
    private void validateId(long id, String label){
        if(id <= 0) throw new ValidationException(label + " is not valid");
    }

    /**
     * Validates rating.
     * @param rating the rating to validate
     * @throws ValidationException if rating is outside accepted range
     */
    private void validateRating(int rating){
        if(rating < 1 || rating > 5) throw new ValidationException("Review rating must be between 1 and 5");
    }

    /**
     * Validates comment.
     * @param comment the comment to validate
     * @throws ValidationException if comment has invalid length
     */
    private void validateComment(String comment){
        if(comment != null && comment.length() > 1000) {
            throw new ValidationException("Review comment cannot exceed 1000 characters");
        }
    }

    /**
     * Validates created date.
     * @param createdAt the created date to validate
     * @throws ValidationException if created date is empty
     */
    private void validateCreatedAt(LocalDateTime createdAt){
        if(createdAt == null) throw new ValidationException("Review created date cannot be empty");
    }

    /**
     * Validates if event exists.
     * @param conn the db connection
     * @param eventId the id of the event to validate
     * @return Event object if found
     * @throws NotFoundException if no event is found with such id
     */
    private Event validateEventExists(Connection conn, long eventId){
        validateId(eventId,"Event id");
        return eventRepository.findById(conn,eventId)
                .orElseThrow(() -> new NotFoundException("Event with id " + eventId + " not found"));
    }

    /**
     * Validates if an event has ended.
     * @param event the event to validate
     * @throws ValidationException if event has not ended yet
     */
    private void validateEventEnded(Event event){
        if(event.getEndDatetime().isAfter(LocalDateTime.now())){
            throw new ValidationException("Cannot review an event that has not ended yet");
        }
    }

    /**
     * Validates if a user has a confirmed booking for an event.
     * @param conn the db connection
     * @param userId the id of the user
     * @param eventId the id of the event
     * @throws ForbiddenException if user has no confirmed booking for the event
     */
    private void validateUserAttendedEvent(Connection conn, long userId, long eventId){
        validateId(userId,"User id");
        List<Booking> bookings = bookingRepository.findAllByUserIdAndEventId(conn,userId,eventId);

        for(Booking booking : bookings){
            if(booking.getStatus() == BookingStatus.CONFIRMED) return;
        }

        throw new ForbiddenException("User did not attend the event");
    }

    /**
     * Validates that user has not already reviewed an event.
     * @param conn the db connection
     * @param userId the id of the user
     * @param eventId the id of the event
     * @throws ConflictException if review already exists
     */
    private void validateUserHasNotReviewedEvent(Connection conn, long userId, long eventId){
        if(reviewRepository.findByUserIdAndEventId(conn,userId,eventId).isPresent()){
            throw new ConflictException("User has already reviewed this event");
        }
    }
}
