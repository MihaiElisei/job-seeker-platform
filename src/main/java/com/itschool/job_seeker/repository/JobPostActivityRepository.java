package com.itschool.job_seeker.repository;

import com.itschool.job_seeker.entity.JobPostActivity;
import com.itschool.job_seeker.model.IRecruiterJobs;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface JobPostActivityRepository extends JpaRepository<JobPostActivity, Long> {

    /**
     * Retrieves a list of job postings associated with a specific recruiter.
     *
     * This query joins the job_post_activity, job_location, and job_company tables
     * to gather job posting information, including total candidates (set to 0 as a placeholder),
     * job details, and associated location and company information for a given recruiter ID.
     *
     * @param recruiter the ID of the recruiter whose job postings are to be retrieved
     * @return List<IRecruiterJobs> a list of IRecruiterJobs indicating job posting details
     */
    @Query(value = "SELECT 0 AS totalCandidates, j.job_post_id, j.job_title, " +
            "l.id AS locationId, l.city, l.county, l.country, c.id AS companyId, c.name " +
            "FROM job_post_activity j " +
            "INNER JOIN job_location l ON j.job_location_id = l.id " +
            "INNER JOIN job_company c ON j.job_company_id = c.id " +
            "WHERE j.posted_by_id = :recruiter " +
            "GROUP BY j.job_post_id, j.job_title, l.id, l.city, l.county, l.country, c.id, c.name",
            nativeQuery = true)
    List<IRecruiterJobs> getRecruiterJobs(@Param("recruiter") Long recruiter);


    /**
     * Searches for job postings based on job title, location, job type, and remote work options,
     * when no specific date is provided.
     *
     * This query joins job_post_activity and job_location tables to filter job postings that match
     * the given criteria. It allows partial matches in job title and location fields.
     *
     * @param job the job title keyword to search for
     * @param location the location keyword to search for (city, country, or county)
     * @param remote the list of remote options to filter by
     * @param type the list of job types to filter by
     * @return List<JobPostActivity> a list of JobPostActivity matching the search criteria
     */
    @Query(value = "SELECT * FROM job_post_activity j INNER JOIN job_location l on j.job_location_id=l.id  WHERE j" +
            ".job_title LIKE %:job%"
            + " AND (l.city LIKE %:location%"
            + " OR l.country LIKE %:location%"
            + " OR l.county LIKE %:location%) " +
            " AND (j.job_type IN(:type)) " +
            " AND (j.remote IN(:remote)) ", nativeQuery = true)

    List<JobPostActivity> searchWithoutDate(@Param("job") String job,
                                            @Param("location") String location,
                                            @Param("remote")List<String> remote,
                                            @Param("type")List<String> type);

    /**
     * Searches for job postings based on job title, location, job type, remote work options,
     * and a specific posted date.
     *
     * This query joins job_post_activity and job_location tables to filter job postings
     * that match the specified criteria, including a date condition for posted jobs.
     *
     * @param job the job title keyword to search for
     * @param location the location keyword to search for (city, country, or county)
     * @param remote the list of remote options to filter by
     * @param type the list of job types to filter by
     * @param date the date to filter job postings that were posted on or after this date
     * @return List<JobPostActivity> a list of JobPostActivity matching the search criteria
     */
    @Query(value = "SELECT * FROM job_post_activity j INNER JOIN job_location l on j.job_location_id=l.id  WHERE j" +
            ".job_title LIKE %:job%"
            + " AND (l.city LIKE %:location%"
            + " OR l.country LIKE %:location%"
            + " OR l.county LIKE %:location%) " +
            " AND (j.job_type IN(:type)) " +
            " AND (j.remote IN(:remote)) " +
            " AND (posted_date >= :date)", nativeQuery = true)
    List<JobPostActivity> search(@Param("job") String job,
                                 @Param("location") String location,
                                 @Param("remote") List<String> remote,
                                 @Param("type") List<String> type,
                                 @Param("date") LocalDate date);
}
