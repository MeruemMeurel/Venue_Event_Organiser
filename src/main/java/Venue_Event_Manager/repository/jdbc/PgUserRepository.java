package Venue_Event_Manager.repository.jdbc;

import Venue_Event_Manager.domain.model.user.AccountStatus;
import Venue_Event_Manager.domain.model.user.User;
import Venue_Event_Manager.repository.UserRepository;

import java.sql.Connection;
import java.util.Optional;


public class PgUserRepository implements UserRepository {


    @Override
    public Optional<User> findById(Connection conn, long userId) {
        return Optional.empty();
    }

    @Override
    public Optional<User> findByEmail(Connection conn, String email) {
        return Optional.empty();
    }

    @Override
    public Optional<User> findByUsername(Connection conn, String username) {
        return Optional.empty();
    }

    @Override
    public Optional<User> findByPhone(Connection conn, String phone) {
        return Optional.empty();
    }

    @Override
    public long insert(Connection conn, User user) {
        return 0;
    }

    @Override
    public void updateAccountStatus(Connection conn, long userId, AccountStatus accountStatus) {

    }
}
