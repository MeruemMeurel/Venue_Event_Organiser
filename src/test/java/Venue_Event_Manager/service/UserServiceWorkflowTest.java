package Venue_Event_Manager.service;

import Venue_Event_Manager.domain.model.user.*;
import Venue_Event_Manager.exception.*;
import Venue_Event_Manager.repository.UserRepository;
import Venue_Event_Manager.util.TestDataFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.sql.Connection;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class UserServiceWorkflowTest {
    private static final String PASSWORD = "Password1!";
    private UserRepository users;
    private PasswordHasher hasher;
    private UserService service;

    @BeforeEach
    void setUp() {
        users = mock(UserRepository.class);
        hasher = new PasswordHasher();
        when(users.findByEmail(any(Connection.class), anyString())).thenReturn(Optional.empty());
        service = new UserService(users, new AuthService(users, hasher));
    }

    @Test
    void registrationShouldStripAdminAndBannedFlagsAndHashPassword() {
        when(users.insert(any(Connection.class), any(), anyString())).thenReturn(22L);
        User supplied = TestDataFactory.createAdminUser("new_user").withAccountStatus(AccountStatus.BANNED);
        assertEquals(22L, service.insert(supplied, PASSWORD));
        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        ArgumentCaptor<String> passwordCaptor = ArgumentCaptor.forClass(String.class);
        verify(users).insert(any(Connection.class), userCaptor.capture(), passwordCaptor.capture());
        assertFalse(userCaptor.getValue().isAdmin());
        assertEquals(AccountStatus.ACTIVE, userCaptor.getValue().getAccountStatus());
        assertNotEquals(PASSWORD, passwordCaptor.getValue());
        assertTrue(hasher.verify(PASSWORD, passwordCaptor.getValue()));
    }

    @Test
    void profileUpdateShouldPreservePrivilegesAndStatus() {
        User stored = TestDataFactory.createAdminUser("stored").withId(4).withAccountStatus(AccountStatus.BANNED);
        User submitted = TestDataFactory.createDefaultUser("changed").withId(4);
        when(users.findById(any(Connection.class), eq(4L))).thenReturn(Optional.of(stored));
        when(users.getPasswordById(any(Connection.class), eq(4L))).thenReturn(Optional.of(hasher.hash(PASSWORD)));
        service.update(submitted, PASSWORD);
        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(users).update(any(Connection.class), captor.capture());
        assertTrue(captor.getValue().isAdmin());
        assertEquals(AccountStatus.BANNED, captor.getValue().getAccountStatus());
    }

    @Test
    void wrongPasswordShouldPreventProfileUpdateAndDeletion() {
        User stored = TestDataFactory.createDefaultUser("stored").withId(4);
        when(users.findById(any(Connection.class), eq(4L))).thenReturn(Optional.of(stored));
        when(users.getPasswordById(any(Connection.class), eq(4L))).thenReturn(Optional.of(hasher.hash(PASSWORD)));
        assertThrows(ForbiddenException.class, () -> service.update(stored, "WrongPass1!"));
        assertThrows(ForbiddenException.class, () -> service.deleteUser(4, "WrongPass1!"));
        verify(users, never()).update(any(), any());
        verify(users, never()).deleteById(any(), anyLong());
    }

    @Test
    void adminShouldBanAndUnbanOrdinaryUser() {
        User admin = TestDataFactory.createAdminUser("admin").withId(1);
        User target = TestDataFactory.createDefaultUser("target").withId(2);
        when(users.findById(any(Connection.class), eq(1L))).thenReturn(Optional.of(admin));
        when(users.findById(any(Connection.class), eq(2L)))
                .thenReturn(Optional.of(target), Optional.of(target.withAccountStatus(AccountStatus.BANNED)));
        when(users.getPasswordById(any(Connection.class), eq(1L))).thenReturn(Optional.of(hasher.hash(PASSWORD)));
        service.ban(1, PASSWORD, 2);
        service.unban(1, PASSWORD, 2);
        verify(users).updateAccountStatus(any(Connection.class), eq(2L), eq(AccountStatus.BANNED));
        verify(users).updateAccountStatus(any(Connection.class), eq(2L), eq(AccountStatus.ACTIVE));
    }

    @Test
    void adminCannotBanSelfOrAnotherAdmin() {
        User admin = TestDataFactory.createAdminUser("admin").withId(1);
        when(users.findById(any(Connection.class), eq(1L))).thenReturn(Optional.of(admin));
        when(users.findById(any(Connection.class), eq(2L)))
                .thenReturn(Optional.of(TestDataFactory.createAdminUser("other").withId(2)));
        when(users.getPasswordById(any(Connection.class), eq(1L))).thenReturn(Optional.of(hasher.hash(PASSWORD)));
        assertThrows(ForbiddenException.class, () -> service.ban(1, PASSWORD, 1));
        assertThrows(ForbiddenException.class, () -> service.ban(1, PASSWORD, 2));
        verify(users, never()).updateAccountStatus(any(), anyLong(), any());
    }

    @Test
    void adminCannotUnbanAnAlreadyActiveUser() {
        User admin = TestDataFactory.createAdminUser("admin").withId(1);
        User activeTarget = TestDataFactory.createDefaultUser("target").withId(2);
        when(users.findById(any(Connection.class), eq(1L))).thenReturn(Optional.of(admin));
        when(users.findById(any(Connection.class), eq(2L))).thenReturn(Optional.of(activeTarget));
        when(users.getPasswordById(any(Connection.class), eq(1L))).thenReturn(Optional.of(hasher.hash(PASSWORD)));

        assertThrows(ConflictException.class, () -> service.unban(1, PASSWORD, 2));
        verify(users, never()).updateAccountStatus(any(), anyLong(), any());
    }

    @Test
    void missingCredentialShouldPreventDelete() {
        when(users.getPasswordById(any(Connection.class), anyLong())).thenReturn(Optional.empty());
        assertThrows(ForbiddenException.class, () -> service.deleteUser(9, PASSWORD));
        verify(users, never()).deleteById(any(), anyLong());
    }
}
