package com.itschool.job_seeker.services.impl;

import com.itschool.job_seeker.entity.JobPostActivity;
import com.itschool.job_seeker.entity.JobSeekerProfile;
import com.itschool.job_seeker.entity.JobSeekerSave;
import com.itschool.job_seeker.repository.JobSeekerSaveRepository;
import com.itschool.job_seeker.services.JobSeekerSaveService;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class JobSeekerSaveServiceImpl implements JobSeekerSaveService {

    private final JobSeekerSaveRepository jobSeekerSaveRepository;

    public JobSeekerSaveServiceImpl(JobSeekerSaveRepository jobSeekerSaveRepository) {
        this.jobSeekerSaveRepository = jobSeekerSaveRepository;
    }

    /**
     * Retrieves a list of job postings saved by a specific job-seeker.
     *
     * @param userAccountId the JobSeekerProfile associated with the job-seeker
     * @return a list of JobSeekerSave entities representing the saved job postings
     */
    public List<JobSeekerSave> getCandidatesJob(JobSeekerProfile userAccountId){
        return jobSeekerSaveRepository.findByUserId(userAccountId);
    }

    /**
     * Retrieves a list of job-seekers who have saved a specific job posting.
     *
     * @param job the JobPostActivity for which to find saved job-seeker records
     * @return a list of JobSeekerSave entities associated with the specified job posting
     */
    public List<JobSeekerSave> getJobCandidates(JobPostActivity job){
        return jobSeekerSaveRepository.findByJob(job);
    }

    /**
     * Saves a new entry of a job-seeker saving a job posting.
     *
     * @param jobSeekerSave the JobSeekerSave entity to be saved
     */
    public void addNew(JobSeekerSave jobSeekerSave){
        jobSeekerSaveRepository.save(jobSeekerSave);
    }
}
