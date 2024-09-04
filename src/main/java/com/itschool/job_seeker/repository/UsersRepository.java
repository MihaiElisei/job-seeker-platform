package com.itschool.job_seeker.repository;

import com.itschool.job_seeker.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UsersRepository extends JpaRepository<Users, Long> {

    /**
     * Retrieves a user based on their email address.
     *
     * This method allows for the retrieval of a user entity by searching
     * through the database for a matching email address. It returns
     * an Optional wrapper around the Users entity to handle cases where
     * the user may not be found.
     *
     * @param email the email address of the user to be retrieved
     * @return Optional<Users> an Optional containing the Users instance if found,
     *                         or an empty Optional if no user with the specified email exists
     */
    Optional<Users> findByEmail(String email);
}
