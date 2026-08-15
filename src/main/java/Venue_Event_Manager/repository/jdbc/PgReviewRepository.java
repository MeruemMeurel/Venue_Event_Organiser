package Venue_Event_Manager.repository.jdbc;

import Venue_Event_Manager.domain.model.feedback.Review;
import Venue_Event_Manager.repository.ReviewRepository;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class PgReviewRepository implements ReviewRepository {

    /**
     * Lambda function to map review sql results to a Review object
     */
    private static final RowMapper<Review> review_mapper = rs -> new Review(
            rs.getLong("id"),
            rs.getLong("user_id"),
            rs.getLong("event_id"),
            rs.getInt("rating"),
            rs.getString("comment"),
            rs.getTimestamp("created_at").toLocalDateTime()
    );


    private static final String SQL_FIND_ALL = "SELECT id, user_id, event_id, rating, comment, created_at " +
                                               "FROM review";
    /**
     * Executes query to database to get all reviews
     * @param conn The database connection used
     * @return {@code List<Review>} object
     */
    @Override
    public List<Review> findAll(Connection conn) {
        List<Review> reviews = new ArrayList<>();
        try (PreparedStatement ps = conn.prepareStatement(SQL_FIND_ALL)) {

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    reviews.add(review_mapper.mapRow(rs));
                }
            }
            return reviews;
        } catch (SQLException e) {
            throw new DaoException("Error while trying to find all reviews", e);
        }
    }


    private static final String SQL_FIND_BY_ID = SQL_FIND_ALL + " WHERE id = ?";
    /**
     * Executes query to database to get review from its id
     * @param conn The database connection used
     * @param reviewId the id of the review
     * @return {@code Optional<Review>} object. Empty if not found
     */
    @Override
    public Optional<Review> findById(Connection conn, long reviewId) {
        try (PreparedStatement ps = conn.prepareStatement(SQL_FIND_BY_ID)) {
            ps.setLong(1, reviewId);

            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return Optional.empty();
                return Optional.of(review_mapper.mapRow(rs));
            }
        } catch (SQLException e) {
            throw new DaoException("Error while trying to find review with id: " + reviewId, e);
        }
    }


    private static final String SQL_FIND_ALL_BY_USER_ID = SQL_FIND_ALL + " WHERE user_id = ?";
    /**
     * Executes query to database to get all reviews from a specific user
     * @param conn The database connection used
     * @param userId the id of the user
     * @return {@code List<Review>} object
     */
    @Override
    public List<Review> findAllByUserId(Connection conn, long userId) {
        List<Review> reviews = new ArrayList<>();
        try (PreparedStatement ps = conn.prepareStatement(SQL_FIND_ALL_BY_USER_ID)) {
            ps.setLong(1, userId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    reviews.add(review_mapper.mapRow(rs));
                }
            }
            return reviews;
        } catch (SQLException e) {
            throw new DaoException("Error while trying to find reviews for user: " + userId, e);
        }
    }


    private static final String SQL_FIND_ALL_BY_EVENT_ID = SQL_FIND_ALL + " WHERE event_id = ?";
    /**
     * Executes query to database to get all reviews for a specific event
     * @param conn The database connection used
     * @param eventId the id of the event
     * @return {@code List<Review>} object
     */
    @Override
    public List<Review> findAllByEventId(Connection conn, long eventId) {
        List<Review> reviews = new ArrayList<>();
        try (PreparedStatement ps = conn.prepareStatement(SQL_FIND_ALL_BY_EVENT_ID)) {
            ps.setLong(1, eventId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    reviews.add(review_mapper.mapRow(rs));
                }
            }
            return reviews;
        } catch (SQLException e) {
            throw new DaoException("Error while trying to find reviews for event: " + eventId, e);
        }
    }


    private static final String SQL_FIND_ALL_BY_RATING = SQL_FIND_ALL + " WHERE rating = ?";
    /**
     * Executes query to database to get all reviews with a specific rating
     * @param conn The database connection used
     * @param rating the rating score
     * @return {@code List<Review>} object
     */
    @Override
    public List<Review> findAllByRating(Connection conn, int rating) {
        List<Review> reviews = new ArrayList<>();
        try (PreparedStatement ps = conn.prepareStatement(SQL_FIND_ALL_BY_RATING)) {
            ps.setInt(1, rating);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    reviews.add(review_mapper.mapRow(rs));
                }
            }
            return reviews;
        } catch (SQLException e) {
            throw new DaoException("Error while trying to find reviews with rating: " + rating, e);
        }
    }


    private static final String SQL_FIND_BY_USER_AND_EVENT = SQL_FIND_ALL + " WHERE user_id = ? AND event_id = ?";
    /**
     * Finds a review for a specific user and event
     * @param conn The database connection used
     * @param userId the user id
     * @param eventId the event id
     * @return {@code Optional<Review>} object
     */
    @Override
    public Optional<Review> findByUserIdAndEventId(Connection conn, long userId, long eventId) {
        try (PreparedStatement ps = conn.prepareStatement(SQL_FIND_BY_USER_AND_EVENT)) {
            ps.setLong(1, userId);
            ps.setLong(2, eventId);

            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return Optional.empty();
                return Optional.of(review_mapper.mapRow(rs));
            }
        } catch (SQLException e) {
            throw new DaoException("Error while trying to find review for user " + userId + " and event " + eventId, e);
        }
    }


    private static final String SQL_AVG_BY_USER = "SELECT AVG(rating) " +
                                                  "FROM review " +
                                                  "WHERE user_id = ?";
    /**
     * Calculates the average rating given by a user
     * @param conn The database connection used
     * @param userId the user id
     * @return double average rating
     */
    @Override
    public double getAverageRatingGivenByUser(Connection conn, long userId) {
        try (PreparedStatement ps = conn.prepareStatement(SQL_AVG_BY_USER)) {
            ps.setLong(1, userId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getDouble(1);
                return 0.0;
            }
        } catch (SQLException e) {
            throw new DaoException("Error calculating average rating for user: " + userId, e);
        }
    }


    private static final String SQL_AVG_BY_EVENT = "SELECT AVG(rating) " +
                                                   "FROM review " +
                                                   "WHERE event_id = ?";
    /**
     * Calculates the average rating received by an event
     * @param conn The database connection used
     * @param eventId the event id
     * @return double average rating
     */
    @Override
    public double getAverageRatingByEvent(Connection conn, long eventId) {
        try (PreparedStatement ps = conn.prepareStatement(SQL_AVG_BY_EVENT)) {
            ps.setLong(1, eventId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getDouble(1);
                return 0.0;
            }
        } catch (SQLException e) {
            throw new DaoException("Error calculating average rating for event: " + eventId, e);
        }
    }


    private static final String SQL_INSERT = "INSERT INTO review (user_id, event_id, rating, comment, created_at) " +
                                             "VALUES (?, ?, ?, ?, ?) RETURNING id";
    /**
     * Executes SQL Query to insert a new review
     * @param conn the connection to database
     * @param review the review object
     * @return long id of the new review
     */
    @Override
    public long insert(Connection conn, Review review) {
        try (PreparedStatement ps = conn.prepareStatement(SQL_INSERT)) {
            ps.setLong(1, review.getUserId());
            ps.setLong(2, review.getEventId());
            ps.setInt(3, review.getRating());
            JdbcUtils.setNullableString(ps, 4, review.getComment());
            ps.setTimestamp(5, Timestamp.valueOf(review.getCreatedAt()));

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getLong(1);
                throw new DaoException("Insert failed: no ID returned");
            }
        } catch (SQLException e) {
            throw new DaoException("Error while trying to insert review", e);
        }
    }


    private static final String SQL_UPDATE = "UPDATE review " +
                                             "SET rating = ?, comment = ? " +
                                             "WHERE id = ?";
    /**
     * Executes SQL Query to update an existing review
     * @param conn the connection to database
     * @param review the review with updated data
     */
    @Override
    public void update(Connection conn, Review review) {
        try (PreparedStatement ps = conn.prepareStatement(SQL_UPDATE)) {
            ps.setInt(1, review.getRating());
            JdbcUtils.setNullableString(ps, 2, review.getComment());
            ps.setLong(3, review.getId());

            int updated = ps.executeUpdate();
            JdbcUtils.requireUpdatedExactly(updated, 1, "updateReview(id=" + review.getId() + ")");
        } catch (SQLException e) {
            throw new DaoException("Error while trying to update review: " + review.getId(), e);
        }
    }


    private static final String SQL_DELETE = "DELETE FROM review " +
                                             "WHERE id = ?";
    /**
     * Deletes a review from database by its id
     * @param conn the database connection
     * @param reviewId the id of the review to delete
     */
    @Override
    public void deleteById(Connection conn, long reviewId) {
        try (PreparedStatement ps = conn.prepareStatement(SQL_DELETE)) {
            ps.setLong(1, reviewId);

            int deleted = ps.executeUpdate();
            JdbcUtils.requireUpdatedExactly(deleted, 1, "deleteReview(id=" + reviewId + ")");
        } catch (SQLException e) {
            throw new DaoException("Error while trying to delete review: " + reviewId, e);
        }
    }
}
