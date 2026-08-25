package venue.event.manager.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import venue.event.manager.domain.model.user.AccountStatus;
import venue.event.manager.domain.model.user.User;
import venue.event.manager.exception.ForbiddenException;
import venue.event.manager.exception.ValidationException;
import venue.event.manager.repository.UserRepository;
import venue.event.manager.util.TestDataFactory;

import java.sql.Connection;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static venue.event.manager.util.TestTransactionManagerFactory.create;

class AuthServiceAuthenticationTest {

    private static final String USERNAME = "authenticated_user";
    private static final String PASSWORD = "correct-password";

    private UserRepository userRepository;
    private PasswordHasher passwordHasher;
    private AuthService authService;
    private User user;

    @BeforeEach
    void setUp() {
        userRepository = mock(UserRepository.class);
        passwordHasher = new PasswordHasher();
        authService = new AuthService(create(), userRepository, passwordHasher);
        user = TestDataFactory.createDefaultUser(USERNAME).withId(7);
    }

    @Test
    void validCredentialsShouldReturnActiveUser() {
        prepareCredentials(user, PASSWORD);

        User authenticated = authService.authenticate(USERNAME, PASSWORD);

        assertEquals(user, authenticated);
        verify(userRepository).findByUsername(any(Connection.class), eq(USERNAME));
        verify(userRepository).getPasswordById(any(Connection.class), eq(7L));
    }

    @Test
    void unknownUsernameShouldBeRejected() {
        when(userRepository.findByUsername(any(Connection.class), eq(USERNAME)))
                .thenReturn(Optional.empty());

        assertThrows(ForbiddenException.class,
                () -> authService.authenticate(USERNAME, PASSWORD));
    }

    @Test
    void wrongPasswordShouldBeRejected() {
        prepareCredentials(user, PASSWORD);

        assertThrows(ForbiddenException.class,
                () -> authService.authenticate(USERNAME, "wrong-password"));
    }

    @Test
    void bannedAccountShouldBeRejectedAfterCredentialVerification() {
        User bannedUser = user.withAccountStatus(AccountStatus.BANNED);
        prepareCredentials(bannedUser, PASSWORD);

        assertThrows(ForbiddenException.class,
                () -> authService.authenticate(USERNAME, PASSWORD));
        verify(userRepository).getPasswordById(any(Connection.class), eq(7L));
    }

    @Test
    void missingCredentialsShouldBeRejectedBeforeRepositoryAccess() {
        assertThrows(ValidationException.class,
                () -> authService.authenticate(null, PASSWORD));
        assertThrows(ValidationException.class,
                () -> authService.authenticate(" ", PASSWORD));
        assertThrows(ValidationException.class,
                () -> authService.authenticate(USERNAME, null));
        assertThrows(ValidationException.class,
                () -> authService.authenticate(USERNAME, ""));
    }

    @Test
    void missingStoredCredentialShouldBeRejected() {
        when(userRepository.findByUsername(any(Connection.class), eq(USERNAME)))
                .thenReturn(Optional.of(user));
        when(userRepository.getPasswordById(any(Connection.class), eq(7L)))
                .thenReturn(Optional.empty());

        assertThrows(ForbiddenException.class,
                () -> authService.authenticate(USERNAME, PASSWORD));
    }

    private void prepareCredentials(User account, String plainTextPassword) {
        when(userRepository.findByUsername(any(Connection.class), eq(USERNAME)))
                .thenReturn(Optional.of(account));
        when(userRepository.getPasswordById(any(Connection.class), eq(account.getId())))
                .thenReturn(Optional.of(passwordHasher.hash(plainTextPassword)));
    }
}
