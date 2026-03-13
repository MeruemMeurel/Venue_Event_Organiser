package Venue_Event_Manager.domain.model.user;

import java.time.LocalDate;
import java.util.Objects;
import static Venue_Event_Manager.domain.model.user.AccountStatus.*;

/**
 * Domain entity representing a physical Venue.
 * Implemented as an immutable object, with multiple constructors to handle default and nullable fields.
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
    private final Boolean isAdmin;
    private final AccountStatus accountStatus;


    //constructors
    /** Initializes an empty user with default values. */
    public User() {
        this(0, "", "", "", null, "", "", null, null);
    }

    /** Master constructor for full initialization */
    public User(long id, String username, String firstname, String lastname, LocalDate birthday, String email,
                String phone, Boolean isAdmin, AccountStatus accountStatus) {
        this.id = id;
        this.username = username;
        this.firstname = firstname;
        this.lastname = lastname;
        this.birthday = birthday;
        this.email = email;
        this.phone = phone;
        this.isAdmin = isAdmin != null ? isAdmin : false;
        this.accountStatus = accountStatus != null ? accountStatus : ACTIVE;
    }

    /** Constructor for unsaved user (ID defaults to 0). */
    public User(String username, String firstname, String lastname, LocalDate birthday, String email, String phone,
                boolean isAdmin, AccountStatus accountStatus) {
        this(0, username, firstname, lastname, birthday, email, phone, isAdmin, accountStatus);
    }


    //getters and withers
    public long getId() { return id; }
    public User withId(long newId) {
        return new User(newId, username, firstname, lastname, birthday, email, phone, isAdmin, accountStatus);
    }

    public String getUsername() { return username; }
    public User withUsername(String newUsername) {
        return new User(id, newUsername, firstname, lastname, birthday, email, phone, isAdmin, accountStatus);
    }

    public String getFirstname() { return firstname; }
    public User withFirstName(String newFirstname) {
        return new User(id, username, newFirstname, lastname, birthday, email, phone, isAdmin, accountStatus);
    }

    public String getLastname() { return lastname; }
    public User withLastName(String newLastname) {
        return new User(id, username, firstname, newLastname, birthday, email, phone, isAdmin, accountStatus);
    }

    public LocalDate getBirthday() { return birthday; }
    public User withBirthday(LocalDate newBirthday) {
        return new User(id, username, firstname, lastname, newBirthday, email, phone, isAdmin, accountStatus);
    }

    public String getEmail() { return email; }
    public User withEmail(String newEmail) {
        return new User(id, username, firstname, lastname, birthday, newEmail, phone, isAdmin, accountStatus);
    }

    public String getPhone() { return phone; }
    public User withPhone(String newPhone) {
        return new User(id, username, firstname, lastname, birthday, email, newPhone, isAdmin, accountStatus);
    }

    public boolean isAdmin() { return isAdmin; }
    public User withIsAdmin(boolean newIsAdmin) {
        return new User(id, username, firstname, lastname, birthday, email, phone, newIsAdmin, accountStatus);
    }

    public AccountStatus getAccountStatus() { return accountStatus; }
    public User withAccountStatus(AccountStatus newAccountStatus) {
        return new User(id, username, firstname, lastname, birthday, email, phone, isAdmin, newAccountStatus);
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
        if (id != 0 && user.id != 0) return id == user.id;
        return Objects.equals(username, user.username) || Objects.equals(email, user.email);
    }

    @Override
    public int hashCode() { return Objects.hash(id, username, email); }
}