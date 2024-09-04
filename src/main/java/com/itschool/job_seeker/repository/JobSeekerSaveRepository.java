package com.itschool.job_seeker.repository;

import com.itschool.job_seeker.entity.JobPostActivity;
import com.itschool.job_seeker.entity.JobSeekerProfile;
import com.itschool.job_seeker.entity.JobSeekerSave;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface JobSeekerSaveRepository extends JpaRepository<JobSeekerSave, Long> {

   /**
    * Retrieves a list of saved job postings for a specific job seeker.
    *
    * This method allows you to find all job postings that have been saved
    * by a job seeker, using their JobSeekerProfile instance to filter the results.
    *
    * @param userAccoundId a JobSeekerProfile instance that identifies the job seeker whose saved jobs are being retrieved
    * @return List<JobSeekerSave> a list of JobSeekerSave instances corresponding to the job postings saved by the user
    */
   List<JobSeekerSave> findByUserId(JobSeekerProfile userAccoundId);

   /**
    * Retrieves a list of saved entries associated with a specific job posting.
    *
    * This method allows you to find all records where a particular job post
    * has been saved by any job seeker. It filters records based on the
    * JobPostActivity instance passed as a parameter.
    *
    * @param job a JobPostActivity instance representing the job posting
    * @return List<JobSeekerSave> a list of JobSeekerSave instances associated with the specified job posting
    */
   List<JobSeekerSave> findByJob(JobPostActivity job);

}
