package com.itschool.job_seeker.services.impl;

import com.itschool.job_seeker.entity.JobSeekerProfile;
import com.itschool.job_seeker.entity.RecruiterProfile;
import com.itschool.job_seeker.entity.Users;
import com.itschool.job_seeker.entity.UsersType;
import com.itschool.job_seeker.model.UsersDTO;
import com.itschool.job_seeker.repository.JobSeekerProfileRepository;
import com.itschool.job_seeker.repository.RecruiterProfileRepository;
import com.itschool.job_seeker.repository.UsersRepository;
import com.itschool.job_seeker.repository.UsersTypeRepository;
import com.itschool.job_seeker.services.UsersService;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.Optional;

@Component
public class UsersServiceImpl implements UsersService {

    private final UsersRepository usersRepository;
    private final UsersTypeRepository usersTypeRepository;
    private final JobSeekerProfileRepository jobSeekerProfileRepository;
    private final RecruiterProfileRepository recruiterProfileRepository;
    private final PasswordEncoder passwordEncoder;

    public UsersServiceImpl(UsersRepository usersRepository, UsersTypeRepository usersTypeRepository, JobSeekerProfileRepository jobSeekerProfileRepository, RecruiterProfileRepository recruiterProfileRepository, PasswordEncoder passwordEncoder) {
        this.usersRepository = usersRepository;
        this.usersTypeRepository = usersTypeRepository;
        this.jobSeekerProfileRepository = jobSeekerProfileRepository;
        this.recruiterProfileRepository = recruiterProfileRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Add a new user
     *
     * @param usersDTO the user to add
     * @return the added user
     */
    @Override
    public UsersDTO addUser(UsersDTO usersDTO) {
        if (usersDTO == null) {
            throw new IllegalArgumentException("UsersDTO cannot be null");
        }

        // Check for existing user with the same email if needed
        if (usersRepository.findByEmail(usersDTO.getEmail()).isPresent()) {
            throw new IllegalArgumentException("Email already registered");
        }

        // Convert DTO to entity
        Users user = convertToEntity(usersDTO);


        // Save the user
        Users savedUser = usersRepository.save(user);

        // Handle user type and save the corresponding profile
        if (usersDTO.getUserTypeId() == 1) {
            RecruiterProfile recruiterProfile = new RecruiterProfile(savedUser);
            recruiterProfileRepository.save(recruiterProfile);
        } else {
            JobSeekerProfile jobSeekerProfile = new JobSeekerProfile(savedUser);
            jobSeekerProfileRepository.save(jobSeekerProfile);
        }

        // Convert the saved user back to DTO
        return convertToDTO(savedUser);
    }

    /**
     * Find user by email
     *
     * @param email find email in  user repository
     *
     * @return the found user with the email address
     */
    @Override
    public Optional<UsersDTO> findUserByEmail(String email) {
        // Check if the email is null or empty
        if (email == null || email.trim().isEmpty()) {
            return Optional.empty(); // Return an empty Optional if the email is invalid
        }

        // Find the user by email
        Optional<Users> userOptional = usersRepository.findByEmail(email);

        // Convert to UsersDTO if present
        return userOptional.map(this::convertToDTO); // Convert Users to UsersDTO if user is found
    }

    /**
     * Retrieves the current logged-in user's profile
     *
     * @return the user's profile (either RecruiterProfile or JobSeekerProfile)
     */
    @Override
    public Object getCurrentUserProfile() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication(); // Retrieve authentication from security context

        // If the user is authenticated (not anonymous)
        if(!(authentication instanceof AnonymousAuthenticationToken)) {
            String username = authentication.getName(); // Get the username of the authenticated user
            Users users = usersRepository.findByEmail(username).orElseThrow(() -> new UsernameNotFoundException("User not found")); // Find the user in the repository

            Long userId = users.getUserId(); // Retrieve user ID

            // Return the appropriate profile based on user type
            if(authentication.getAuthorities().contains(new SimpleGrantedAuthority("Recruiter"))){
                // If the user is a Recruiter
                return recruiterProfileRepository.findById(userId).orElse(new RecruiterProfile()); // Return the RecruiterProfile
            } else {
                // If the user is a Job Seeker
                return jobSeekerProfileRepository.findById(userId).orElse(new JobSeekerProfile()); // Return the JobSeekerProfile
            }
        }
        return null;
    }

    /**
     * Retrieves the current logged-in user
     *
     * @return the current user's data transfer object
     */
    @Override
    public Users getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        // Check if the user is authenticated (not anonymous)
        if(!(authentication instanceof AnonymousAuthenticationToken)) {
            String username = authentication.getName(); // Get the username of the authenticated user
            return usersRepository.findByEmail(username)
                    .orElseThrow(() -> new UsernameNotFoundException("Could not find " + username));
        }
        return null;  // Return null if the user is not authenticated
    }

    /**
     * Find a user by their email and return the Users entity.
     *
     * @param username the email address of the user
     * @return the found Users entity
     * @throws UsernameNotFoundException if no user is found with the associated email
     */
    @Override
    public Users findByEmail(String username) {
        return usersRepository.findByEmail(username).orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }

    /**
     * Converts a Users entity to UsersDTO.
     *
     * @param users the Users entity to convert
     * @return the converted UsersDTO
     */
    private UsersDTO convertToDTO(Users users) {
        if (users == null) {
            return null;
        }

        UsersDTO usersDTO = new UsersDTO();
        usersDTO.setUserId(users.getUserId());
        usersDTO.setEmail(users.getEmail());
        usersDTO.setPassword(passwordEncoder.encode(users.getPassword()));
        usersDTO.setActive(true);
        usersDTO.setRegistrationDate(new Date(System.currentTimeMillis()));

        // Directly set the userTypeId
        if (users.getUserTypeId() != null) {
            usersDTO.setUserTypeId(users.getUserTypeId().getUserTypeId());
        }
        return usersDTO;
    }

    /**
     * Converts a UsersDTO to Users entity.
     *
     * @param usersDTO the UsersDTO to convert
     * @return the converted Users entity
     * @throws IllegalArgumentException if the user type ID is invalid
     */

    private Users convertToEntity(UsersDTO usersDTO) {
        if (usersDTO == null) {
            return null;
        }

        Users users = new Users();
        users.setUserId(usersDTO.getUserId());
        users.setEmail(usersDTO.getEmail());
        users.setPassword(passwordEncoder.encode(usersDTO.getPassword()));
        users.setActive(true);
        users.setRegistrationDate(new Date(System.currentTimeMillis()));

        // Convert the userTypeId from Long to UsersType
        if (usersDTO.getUserTypeId() != null) {
            UsersType userType = usersTypeRepository.findById(usersDTO.getUserTypeId())
                    .orElseThrow(() -> new IllegalArgumentException("Invalid user type ID: " + usersDTO.getUserTypeId()));
            users.setUserTypeId(userType);
        }

        return users;
    }
}

