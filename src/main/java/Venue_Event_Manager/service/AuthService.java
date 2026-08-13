package Venue_Event_Manager.service;

import Venue_Event_Manager.config.TransactionManager;
import Venue_Event_Manager.domain.model.user.User;
import Venue_Event_Manager.exception.ForbiddenException;
import Venue_Event_Manager.exception.NotFoundException;
import Venue_Event_Manager.exception.ValidationException;
import Venue_Event_Manager.repository.UserRepository;

import java.sql.Connection;

/**
 * Handles credential verification, password changes and authorization checks.
 */
public class AuthService {

    private final TransactionManager transactionManager;
    private final UserRepository userRepository;

    /**
     * Initializes the authentication service.
     * @param userRepository repository used to load and update user credentials
     */
    public AuthService(UserRepository userRepository) {
        this.transactionManager = TransactionManager.getInstance();
        this.userRepository = userRepository;
    }

    /**
     * Changes a user's password after verifying the current password.
     * @param userId id of the user changing the password
     * @param oldPassword current password used to authorize the change
     * @param newPassword new password to persist
     * @throws NotFoundException if the user does not exist
     * @throws ForbiddenException if the current password is incorrect
     * @throws ValidationException if the new password does not satisfy the policy
     */
    public void changePassword(long userId, String oldPassword, String newPassword) {
        validatePassword(newPassword);

        transactionManager.inTransaction(conn -> {
            requireValidPassword(conn,userId,oldPassword);
            userRepository.updatePassword(conn,userId,newPassword);
            return null;
        });
    }

    /**
     * Requires valid administrator credentials.
     * @param adminId id of the user expected to be an administrator
     * @param password administrator password to verify
     * @throws NotFoundException if the administrator does not exist
     * @throws ForbiddenException if the user is not an administrator or the password is incorrect
     */
    void requireAdminCredentials(long adminId, String password) {
        transactionManager.inReadOnly(conn -> {
            User admin = userRepository.findById(conn,adminId)
                    .orElseThrow(() -> new NotFoundException("Admin does not exist"));

            if(!admin.isAdmin()) {
                throw new ForbiddenException("Admin privileges required for such action");
            }

            requireValidPassword(conn,adminId,password);
            return null;
        });
    }

    /**
     * Requires the supplied password to match the stored credential.
     * @param conn active database connection used to load the stored credential
     * @param userId id of the user whose password must be verified
     * @param password password supplied by the caller
     * @throws NotFoundException if the user does not exist
     * @throws ForbiddenException if the supplied password is incorrect
     */
    void requireValidPassword(Connection conn, long userId, String password) {
        String storedPassword = userRepository.getPasswordById(conn,userId)
                .orElseThrow(() -> new NotFoundException("User with id " + userId + " not found"));

        // TODO security: replace plain-text comparison with salted PBKDF2 password hashes and migrate existing seed/user credentials.
        if(!storedPassword.equals(password)) {
            throw new ForbiddenException("Wrong password");
        }
    }

    /**
     * Validates password input according to the current project policy.
     * @param password password to validate
     * @throws ValidationException if the password is empty or has an invalid length
     */
    void validatePassword(String password) {
        if(password == null || password.isEmpty()) {
            throw new ValidationException("Password cannot be empty");
        }
        if(password.length() < 8 || password.length() > 30) {
            throw new ValidationException("Password must be between 8 and 30 characters");
        }
    }
}
