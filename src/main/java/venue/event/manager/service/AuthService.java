package venue.event.manager.service;

import venue.event.manager.config.TransactionManager;
import venue.event.manager.domain.model.user.User;
import venue.event.manager.exception.ForbiddenException;
import venue.event.manager.exception.NotFoundException;
import venue.event.manager.exception.ValidationException;
import venue.event.manager.repository.UserRepository;

import java.sql.Connection;

/**
 * Handles credential verification, password changes and authorization checks.
 */
public class AuthService {

    private final TransactionManager transactionManager;
    private final UserRepository userRepository;
    private final PasswordHasher passwordHasher;

    /**
     * Initializes the authentication service.
     * @param userRepository repository used to load and update user credentials
     */
    public AuthService(UserRepository userRepository) {
        this(userRepository, new PasswordHasher());
    }

    /**
     * Initializes the authentication service with an explicit password hasher.
     *
     * @param userRepository repository used to load and update user credentials
     * @param passwordHasher component used to hash and verify credentials
     */
    AuthService(UserRepository userRepository, PasswordHasher passwordHasher) {
        this.transactionManager = TransactionManager.getInstance();
        this.userRepository = userRepository;
        this.passwordHasher = passwordHasher;
    }

    /**
     * Changes a user's password after verifying the current password.
     * @param userId id of the user changing the password
     * @param oldPassword current password used to authorize the change
     * @param newPassword new password to persist
     * @throws ForbiddenException if credentials are unavailable or the current password is incorrect
     * @throws ValidationException if the new password does not satisfy the policy
     */
    public void changePassword(long userId, String oldPassword, String newPassword) {
        String encodedPassword = hashPassword(newPassword);

        transactionManager.inTransaction(conn -> {
            requireValidPassword(conn,userId,oldPassword);
            userRepository.updatePassword(conn,userId,encodedPassword);
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
            requireAdminCredentials(conn,adminId,password);
            return null;
        });
    }

    /**
     * Requires valid administrator credentials using an existing transaction.
     * @param conn active database connection
     * @param adminId id of the user expected to be an administrator
     * @param password administrator password to verify
     * @throws NotFoundException if the administrator does not exist
     * @throws ForbiddenException if the user is not an administrator or the password is incorrect
     */
    void requireAdminCredentials(Connection conn, long adminId, String password) {
        User admin = userRepository.findById(conn,adminId)
                .orElseThrow(() -> new NotFoundException("Admin does not exist"));

        if(!admin.isAdmin()) {
            throw new ForbiddenException("Admin privileges required for such action");
        }

        requireValidPassword(conn,adminId,password);
    }

    /**
     * Requires the supplied password to match the stored credential.
     * @param conn active database connection used to load the stored credential
     * @param userId id of the user whose password must be verified
     * @param password password supplied by the caller
     * @throws ForbiddenException if credentials are unavailable or the supplied password is incorrect
     */
    void requireValidPassword(Connection conn, long userId, String password) {
        String storedPassword = userRepository.getPasswordById(conn,userId)
                .orElseThrow(() -> new ForbiddenException("Credentials unavailable"));

        if (!passwordHasher.verify(password, storedPassword)) {
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

    /**
     * Validates and hashes a new password before persistence.
     *
     * @param password plain-text password to validate and hash
     * @return encoded PBKDF2 credential
     * @throws ValidationException if the password does not satisfy the policy
     */
    String hashPassword(String password) {
        validatePassword(password);
        return passwordHasher.hash(password);
    }

}
