package com.itschool.job_seeker.controller;

import com.itschool.job_seeker.entity.*;
import com.itschool.job_seeker.model.*;
import com.itschool.job_seeker.services.JobPostActivityService;
import com.itschool.job_seeker.services.UsersService;
import com.itschool.job_seeker.services.impl.JobSeekerApplyServiceImpl;
import com.itschool.job_seeker.services.impl.JobSeekerSaveServiceImpl;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Objects;

@Controller
public class JobPostActivityController {

    private final UsersService usersService; // Service for user-related operations
    private final JobPostActivityService jobPostActivityService; // Service for job post activities
    private final JobSeekerApplyServiceImpl jobSeekerApplyServiceImpl; // Service for job applications
    private final JobSeekerSaveServiceImpl jobSeekerSaveServiceImpl; // Service for saved job posts

    // Constructor to initialize services
    public JobPostActivityController(UsersService usersService, JobPostActivityService jobPostActivityService, JobSeekerApplyServiceImpl jobSeekerApplyServiceImpl, JobSeekerSaveServiceImpl jobSeekerSaveServiceImpl) {
        this.usersService = usersService;
        this.jobPostActivityService = jobPostActivityService;
        this.jobSeekerApplyServiceImpl = jobSeekerApplyServiceImpl;
        this.jobSeekerSaveServiceImpl = jobSeekerSaveServiceImpl;
    }

    /**
     * Handler for displaying the job search dashboard.
     *
     * This method retrieves the current user's profile and checks their authentication status.
     * If the user is a recruiter, it fetches the jobs they have posted and adds
     * this information to the model to be displayed on the dashboard.
     *
     * @param model the Model object used to pass data to the view
     * @param job the job title to filter
     * @param location the location to filter
     * @param partTime part-time filter
     * @param fullTime full-time filter
     * @param freelance freelance filter
     * @param remoteOnly filter for remote jobs
     * @param officeOnly filter for office jobs
     * @param hybrid filter for hybrid jobs
     * @param today filter for jobs posted today
     * @param days7 filter for jobs posted in the last 7 days
     * @param days30 filter for jobs posted in the last 30 days
     * @return String representing the view name (dashboard)
     */
    @GetMapping("/dashboard/")
    public String searchJobs(Model model,
                             @RequestParam(value = "job", required = false) String job, // Job title filter
                             @RequestParam(value = "location", required = false) String location, // Location filter
                             @RequestParam(value = "partTime", required = false) String partTime, // Part-time filter
                             @RequestParam(value = "fullTime", required = false) String fullTime, // Full-time filter
                             @RequestParam(value = "freelance", required = false) String freelance, // Freelance filter
                             @RequestParam(value = "remoteOnly", required = false) String remoteOnly, // Remote jobs filter
                             @RequestParam(value = "officeOnly", required = false) String officeOnly, // Office jobs filter
                             @RequestParam(value = "hybrid", required = false) String hybrid, // Hybrid jobs filter
                             @RequestParam(value = "today", required = false) boolean today, // Jobs posted today filter
                             @RequestParam(value = "days7", required = false) boolean days7, // Jobs posted in the last 7 days filter
                             @RequestParam(value = "days30", required = false) boolean days30 // Jobs posted in the last 30 days filter
    ) {

        // Adds filter parameters to the model for access in the view
        model.addAttribute("partTime", Objects.equals(partTime, "Part-Time"));
        model.addAttribute("fullTime", Objects.equals(fullTime, "Full-Time"));
        model.addAttribute("freelance", Objects.equals(freelance, "Freelance"));
        model.addAttribute("remoteOnly", Objects.equals(remoteOnly, "Remote-Only"));
        model.addAttribute("officeOnly", Objects.equals(officeOnly, "Office-Only"));
        model.addAttribute("hybrid", Objects.equals(hybrid, "Hybrid"));
        model.addAttribute("today", today);
        model.addAttribute("days7", days7);
        model.addAttribute("days30", days30);
        model.addAttribute("job", job); // Add job title to the model
        model.addAttribute("location", location); // Add location to the model

        LocalDate searchDate = null; // Initialize variable for search date
        List<JobPostActivityDTO> jobPost = null; // List for job postings

        boolean dateSearchFlag = false; // Flag to check if a date filter is applied
        boolean remote = true; // Flag to check if remote jobs should be included
        boolean type = true; // Flag for determining job type selections

        // Determine the search date based on filter flags
        if (days30) {
            searchDate = LocalDate.now().minusDays(30); // Set date for last 30 days
        } else if (days7) {
            searchDate = LocalDate.now().minusDays(7); // Set date for last 7 days
        } else if (today) {
            searchDate = LocalDate.now(); // Set date to today
        } else {
            dateSearchFlag = false; // No specific date filter applied
        }

        // Set default selections if no job type filter is specifically chosen
        if (partTime == null && fullTime == null && freelance == null) {
            partTime = "Part-Time"; // Default to Part-Time jobs
            fullTime = "Full-Time"; // Default to Full-Time jobs
            freelance = "Freelance"; // Default to Freelance jobs
            remote = false; // Assume not filtering by remote jobs by default
        }

        // Set default selections for location type if none specified
        if (officeOnly == null && remoteOnly == null && hybrid == null) {
            officeOnly = "Office-Only"; // Default to Office-Only jobs
            remoteOnly = "Remote-Only"; // Default to Remote-Only jobs
            hybrid = "Hybrid"; // Default to Hybrid jobs
            type = false; // Assume not filtering by job type
        }

        // Determine which job posts to fetch based on selected criteria
        if (!dateSearchFlag && !remote && !type && !StringUtils.hasText(job) && !StringUtils.hasText(location)) {
            jobPost = jobPostActivityService.getAll(); // Fetch all jobs if no filters specified
        } else {
            // Fetch jobs based on provided search criteria
            jobPost = jobPostActivityService.search(job, location, Arrays.asList(partTime, fullTime, freelance),
                    Arrays.asList(remoteOnly, officeOnly, hybrid), searchDate);
        }

        // Retrieve the current user profile
        Object currentUserProfile = usersService.getCurrentUserProfile();
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        // Check if the user is authenticated
        if (auth instanceof AnonymousAuthenticationToken == false) {
            String currentUsername = auth.getName(); // Get authenticated username
            model.addAttribute("username", currentUsername); // Add username to the model

            // If the user is a recruiter, get their posted jobs
            if (auth.getAuthorities().contains(new SimpleGrantedAuthority("Recruiter"))) {
                List<RecruiterJobsDTO> recruiterJobs = jobPostActivityService.getRecruiterJobs(((RecruiterProfile) currentUserProfile).getUserAccountId());
                model.addAttribute("jobPost", recruiterJobs); // Add recruiter jobs to the model
            } else {
                // If the user is a job seeker
                List<JobSeekerApply> jobSeekerApplyList = jobSeekerApplyServiceImpl.getCandidatesJobs((JobSeekerProfile) currentUserProfile);
                List<JobSeekerSave> jobSeekerSaveList = jobSeekerSaveServiceImpl.getCandidatesJob((JobSeekerProfile) currentUserProfile);

                // Check each job post to see if it's active or saved by the user
                boolean exist;
                boolean saved;

                for (JobPostActivityDTO jobActivity : jobPost) {
                    exist = false; // Flag to indicate if the job was found in applied jobs
                    saved = false; // Flag to indicate if the job was saved by the user
                    for (JobSeekerApply jobSeekerApply : jobSeekerApplyList) {
                        if (Objects.equals(jobActivity.getJobPostId(), jobSeekerApply.getJob().getJobPostId())) {
                            jobActivity.setIsActive(true); // Set job as active if found in applied jobs
                            exist = true;
                            break; // Exit the loop if found
                        }
                    }
                    for (JobSeekerSave jobSeekerSave : jobSeekerSaveList) {
                        if (Objects.equals(jobActivity.getJobPostId(), jobSeekerSave.getJob().getJobPostId())) {
                            jobActivity.setIsSaved(true); // Set job as saved if found in saved jobs
                            saved = true;
                            break; // Exit the loop if found
                        }
                    }
                    // If job not found in applied list, set its status
                    if (!exist) {
                        jobActivity.setIsActive(false); // Mark as not active if not applied
                    }
                    if (!saved) {
                        jobActivity.setIsSaved(false); // Mark as not saved if not saved
                    }
                }
                model.addAttribute("jobPost", jobPost); // Add updated job postings to the model
            }
        }
        model.addAttribute("user", currentUserProfile); // Add current user profile to the model
        return "dashboard"; // Return view name for the dashboard
    }

    /**
     * Handler for the global job search feature.
     *
     * This method processes the parameters for job search and retrieves the job postings
     * based on the filters applied by the user.
     *
     * @param model the Model object used to pass data to the view
     * @param job the job title to filter
     * @param location the location to filter
     * @param partTime part-time filter
     * @param fullTime full-time filter
     * @param freelance freelance filter
     * @param remoteOnly filter for remote jobs
     * @param officeOnly filter for office jobs
     * @param hybrid filter for hybrid jobs
     * @param today filter for jobs posted today
     * @param days7 filter for jobs posted in the last 7 days
     * @param days30 filter for jobs posted in the last 30 days
     * @return String representing the view name (global-search)
     */
    @GetMapping("global-search/")
    public String globalSearch(
            Model model,
            @RequestParam(value = "job", required = false) String job,
            @RequestParam(value = "location", required = false) String location,
            @RequestParam(value = "partTime", required = false) String partTime,
            @RequestParam(value = "fullTime", required = false) String fullTime,
            @RequestParam(value = "freelance", required = false) String freelance,
            @RequestParam(value = "remoteOnly", required = false) String remoteOnly,
            @RequestParam(value = "officeOnly", required = false) String officeOnly,
            @RequestParam(value = "hybrid", required = false) String hybrid,
            @RequestParam(value = "today", required = false) boolean today,
            @RequestParam(value = "days7", required = false) boolean days7,
            @RequestParam(value = "days30", required = false) boolean days30) {

        // Adds filter parameters to the model for access in the view
        model.addAttribute("partTime", Objects.equals(partTime, "Part-Time"));
        model.addAttribute("fullTime", Objects.equals(fullTime, "Full-Time"));
        model.addAttribute("freelance", Objects.equals(freelance, "Freelance"));
        model.addAttribute("remoteOnly", Objects.equals(remoteOnly, "Remote-Only"));
        model.addAttribute("officeOnly", Objects.equals(officeOnly, "Office-Only"));
        model.addAttribute("hybrid", Objects.equals(hybrid, "Hybrid"));
        model.addAttribute("today", today);
        model.addAttribute("days7", days7);
        model.addAttribute("days30", days30);
        model.addAttribute("job", job); // Add job title to the model
        model.addAttribute("location", location); // Add location to the model

        LocalDate searchDate = null; // Initialize search date variable
        List<JobPostActivityDTO> jobPost = null; // List for job postings

        boolean dateSearchFlag = false; // Flag to check if a date filter is applied
        boolean remote = true; // Flag to check if remote jobs should be included
        boolean type = true; // Flag to determine job type selections

        // Determine the search date based on filter flags
        if (days30) {
            searchDate = LocalDate.now().minusDays(30); // Set date for last 30 days
        } else if (days7) {
            searchDate = LocalDate.now().minusDays(7); // Set date for last 7 days
        } else if (today) {
            searchDate = LocalDate.now(); // Set date to today
        } else {
            dateSearchFlag = false; // No specific date filter applied
        }

        // Set default selections if no job type filter is specifically chosen
        if (partTime == null && fullTime == null && freelance == null) {
            partTime = "Part-Time"; // Default to Part-Time jobs
            fullTime = "Full-Time"; // Default to Full-Time jobs
            freelance = "Freelance"; // Default to Freelance jobs
            remote = false; // Assume not filtering by remote jobs if none selected
        }

        // Set default selections for location type if none specified
        if (officeOnly == null && remoteOnly == null && hybrid == null) {
            officeOnly = "Office-Only"; // Default to Office-Only jobs
            remoteOnly = "Remote-Only"; // Default to Remote-Only jobs
            hybrid = "Hybrid"; // Default to Hybrid jobs
            type = false; // Assume not filtering by job type
        }

        // Determine which job posts to fetch based on selected criteria
        if (!dateSearchFlag && !remote && !type && !StringUtils.hasText(job) && !StringUtils.hasText(location)) {
            jobPost = jobPostActivityService.getAll(); // Fetch all jobs if no filters specified
        } else {
            // Fetch jobs based on provided search criteria
            jobPost = jobPostActivityService.search(job, location,
                    Arrays.asList(partTime, fullTime, freelance),
                    Arrays.asList(remoteOnly, officeOnly, hybrid), searchDate);
        }

        model.addAttribute("jobPost", jobPost); // Add the job postings to the model
        return "global-search"; // Return view name for the global search
    }

    /**
     * Handler for rendering the job posting form.
     *
     * Prepares a new JobPostActivity object to be filled in the form
     * for adding a new job post and adds the user profile to the model.
     *
     * @param model the Model object used to pass data to the view
     * @return String representing the view name (add-jobs)
     */
    @GetMapping("/dashboard/add")
    public String addJobs(Model model) {
        model.addAttribute("jobPostActivity", new JobPostActivity()); // Add a new job post activity object
        model.addAttribute("user", usersService.getCurrentUserProfile()); // Add current user profile to the model
        return "add-jobs"; // Return view name for adding job
    }

    /**
     * Handler for processing the addition of a new job post.
     *
     * This method accepts a JobPostActivityDTO, sets the current user as the poster,
     * and saves the new job post. After saving, it redirects to the dashboard.
     *
     * @param jobPostActivityDTO the DTO containing data for the new job post
     * @param model the Model object used to pass data to the view
     * @return String redirecting to the dashboard after processing
     */
    @PostMapping("/dashboard/addNew")
    public String addNew(JobPostActivityDTO jobPostActivityDTO, Model model) {

        Users users = usersService.getCurrentUser(); // Get the current authenticated user

        if (users != null) {
            jobPostActivityDTO.setPostedById(users); // Set the poster of the job post
        }

        jobPostActivityDTO.setPostedDate(new Date(System.currentTimeMillis())); // Set the current date as the posted date
        model.addAttribute("jobPostActivity", jobPostActivityDTO); // Add job post activity to the model
        JobPostActivityDTO saved = jobPostActivityService.addNew(jobPostActivityDTO); // Save the new job post

        // Continue processing and saving
        return "redirect:/dashboard/"; // Redirect to the dashboard
    }

    /**
     * Handler for rendering the edit job posting form.
     *
     * This method fetches an existing job post by its ID and returns the edit form
     * populated with the job details.
     *
     * @param id the ID of the job post to edit
     * @param model the Model object used to pass data to the view
     * @return String representing the view name (add-jobs)
     */
    @PostMapping("/dashboard/edit/{id}")
    public String editJob(@PathVariable("id") Long id, Model model) {
        JobPostActivity jobPostActivity = jobPostActivityService.getOne(id); // Retrieve the job post by its ID
        model.addAttribute("jobPostActivity", jobPostActivity); // Add job post activity to the model for editing
        model.addAttribute("user", usersService.getCurrentUserProfile()); // Add current user profile to the model
        return "add-jobs"; // Return view name for editing job
    }
}