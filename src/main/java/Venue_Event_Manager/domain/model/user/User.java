package Venue_Event_Manager.domain.model.user;

import java.time.LocalDate;
import java.util.Objects;
import static Venue_Event_Manager.domain.model.user.AccountStatus.*;

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
     * Performs the {@code getId} operation.
     * @return operation result
     */
    public long getId() { return id; }
    /**
     * Performs the {@code withId} operation.
     * @param newId newId value
     * @return operation result
     */
    public User withId(long newId) {
        return new User(newId, username, firstname, lastname, birthday, email, phone, is_admin, account_status);
    }

    /**
     * Performs the {@code getUsername} operation.
     * @return operation result
     */
    public String getUsername() { return username; }
    /**
     * Performs the {@code withUsername} operation.
     * @param newUsername newUsername value
     * @return operation result
     */
    public User withUsername(String newUsername) {
        return new User(id, newUsername, firstname, lastname, birthday, email, phone, is_admin, account_status);
    }

    /**
     * Performs the {@code getFirstname} operation.
     * @return operation result
     */
    public String getFirstname() { return firstname; }
    /**
     * Performs the {@code withFirstName} operation.
     * @param newFirstname newFirstname value
     * @return operation result
     */
    public User withFirstName(String newFirstname) {
        return new User(id, username, newFirstname, lastname, birthday, email, phone, is_admin, account_status);
    }

    /**
     * Performs the {@code getLastname} operation.
     * @return operation result
     */
    public String getLastname() { return lastname; }
    /**
     * Performs the {@code withLastName} operation.
     * @param newLastname newLastname value
     * @return operation result
     */
    public User withLastName(String newLastname) {
        return new User(id, username, firstname, newLastname, birthday, email, phone, is_admin, account_status);
    }

    /**
     * Performs the {@code getBirthday} operation.
     * @return operation result
     */
    public LocalDate getBirthday() { return birthday; }
    /**
     * Performs the {@code withBirthday} operation.
     * @param newBirthday newBirthday value
     * @return operation result
     */
    public User withBirthday(LocalDate newBirthday) {
        return new User(id, username, firstname, lastname, newBirthday, email, phone, is_admin, account_status);
    }

    /**
     * Performs the {@code getEmail} operation.
     * @return operation result
     */
    public String getEmail() { return email; }
    /**
     * Performs the {@code withEmail} operation.
     * @param newEmail newEmail value
     * @return operation result
     */
    public User withEmail(String newEmail) {
        return new User(id, username, firstname, lastname, birthday, newEmail, phone, is_admin, account_status);
    }

    /**
     * Performs the {@code getPhone} operation.
     * @return operation result
     */
    public String getPhone() { return phone; }
    /**
     * Performs the {@code withPhone} operation.
     * @param newPhone newPhone value
     * @return operation result
     */
    public User withPhone(String newPhone) {
        return new User(id, username, firstname, lastname, birthday, email, newPhone, is_admin, account_status);
    }

    /**
     * Performs the {@code isAdmin} operation.
     * @return operation result
     */
    public boolean isAdmin() { return is_admin; }
    /**
     * Performs the {@code withIsAdmin} operation.
     * @param newIsAdmin newIsAdmin value
     * @return operation result
     */
    public User withIsAdmin(boolean newIsAdmin) {
        return new User(id, username, firstname, lastname, birthday, email, phone, newIsAdmin, account_status);
    }

    /**
     * Performs the {@code getAccountStatus} operation.
     * @return operation result
     */
    public AccountStatus getAccountStatus() { return account_status; }
    /**
     * Performs the {@code withAccountStatus} operation.
     * @param newAccountStatus newAccountStatus value
     * @return operation result
     */
    public User withAccountStatus(AccountStatus newAccountStatus) {
        return new User(id, username, firstname, lastname, birthday, email, phone, is_admin, newAccountStatus);
    }


    @Override
    /**
     * Performs the {@code toString} operation.
     * @return operation result
     */
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
    /**
     * Performs the {@code equals} operation.
     * @param other other value
     * @return operation result
     */
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
    /**
     * Performs the {@code hashCode} operation.
     * @return operation result
     */
    public int hashCode() {
        if(id != 0){
            return Objects.hash(id);
        }
        return Objects.hash(username, email);
    }
}
