package com.itschool.job_seeker.repository;

import com.itschool.job_seeker.entity.JobPostActivity;
import com.itschool.job_seeker.entity.JobSeekerApply;
import com.itschool.job_seeker.entity.JobSeekerProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface JobSeekerApplyRepository extends JpaRepository<JobSeekerApply, Long> {

    /**
     * Retrieves a list of job applications submitted by a specific job seeker.
     *
     * This method uses the JobSeekerProfile entity to filter job applications based on the user ID,
     * allowing the retrieval of all applications made by a particular job seeker.
     *
     * @param userId a JobSeekerProfile instance that identifies the job seeker
     * @return List<JobSeekerApply> a list of JobSeekerApply instances corresponding to the user's applications
     */
    List<JobSeekerApply> findUserByUserId(JobSeekerProfile userId);

    /**
     * Retrieves a list of job applications associated with a specific job posting.
     *
     * This method filters job applications based on the JobPostActivity entity,
     * allowing the retrieval of all applications related to a specific job post.
     *
     * @param job a JobPostActivity instance that represents the job posting for which applications are being queried
     * @return List<JobSeekerApply> a list of JobSeekerApply instances corresponding to the specified job post
     */
    List<JobSeekerApply> findByJob(JobPostActivity job);
}
