package venue.event.manager.repository;

import venue.event.manager.domain.model.user.User;
import venue.event.manager.domain.model.user.AccountStatus;
import java.sql.Connection;
import java.util.List;
import java.util.Optional;

/** Defines persistence operations for the corresponding domain model. */
public interface UserRepository {

    /**
     * Returns all persisted records.
     * @param conn active database connection
     * @return result produced by the repository operation
     */
    List<User> findAll(Connection conn);

    /**
     * Finds a persisted record by identifier.
     * @param conn active database connection
     * @param userId identifier used by the operation
     * @return result produced by the repository operation
     */
    Optional<User> findById(Connection conn, long userId);

    /**
     * Finds persisted records by username.
     * @param conn active database connection
     * @param username username to match
     * @return result produced by the repository operation
     */
    Optional<User> findByUsername(Connection conn, String username);

    /**
     * Finds persisted records by email.
     * @param conn active database connection
     * @param email email address to match
     * @return result produced by the repository operation
     */
    Optional<User> findByEmail(Connection conn, String email);

    /**
     * Finds persisted records by phone.
     * @param conn active database connection
     * @param phone phone number to match
     * @return result produced by the repository operation
     */
    Optional<User> findByPhone(Connection conn, String phone);

    /**
     * Returns persisted records filtered by is admin.
     * @param conn active database connection
     * @param isAdmin filter value
     * @return result produced by the repository operation
     */
    List<User> findAllByIsAdmin(Connection conn, boolean isAdmin);

    /**
     * Returns persisted records filtered by account status.
     * @param conn active database connection
     * @param accountStatus filter value
     * @return result produced by the repository operation
     */
    List<User> findAllByAccountStatus(Connection conn, AccountStatus accountStatus);

    /**
     * Calculates the average rating submitted by a user.
     * @param conn active database connection
     * @param userId identifier used by the operation
     * @return result produced by the repository operation
     */
    Optional<Double> getAverageRatingGivenByUser(Connection conn, long userId);

    /**
     * Finds the encoded credential of a user.
     * @param conn active database connection
     * @param userId identifier used by the operation
     * @return result produced by the repository operation
     */
    Optional<String> getPasswordById(Connection conn, long userId);

    /**
     * Persists a new record.
     * @param conn active database connection
     * @param user record to persist
     * @param encodedPassword encoded credential to persist
     * @return result produced by the repository operation
     */
    long insert(Connection conn, User user, String encodedPassword);

    /**
     * Updates an existing record.
     * @param conn active database connection
     * @param user record to persist
     */
    void update(Connection conn, User user);

    /**
     * Updates the moderation status of a user account.
     * @param conn active connection
     * @param userId user identifier
     * @param newAccountStatus status to persist
     */
    void updateAccountStatus(Connection conn, long userId, AccountStatus newAccountStatus);

    /**
     * Replaces the encoded credential of a user.
     * @param conn active connection
     * @param userId user identifier
     * @param encodedPassword encoded credential to persist
     */
    void updatePassword(Connection conn, long userId, String encodedPassword);

    /**
     * Deletebyids the supplied persistent data.
     * @param conn active database connection
     * @param userId identifier used by the operation
     */
    void deleteById(Connection conn, long userId);

}
