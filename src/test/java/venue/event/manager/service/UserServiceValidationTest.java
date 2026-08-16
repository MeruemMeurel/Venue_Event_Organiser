package venue.event.manager.service;

import venue.event.manager.domain.model.user.User;
import venue.event.manager.exception.ValidationException;
import venue.event.manager.repository.UserRepository;
import venue.event.manager.util.TestDataFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class UserServiceValidationTest {
    private UserService service;
    private UserRepository repository;

    @BeforeEach void setUp() {
        repository = mock(UserRepository.class);
        when(repository.findByEmail(any(Connection.class), anyString())).thenReturn(Optional.empty());
        service = new UserService(repository, new AuthService(repository));
    }

    @Test void nullUserShouldBeRejected() { assertThrows(ValidationException.class, () -> service.insert(null, "Password1!")); }
    @Test void shortUsernameShouldBeRejected() { assertThrows(ValidationException.class, () -> service.insert(validUser().withUsername("a"), "Password1!")); }
    @Test void emptyFirstnameShouldBeRejected() { assertThrows(ValidationException.class, () -> service.insert(validUser().withFirstName(""), "Password1!")); }
    @Test void emptyLastnameShouldBeRejected() { assertThrows(ValidationException.class, () -> service.insert(validUser().withLastName(""), "Password1!")); }
    @Test void malformedEmailShouldBeRejected() { assertThrows(ValidationException.class, () -> service.insert(validUser().withEmail("invalid"), "Password1!")); }
    @Test void duplicateEmailShouldBeRejected() {
        when(repository.findByEmail(any(Connection.class), anyString())).thenReturn(Optional.of(validUser().withId(2)));
        assertThrows(ValidationException.class, () -> service.insert(validUser(), "Password1!"));
    }
    @Test void malformedPhoneShouldBeRejected() { assertThrows(ValidationException.class, () -> service.insert(validUser().withPhone("abcde"), "Password1!")); }
    @Test void shortPhoneShouldBeRejected() { assertThrows(ValidationException.class, () -> service.insert(validUser().withPhone("123"), "Password1!")); }
    @Test void missingBirthdayShouldBeRejected() { assertThrows(ValidationException.class, () -> service.insert(validUser().withBirthday(null), "Password1!")); }
    @Test void futureBirthdayShouldBeRejected() { assertThrows(ValidationException.class, () -> service.insert(validUser().withBirthday(LocalDate.now().plusDays(1)), "Password1!")); }
    @Test void implausiblyOldBirthdayShouldBeRejected() { assertThrows(ValidationException.class, () -> service.insert(validUser().withBirthday(LocalDate.of(1899, 12, 31)), "Password1!")); }
    @Test void emptyEmailLookupShouldBeRejected() { assertThrows(ValidationException.class, () -> service.getByEmail("")); }
    @Test void nullUsernameLookupShouldBeRejected() { assertThrows(ValidationException.class, () -> service.getByUsername(null)); }
    @Test void emptyPhoneLookupShouldBeRejected() { assertThrows(ValidationException.class, () -> service.getByPhone("")); }

    private User validUser() { return TestDataFactory.createDefaultUser("valid_user"); }
}
