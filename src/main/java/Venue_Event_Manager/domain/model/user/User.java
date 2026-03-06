package Venue_Event_Manager.domain.model.user;

import java.time.LocalDate;
import java.util.Objects;
import static Venue_Event_Manager.domain.model.user.AccountStatus.*;

/**
 * Represents a User within the system.
 * This class is immutable; any modification results in a new instance.
 * It follows the Domain-Driven Design principles for Entity modeling.
 */
public class User {

    /** The unique identifier for the user. */
    private final long id;
    /** The unique username for authentication. */
    private final String username;
    /** The user's first name. */
    private final String firstname;
    /** The user's last name. */
    private final String lastname;
    /** The user's date of birth. */
    private final LocalDate birthday;
    /** The unique email address. */
    private final String email;
    /** The user's phone contact. */
    private final String phone;
    /** Flag indicating if the user has administrative privileges. */
    private final boolean isAdmin;
    /** The current status of the user account (e.g., ACTIVE, BANNED). */
    private final AccountStatus accountStatus;

    /**
     * Default constructor creating an empty User.
     * Initializes the user with default/null values and ACTIVE status.
     */
    public User() {
        this(0, "", "", "", null, "", "", false, ACTIVE);
    }

    /**
     * Master constructor for the User class.
     *
     * @param id The unique identifier.
     * @param username The unique username.
     * @param firstname The first name.
     * @param lastname The last name.
     * @param birthday The date of birth.
     * @param email The unique email.
     * @param phone The phone number.
     * @param isAdmin True if the user is an admin.
     * @param accountStatus The status of the account.
     */
    public User(long id, String username, String firstname, String lastname, LocalDate birthday, String email,
                String phone, boolean isAdmin, AccountStatus accountStatus) {
        this.id = id;
        this.username = username;
        this.firstname = firstname;
        this.lastname = lastname;
        this.birthday = birthday;
        this.email = email;
        this.phone = phone;
        this.isAdmin = isAdmin;
        this.accountStatus = accountStatus != null ? accountStatus : ACTIVE;
    }

    /**
     * Constructor for unsaved users (without ID).
     * Assigns a default ID of 0.
     */
    public User(String username, String firstname, String lastname, LocalDate birthday, String email, String phone,
                boolean isAdmin, AccountStatus accountStatus) {
        this(0, username, firstname, lastname, birthday, email, phone, isAdmin, accountStatus);
    }

    /**
     * Constructor with default isAdmin value (false).
     */
    public User(long id, String username, String firstname, String lastname, LocalDate birthday, String email,
                String phone, AccountStatus accountStatus) {
        this(id, username, firstname, lastname, birthday, email, phone, false, accountStatus);
    }

    /**
     * Constructor for unsaved users with default isAdmin value (false).
     */
    public User(String username, String firstname, String lastname, LocalDate birthday, String email, String phone,
                AccountStatus accountStatus) {
        this(username, firstname, lastname, birthday, email, phone, false, accountStatus);
    }

    /**
     * Constructor with default ACTIVE status.
     */
    public User(long id, String username, String firstname, String lastname, LocalDate birthday, String email,
                String phone, boolean isAdmin) {
        this(id, username, firstname, lastname, birthday, email, phone, isAdmin, ACTIVE);
    }

    /**
     * Constructor for unsaved users with default ACTIVE status.
     */
    public User(String username, String firstname, String lastname, LocalDate birthday, String email, String phone,
                boolean isAdmin) {
        this(username, firstname, lastname, birthday, email, phone, isAdmin, ACTIVE);
    }

    /**
     * Constructor with default isAdmin (false) and ACTIVE status.
     */
    public User(long id, String username, String firstname, String lastname, LocalDate birthday, String email,
                String phone) {
        this(id, username, firstname, lastname, birthday, email, phone, false, ACTIVE);
    }

    /**
     * Constructor for unsaved users with default isAdmin (false) and ACTIVE status.
     */
    public User(String username, String firstname, String lastname, LocalDate birthday, String email, String phone) {
        this(username, firstname, lastname, birthday, email, phone, false, ACTIVE);
    }

    /** @return The unique identifier of the user. */
    public long getId() { return id; }

    /**
     * Returns a copy of this user with a new ID.
     * @param newId The new identifier.
     * @return A new User instance.
     */
    public User withId(long newId) {
        return new User(newId, this.username, this.firstname, this.lastname, this.birthday, this.email, this.phone,
                this.isAdmin, this.accountStatus);
    }

    /** @return The username. */
    public String getUsername() { return username; }

    /**
     * Returns a copy of this user with a new username.
     * @param newUsername The new username.
     * @return A new User instance.
     */
    public User withUsername(String newUsername) {
        return new User(this.id, newUsername, this.firstname, this.lastname, this.birthday, this.email, this.phone,
                this.isAdmin, this.accountStatus);
    }

    /** @return The first name. */
    public String getFirstname() { return firstname; }

    /**
     * Returns a copy of this user with a new first name.
     * @param newFirstname The new first name.
     * @return A new User instance.
     */
    public User withFirstName(String newFirstname) {
        return new User(this.id, this.username, newFirstname, this.lastname, this.birthday, this.email, this.phone,
                this.isAdmin, this.accountStatus);
    }

    /** @return The last name. */
    public String getLastname() { return lastname; }

    /**
     * Returns a copy of this user with a new last name.
     * @param newLastname The new last name.
     * @return A new User instance.
     */
    public User withLastName(String newLastname) {
        return new User(this.id, this.username, this.firstname, newLastname, this.birthday, this.email, this.phone,
                this.isAdmin, this.accountStatus);
    }

    /** @return The date of birth. */
    public LocalDate getBirthday() { return birthday; }

    /**
     * Returns a copy of this user with a new birthday.
     * @param newBirthday The new date of birth.
     * @return A new User instance.
     */
    public User withBirthday(LocalDate newBirthday) {
        return new User(this.id, this.username, this.firstname, this.lastname, newBirthday, this.email, this.phone,
                this.isAdmin, this.accountStatus);
    }

    /** @return The unique email address. */
    public String getEmail() { return email; }

    /**
     * Returns a copy of this user with a new email.
     * @param newEmail The new email address.
     * @return A new User instance.
     */
    public User withEmail(String newEmail) {
        return new User(this.id, this.username, this.firstname, this.lastname, this.birthday, newEmail, this.phone,
                this.isAdmin, this.accountStatus);
    }

    /** @return The phone number. */
    public String getPhone() { return phone; }

    /**
     * Returns a copy of this user with a new phone number.
     * @param newPhone The new phone number.
     * @return A new User instance.
     */
    public User withPhone(String newPhone) {
        return new User(this.id, this.username, this.firstname, this.lastname, this.birthday, this.email, newPhone,
                this.isAdmin, this.accountStatus);
    }

    /** @return True if user is admin, false otherwise. */
    public boolean isAdmin() { return isAdmin; }

    /**
     * Returns a copy of this user with modified admin status.
     * @param newIsAdmin The new admin status.
     * @return A new User instance.
     */
    public User withIsAdmin(boolean newIsAdmin) {
        return new User(this.id, this.username, this.firstname, this.lastname, this.birthday, this.email, this.phone,
                newIsAdmin, this.accountStatus);
    }

    /** @return The current account status. */
    public AccountStatus getAccountStatus() { return accountStatus; }

    /**
     * Returns a copy of this user with a new account status.
     * @param newAccountStatus The new account status.
     * @return A new User instance.
     */
    public User withAccountStatus(AccountStatus newAccountStatus) {
        return new User(this.id, this.username, this.firstname, this.lastname, this.birthday, this.email, this.phone,
                this.isAdmin, newAccountStatus);
    }

    /**
     * Returns a string representation of the User.
     * @return A formatted string containing user attributes.
     */
    @Override
    public String toString() {
        return "User{" +
                "id=" + id + "; " +
                "username='" + username + "'; " +
                "firstname='" + firstname + "'; " +
                "lastname='" + lastname + "'; " +
                "birthday=" + birthday + "; " +
                "email='" + email + "'; " +
                "phone='" + phone + "'; " +
                "isAdmin=" + isAdmin + "; " +
                "accountStatus=" + accountStatus + "; " +
                "}";
    }

    /**
     * Compares this user with another object for equality.
     * Identity is based on ID if present, otherwise on unique fields (username, email).
     *
     * @param other The object to compare with.
     * @return True if objects are considered equal.
     */
    @Override
    public boolean equals(Object other) {
        if (this == other) return true;
        if (other == null || getClass() != other.getClass()) return false;
        User user = (User) other;
        if (id != 0 && user.id != 0) {
            return id == user.id;
        }
        return Objects.equals(username, user.username) || Objects.equals(email, user.email);
    }

    /**
     * Generates a hash code for the user.
     * Consistent with the equals method using id, username, and email.
     * @return The hash code.
     */
    @Override
    public int hashCode() {
        return Objects.hash(id, username, email);
    }
}