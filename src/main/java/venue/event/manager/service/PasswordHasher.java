package venue.event.manager.service;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Base64;

/**
 * Creates and verifies password hashes using PBKDF2 with HMAC-SHA256.
 *
 * <p>Passwords are stored using the following format:</p>
 *
 * <pre>
 * pbkdf2-sha256$210000$&lt;salt-base64&gt;$&lt;hash-base64&gt;
 * </pre>
 */
public final class PasswordHasher {

    private static final String ALGORITHM = "PBKDF2WithHmacSHA256";
    private static final String FORMAT_ID = "pbkdf2-sha256";
    private static final int ITERATIONS = 210_000;
    private static final int SALT_LENGTH_BYTES = 16;
    private static final int KEY_LENGTH_BITS = 256;
    private static final int KEY_LENGTH_BYTES = KEY_LENGTH_BITS / 8;

    private final SecureRandom secureRandom;

    /**
     * Initializes a password hasher using a cryptographically secure random
     * number generator.
     */
    public PasswordHasher() {
        this.secureRandom = new SecureRandom();
    }

    /**
     * Creates a salted PBKDF2-SHA256 hash for the supplied password.
     *
     * @param password plain-text password to hash
     * @return encoded password containing algorithm identifier, iteration count,
     *         salt and derived hash
     * @throws IllegalArgumentException if the password is null or empty
     * @throws IllegalStateException if the PBKDF2 algorithm is unavailable
     */
    public String hash(String password) {
        if (password == null || password.isEmpty()) {
            throw new IllegalArgumentException("Password cannot be null or empty");
        }

        byte[] salt = new byte[SALT_LENGTH_BYTES];
        secureRandom.nextBytes(salt);

        byte[] derivedHash = deriveHash(password, salt, ITERATIONS);

        return FORMAT_ID
                + "$" + ITERATIONS
                + "$" + Base64.getEncoder().encodeToString(salt)
                + "$" + Base64.getEncoder().encodeToString(derivedHash);
    }

    /**
     * Verifies a plain-text password against an encoded PBKDF2 password hash.
     *
     * <p>Invalid, null, empty or malformed values are rejected by returning
     * {@code false}.</p>
     *
     * @param password plain-text password supplied for verification
     * @param encodedPassword encoded PBKDF2 password stored in persistence
     * @return {@code true} if the password matches the stored hash;
     *         {@code false} otherwise
     * @throws IllegalStateException if the PBKDF2 algorithm is unavailable
     */
    public boolean verify(String password, String encodedPassword) {
        if (password == null || password.isEmpty()) {
            return false;
        }

        ParsedHash parsedHash = parse(encodedPassword);

        if (parsedHash == null) {
            return false;
        }

        byte[] candidateHash = deriveHash(
                password,
                parsedHash.salt,
                parsedHash.iterations
        );

        return MessageDigest.isEqual(parsedHash.hash, candidateHash);

    }

    /**
     * Determines whether the supplied value is a valid password hash using the
     * format supported by this class.
     *
     * @param storedPassword password value read from persistence
     * @return {@code true} if the value is a valid PBKDF2-SHA256 encoded
     *         password; {@code false} otherwise
     */
    public boolean isHashed(String storedPassword) {
        return parse(storedPassword) != null;
    }

    /**
     * Derives a PBKDF2-SHA256 key from a password and salt.
     *
     * @param password plain-text password
     * @param salt random salt
     * @param iterations number of PBKDF2 iterations
     * @return derived password hash
     * @throws IllegalStateException if PBKDF2-SHA256 is unavailable
     */
    private byte[] deriveHash(String password, byte[] salt, int iterations) {
        PBEKeySpec keySpec = new PBEKeySpec(
                password.toCharArray(),
                salt,
                iterations,
                KEY_LENGTH_BITS
        );

        try {
            SecretKeyFactory secretKeyFactory = SecretKeyFactory.getInstance(ALGORITHM);
            return secretKeyFactory.generateSecret(keySpec).getEncoded();
        } catch (GeneralSecurityException e) {
            throw new IllegalStateException(
                    "PBKDF2-SHA256 password hashing is unavailable", e
            );
        }finally {
            keySpec.clearPassword();
        }
    }

    /**
     * Parses and validates an encoded password hash.
     *
     * @param encodedPassword encoded password value
     * @return parsed hash data, or {@code null} if the value is invalid
     */
    private ParsedHash parse(String encodedPassword) {
        if (encodedPassword == null || encodedPassword.isEmpty()) {
            return null;
        }

        String[] parts = encodedPassword.split("\\$", -1);

        if (parts.length != 4) {
            return null;
        }

        if (!FORMAT_ID.equals(parts[0])) {
            return null;
        }

        final int iterations;

        try {
            iterations = Integer.parseInt(parts[1]);
        } catch (NumberFormatException e) {
            return null;
        }

        // Only parameters generated and supported by this implementation are accepted.
        // This also prevents malformed values from requesting excessive PBKDF2 work.
        if (iterations != ITERATIONS) {
            return null;
        }

        try {
            byte[] salt = Base64.getDecoder().decode(parts[2]);
            byte[] hash = Base64.getDecoder().decode(parts[3]);

            if (salt.length != SALT_LENGTH_BYTES) {
                return null;
            }

            if (hash.length != KEY_LENGTH_BYTES) {
                return null;
            }

            return new ParsedHash(iterations, salt, hash);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    /**
     * Holds the validated components of an encoded PBKDF2 password.
     */
    private static class ParsedHash {
        private final int iterations;
        private final byte[] salt;
        private final byte[] hash;

        /**
         * Creates parsed password hash data.
         *
         * @param iterations PBKDF2 iteration count
         * @param salt decoded salt
         * @param hash decoded password hash
         */
        private ParsedHash(int iterations, byte[] salt, byte[] hash) {
            this.iterations = iterations;
            this.salt = salt;
            this.hash = hash;
        }
    }
}
