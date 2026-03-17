package Venue_Event_Manager.repository;

import Venue_Event_Manager.domain.model.feedback.Review;
import java.sql.Connection;
import java.util.List;
import java.util.Optional;

public interface ReviewRepository {

    List<Review> findAll(Connection conn);

    Optional<Review> findById(Connection conn, long reviewId);

    List<Review> findAllByUserId(Connection conn, long userId);

    List<Review> findAllByEventId(Connection conn, long eventId);

    List<Review> findAllByRating(Connection conn, int rating);

    Optional<Review> findByUserIdAndEventId(Connection conn, long userId, long eventId);

    double getAverageRatingByUser(Connection conn, long userId);

    double getAverageRatingByEvent(Connection conn, long eventId);

    long insert(Connection conn, Review review);

    void update(Connection conn, Review review);

    void deleteById(Connection conn, long reviewId);

}