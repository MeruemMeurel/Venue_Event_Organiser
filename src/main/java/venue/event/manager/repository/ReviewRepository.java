package venue.event.manager.repository;

import venue.event.manager.domain.model.feedback.Review;
import java.sql.Connection;
import java.util.List;
import java.util.Optional;

/** Defines persistence operations for the corresponding domain model. */
public interface ReviewRepository {

    /**
     * Returns all persisted records.
     * @param conn active database connection
     * @return result produced by the repository operation
     */
    List<Review> findAll(Connection conn);

    /**
     * Finds a persisted record by identifier.
     * @param conn active database connection
     * @param reviewId identifier used by the operation
     * @return result produced by the repository operation
     */
    Optional<Review> findById(Connection conn, long reviewId);

    /**
     * Returns persisted records filtered by user id.
     * @param conn active database connection
     * @param userId identifier used by the operation
     * @return result produced by the repository operation
     */
    List<Review> findAllByUserId(Connection conn, long userId);

    /**
     * Returns persisted records filtered by event id.
     * @param conn active database connection
     * @param eventId identifier used by the operation
     * @return result produced by the repository operation
     */
    List<Review> findAllByEventId(Connection conn, long eventId);

    /**
     * Returns persisted records filtered by rating.
     * @param conn active database connection
     * @param rating filter value
     * @return result produced by the repository operation
     */
    List<Review> findAllByRating(Connection conn, int rating);

    /**
     * Finds persisted records by user id and event id.
     * @param conn active database connection
     * @param userId identifier used by the operation
     * @param eventId identifier used by the operation
     * @return result produced by the repository operation
     */
    Optional<Review> findByUserIdAndEventId(Connection conn, long userId, long eventId);

    /**
     * Calculates the average rating submitted by a user.
     * @param conn active database connection
     * @param userId identifier used by the operation
     * @return result produced by the repository operation
     */
    double getAverageRatingGivenByUser(Connection conn, long userId);

    /**
     * Calculates the average rating received by an event.
     * @param conn active database connection
     * @param eventId identifier used by the operation
     * @return result produced by the repository operation
     */
    double getAverageRatingByEvent(Connection conn, long eventId);

    /**
     * Persists a new record.
     * @param conn active database connection
     * @param review record to persist
     * @return result produced by the repository operation
     */
    long insert(Connection conn, Review review);

    /**
     * Updates an existing record.
     * @param conn active database connection
     * @param review record to persist
     */
    void update(Connection conn, Review review);

    /**
     * Deletebyids the supplied persistent data.
     * @param conn active database connection
     * @param reviewId identifier used by the operation
     */
    void deleteById(Connection conn, long reviewId);

}
