package com.itschool.job_seeker.services;


import com.itschool.job_seeker.model.RecruiterProfileDTO;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public interface RecruiterProfileService {

    /**
     * Retrieves a recruiter profile by its identifier.
     *
     * @param id the identifier of the recruiter profile to retrieve
     * @return an Optional containing the RecruiterProfileDTO if found, or empty if not found
     */
    Optional<RecruiterProfileDTO> getOne(Long id);

    /**
     * Adds a new recruiter profile.
     *
     * @param recruiterProfileDTO the DTO containing the details of the new recruiter profile
     * @return the newly created RecruiterProfileDTO
     */
    RecruiterProfileDTO addNew(RecruiterProfileDTO recruiterProfileDTO);

    /**
     * Retrieves the profile of the currently authenticated recruiter.
     *
     * @return the RecruiterProfileDTO of the currently authenticated recruiter
     */
    RecruiterProfileDTO getCurrentRecruiterProfile();

}
