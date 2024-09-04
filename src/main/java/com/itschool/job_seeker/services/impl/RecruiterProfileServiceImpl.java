package com.itschool.job_seeker.services.impl;

import com.itschool.job_seeker.entity.RecruiterProfile;
import com.itschool.job_seeker.entity.Users;
import com.itschool.job_seeker.model.RecruiterProfileDTO;
import com.itschool.job_seeker.repository.RecruiterProfileRepository;
import com.itschool.job_seeker.repository.UsersRepository;
import com.itschool.job_seeker.services.RecruiterProfileService;
import org.modelmapper.ModelMapper;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * Implementation of the RecruiterProfileService interface.
 * This class provides methods for managing recruiter profiles,
 * including retrieving and adding new profiles.
 */

@Component
public class RecruiterProfileServiceImpl implements RecruiterProfileService {

    private final RecruiterProfileRepository recruiterProfileRepository;
    private final UsersRepository usersRepository;
    private final ModelMapper modelMapper;

    /**
     * Constructor for RecruiterProfileServiceImpl.
     *
     * @param recruiterProfileRepository repository for managing recruiter profiles
     * @param modelMapper model mapper for converting between entity and DTO
     */
    public RecruiterProfileServiceImpl(RecruiterProfileRepository recruiterProfileRepository, UsersRepository usersRepository, ModelMapper modelMapper) {
        this.recruiterProfileRepository = recruiterProfileRepository;
        this.usersRepository = usersRepository;
        this.modelMapper = modelMapper;
    }

    /**
     * Retrieves a recruiter profile by its ID.
     *
     * @param id the identifier of the recruiter profile to retrieve
     * @return an Optional containing the RecruiterProfileDTO if found, or empty if not found
     */
    @Override
    public Optional<RecruiterProfileDTO> getOne(Long id) {
        return recruiterProfileRepository.findById(id)
                .map(this::mapToRecruiterProfileDTO);  // Converts Optional<RecruiterProfile> to Optional<RecruiterProfileDTO>
    }

    /**
     * Adds a new recruiter profile.
     *
     * @param recruiterProfileDTO the DTO containing the details of the new recruiter profile
     * @return the newly created RecruiterProfileDTO
     */
    @Override
    public RecruiterProfileDTO addNew(RecruiterProfileDTO recruiterProfileDTO) {
        // Convert DTO to Entity
        RecruiterProfile recruiterProfile = mapToRecruiterProfile(recruiterProfileDTO);
        // Save entity to the database
        RecruiterProfile savedRecruiterProfile = recruiterProfileRepository.save(recruiterProfile);
        // Convert saved entity back to DTO
        return mapToRecruiterProfileDTO(savedRecruiterProfile);
    }

    /**
     * Retrieves the currently authenticated recruiter's profile.
     *
     * @return the RecruiterProfileDTO of the current user, or null if the user is unauthorized
     */
    @Override
    public RecruiterProfileDTO getCurrentRecruiterProfile() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        // Check if the current auth token is not anonymous
        if (!(authentication instanceof AnonymousAuthenticationToken)) {
            String username = authentication.getName();

            // Find the user by email
            Users user = usersRepository.findByEmail(username)
                    .orElseThrow(() -> new UsernameNotFoundException("User Not Found"));

            // Retrieve the recruiter's profile using the found user's ID
            return getOne(user.getUserId()).orElse(null); // Handle the absence appropriately
        }
        return null; // Return null for unauthenticated users
    }

    /**
     * Converts a RecruiterProfile entity to RecruiterProfileDTO.
     *
     * @param recruiterProfile the RecruiterProfile entity to convert
     * @return corresponding RecruiterProfileDTO
     */
    private RecruiterProfileDTO mapToRecruiterProfileDTO(RecruiterProfile recruiterProfile) {
        return modelMapper.map(recruiterProfile, RecruiterProfileDTO.class);
    }

    /**
     * Converts a RecruiterProfileDTO to RecruiterProfile entity.
     *
     * @param recruiterProfileDTO the RecruiterProfileDTO to convert
     * @return corresponding RecruiterProfile entity
     */
    private RecruiterProfile mapToRecruiterProfile(RecruiterProfileDTO recruiterProfileDTO) {
        return modelMapper.map(recruiterProfileDTO, RecruiterProfile.class);
    }
}
