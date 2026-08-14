package Venue_Event_Manager.service;

import Venue_Event_Manager.config.TransactionManager;
import Venue_Event_Manager.domain.model.user.*;
import Venue_Event_Manager.repository.UserRepository;
import Venue_Event_Manager.exception.ConflictException;
import Venue_Event_Manager.exception.ForbiddenException;
import Venue_Event_Manager.exception.NotFoundException;
import Venue_Event_Manager.exception.ValidationException;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public class UserService {

    private final TransactionManager transactionManager;
    private final UserRepository userRepository;
    private final AuthService authService;

    /**
     * Initializes UserService with the repository needed to handle users.
     * @param userRepository repository used to access user data
     */
    public UserService(UserRepository userRepository){
        this(userRepository,new AuthService(userRepository));
    }

    UserService(UserRepository userRepository, AuthService authService){
        this.transactionManager = TransactionManager.getInstance();
        this.userRepository = userRepository;
        this.authService = authService;
    }

    /**
     * Gets all users stored in database.
     * @return List of all users
     */
    public List<User> findAll(){
        return transactionManager.inReadOnly( conn ->
                userRepository.findAll( conn ));
    }

    /**
     * Gets a user from its id.
     * @param id the id of the user to find
     * @return User object if found
     * @throws NotFoundException if no user is found with such id
     */
    public User getById(long id){
        return transactionManager.inReadOnly(conn ->
                userRepository.findById(conn,id)
                        .orElseThrow(() -> new NotFoundException("User with id " + id + " not found"))
        );
    }

    /**
     * Gets a user from its email.
     * @param email the email of the user to find
     * @return User object if found
     * @throws ValidationException if email is empty
     * @throws NotFoundException if no user is found with such email
     */
    public User getByEmail(String email){
        if(email == null || email.isEmpty()){
            throw new ValidationException("Email cannot be empty");
        }

        return transactionManager.inReadOnly(conn ->
                userRepository.findByEmail(conn,email))
                        .orElseThrow(() -> new NotFoundException("User with email " + email + " not found"));
    }

    /**
     * Gets a user from its username.
     * @param username the username of the user to find
     * @return User object if found
     * @throws ValidationException if username is empty
     * @throws NotFoundException if no user is found with such username
     */
    public User getByUsername(String username){
        if(username == null || username.isEmpty()){
            throw new ValidationException("Username cannot be empty");
        }

        return transactionManager.inReadOnly(conn ->
                userRepository.findByUsername(conn,username).orElseThrow(() -> new NotFoundException("Username " + username + " not found")));

    }

    /**
     * Gets a user from its phone number.
     * @param phone the phone number of the user to find
     * @return User object if found
     * @throws ValidationException if phone is empty
     * @throws NotFoundException if no user is found with such phone number
     */
    public User getByPhone(String phone){
        if(phone == null || phone.isEmpty()){
            throw new ValidationException("Phone cannot be empty");
        }

        return transactionManager.inReadOnly(conn ->
                userRepository.findByPhone(conn,phone).orElseThrow(() -> new NotFoundException("Phone " + phone + " not found")));

    }

    /**
     * Gets all users with admin privileges.
     * @return List of admin users
     */
    public List<User> getAdmins(){
        return transactionManager.inReadOnly( conn ->
                userRepository.findAllByIsAdmin(conn,true));
    }

    /**
     * Gets all banned users.
     * @return List of banned users
     */
    public List<User> getBannedUsers(){
        return transactionManager.inReadOnly(conn ->
                userRepository.findAllByAccountStatus(conn,AccountStatus.BANNED));
    }

    /**
     * Gets all users with a specific account status.
     * @param accountStatus the status used to filter users
     * @return List of users with the given status
     */
    public List<User> getAccountsWithStatus(AccountStatus accountStatus){
        return transactionManager.inReadOnly( conn ->
                userRepository.findAllByAccountStatus(conn,accountStatus));
    }

    /**
     * Gets the average rating assigned by a user to reviewed events.
     * @param userId the id of the user who submitted the reviews
     * @return average rating, or an empty Optional if the user has not submitted reviews
     */
    public Optional<Double> getAverageRatingGivenByUser(long userId){
        return transactionManager.inReadOnly(conn ->
                userRepository.getAverageRatingGivenByUser(conn,userId));
    }

    /**
     * Inserts a new user in database.
     * @param user the user to insert
     * @param password the password of the new user
     * @return generated id of the new user
     * @throws ValidationException if user data or password are not valid
     */
    public long insert(User user,String password){
        if(user == null) throw new ValidationException("User cannot be null");

        User userToInsert = user
                .withIsAdmin(false)
                .withAccountStatus(AccountStatus.ACTIVE);

        validate(userToInsert);
        String encodedPassword = authService.hashPassword(password);
        return transactionManager.inTransaction(conn ->
                userRepository.insert(conn,userToInsert,encodedPassword));
    }

    /**
     * Updates an existing user after checking the provided password.
     * @param user the user object with updated data
     * @param password the current password of the user
     * @throws ValidationException if user data are not valid
     * @throws ForbiddenException if password is wrong
     */
    public void update(User user, String password){
        validate(user);

        transactionManager.inTransaction(conn -> {
            User storedUser = userRepository.findById(conn,user.getId())
                    .orElseThrow(() -> new NotFoundException("User with id " + user.getId() + " not found"));

            authService.requireValidPassword(conn,user.getId(),password);

            User profileToUpdate = user
                    .withIsAdmin(storedUser.isAdmin())
                    .withAccountStatus(storedUser.getAccountStatus());

            userRepository.update(conn,profileToUpdate);
            return null;
        });
    }

    /**
     * Bans a user after checking admin privileges.
     * @param adminId the id of the admin performing the action
     * @param adminPassword the password of the admin
     * @param userId the id of the user to ban
     * @throws ForbiddenException if admin privileges are missing or password is wrong
     */
    public void ban(long adminId,String adminPassword,long userId){
        authService.requireAdminCredentials(adminId,adminPassword);
        validateUserCanBeBanned(adminId,userId);

        transactionManager.inTransaction(conn -> {
                userRepository.updateAccountStatus(conn,userId,AccountStatus.BANNED);
                return null;
        });

    }

    /**
     * Unbans a user after checking admin privileges.
     * @param adminId the id of the admin performing the action
     * @param adminPassword the password of the admin
     * @param userId the id of the user to unban
     * @throws ForbiddenException if admin privileges are missing or password is wrong
     */
    public void unban(long adminId,String adminPassword,long userId){
        authService.requireAdminCredentials(adminId,adminPassword);
        transactionManager.inTransaction(conn -> {
            userRepository.updateAccountStatus(conn,userId,AccountStatus.ACTIVE);
            return null;
        });

    }

    /**
     * Changes the password of a user after checking the old password.
     * @param userId the id of the user
     * @param oldPassword the current password of the user
     * @param newPassword the new password to set
     * @throws ValidationException if new password is not valid
     * @throws ForbiddenException if old password is wrong
     */
    public void changePassword(long userId, String oldPassword, String newPassword){
        authService.changePassword(userId,oldPassword,newPassword);
    }

    /**
     * Deletes a user after checking the provided password.
     * @param userId the id of the user to delete
     * @param password the current password of the user
     * @throws ForbiddenException if password is wrong
     */
    public void deleteUser(long userId, String password){
        transactionManager.inTransaction(conn -> {
            authService.requireValidPassword(conn,userId,password);
            userRepository.deleteById(conn,userId);
            return null;
        });
    }

    /**
     * Validates all user fields before insert or update.
     * @param user the user to validate
     * @throws ValidationException if one or more fields are not valid
     */
    private void validate(User user){
        if(user == null) throw new ValidationException("User cannot be null");
        validateUsername(user.getUsername());
        validateFirstName(user.getFirstname());
        validateLastName(user.getLastname());
        validateEmail(user.getEmail(), user.getId());
        validatePhone(user.getPhone());
        validateBirthday(user.getBirthday());
    }

    /**
     * Validates username format and length.
     * @param username the username to validate
     * @throws ValidationException if username is empty or has invalid length
     */
    private void validateUsername(String username){
        if(username == null || username.isEmpty()) throw new ValidationException("Username cannot be empty");
        if(username.length()<2 || username.length() >35) throw new ValidationException("Username must be between 2 and 35 characters");
    }

    /**
     * Validates first name format and length.
     * @param firstName the first name to validate
     * @throws ValidationException if first name is empty or has invalid length
     */
    private void validateFirstName(String firstName){
        if(firstName == null || firstName.isEmpty()) throw new ValidationException("First name cannot be empty");
        if(firstName.length() < 2 || firstName.length() >35) throw new ValidationException("First name must be between 2 and 35 characters");
    }

    /**
     * Validates last name format and length.
     * @param lastName the last name to validate
     * @throws ValidationException if last name is empty or has invalid length
     */
    private void validateLastName(String lastName){
        if(lastName == null || lastName.isEmpty()) throw new ValidationException("Last name cannot be empty");
        if(lastName.length() <2 || lastName.length() >35) throw new ValidationException("Last name must be between 2 and 35 characters");
    }

    /**
     * Validates email format and uniqueness.
     * @param email the email to validate
     * @param currentUserId the id of the user being validated
     * @throws ValidationException if email is empty, invalid or already used
     */
    private void validateEmail(String email, long currentUserId){
        if(email == null || email.isEmpty()) throw new ValidationException("Email cannot be empty");
        if(!email.matches("^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$")) throw new ValidationException("Invalid email format");
        if(transactionManager.inReadOnly(conn ->
                userRepository.findByEmail(conn,email)
                        .map(existingUser -> existingUser.getId() != currentUserId)
                        .orElse(false))) {
            throw new ValidationException("Email already used");
        }
    }

    /**
     * Validates phone format and length.
     * @param phone the phone to validate
     * @throws ValidationException if phone has invalid format or length
     */
    private void validatePhone(String phone){
        if(phone == null || phone.isBlank()) return;
        if(phone.length() < 5 || phone.length() > 20) throw new ValidationException("Phone must be between 5 and 20 characters");
        if(!phone.matches("^\\+?[0-9 ]+$")) throw new ValidationException("Invalid phone format");
    }

    /**
     * Validates birthday date.
     * @param birthday the birthday to validate
     * @throws ValidationException if birthday is empty or outside accepted range
     */
    private void validateBirthday(LocalDate birthday){
        if(birthday == null) throw new ValidationException("Birthday cannot be empty");
        if(birthday.isAfter(LocalDate.now())) throw new ValidationException("Birthday must be before "+LocalDate.now());
        if(birthday.isBefore(LocalDate.of(1900,1,1))) throw new ValidationException("Birthday cannot be before "+LocalDate.of(1900,1,1));
    }

    /**
     * Validates if a user can be banned.
     * @param adminId the id of the admin performing the action
     * @param userId the id of the user to ban
     * @throws ForbiddenException if admin is trying to ban himself or another admin
     * @throws ConflictException if user is already banned
     */
    private void validateUserCanBeBanned(long adminId, long userId){
        if(adminId == userId) throw new ForbiddenException("Admin cannot ban himself");

        User user = transactionManager.inReadOnly(conn ->
                userRepository.findById(conn,userId)
                        .orElseThrow(() -> new NotFoundException("User with id " + userId + " not found")));

        if(user.isAdmin()) throw new ForbiddenException("Cannot ban an admin user");
        if(user.getAccountStatus() == AccountStatus.BANNED) throw new ConflictException("User is already banned");
    }




}
