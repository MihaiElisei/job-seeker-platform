package com.itschool.job_seeker.services;

import com.itschool.job_seeker.entity.Users;
import com.itschool.job_seeker.model.UsersDTO;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public interface UsersService {

    /**
     * Add a new user
     *
     * @param usersDTO the user to add
     * @return the added user
     */
    UsersDTO addUser(UsersDTO usersDTO);

    /**
     * Find user by Email
     *
     * @param email the post to add
     * @return the found user
     */
    Optional<UsersDTO> findUserByEmail(String email);

    /**
     * Retrieve the profile of the currently authenticated user.
     *
     * @return an object containing the profile information of the current user
     */
    Object getCurrentUserProfile();

    /**
     * Retrieve the currently authenticated user entity.
     *
     * @return the Users entity representing the currently authenticated user
     */
    Users getCurrentUser();

    /**
     * Find a user by their email address, returning the Users entity.
     *
     * @param username the email of the user to find
     * @return the Users entity corresponding to the specified email,
     *         or null if no user is found
     */
    Users findByEmail(String username);
}
