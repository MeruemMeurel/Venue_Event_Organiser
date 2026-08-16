package Venue_Event_Manager.repository;

import Venue_Event_Manager.domain.model.feedback.Review;
import java.sql.Connection;
import java.util.List;
import java.util.Optional;

/** Defines persistence operations for the corresponding domain model. */
public interface ReviewRepository {

    /**
     * Performs the {@code findAll} repository operation.
     * @param conn conn value
     * @return operation result
     */
    List<Review> findAll(Connection conn);

    /**
     * Performs the {@code findById} repository operation.
     * @param conn conn value
     * @param reviewId reviewId value
     * @return operation result
     */
    Optional<Review> findById(Connection conn, long reviewId);

    /**
     * Performs the {@code findAllByUserId} repository operation.
     * @param conn conn value
     * @param userId userId value
     * @return operation result
     */
    List<Review> findAllByUserId(Connection conn, long userId);

    /**
     * Performs the {@code findAllByEventId} repository operation.
     * @param conn conn value
     * @param eventId eventId value
     * @return operation result
     */
    List<Review> findAllByEventId(Connection conn, long eventId);

    /**
     * Performs the {@code findAllByRating} repository operation.
     * @param conn conn value
     * @param rating rating value
     * @return operation result
     */
    List<Review> findAllByRating(Connection conn, int rating);

    /**
     * Performs the {@code findByUserIdAndEventId} repository operation.
     * @param conn conn value
     * @param userId userId value
     * @param eventId eventId value
     * @return operation result
     */
    Optional<Review> findByUserIdAndEventId(Connection conn, long userId, long eventId);

    /**
     * Performs the {@code getAverageRatingGivenByUser} repository operation.
     * @param conn conn value
     * @param userId userId value
     * @return operation result
     */
    double getAverageRatingGivenByUser(Connection conn, long userId);

    /**
     * Performs the {@code getAverageRatingByEvent} repository operation.
     * @param conn conn value
     * @param eventId eventId value
     * @return operation result
     */
    double getAverageRatingByEvent(Connection conn, long eventId);

    /**
     * Performs the {@code insert} repository operation.
     * @param conn conn value
     * @param review review value
     * @return operation result
     */
    long insert(Connection conn, Review review);

    /**
     * Performs the {@code update} repository operation.
     * @param conn conn value
     * @param review review value
     */
    void update(Connection conn, Review review);

    /**
     * Performs the {@code deleteById} repository operation.
     * @param conn conn value
     * @param reviewId reviewId value
     */
    void deleteById(Connection conn, long reviewId);

}
