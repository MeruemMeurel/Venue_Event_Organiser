package Venue_Event_Manager.service;

import Venue_Event_Manager.config.TransactionManager;
import Venue_Event_Manager.domain.model.user.User;
import Venue_Event_Manager.exception.ForbiddenException;
import Venue_Event_Manager.repository.UserRepository;
import Venue_Event_Manager.repository.jdbc.PgUserRepository;
import Venue_Event_Manager.util.TestDataFactory;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

class PasswordCredentialIntegrationTest {

    private static final String JDBC_URL = "jdbc:postgresql://localhost:5433/event_manager_db";
    private static final String DB_USER = "admin";
    private static final String DB_PASSWORD = "change_me";
    private static final String TEST_USERNAME = "password_test_user";
    private static final String OLD_PASSWORD = "OldPassword1!";
    private static final String NEW_PASSWORD = "NewPassword2!";

    private UserRepository userRepository;
    private UserService userService;
    private AuthService authService;
    private TransactionManager transactionManager;
    private PasswordHasher passwordHasher;

    @BeforeAll
    static void requireLocalDatabase() {
        try (Connection ignored = DriverManager.getConnection(JDBC_URL, DB_USER, DB_PASSWORD)) {
            // Database is available: integration tests can run.
        } catch (SQLException exception) {
            assumeTrue(false, "Local PostgreSQL is unavailable: " + exception.getMessage());
        }
    }

    @BeforeEach
    void setUp() {
        userRepository = new PgUserRepository();
        userService = new UserService(userRepository);
        authService = new AuthService(userRepository);
        transactionManager = TransactionManager.getInstance();
        passwordHasher = new PasswordHasher();
        deleteTestUser();
    }

    @AfterEach
    void tearDown() {
        deleteTestUser();
    }

    @Test
    void registrationShouldPersistOnlyAnEncodedPassword() {
        long userId = userService.insert(newTestUser(), OLD_PASSWORD);

        String storedPassword = getStoredPassword(userId);

        assertNotEquals(OLD_PASSWORD, storedPassword);
        assertTrue(passwordHasher.isHashed(storedPassword));
        assertTrue(passwordHasher.verify(OLD_PASSWORD, storedPassword));
    }

    @Test
    void changePasswordShouldReplaceTheStoredHash() {
        long userId = userService.insert(newTestUser(), OLD_PASSWORD);
        String oldHash = getStoredPassword(userId);

        userService.changePassword(userId, OLD_PASSWORD, NEW_PASSWORD);
        String newHash = getStoredPassword(userId);

        assertNotEquals(oldHash, newHash);
        assertFalse(passwordHasher.verify(OLD_PASSWORD, newHash));
        assertTrue(passwordHasher.verify(NEW_PASSWORD, newHash));
    }

    @Test
    void changePasswordShouldRejectWrongCurrentPasswordWithoutChangingHash() {
        long userId = userService.insert(newTestUser(), OLD_PASSWORD);
        String originalHash = getStoredPassword(userId);

        assertThrows(ForbiddenException.class,
                () -> userService.changePassword(userId, "WrongPassword1!", NEW_PASSWORD));

        assertTrue(passwordHasher.verify(OLD_PASSWORD, getStoredPassword(userId)));
        assertEquals(originalHash, getStoredPassword(userId));
    }

    @Test
    void seededAdminCredentialsShouldBeAccepted() {
        long adminId = transactionManager.inReadOnly(connection ->
                userRepository.findByUsername(connection, "admin_mario")
                        .orElseThrow()
                        .getId());

        authService.requireAdminCredentials(adminId, "Admin123!");
        assertThrows(ForbiddenException.class,
                () -> authService.requireAdminCredentials(adminId, "WrongAdmin1!"));
    }

    @Test
    void malformedStoredHashShouldBeRejected() {
        long userId = transactionManager.inTransaction(connection ->
                userRepository.insert(connection, newTestUser(), "not-a-valid-hash"));

        assertThrows(ForbiddenException.class,
                () -> transactionManager.inReadOnly(connection -> {
                    authService.requireValidPassword(connection, userId, "not-a-valid-hash");
                    return null;
                }));
    }

    private User newTestUser() {
        return TestDataFactory.createDefaultUser(TEST_USERNAME);
    }

    private String getStoredPassword(long userId) {
        return transactionManager.inReadOnly(connection ->
                userRepository.getPasswordById(connection, userId).orElseThrow());
    }

    private void deleteTestUser() {
        try (Connection connection = DriverManager.getConnection(JDBC_URL, DB_USER, DB_PASSWORD);
             var statement = connection.prepareStatement("DELETE FROM \"USER\" WHERE username = ?")) {
            statement.setString(1, TEST_USERNAME);
            statement.executeUpdate();
        } catch (SQLException exception) {
            throw new IllegalStateException("Could not clean password integration-test data", exception);
        }
    }

}
