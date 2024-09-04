package com.itschool.job_seeker.services.impl;

import com.itschool.job_seeker.entity.JobCompany;
import com.itschool.job_seeker.entity.JobLocation;
import com.itschool.job_seeker.entity.JobPostActivity;
import com.itschool.job_seeker.model.IRecruiterJobs;
import com.itschool.job_seeker.model.JobPostActivityDTO;
import com.itschool.job_seeker.model.RecruiterJobsDTO;
import com.itschool.job_seeker.repository.JobPostActivityRepository;
import com.itschool.job_seeker.services.JobPostActivityService;
import jakarta.persistence.Transient;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Component
public class JobPostActivityServiceImpl implements JobPostActivityService {

    private final ModelMapper modelMapper;
    private final JobPostActivityRepository jobPostActivityRepository;

    // Constructor for JobPostActivityServiceImpl
    public JobPostActivityServiceImpl(ModelMapper modelMapper, JobPostActivityRepository jobPostActivityRepository) {
        this.modelMapper = modelMapper;
        this.jobPostActivityRepository = jobPostActivityRepository;
    }

    /**
     * Adds a new job post activity.
     *
     * @param jobPostActivityDTO the DTO containing information for the new job post activity
     * @return JobPostActivityDTO the DTO representation of the saved job post activity
     */
    @Transient
    @Override
    public JobPostActivityDTO addNew(JobPostActivityDTO jobPostActivityDTO) {
        // Convert DTO to entity
        JobPostActivity jobPostActivity = mapToJobPostActivity(jobPostActivityDTO);

        // Save the entity using the repository
        JobPostActivity savedJobPostActivity = jobPostActivityRepository.save(jobPostActivity);

        // Convert the saved entity back to DTO and return it
        return mapToJobPostActivityDTO(savedJobPostActivity);
    }

    /**
     * Retrieves job postings associated with a specific recruiter.
     *
     * @param recruiter the ID of the recruiter whose job postings are to be retrieved
     * @return List<RecruiterJobsDTO> a list of RecruiterJobsDTO objects representing the job postings
     */
    @Override
    public List<RecruiterJobsDTO> getRecruiterJobs(Long recruiter) {
        List<IRecruiterJobs> recruiterJobsDTO = jobPostActivityRepository.getRecruiterJobs(recruiter);

        List<RecruiterJobsDTO> recruiterJobsDTOList = new ArrayList<>();

        for(IRecruiterJobs recruiterJob : recruiterJobsDTO) {
            JobLocation location = new JobLocation(recruiterJob.getLocationId(), recruiterJob.getCity(), recruiterJob.getCounty(),recruiterJob.getCountry());
            JobCompany company = new JobCompany(recruiterJob.getCompanyId(), recruiterJob.getName(),"");
            recruiterJobsDTOList.add(new RecruiterJobsDTO(recruiterJob.getTotalCandidates(),recruiterJob.getJob_post_id(),recruiterJob.getJob_title(),location,company));
        }

        return recruiterJobsDTOList;
    }

    /**
     * Retrieves a job post activity by its ID.
     *
     * @param id the ID of the job post activity to retrieve
     * @return JobPostActivity the entity corresponding to the specified ID
     * @throws RuntimeException if the job post activity is not found
     */
    @Override
    public JobPostActivity getOne(Long id) {
        return jobPostActivityRepository.findById(id).orElseThrow(()-> new RuntimeException("Job not found"));
    }

    /**
     * Retrieves all job post activities.
     *
     * @return List<JobPostActivityDTO> a list of all JobPostActivityDTO objects
     */
    @Override
    public List<JobPostActivityDTO> getAll() {

        // Fetch all job post activities from the repository
        List<JobPostActivity> jobPostActivities = jobPostActivityRepository.findAll();
        List<JobPostActivityDTO> jobPostActivityDTOList = new ArrayList<>();

        // Convert each job post activity to its DTO representation
        for (JobPostActivity jobPostActivity : jobPostActivities) {
            jobPostActivityDTOList.add(mapToJobPostActivityDTO(jobPostActivity));
        }
        return jobPostActivityDTOList; // Return the list of JobPostActivityDTO
    }


    /**
     * Searches for job postings based on various criteria.
     *
     * @param job the job title to search for
     * @param location the job location to search for
     * @param types the types of job (e.g., full-time, part-time) to filter by
     * @param remote the remote working options to filter by
     * @param searchDate the date to filter by (may be null)
     * @return List<JobPostActivityDTO> a list of job postings matching the search criteria
     */
    @Override
    public List<JobPostActivityDTO> search(String job, String location, List<String> types, List<String> remote, LocalDate searchDate) {

        List<JobPostActivity> jobPostActivities;
        // Perform search based on the presence of searchDate
        if (Objects.isNull(searchDate)) {
            jobPostActivities = jobPostActivityRepository.searchWithoutDate(job, location, remote, types);
        } else {
            jobPostActivities = jobPostActivityRepository.search(job, location, remote, types, searchDate);
        }

        // Map the list of JobPostActivity to JobPostActivityDTO and return
        return jobPostActivities.stream()
                .map(this::mapToJobPostActivityDTO)
                .toList(); // Collecting the DTOs to a list
    }

    /**
     * Converts a JobPostActivity entity to JobPostActivityDTO.
     *
     * @param jobPostActivity the JobPostActivity entity to convert
     * @return JobPostActivityDTO the corresponding DTO representation
     */
    private JobPostActivityDTO mapToJobPostActivityDTO(JobPostActivity jobPostActivity) {
        return modelMapper.map(jobPostActivity, JobPostActivityDTO.class);
    }

    /**
     * Converts a JobPostActivityDTO to JobPostActivity entity.
     *
     * @param jobPostActivityDTO the JobPostActivityDTO to convert
     * @return JobPostActivity the corresponding entity representation
     */
    private JobPostActivity mapToJobPostActivity(JobPostActivityDTO jobPostActivityDTO) {
        return modelMapper.map(jobPostActivityDTO, JobPostActivity.class);
    }
}
