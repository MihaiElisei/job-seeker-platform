package com.itschool.job_seeker.services;

import com.itschool.job_seeker.entity.JobSeekerProfile;
import com.itschool.job_seeker.model.JobSeekerProfileDTO;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public interface JobSeekerProfileService {

    /**
     * Retrieves a job seeker profile by its unique identifier.
     *
     * @param id the identifier of the job-seeker profile to retrieve
     * @return an Optional containing the JobSeekerProfile if found,
     *         or empty if no profile exists for the given identifier
     */
    Optional<JobSeekerProfile> getOne(Long id);

    /**
     * Adds a new job-seeker profile to the system.
     *
     * @param jobSeekerProfileDTO the DTO containing details of the new job-seeker profile
     * @return the newly created JobSeekerProfileDTO representing the added profile
     */
    JobSeekerProfileDTO addNew(JobSeekerProfileDTO jobSeekerProfileDTO);

    /**
     * Retrieves the profile of the currently authenticated job-seeker.
     *
     * @return the JobSeekerProfile representing the currently authenticated job-seeker
     */
    JobSeekerProfile getCurrentSeekerProfile();
}
