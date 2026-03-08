package Venue_Event_Manager.domain.model.user;

import java.time.LocalDate;
import java.util.Objects;
import static Venue_Event_Manager.domain.model.user.AccountStatus.*;

/**
 * Domain entity representing a system user.
 * Built as an immutable object to ensure thread-safety and state consistency.
 */
public class User {

    //attributes
    private final long id;
    private final String username;
    private final String firstname;
    private final String lastname;
    private final LocalDate birthday;
    private final String email;
    private final String phone;
    private final boolean isAdmin;
    private final AccountStatus accountStatus;


    //constructors
    /** Initializes an empty user with default values. */
    public User() {
        this(0, "", "", "", null, "", "", false, ACTIVE);
    }

    /** Master constructor for full initialization */
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

    /** Constructor for unsaved user (ID defaults to 0). */
    public User(String username, String firstname, String lastname, LocalDate birthday, String email, String phone,
                boolean isAdmin, AccountStatus accountStatus) {
        this(0, username, firstname, lastname, birthday, email, phone, isAdmin, accountStatus);
    }

    /** Constructor with default isAdmin value (false). */
    public User(long id, String username, String firstname, String lastname, LocalDate birthday, String email,
                String phone, AccountStatus accountStatus) {
        this(id, username, firstname, lastname, birthday, email, phone, false, accountStatus);
    }

    /** Constructor for unsaved users with default isAdmin value (false). */
    public User(String username, String firstname, String lastname, LocalDate birthday, String email, String phone,
                AccountStatus accountStatus) {
        this(username, firstname, lastname, birthday, email, phone, false, accountStatus);
    }

    /** Constructor with default ACTIVE status. */
    public User(long id, String username, String firstname, String lastname, LocalDate birthday, String email,
                String phone, boolean isAdmin) {
        this(id, username, firstname, lastname, birthday, email, phone, isAdmin, ACTIVE);
    }

    /** Constructor for unsaved users with default ACTIVE status. */
    public User(String username, String firstname, String lastname, LocalDate birthday, String email, String phone,
                boolean isAdmin) {
        this(username, firstname, lastname, birthday, email, phone, isAdmin, ACTIVE);
    }

    /** Constructor with default isAdmin (false) and ACTIVE status. */
    public User(long id, String username, String firstname, String lastname, LocalDate birthday, String email,
                String phone) {
        this(id, username, firstname, lastname, birthday, email, phone, false, ACTIVE);
    }

    /** Constructor for unsaved users with default isAdmin (false) and ACTIVE status. */
    public User(String username, String firstname, String lastname, LocalDate birthday, String email, String phone) {
        this(username, firstname, lastname, birthday, email, phone, false, ACTIVE);
    }


    // Getters and Withers
    public long getId() { return id; }
    public User withId(long newId) {
        return new User(newId, this.username, this.firstname, this.lastname, this.birthday, this.email, this.phone,
                this.isAdmin, this.accountStatus);
    }

    public String getUsername() { return username; }
    public User withUsername(String newUsername) {
        return new User(this.id, newUsername, this.firstname, this.lastname, this.birthday, this.email, this.phone,
                this.isAdmin, this.accountStatus);
    }

    public String getFirstname() { return firstname; }
    public User withFirstName(String newFirstname) {
        return new User(this.id, this.username, newFirstname, this.lastname, this.birthday, this.email, this.phone,
                this.isAdmin, this.accountStatus);
    }

    public String getLastname() { return lastname; }
    public User withLastName(String newLastname) {
        return new User(this.id, this.username, this.firstname, newLastname, this.birthday, this.email, this.phone,
                this.isAdmin, this.accountStatus);
    }

    public LocalDate getBirthday() { return birthday; }
    public User withBirthday(LocalDate newBirthday) {
        return new User(this.id, this.username, this.firstname, this.lastname, newBirthday, this.email, this.phone,
                this.isAdmin, this.accountStatus);
    }

    public String getEmail() { return email; }
    public User withEmail(String newEmail) {
        return new User(this.id, this.username, this.firstname, this.lastname, this.birthday, newEmail, this.phone,
                this.isAdmin, this.accountStatus);
    }

    public String getPhone() { return phone; }
    public User withPhone(String newPhone) {
        return new User(this.id, this.username, this.firstname, this.lastname, this.birthday, this.email, newPhone,
                this.isAdmin, this.accountStatus);
    }

    public boolean isAdmin() { return isAdmin; }
    public User withIsAdmin(boolean newIsAdmin) {
        return new User(this.id, this.username, this.firstname, this.lastname, this.birthday, this.email, this.phone,
                newIsAdmin, this.accountStatus);
    }

    public AccountStatus getAccountStatus() { return accountStatus; }
    public User withAccountStatus(AccountStatus newAccountStatus) {
        return new User(this.id, this.username, this.firstname, this.lastname, this.birthday, this.email, this.phone,
                this.isAdmin, newAccountStatus);
    }


    @Override
    public String toString() {
        return "User{" +
                "id=" + id + "; " +
                "username=" + username + "; " +
                "firstname=" + firstname + "; " +
                "lastname=" + lastname + "; " +
                "birthday=" + birthday + "; " +
                "email=" + email + "; " +
                "phone=" + phone + "; " +
                "isAdmin=" + isAdmin + "; " +
                "accountStatus=" + accountStatus + ";" +
                "}";
    }

    /** Compares users based on ID or username and email uniqueness. */
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

    @Override
    public int hashCode() {
        return Objects.hash(id, username, email);
    }
}