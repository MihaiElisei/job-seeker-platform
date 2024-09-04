package com.itschool.job_seeker.services.impl;

import com.itschool.job_seeker.entity.Users;
import com.itschool.job_seeker.repository.UsersRepository;
import com.itschool.job_seeker.services.CustomUserDetailsService;
import com.itschool.job_seeker.util.CustomUserDetails;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;


@Component
public class CustomUserDetailServiceImpl implements CustomUserDetailsService {

    private final UsersRepository usersRepository;

    public CustomUserDetailServiceImpl(UsersRepository usersRepository) {
        this.usersRepository = usersRepository;
    }

    /**
     * Loads user details by username (in this case, by email).
     *
     * @param username the email of the user to be loaded
     * @return UserDetails the details of the user found in the repository
     * @throws UsernameNotFoundException if the user with the specified email is not found
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        Users user =usersRepository.findByEmail(username).orElseThrow(() -> new UsernameNotFoundException("Could not find user " + username));

        return new CustomUserDetails(user); // Wrap the Users entity into CustomUserDetails and return it
    }
}
