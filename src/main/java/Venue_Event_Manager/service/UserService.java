package Venue_Event_Manager.service;

import Venue_Event_Manager.config.TransactionManager;
import Venue_Event_Manager.domain.model.user.*;
import Venue_Event_Manager.repository.jdbc.PgUserRepository;
import Venue_Event_Manager.repository.UserRepository;
import Venue_Event_Manager.util.*;
import Venue_Event_Manager.exception.*;

import java.sql.Connection;
import java.time.LocalDate;
import java.util.List;

public class UserService {

    private final TransactionManager transactionManager;
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository){
        this.transactionManager = TransactionManager.getInstance();
        this.userRepository = userRepository;
    }

    public List<User> findAll(){
        return transactionManager.inReadOnly( conn ->
                userRepository.findAll( conn ));
    }

    public User getById(long id){
        return transactionManager.inReadOnly(conn ->
                userRepository.findById(conn,id)
                        .orElseThrow(() -> new NotFoundException("User with id " + id + " not found"))
        );
    }

    public User getByEmail(String email){
        if(email == null || email.isEmpty()){
            throw new ValidationException("Email cannot be empty");
        }

        return transactionManager.inReadOnly(conn ->
                userRepository.findByEmail(conn,email))
                        .orElseThrow(() -> new NotFoundException("User with email " + email + " not found"));
    }

    public User getByUsername(String username){
        if(username == null || username.isEmpty()){
            throw new ValidationException("Username cannot be empty");
        }

        return transactionManager.inReadOnly(conn ->
                userRepository.findByUsername(conn,username).orElseThrow(() -> new NotFoundException("Username " + username + " not found")));

    }

    public User getByPhone(String phone){
        if(phone == null || phone.isEmpty()){
            throw new ValidationException("Phone cannot be empty");
        }

        return transactionManager.inReadOnly(conn ->
                userRepository.findByPhone(conn,phone).orElseThrow(() -> new NotFoundException("Phone " + phone + " not found")));

    }

    public List<User> getAdmins(){
        return transactionManager.inReadOnly( conn ->
                userRepository.findAllByIsAdmin(conn,true));
    }

    public List<User> getBannedUsers(){
        return transactionManager.inReadOnly(conn ->
                userRepository.findAllByAccountStatus(conn,AccountStatus.BANNED));
    }

    public List<User> getAccountsWithStatus(AccountStatus accountStatus){
        return transactionManager.inReadOnly( conn ->
                userRepository.findAllByAccountStatus(conn,accountStatus));
    }

    //TODO averageReview

    public long insert(User user,String password){
        validate(user);
        validatePassword(password);
        return transactionManager.inTransaction(conn ->
                userRepository.insert(conn,user,password));
    }

    public void update(User user, String password){
        validate(user);
        validatePassword(password);
        if(!checkPassword(user.getId(), password)){
            throw new ForbiddenException("Wrong password");
        }
        transactionManager.inTransaction(conn -> {
                userRepository.insert(conn,user,password);
                return null;
        });
    }

    public void ban(long adminId,String adminPassword,long userId){
        checkPrivileges(adminId,adminPassword);
        transactionManager.inTransaction(conn -> {
                userRepository.updateAccountStatus(conn,userId,AccountStatus.BANNED);
                return null;
        });

    }

    public void unban(long adminId,String adminPassword,long userId){
        checkPrivileges(adminId,adminPassword);
        transactionManager.inTransaction(conn -> {
            userRepository.updateAccountStatus(conn,userId,AccountStatus.ACTIVE);
            return null;
        });

    }

    public void changePassword(long userId, String oldPassword, String newPassword){
        validatePassword(newPassword);
        if(!checkPassword(userId,oldPassword)) throw new ForbiddenException("Wrong password");
        transactionManager.inTransaction(conn -> {
            userRepository.updatePassword(conn,userId,newPassword);
            return null;
        });

    }

    public void deleteUser(long userId, String password){
        if(!checkPassword(userId,password)) throw new ForbiddenException("Wrong password");
        transactionManager.inTransaction(conn -> {
            userRepository.deleteById(conn,userId);
            return null;
        });
    }

    //-----UTILS-----

    private boolean checkPassword(long userId,String password){
        String dbPassword = transactionManager.inTransaction(conn ->
                userRepository.getPasswordById(conn,userId)
                        .orElseThrow(() -> new NotFoundException("User with id " + userId + " not found")));

        return dbPassword.equals(password);

    }

    private void checkPrivileges(long adminId, String password){
        User admin = transactionManager.inTransaction(conn ->
                userRepository.findById(conn,adminId).orElseThrow(() -> new NotFoundException("Admin does not exist")));
        if(!admin.isAdmin()) throw new ForbiddenException("Admin privileges required for such action");

        if(!checkPassword(adminId,password)) throw new ForbiddenException("Wrong password");
    }

    private void validate(User user){
        validateUsername(user.getUsername());
        validateFirstName(user.getFirstname());
        validateLastName(user.getLastname());
        validateEmail(user.getEmail());
        validateBirthday(user.getBirthday());
    }

    private void validateUsername(String username){
        if(username.isEmpty()) throw new ValidationException("Username cannot be empty");
        if(username.length()<2 || username.length() >35) throw new ValidationException("Username must be between 2 and 35 characters");
    }
    private void validateFirstName(String firstName){
        if(firstName.isEmpty()) throw new ValidationException("First name cannot be empty");
        if(firstName.length() < 2 || firstName.length() >35) throw new ValidationException("First name must be between 2 and 35 characters");
    }
    private void validateLastName(String lastName){
        if(lastName.isEmpty()) throw new ValidationException("Last name cannot be empty");
        if(lastName.length() <2 || lastName.length() >35) throw new ValidationException("Last name must be between 2 and 35 characters");
    }
    private void validateEmail(String email){
        if(email.isEmpty()) throw new ValidationException("Email cannot be empty");
        if(!email.matches("^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$")) throw new ValidationException("Invalid email format");
        if(transactionManager.inReadOnly(conn -> userRepository.findByEmail(conn,email).isPresent())) throw new ValidationException("Email already used");
    }
    private void validatePassword(String password){
        if(password.isEmpty()) throw new ValidationException("Password cannot be empty");
        if(password.length() < 8 || password.length() > 30) throw new ValidationException("Password must be between 8 and 30 characters");
    }
    private void validateBirthday(LocalDate birthday){
        if(birthday == null) throw new ValidationException("Birthday cannot be empty");
        if(birthday.isAfter(LocalDate.now())) throw new ValidationException("Birthday must be before "+LocalDate.now());
        if(birthday.isBefore(LocalDate.of(1900,1,1))) throw new ValidationException("Birthday must be before "+LocalDate.of(1900,1,1));
    }




}
