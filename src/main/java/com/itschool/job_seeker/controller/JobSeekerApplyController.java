package com.itschool.job_seeker.controller;

import com.itschool.job_seeker.entity.*;
import com.itschool.job_seeker.model.RecruiterProfileDTO;
import com.itschool.job_seeker.services.JobPostActivityService;
import com.itschool.job_seeker.services.JobSeekerProfileService;
import com.itschool.job_seeker.services.RecruiterProfileService;
import com.itschool.job_seeker.services.UsersService;
import com.itschool.job_seeker.services.impl.JobSeekerApplyServiceImpl;
import com.itschool.job_seeker.services.impl.JobSeekerSaveServiceImpl;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping
public class JobSeekerApplyController {

    private final JobPostActivityService jobPostActivityService;
    private final UsersService usersService;
    private final JobSeekerApplyServiceImpl jobSeekerApplyService;
    private final JobSeekerSaveServiceImpl jobSeekerSaveService;
    private final RecruiterProfileService recruiterProfileService;
    private final JobSeekerProfileService jobSeekerProfileService;

    public JobSeekerApplyController(JobPostActivityService jobPostActivityService, UsersService usersService, JobSeekerApplyServiceImpl jobSeekerApplyService, JobSeekerSaveServiceImpl jobSeekerSaveService, RecruiterProfileService recruiterProfileService, JobSeekerProfileService jobSeekerProfileService) {
        this.jobPostActivityService = jobPostActivityService;
        this.usersService = usersService;
        this.jobSeekerApplyService = jobSeekerApplyService;
        this.jobSeekerSaveService = jobSeekerSaveService;
        this.recruiterProfileService = recruiterProfileService;
        this.jobSeekerProfileService = jobSeekerProfileService;
    }

    /**
     * Displays the details of a specific job post for application.
     *
     * This method handles GET requests to the "/job-details-apply/{id}" URL,
     * retrieves the job details based on the provided job post ID, and adds
     * the job details and the current user's profile to the model.
     *
     * @param id the ID of the job post whose details are to be displayed
     * @param model the Model object used to pass data to the view
     * @return String the name of the view to be rendered (job-details)
     */
    @GetMapping("/job-details-apply/{id}")
    public String display(@PathVariable("id") Long id, Model model) {
        JobPostActivity jobDetails = jobPostActivityService.getOne(id);
        List<JobSeekerApply> jobSeekerApplyList = jobSeekerApplyService.getJobCandidates(jobDetails);
        List<JobSeekerSave> jobSeekerSaveList = jobSeekerSaveService.getJobCandidates(jobDetails);

        // Obtain the authentication object to check user type
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        // Check if the user is authenticated
        if(!(authentication instanceof AnonymousAuthenticationToken)) {
            // If the authenticated user is a recruiter
            if(authentication.getAuthorities().contains(new SimpleGrantedAuthority("Recruiter"))) {
               RecruiterProfileDTO user = recruiterProfileService.getCurrentRecruiterProfile(); // Get the current recruiter's profile
                if(user != null) {
                   model.addAttribute("applyList", jobSeekerApplyList); // Add the candidate applications to the model
               }
            }else{
                JobSeekerProfile user = jobSeekerProfileService.getCurrentSeekerProfile(); // Get the current job seeker profile
                if(user != null) {
                    boolean exists = false;
                    boolean saved = false;
                    for(JobSeekerApply jobSeekerApply : jobSeekerApplyList) {
                        if(jobSeekerApply.getUserId().getUserAccountId() == user.getUserAccountId()) {
                            exists = true;
                            break;
                        }
                    }
                    for(JobSeekerSave jobSeekerSave : jobSeekerSaveList) {
                        if(jobSeekerSave.getUserId().getUserAccountId() == user.getUserAccountId()) {
                            saved = true;
                            break;
                        }
                    }
                    // Add flags to model to indicate whether the job-seeker has already applied or saved the job
                    model.addAttribute("alreadyApplied", exists);
                    model.addAttribute("alreadySaved", saved);
                }
            }
        }
        JobSeekerApply jobSeekerApply = new JobSeekerApply(); // Create a new JobSeekerApply object for application
        model.addAttribute("applyJob", jobSeekerApply);// Add it to the model
        model.addAttribute("jobDetails", jobDetails); // Add job details to the model
        model.addAttribute("user", usersService.getCurrentUserProfile()); // Add current user profile to the model
        return "job-details";
    }

    /**
     * Handles the job application process.
     *
     * This method processes POST requests to the "/job-details/apply/{id}" URL.
     * It creates a new job application for the job seeker and redirects them to the dashboard.
     *
     * @param id the ID of the job post to apply for
     * @param jobSeekerApply the JobSeekerApply entity to hold application data
     * @return String redirecting to the dashboard after processing
     */
    @PostMapping("/job-details/apply/{id}")
    public String apply(@PathVariable("id") Long id, JobSeekerApply jobSeekerApply) {

        // Obtain the authentication object to check user type
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        // Check if the user is authenticated
        if (!(authentication instanceof AnonymousAuthenticationToken)) {
            String username = authentication.getName(); // Get the authenticated user's username
            Users user = usersService.findByEmail(username); // Fetch the user by email

            // Fetching the JobSeekerProfile entity directly, not the DTO
            Optional<JobSeekerProfile> seekerProfile = jobSeekerProfileService.getOne(user.getUserId());
            JobPostActivity jobPostActivity = jobPostActivityService.getOne(id);

            // Check if the seeker profile and job post are valid
            if (seekerProfile.isPresent() && jobPostActivity != null) {
                jobSeekerApply = new JobSeekerApply();
                jobSeekerApply.setUserId(seekerProfile.get());
                jobSeekerApply.setJob(jobPostActivity);
                jobSeekerApply.setApplyDate(new Date(System.currentTimeMillis()));
            } else {
                throw new RuntimeException("user not found"); // Handle case where user is not valid
            }
            jobSeekerApplyService.addNew(jobSeekerApply); // Save the new job application
        }
        return "redirect:/dashboard/";
    }
}
