package com.itschool.job_seeker.services.impl;

import com.itschool.job_seeker.entity.JobSeekerProfile;
import com.itschool.job_seeker.entity.Users;
import com.itschool.job_seeker.model.JobSeekerProfileDTO;
import com.itschool.job_seeker.repository.JobSeekerProfileRepository;
import com.itschool.job_seeker.repository.UsersRepository;
import com.itschool.job_seeker.services.JobSeekerProfileService;
import org.modelmapper.ModelMapper;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class JobSeekerProfileServiceImpl implements JobSeekerProfileService {

    private final JobSeekerProfileRepository jobSeekerProfileRepository;
    private final ModelMapper modelMapper;
    private final UsersRepository usersRepository;

    public JobSeekerProfileServiceImpl(JobSeekerProfileRepository jobSeekerProfileRepository, ModelMapper modelMapper, UsersRepository usersRepository) {
        this.jobSeekerProfileRepository = jobSeekerProfileRepository;
        this.modelMapper = modelMapper;
        this.usersRepository = usersRepository;
    }

    /**
     * Retrieves a job-seeker profile by its ID.
     *
     * @param id the identifier of the job-seeker profile to retrieve
     * @return an Optional containing the JobSeekerProfileDTO if found, or empty if not found
     */
    @Override
    public Optional<JobSeekerProfile> getOne(Long id) {
        return jobSeekerProfileRepository.findById(id);
    }

    /**
     * Adds a new job-seeker profile.
     *
     * @param jobSeekerProfileDTO the DTO containing the details of the new job-seeker profile
     * @return the newly created JobSeekerProfileDTO
     */
    @Override
    public JobSeekerProfileDTO addNew(JobSeekerProfileDTO jobSeekerProfileDTO) {
        // Convert DTO to Entity
        JobSeekerProfile jobSeekerProfile = mapToJobSeekerProfile(jobSeekerProfileDTO);
        // Save entity to the database
        JobSeekerProfile savedJobSeekerProfile = jobSeekerProfileRepository.save(jobSeekerProfile);
        // Convert saved entity back to DTO
        return mapToJobSeekerProfileDTO(savedJobSeekerProfile);
    }

    /**
     * Retrieves the currently authenticated job-seeker’s profile.
     *
     * @return the JobSeekerProfile if found, or null if the user is not authenticated
     */
    @Override
    public JobSeekerProfile getCurrentSeekerProfile() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (!(authentication instanceof AnonymousAuthenticationToken)) {
            String username = authentication.getName();
            Users user = usersRepository.findByEmail(username)
                    .orElseThrow(() -> new UsernameNotFoundException("User Not Found"));

            return getOne(user.getUserId()).orElse(null); // Handle the absence appropriately
        }
        return null; // Handle unauthenticated users appropriately (throw an exception, return null, etc.)
    }

    /**
     * Converts a JobSeekerProfile entity to JobSeekerProfileDTO.
     *
     * @param jobSeekerProfile the RecruiterProfile entity to convert
     * @return corresponding JobSeekerProfileDTO
     */
    public JobSeekerProfileDTO mapToJobSeekerProfileDTO(JobSeekerProfile jobSeekerProfile) {
        return modelMapper.map(jobSeekerProfile, JobSeekerProfileDTO.class);
    }

    /**
     * Converts a JobSeekerProfileDTO to JobSeekerProfile entity.
     *
     * @param jobSeekerProfileDTO the JobSeekerProfileDTO to convert
     * @return corresponding JobSeekerProfile entity
     */
    public JobSeekerProfile mapToJobSeekerProfile(JobSeekerProfileDTO jobSeekerProfileDTO) {
        return modelMapper.map(jobSeekerProfileDTO, JobSeekerProfile.class);
    }
}
