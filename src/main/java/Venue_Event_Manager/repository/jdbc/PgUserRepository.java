package Venue_Event_Manager.repository.jdbc;

import Venue_Event_Manager.domain.model.user.AccountStatus;
import Venue_Event_Manager.domain.model.user.User;
import Venue_Event_Manager.repository.UserRepository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;


public class PgUserRepository implements UserRepository {

    private static final String SQL_FIND_BY_ID = "SELECT * FROM user WHERE id = ?";
    private static final String SQL_FIND_BY_EMAIL = "SELECT * FROM user WHERE email = ?";
    private static final String SQL_FIND_BY_USERNAME = "SELECT * FROM user WHERE username = ?";
    private static final String SQL_FIND_BY_PHONE = "SELECT * FROM user WHERE phone = ?";

    /**
     * Lambda function to implement RowMapper interface
     */
    private static final RowMapper<User> user_mapper = rs -> new User(
            rs.getLong("id"),
            rs.getString("username"),
            rs.getString("firstname"),
            rs.getString("lastname"),
            rs.getDate("birthday") != null ? rs.getDate("birthday").toLocalDate() : null,
            rs.getString("email"),
            rs.getString("phone"),
            rs.getBoolean("is_admin"),
            AccountStatus.valueOf(rs.getString("account_status").toUpperCase())
    );

    @Override
    public Optional<User> findById(Connection conn, long userId) {
        //Try-with-resources Java automatically calls for methdo ps.close() at the end of try catch
        try (PreparedStatement ps = conn.prepareStatement(SQL_FIND_BY_ID)){

            ps.setLong(1, userId);

            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return Optional.empty();
                return Optional.of(user_mapper.mapRow(rs));
            }


        }catch(SQLException e){
            throw new DaoException("Error while trying to find user with id " + userId, e);
        }

    }

    @Override
    public Optional<User> findByEmail(Connection conn, String email) {
        try (PreparedStatement ps = conn.prepareStatement(SQL_FIND_BY_EMAIL)) {

            ps.setString(1, email);

            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return Optional.empty();
                return Optional.of(user_mapper.mapRow(rs));
            }


        }catch(SQLException e){
            throw new DaoException("Error while trying to find user with email " + email, e);
        }

    }

    @Override
    public Optional<User> findByUsername(Connection conn, String username) {

        try(PreparedStatement ps = conn.prepareStatement(SQL_FIND_BY_USERNAME)){

            ps.setString(1,username);

            try (ResultSet rs = ps.executeQuery()) {
                if(!rs.next()) return Optional.empty();
                return Optional.of(user_mapper.mapRow(rs));

            }

        }catch(SQLException e){
            throw new DaoException("Error while trying to find user with username " + username, e);
        }

    }

    @Override
    public Optional<User> findByPhone(Connection conn, String phone) {
        try(PreparedStatement ps = conn.prepareStatement(SQL_FIND_BY_PHONE)){

            ps.setString(1,phone);

            try (ResultSet rs = ps.executeQuery()) {
                if(!rs.next()) return Optional.empty();
                return Optional.of(user_mapper.mapRow(rs));

            }

        }catch(SQLException e){
            throw new DaoException("Error while trying to find user with phone " + phone, e);
        }
    }

    @Override
    public long insert(Connection conn, User user) {
        return 0;
    }

    @Override
    public void updateAccountStatus(Connection conn, long userId, AccountStatus accountStatus) {

    }
}
