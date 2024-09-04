package com.itschool.job_seeker.services.impl;

import com.itschool.job_seeker.entity.JobPostActivity;
import com.itschool.job_seeker.entity.JobSeekerApply;
import com.itschool.job_seeker.entity.JobSeekerProfile;
import com.itschool.job_seeker.repository.JobSeekerApplyRepository;
import com.itschool.job_seeker.services.JobSeekerApplyService;
import org.springframework.stereotype.Component;

import java.util.List;


@Component
public class JobSeekerApplyServiceImpl implements JobSeekerApplyService {

    private final JobSeekerApplyRepository jobSeekerApplyRepository;

    public JobSeekerApplyServiceImpl(JobSeekerApplyRepository jobSeekerApplyRepository) {
        this.jobSeekerApplyRepository = jobSeekerApplyRepository;
    }

    /**
     * Retrieves a list of job applications submitted by a specific job-seeker.
     *
     * @param userAccountId the JobSeekerProfile of the user to retrieve applications for
     * @return a list of JobSeekerApply entities representing the job applications by the specified job-seeker
     */
    public List<JobSeekerApply> getCandidatesJobs(JobSeekerProfile userAccountId) {
        return jobSeekerApplyRepository.findUserByUserId(userAccountId);
    }

    /**
     * Retrieves a list of job applications for a specific job posting.
     *
     * @param job the JobPostActivity for which to find job applications
     * @return a list of JobSeekerApply entities associated with the specified job posting
     */
    public List<JobSeekerApply> getJobCandidates(JobPostActivity job) {
        return jobSeekerApplyRepository.findByJob(job);
    }

    /**
     * Adds a new job application to the repository.
     *
     * @param jobSeekerApply the JobSeekerApply entity to be saved
     */
    public void addNew(JobSeekerApply jobSeekerApply) {
        jobSeekerApplyRepository.save(jobSeekerApply);
    }
}
