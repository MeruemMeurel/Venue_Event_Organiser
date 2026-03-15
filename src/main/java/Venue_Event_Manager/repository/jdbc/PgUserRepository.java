package Venue_Event_Manager.repository.jdbc;

import Venue_Event_Manager.domain.model.user.AccountStatus;
import Venue_Event_Manager.domain.model.user.User;
import Venue_Event_Manager.repository.ReviewRepository;
import Venue_Event_Manager.repository.UserRepository;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class PgUserRepository implements UserRepository {

    /**
     * Lambda function to implement RowMapper interface
     */
    private static final RowMapper<User> user_mapper = rs -> new User(
            rs.getLong("id"),
            rs.getString("username"),
            rs.getString("firstname"),
            rs.getString("lastname"),
            rs.getDate("birthday").toLocalDate(),
            rs.getString("email"),
            rs.getString("phone"),
            rs.getBoolean("is_admin"),
            AccountStatus.valueOf(rs.getString("account_status").toUpperCase())
    );


    private static final String SQL_FIND_ALL =
            "SELECT id, username, firstname, lastname, birthday, email, phone, is_admin, account_status " +
            "FROM user";
    /**
     * Executes query to database to get all Users
     * @param conn The database connection used
     * @return List<User> object
     */
    @Override
    public List<User> findAll(Connection conn) {
        List<User> users = new ArrayList<>();
        try(PreparedStatement ps = conn.prepareStatement(SQL_FIND_ALL)){
            
            try(ResultSet rs = ps.executeQuery()){
                while(rs.next()){
                    users.add(user_mapper.mapRow(rs));
                }
            }
            return users;
        } catch(SQLException e){
            throw new DaoException("Error while trying to find all users", e);
        }
    }


    private static final String SQL_FIND_BY_ID = SQL_FIND_ALL + " WHERE id = ?";
    /**
     * Executes query to database to get User from his id
     * @param conn The database connection used
     * @param userId the id of the user
     * @return Optional<User> object. Empty if not found
     */
    @Override
    public Optional<User> findById(Connection conn, long userId) {
        try (PreparedStatement ps = conn.prepareStatement(SQL_FIND_BY_ID)){
            ps.setLong(1, userId);
            
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return Optional.empty();
                return Optional.of(user_mapper.mapRow(rs));
            }
        } catch(SQLException e){
            throw new DaoException("Error while trying to find user with id " + userId, e);
        }
    }


    private static final String SQL_FIND_BY_USERNAME = SQL_FIND_ALL + " WHERE username = ?";
    /**
     * Executes query to database to get User from his username
     * @param conn The database connection used
     * @param username the username of the user
     * @return Optional<User> object. Empty if not found
     */
    @Override
    public Optional<User> findByUsername(Connection conn, String username) {
        try(PreparedStatement ps = conn.prepareStatement(SQL_FIND_BY_USERNAME)){
            ps.setString(1, username);
            
            try (ResultSet rs = ps.executeQuery()) {
                if(!rs.next()) return Optional.empty();
                return Optional.of(user_mapper.mapRow(rs));
            }
        } catch(SQLException e){
            throw new DaoException("Error while trying to find user with username " + username, e);
        }
    }


    private static final String SQL_FIND_BY_EMAIL = SQL_FIND_ALL + " WHERE email = ?";
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
        } catch(SQLException e){
            throw new DaoException("Error while trying to find user with email " + email, e);
        }
    }


    private static final String SQL_FIND_BY_PHONE = SQL_FIND_ALL + " WHERE phone = ?";
    /**
     * Executes query to database to get User from his phone
     * @param conn The database connection used
     * @param phone the phone of the user
     * @return Optional<User> object. Empty if not found
     */
    @Override
    public Optional<User> findByPhone(Connection conn, String phone) {
        try(PreparedStatement ps = conn.prepareStatement(SQL_FIND_BY_PHONE)){
            ps.setString(1, phone);
            
            try (ResultSet rs = ps.executeQuery()) {
                if(!rs.next()) return Optional.empty();
                return Optional.of(user_mapper.mapRow(rs));
            }
        } catch(SQLException e){
            throw new DaoException("Error while trying to find user with phone " + phone, e);
        }
    }


    private static final String SQL_ALL_BY_IS_ADMIN = SQL_FIND_ALL + " WHERE is_admin = ?";
    /**
     * Executes query to database to get all Users filtered by admin status
     * @param conn The database connection used
     * @param isAdmin the admin status to filter by
     * @return List<User> object
     */
    @Override
    public List<User> findAllByIsAdmin(Connection conn, boolean isAdmin) {
        List<User> users = new ArrayList<>();
        try(PreparedStatement ps = conn.prepareStatement(SQL_ALL_BY_IS_ADMIN)){
            ps.setBoolean(1, isAdmin);
            
            try(ResultSet rs = ps.executeQuery()){
                while(rs.next()) users.add(user_mapper.mapRow(rs));
            }
            return users;
        } catch(SQLException e){
            throw new DaoException("Error while trying to find users with is_admin " + isAdmin, e);
        }
    }


    private static final String SQL_ALL_BY_ACCOUNT_STATUS = SQL_FIND_ALL + " WHERE account_status = ?";
    /**
     * Executes query to database to get all Users filtered by account status
     * @param conn The database connection used
     * @param accountStatus the account status to filter by
     * @return List<User> object
     */
    @Override
    public List<User> findAllByAccountStatus(Connection conn, AccountStatus accountStatus) {
        List<User> users = new ArrayList<>();
        try(PreparedStatement ps = conn.prepareStatement(SQL_ALL_BY_ACCOUNT_STATUS)){
            ps.setString(1, accountStatus.name());
            
            try(ResultSet rs = ps.executeQuery()){
                while(rs.next()) users.add(user_mapper.mapRow(rs));
            }
            return users;
        } catch(SQLException e){
            throw new DaoException("Error while trying to find users with account_status " + accountStatus, e);
        }
    }


    /**
     * Calculates the average review score for a specific user
     * @param conn the database connection
     * @param userId the id of the user
     * @return Optional<Integer> with the average score
     */
    @Override
    public Optional<Double> getAverageReview(Connection conn, long userId) {
        ReviewRepository reviewRepository = new PgReviewRepository();
        double average = reviewRepository.getAverageRatingByUser(conn, userId);
        return average != 0.0 ? Optional.of(average) : Optional.empty();
    }


    private static final String SQL_GET_PASSWORD = "SELECT password FROM user WHERE id = ?";
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
                if(!rs.next()) return false;
                return rs.getString("password").equals(password);
            }
        } catch(SQLException e){
            throw new DaoException("Error while checking password for user with id " + userId, e);
        }
    }


    private static final String SQL_INSERT =
            "INSERT INTO user (username, password, firstname, lastname, birthday, email, phone, is_admin, account_status) " +
            "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?) RETURNING id";
    /**
     * Executes SQL Query to insert user object to database
     * @param conn the connection to database
     * @param user the user to insert
     * @param password the password of the new user
     * @return long int id of the new user created
     */
    @Override
    public long insert(Connection conn, User user, String password) {
        try (PreparedStatement ps = conn.prepareStatement(SQL_INSERT)){
            ps.setString(1, user.getUsername());
            ps.setString(2, password);
            ps.setString(3, user.getFirstname());
            ps.setString(4, user.getLastname());
            ps.setDate(5, Date.valueOf(user.getBirthday()));
            ps.setString(6, user.getEmail());
            ps.setString(7, user.getPhone());
            ps.setBoolean(8, user.isAdmin());
            ps.setString(9, user.getAccountStatus().name());

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getLong(1);
                throw new DaoException("Error: ID not returned after insertion");
            }
        } catch (SQLException e) {
            throw new DaoException("Error while trying to insert user", e);
        }
    }


    private static final String SQL_UPDATE =
            "UPDATE user " +
            "SET username = ?, firstname = ?, lastname = ?, birthday = ?, email = ?, phone = ?, is_admin = ?, " +
                "account_status = ? WHERE id = ?";
    /**
     * Executes SQL Query to update a user's profile information
     * @param conn the connection to database
     * @param user the user object with updated data
     * @param password the password to verify the operation
     */
    @Override
    public void update(Connection conn, User user, String password) {
        if (checkPassword(conn, user.getId(), password)) {
            try (PreparedStatement ps = conn.prepareStatement(SQL_UPDATE)) {
                ps.setString(1, user.getUsername());
                ps.setString(2, user.getFirstname());
                ps.setString(3, user.getLastname());
                ps.setDate(4, Date.valueOf(user.getBirthday()));
                ps.setString(5, user.getEmail());
                ps.setString(6, user.getPhone());
                ps.setBoolean(7, user.isAdmin());
                ps.setString(8, user.getAccountStatus().name());
                ps.setLong(9, user.getId());

                int updated = ps.executeUpdate();
                JdbcUtils.requireUpdatedExactly(updated, 1, "updateUser(id=" + user.getId() + ")");
            } catch (SQLException e) {
                throw new DaoException("Error while trying to update user with id " + user.getId(), e);
            }
        } else {
            throw new DaoException("Authentication failed: wrong password for update");
        }
    }


    private static final String SQL_UPDATE_ACCOUNT_STATUS = "UPDATE user SET account_status = ? WHERE id = ?";
    /**
     * Executes SQL Query to update a user's Account Status
     * @param conn the connection to database
     * @param userId the id of the user
     * @param accountStatus the status to update to
     */
    @Override
    public void updateAccountStatus(Connection conn, long userId, String password, AccountStatus accountStatus) {
        if (checkPassword(conn, userId, password)) {
            try(PreparedStatement ps = conn.prepareStatement(SQL_UPDATE_ACCOUNT_STATUS)){
                ps.setString(1, accountStatus.name());
                ps.setLong(2, userId);

                int updated = ps.executeUpdate();
                JdbcUtils.requireUpdatedExactly(updated, 1, "updateAccountStatus(userId=" + userId + ")");
            } catch(SQLException e){
                throw new DaoException("Error while trying to update account status for user " + userId, e);
            }
        } else {
            throw new DaoException("Authentication failed: wrong password for update");
        }
    }


    private static final String SQL_UPDATE_PASSWORD = "UPDATE user SET password = ? WHERE id = ?";
    /**
     * Changes password of a user, if the old password provided is correct
     * @param conn the database connection
     * @param userId the id of the user
     * @param oldPassword the old password to check
     * @param newPassword the new password to set
     */
    @Override
    public void updatePassword(Connection conn, long userId, String oldPassword, String newPassword) {
        if(checkPassword(conn, userId, oldPassword)) {
            try (PreparedStatement ps = conn.prepareStatement(SQL_UPDATE_PASSWORD)){
                ps.setString(1, newPassword);
                ps.setLong(2, userId);
                
                int updated = ps.executeUpdate();
                JdbcUtils.requireUpdatedExactly(updated, 1, "updatePassword(userId=" + userId + ")");
            } catch(SQLException e){
                throw new DaoException("Error while trying to update password for user " + userId, e);
            }
        } else {
            throw new DaoException("Authentication failed: wrong old password");
        }
    }


    private static final String SQL_DELETE = "DELETE FROM user WHERE id = ?";
    /**
     * Deletes a user from database if the password is correct
     * @param conn the database connection
     * @param userId the id of the user to delete
     * @param password the password to verify the operation
     */
    @Override
    public void deleteById(Connection conn, long userId, String password) {
        if (checkPassword(conn, userId, password)) {
            try (PreparedStatement ps = conn.prepareStatement(SQL_DELETE)) {
                ps.setLong(1, userId);
                
                int deleted = ps.executeUpdate();
                JdbcUtils.requireUpdatedExactly(deleted, 1, "deleteById(userId=" + userId + ")");
            } catch (SQLException e) {
                throw new DaoException("Error while trying to delete user " + userId, e);
            }
        } else {
            throw new DaoException("Authentication failed: wrong password for deletion");
        }
    }
}