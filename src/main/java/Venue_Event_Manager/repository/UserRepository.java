package Venue_Event_Manager.repository;

import Venue_Event_Manager.domain.model.user.User;
import Venue_Event_Manager.domain.model.user.AccountStatus;
import java.sql.Connection;
import java.util.List;
import java.util.Optional;

public interface UserRepository {

    List<User> findAll(Connection conn);

    Optional<User> findById(Connection conn, long userId);

    Optional<User> findByUsername(Connection conn, String username);

    Optional<User> findByEmail(Connection conn, String email);

    Optional<User> findByPhone(Connection conn, String phone);

    List<User> findAllByIsAdmin(Connection conn, boolean isAdmin);

    List<User> findAllByAccountStatus(Connection conn, AccountStatus accountStatus);

    Optional<Double> getAverageRatingGivenByUser(Connection conn, long userId);

    Optional<String> getPasswordById(Connection conn, long userId);

    long insert(Connection conn, User user, String encodedPassword);

    void update(Connection conn, User user);

    void updateAccountStatus(Connection conn, long userId, AccountStatus newAccountStatus);

    void updatePassword(Connection conn, long userId, String encodedPassword);

    void deleteById(Connection conn, long userId);

}
