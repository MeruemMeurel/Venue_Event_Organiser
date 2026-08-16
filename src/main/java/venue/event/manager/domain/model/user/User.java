package venue.event.manager.domain.model.user;

import java.time.LocalDate;
import java.util.Objects;
import static venue.event.manager.domain.model.user.AccountStatus.*;

/**
 * Domain entity representing a physical User.
 * Implemented as an immutable object, with multiple constructors to handle default and nullable fields.
 */
public class User {

    //attributes
    private final long id;
    private final String username;
    private final String firstname;
    private final String lastname;
    private final LocalDate birthday; // Null only in the empty model; required for persisted users.
    private final String email;
    private final String phone;
    private final Boolean is_admin;
    private final AccountStatus account_status;


    //constructors
    /** Initializes an empty user with default values. */
    public User() {
        this(0, "", "", "", null, "", "", null, null);
    }

    /** Master constructor for full initialization.
     *
     * @param id persistent identifier
     * @param username unique username
     * @param firstname first name
     *
     * @param lastname last name
     * @param birthday birth date
     * @param email unique email address
     *
     * @param phone optional phone number
     * @param is_admin whether the user is an administrator
     *
     * @param account_status account status */
    public User(long id, String username, String firstname, String lastname, LocalDate birthday, String email,
                String phone, Boolean is_admin, AccountStatus account_status) {
        this.id = id;
        this.username = username;
        this.firstname = firstname;
        this.lastname = lastname;
        this.birthday = birthday;
        this.email = email;
        this.phone = phone;
        this.is_admin = is_admin != null ? is_admin : false;
        this.account_status = account_status != null ? account_status : ACTIVE;
    }

    /** Creates an unsaved user.
     *
     * @param username unique username
     * @param firstname first name
     * @param lastname last name
     *
     * @param birthday birth date
     * @param email unique email address
     * @param phone optional phone number
     *
     * @param is_admin whether the user is an administrator
     * @param account_status account status */
    public User(String username, String firstname, String lastname, LocalDate birthday, String email, String phone,
                boolean is_admin, AccountStatus account_status) {
        this(0, username, firstname, lastname, birthday, email, phone, is_admin, account_status);
    }


    //getters and withers
    /**
     * Returns the id.
     * @return id
     */
    public long getId() { return id; }
    /**
     * Returns a copy with an updated id.
     * @param newId replacement id
     * @return copy with the updated id
     */
    public User withId(long newId) {
        return new User(newId, username, firstname, lastname, birthday, email, phone, is_admin, account_status);
    }

    /**
     * Returns the username.
     * @return username
     */
    public String getUsername() { return username; }
    /**
     * Returns a copy with an updated username.
     * @param newUsername replacement username
     * @return copy with the updated username
     */
    public User withUsername(String newUsername) {
        return new User(id, newUsername, firstname, lastname, birthday, email, phone, is_admin, account_status);
    }

    /**
     * Returns the firstname.
     * @return firstname
     */
    public String getFirstname() { return firstname; }
    /**
     * Returns a copy with an updated first name.
     * @param newFirstname replacement first name
     * @return copy with the updated first name
     */
    public User withFirstName(String newFirstname) {
        return new User(id, username, newFirstname, lastname, birthday, email, phone, is_admin, account_status);
    }

    /**
     * Returns the lastname.
     * @return lastname
     */
    public String getLastname() { return lastname; }
    /**
     * Returns a copy with an updated last name.
     * @param newLastname replacement last name
     * @return copy with the updated last name
     */
    public User withLastName(String newLastname) {
        return new User(id, username, firstname, newLastname, birthday, email, phone, is_admin, account_status);
    }

    /**
     * Returns the birthday.
     * @return birthday
     */
    public LocalDate getBirthday() { return birthday; }
    /**
     * Returns a copy with an updated birthday.
     * @param newBirthday replacement birthday
     * @return copy with the updated birthday
     */
    public User withBirthday(LocalDate newBirthday) {
        return new User(id, username, firstname, lastname, newBirthday, email, phone, is_admin, account_status);
    }

    /**
     * Returns the email.
     * @return email
     */
    public String getEmail() { return email; }
    /**
     * Returns a copy with an updated email.
     * @param newEmail replacement email
     * @return copy with the updated email
     */
    public User withEmail(String newEmail) {
        return new User(id, username, firstname, lastname, birthday, newEmail, phone, is_admin, account_status);
    }

    /**
     * Returns the phone.
     * @return phone
     */
    public String getPhone() { return phone; }
    /**
     * Returns a copy with an updated phone.
     * @param newPhone replacement phone
     * @return copy with the updated phone
     */
    public User withPhone(String newPhone) {
        return new User(id, username, firstname, lastname, birthday, email, newPhone, is_admin, account_status);
    }

    /**
     * Indicates whether the user has administrator privileges.
     * @return true when the user is an administrator
     */
    public boolean isAdmin() { return is_admin; }
    /**
     * Returns a copy with an updated is admin.
     * @param newIsAdmin replacement is admin
     * @return copy with the updated is admin
     */
    public User withIsAdmin(boolean newIsAdmin) {
        return new User(id, username, firstname, lastname, birthday, email, phone, newIsAdmin, account_status);
    }

    /**
     * Returns the account status.
     * @return account status
     */
    public AccountStatus getAccountStatus() { return account_status; }
    /**
     * Returns a copy with an updated account status.
     * @param newAccountStatus replacement account status
     * @return copy with the updated account status
     */
    public User withAccountStatus(AccountStatus newAccountStatus) {
        return new User(id, username, firstname, lastname, birthday, email, phone, is_admin, newAccountStatus);
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
                "isAdmin=" + is_admin + "; " +
                "accountStatus=" + account_status + ";" +
                "}";
    }

    /** Compares users based on ID or username and email uniqueness. */
    @Override
    public boolean equals(Object other) {
        if (this == other) return true;
        if (other == null || getClass() != other.getClass()) return false;
        User user = (User) other;
        if (id != 0 && user.id != 0){
            return id == user.id;
        }
        return Objects.equals(username, user.username) && Objects.equals(email, user.email);
    }

    @Override
    public int hashCode() {
        if(id != 0){
            return Objects.hash(id);
        }
        return Objects.hash(username, email);
    }
}
