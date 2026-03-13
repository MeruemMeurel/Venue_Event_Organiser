package Venue_Event_Manager.repository.jdbc;

import Venue_Event_Manager.domain.model.user.AccountStatus;
import Venue_Event_Manager.domain.model.user.User;
import Venue_Event_Manager.repository.UserRepository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Optional;


public class PgUserRepository implements UserRepository {

    private static final String SQL_FIND_ALL = "SELECT username, firstname, lastname, birthday ,email" +
            "phone, is_admin, account_status FROM user";
    private static final String SQL_FIND_BY_ID = "SELECT username, firstname, lastname, birthday ,email" +
            "phone, is_admin, account_status FROM user WHERE id = ?";
    private static final String SQL_FIND_BY_EMAIL = "SELECT username, firstname, lastname, birthday ,email" +
            "phone, is_admin, account_status FROM user WHERE email = ?";
    private static final String SQL_FIND_BY_USERNAME = "SELECT username, firstname, lastname, birthday ,email"+
            "phone, is_admin, account_status FROM user WHERE username = ?";
    private static final String SQL_FIND_BY_PHONE = "SELECT username, firstname, lastname, birthday ,email" +
            "phone, is_admin, account_status FROM user WHERE phone = ?";

    private static final String SQL_GET_PASSWORD = "SELECT password FROM user WHERE id = ?";

    private static final String SQL_INSERT = "INSERT INTO user (username, password, firstname, lastname, birthday ,email" +
            "phone, is_admin, account_status) VALUES (?, ?, ?, ?,?,?,?,?,?)";
    private static final String SQL_UPDATE_ACCOUNT_STATUS = "UPDATE user SET account_status = ? WHERE userid = ?";
    private static final String SQL_UPDATE_PASSWORD = "UPDATE user SET password = ? WHERE userid = ?";

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
    public ArrayList<User> findAll(Connection conn) {
        ArrayList<User> users = new ArrayList<>();

        try(PreparedStatement ps = conn.prepareStatement(SQL_FIND_ALL)){
            try(ResultSet rs = ps.executeQuery()){
                while(rs.next()){
                    users.add(user_mapper.mapRow(rs));
                }
            }
            return users;

        }catch(SQLException e){
            throw new DaoException("Error while trying to find all users", e);
        }

    }

    /**
     * Executes query to database to get User from his id
     * @param conn The database connection used
     * @param userId the id of the user
     * @return Optional<User> object. Empty if not found
     */
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

    /**
     * Executes query to database to get User from his email
     * @param conn The database connection used
     * @param email the email of the user
     * @return Optional<User> object. Empty if not found
     */
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

    /**
     * Executes query to database to get User from his username
     * @param conn The database connection used
     * @param username the username of the user
     * @return Optional<User> object. Empty if not found
     */
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

    /**
     * Executes query to database to get User from his phone
     * @param conn The database connection used
     * @param phone the phone of the user
     * @return Optional<User> object. Empty if not found
     */
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

    /**
     * Checks if a password provided for a user is correct
     * @param conn the database connection
     * @param userId the id of the user
     * @param password the password to check
     * @return true if password is correct, false otherwise
     */
    @Override
    public boolean checkPassword(Connection conn, long userId, String password) {
        try(PreparedStatement ps = conn.prepareStatement(SQL_GET_PASSWORD)){
            ps.setLong(1, userId);

            try(ResultSet rs = ps.executeQuery()) {
                if(!rs.next()) throw new DaoException("No user found with id " + userId);
                String dbpassword = rs.getString("password");

                if(dbpassword.equals(password)) return true;
                else throw new AuthenticationException("Wrong password for user with id: " + userId);

            }

        }catch(SQLException e){
            throw new DaoException("Error while trying to update password of user with id " + userId, e);
        }
    }

    /**
     * Executes SQL Query to insert user object to database
     * @param conn the connection to database
     * @param user the user to insert
     * @param password the password of the new user
     * @return long int id of the new user creted
     */
    @Override
    public long insert(Connection conn, User user, String password) {
        try (PreparedStatement ps = conn.prepareStatement(SQL_INSERT)){

            ps.setString(1, user.getUsername());
            ps.setString(2, password);
            ps.setString(3, user.getFirstname());
            ps.setString(4, user.getLastname());
            ps.setString(5, user.getBirthday().toString());
            ps.setString(6, user.getEmail());

            JdbcUtils.setNullableString(ps, 7, user.getPhone());

            ps.setBoolean(8, user.isAdmin());
            ps.setString(9, user.getAccountStatus().name());

            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                return rs.getLong(1);
            }

        } catch (SQLException e) {
            throw new DaoException("Error while trying to insert user",e);
        }

    }

    /**
     * Executes SQL Query to update a user's Account Status
     * @param conn the connection to database
     * @param userId the id of the user
     * @param accountStatus the status to update to
     */
    @Override
    public void updateAccountStatus(Connection conn, long userId, AccountStatus accountStatus) {
        try(PreparedStatement ps = conn.prepareStatement(SQL_UPDATE_ACCOUNT_STATUS)){

            ps.setString(1, accountStatus.name());
            ps.setLong(2, userId);

            int updated = ps.executeUpdate();

            JdbcUtils.requireUpdatedExactly(updated,1,"updateAccountStatus(userId="+userId+")");

        }catch(SQLException e){
            throw new DaoException("Error while trying to update user with id " + userId, e);
        }
    }

    /**
     * Changes password of a user, if the old password provided is correct
     * @param conn the database connection
     * @param userId the id of the user
     * @param oldPassword the old password to change
     * @param newPassword the new password to set
     */
    @Override
    public void updatePassword(Connection conn, long userId, String oldPassword, String newPassword) {

        if(checkPassword(conn, userId, oldPassword)) {

            try (PreparedStatement ps = conn.prepareStatement(SQL_UPDATE_PASSWORD)){
                ps.setString(1, newPassword);
                ps.setLong(2, userId);

                int  updated = ps.executeUpdate();

                JdbcUtils.requireUpdatedExactly(updated,1,"updatePassword(userId="+userId+")");

            }catch(SQLException | AuthenticationException e){
                throw new DaoException("Error while trying to update password of user with id " + userId, e);
            }

        }


    }

    //TODO Call method from ReviewRepository
    @Override
    public Optional<Integer> getAverageReview(Connection conn, long userId) {

        return Optional.empty();

    }
}
