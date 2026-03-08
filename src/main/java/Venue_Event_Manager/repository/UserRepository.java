package Venue_Event_Manager.repository;

import Venue_Event_Manager.domain.model.user.*;

import java.sql.Connection;
import java.util.Optional;

public interface UserRepository {

    Optional<User> findById(Connection conn, long userId);

    Optional<User> findByEmail(Connection conn, String email);

    Optional<User> findByUsername(Connection conn, String username);

    Optional<User> findByPhone(Connection conn, String phone);

    long insert(Connection conn, User user);

    void updateAccountStatus(Connection conn, long userId, AccountStatus accountStatus);

}
