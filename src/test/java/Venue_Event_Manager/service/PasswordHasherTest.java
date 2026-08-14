package Venue_Event_Manager.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PasswordHasherTest {

    private PasswordHasher passwordHasher;

    @BeforeEach
    void setUp() {
        passwordHasher = new PasswordHasher();
    }

    @Test
    void hashShouldCreateVerifiableEncodedPassword() {
        String encodedPassword = passwordHasher.hash("correct-password");

        assertTrue(passwordHasher.isHashed(encodedPassword));
        assertTrue(passwordHasher.verify("correct-password", encodedPassword));
    }

    @Test
    void verifyShouldRejectWrongPassword() {
        String encodedPassword = passwordHasher.hash("correct-password");

        assertFalse(passwordHasher.verify("wrong-password", encodedPassword));
    }

    @Test
    void hashShouldUseADifferentSaltEachTime() {
        String firstHash = passwordHasher.hash("same-password");
        String secondHash = passwordHasher.hash("same-password");

        assertNotEquals(firstHash, secondHash);
        assertTrue(passwordHasher.verify("same-password", firstHash));
        assertTrue(passwordHasher.verify("same-password", secondHash));
    }

    @Test
    void hashShouldRejectNullOrEmptyPasswords() {
        assertThrows(IllegalArgumentException.class, () -> passwordHasher.hash(null));
        assertThrows(IllegalArgumentException.class, () -> passwordHasher.hash(""));
    }

    @Test
    void verifyShouldRejectNullOrEmptyPasswords() {
        String encodedPassword = passwordHasher.hash("correct-password");

        assertFalse(passwordHasher.verify(null, encodedPassword));
        assertFalse(passwordHasher.verify("", encodedPassword));
    }

    @Test
    void malformedEncodedPasswordsShouldBeRejected() {
        assertFalse(passwordHasher.isHashed(null));
        assertFalse(passwordHasher.isHashed(""));
        assertFalse(passwordHasher.isHashed("plain-text-password"));
        assertFalse(passwordHasher.isHashed("unknown$210000$AAAA$AAAA"));
        assertFalse(passwordHasher.isHashed("pbkdf2-sha256$invalid$AAAA$AAAA"));
        assertFalse(passwordHasher.isHashed("pbkdf2-sha256$0$AAAAAAAAAAAAAAAAAAAAAA==$AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA="));
        assertFalse(passwordHasher.isHashed("pbkdf2-sha256$999999999$AAAAAAAAAAAAAAAAAAAAAA==$AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA="));
    }

    @Test
    void verifyShouldRejectMalformedEncodedPasswordWithoutThrowing() {
        assertFalse(passwordHasher.verify("password", "pbkdf2-sha256$invalid$not-base64$not-base64"));
    }
}
