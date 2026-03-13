package Venue_Event_Manager.repository;

import Venue_Event_Manager.domain.model.user.*;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.Optional;

public interface UserRepository {

    ArrayList<User> findAll(Connection conn);

    Optional<User> findById(Connection conn, long userId);

    Optional<User> findByEmail(Connection conn, String email);

    Optional<User> findByUsername(Connection conn, String username);

    Optional<User> findByPhone(Connection conn, String phone);

    boolean checkPassword(Connection conn, long userId, String password);

    long insert(Connection conn, User user, String password);

    void updateAccountStatus(Connection conn, long userId, AccountStatus accountStatus);

    void updatePassword (Connection conn, long userId, String oldPassword, String newPassword);

    Optional<Integer> getAverageReview(Connection conn, long userId);




}
