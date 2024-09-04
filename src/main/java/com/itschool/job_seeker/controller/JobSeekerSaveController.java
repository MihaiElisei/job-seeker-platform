package com.itschool.job_seeker.controller;

import com.itschool.job_seeker.entity.JobPostActivity;
import com.itschool.job_seeker.entity.JobSeekerProfile;
import com.itschool.job_seeker.entity.JobSeekerSave;
import com.itschool.job_seeker.entity.Users;
import com.itschool.job_seeker.services.JobPostActivityService;
import com.itschool.job_seeker.services.JobSeekerProfileService;
import com.itschool.job_seeker.services.UsersService;
import com.itschool.job_seeker.services.impl.JobSeekerSaveServiceImpl;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Controller
public class JobSeekerSaveController {

    private final UsersService usersService; // Service for user-related operations
    private final JobSeekerProfileService jobSeekerProfileService; // Service for job seeker profiles
    private final JobPostActivityService jobPostActivityService; // Service for job posts
    private final JobSeekerSaveServiceImpl jobSeekerSaveService; // Service for saving job seeker saves
    private final JobSeekerSaveServiceImpl jobSeekerSaveServiceImpl; // Additional service reference for job seeker saves

    // Constructor for dependency injection of services
    public JobSeekerSaveController(UsersService usersService,
                                   JobSeekerProfileService jobSeekerProfileService,
                                   JobPostActivityService jobPostActivityService,
                                   JobSeekerSaveServiceImpl jobSeekerSaveService,
                                   JobSeekerSaveServiceImpl jobSeekerSaveServiceImpl) {
        this.usersService = usersService;
        this.jobSeekerProfileService = jobSeekerProfileService;
        this.jobPostActivityService = jobPostActivityService;
        this.jobSeekerSaveService = jobSeekerSaveService; // Initializing the primary save service
        this.jobSeekerSaveServiceImpl = jobSeekerSaveServiceImpl; // Initializing the additional save service
    }

    /**
     * Saves a job for the authenticated job seeker.
     *
     * This method handles POST requests to the "job-details/save/{id}" URL,
     * which allows a user to save a job post for later reference.
     *
     * @param id the ID of the job post to save
     * @param jobSeekerSave the object containing details of the job seeker saving the job
     * @return String redirecting to the dashboard
     */
    @PostMapping("job-details/save/{id}")
    public String save(@PathVariable("id") Long id, JobSeekerSave jobSeekerSave) {
        // Get the current user's authentication details
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        // Ensure the user is authenticated (not anonymous)
        if (!(authentication instanceof AnonymousAuthenticationToken)) {
            String username = authentication.getName(); // Get the authenticated user's username
            Users user = usersService.findByEmail(username); // Fetch user by their email
            Optional<JobSeekerProfile> seekerProfile = jobSeekerProfileService.getOne(user.getUserId()); // Get the job seeker profile by user ID
            JobPostActivity jobPostActivity = jobPostActivityService.getOne(id); // Get the job post activity by ID

            // Check if the job seeker profile and job post exist
            if (seekerProfile.isPresent() && jobPostActivity != null) {
                // Set the job and user ID in the jobSeekerSave entity
                jobSeekerSave.setJob(jobPostActivity);
                jobSeekerSave.setUserId(seekerProfile.get());
            } else {
                throw new RuntimeException("User not found"); // Handle case where profile or job post is not found
            }
            jobSeekerSaveService.addNew(jobSeekerSave); // Save the jobSeekerSave entity to the database
        }

        return "redirect:/dashboard/"; // Redirect to dashboard after saving the job
    }

    /**
     * Displays saved jobs for the authenticated job seeker.
     *
     * This method handles GET requests to the "saved-jobs/" URL to show
     * the list of jobs saved by the user.
     *
     * @param model the Model object used to pass data to the view
     * @return String representing the view name (saved-jobs)
     */
    @GetMapping("saved-jobs/")
    public String savedJobs(Model model) {
        // Initialize a list to hold the saved job posts
        List<JobPostActivity> jobPost = new ArrayList<>();
        Object currentUserProfile = usersService.getCurrentUserProfile(); // Get the current user's profile

        // Retrieve the list of jobs saved by the current job seeker
        List<JobSeekerSave> jobSeekerSaveList = jobSeekerSaveServiceImpl.getCandidatesJob((JobSeekerProfile) currentUserProfile);

        // Loop through saved jobs and add them to the job post list
        for (JobSeekerSave jobSeekerSave : jobSeekerSaveList) {
            jobPost.add(jobSeekerSave.getJob()); // Add the job associated with the saved entry
        }

        // Add the job post list and user profile to the model for the view
        model.addAttribute("jobPost", jobPost);
        model.addAttribute("user", currentUserProfile);
        return "saved-jobs"; // Return the view for saved jobs
    }
}