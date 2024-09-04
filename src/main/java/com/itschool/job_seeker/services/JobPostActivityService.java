package com.itschool.job_seeker.services;

import com.itschool.job_seeker.entity.JobPostActivity;
import com.itschool.job_seeker.model.JobPostActivityDTO;
import com.itschool.job_seeker.model.RecruiterJobsDTO;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public interface JobPostActivityService {

    /**
     * Adds a new job post activity.
     *
     * @param jobPostActivityDTO the DTO containing the details of the job post activity to be added
     * @return JobPostActivityDTO the DTO of the newly created job post activity
     */
    JobPostActivityDTO addNew(JobPostActivityDTO jobPostActivityDTO);

    /**
     * Retrieves a list of job postings associated with a specific recruiter.
     *
     * @param recruiter the ID of the recruiter whose job postings are to be retrieved
     * @return List<RecruiterJobsDTO> a list of RecruiterJobsDTOs representing the job postings
     */
    List<RecruiterJobsDTO> getRecruiterJobs(Long recruiter);

    /**
     * Retrieves a single job post activity by its ID.
     *
     * @param id the ID of the job post activity to retrieve
     * @return JobPostActivity the job post activity entity with the specified ID
     */
    JobPostActivity getOne(Long id);

    /**
     * Retrieves all job post activities in the system.
     *
     * @return List<JobPostActivityDTO> a list of all JobPostActivity DTOs
     */
    List<JobPostActivityDTO> getAll();

    /**
     * Searches for job post activities based on various criteria.
     *
     * @param job the job title to search for
     * @param location the location associated with the job postings
     * @param list a list of additional filters (for example, skills or industries)
     * @param list1 a list of sorting options or other criteria
     * @param searchDate the date associated with the job search
     * @return List<JobPostActivityDTO> a list of JobPostActivityDTOs matching the search criteria
     */
    List<JobPostActivityDTO> search(String job, String location, List<String> list, List<String> list1, LocalDate searchDate);

}
