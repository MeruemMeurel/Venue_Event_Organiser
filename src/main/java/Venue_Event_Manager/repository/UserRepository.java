package Venue_Event_Manager.repository;

import Venue_Event_Manager.domain.model.user.User;
import Venue_Event_Manager.domain.model.user.AccountStatus;
import java.sql.Connection;
import java.util.List;
import java.util.Optional;

/** Defines persistence operations for the corresponding domain model. */
public interface UserRepository {

    /**
     * Performs the {@code findAll} repository operation.
     * @param conn conn value
     * @return operation result
     */
    List<User> findAll(Connection conn);

    /**
     * Performs the {@code findById} repository operation.
     * @param conn conn value
     * @param userId userId value
     * @return operation result
     */
    Optional<User> findById(Connection conn, long userId);

    /**
     * Performs the {@code findByUsername} repository operation.
     * @param conn conn value
     * @param username username value
     * @return operation result
     */
    Optional<User> findByUsername(Connection conn, String username);

    /**
     * Performs the {@code findByEmail} repository operation.
     * @param conn conn value
     * @param email email value
     * @return operation result
     */
    Optional<User> findByEmail(Connection conn, String email);

    /**
     * Performs the {@code findByPhone} repository operation.
     * @param conn conn value
     * @param phone phone value
     * @return operation result
     */
    Optional<User> findByPhone(Connection conn, String phone);

    /**
     * Performs the {@code findAllByIsAdmin} repository operation.
     * @param conn conn value
     * @param isAdmin isAdmin value
     * @return operation result
     */
    List<User> findAllByIsAdmin(Connection conn, boolean isAdmin);

    /**
     * Performs the {@code findAllByAccountStatus} repository operation.
     * @param conn conn value
     * @param accountStatus accountStatus value
     * @return operation result
     */
    List<User> findAllByAccountStatus(Connection conn, AccountStatus accountStatus);

    /**
     * Performs the {@code getAverageRatingGivenByUser} repository operation.
     * @param conn conn value
     * @param userId userId value
     * @return operation result
     */
    Optional<Double> getAverageRatingGivenByUser(Connection conn, long userId);

    /**
     * Performs the {@code getPasswordById} repository operation.
     * @param conn conn value
     * @param userId userId value
     * @return operation result
     */
    Optional<String> getPasswordById(Connection conn, long userId);

    /**
     * Performs the {@code insert} repository operation.
     * @param conn conn value
     * @param user user value
     * @param encodedPassword encodedPassword value
     * @return operation result
     */
    long insert(Connection conn, User user, String encodedPassword);

    /**
     * Performs the {@code update} repository operation.
     * @param conn conn value
     * @param user user value
     */
    void update(Connection conn, User user);

    /**
     * Performs the {@code updateAccountStatus} repository operation.
     * @param conn conn value
     * @param userId userId value
     * @param newAccountStatus newAccountStatus value
     */
    void updateAccountStatus(Connection conn, long userId, AccountStatus newAccountStatus);

    /**
     * Performs the {@code updatePassword} repository operation.
     * @param conn conn value
     * @param userId userId value
     * @param encodedPassword encodedPassword value
     */
    void updatePassword(Connection conn, long userId, String encodedPassword);

    /**
     * Performs the {@code deleteById} repository operation.
     * @param conn conn value
     * @param userId userId value
     */
    void deleteById(Connection conn, long userId);

}
