package Venue_Event_Manager.service;

import Venue_Event_Manager.config.TransactionManager;
import Venue_Event_Manager.domain.model.user.*;
import Venue_Event_Manager.repository.jdbc.PgUserRepository;
import Venue_Event_Manager.repository.UserRepository;
import Venue_Event_Manager.util.*;
import Venue_Event_Manager.exception.*;

import java.sql.Connection;
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
                userRepository.findAll( conn );
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



}
