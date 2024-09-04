package com.itschool.job_seeker.controller;

import com.itschool.job_seeker.entity.JobSeekerProfile;
import com.itschool.job_seeker.entity.Skills;
import com.itschool.job_seeker.entity.Users;
import com.itschool.job_seeker.model.JobSeekerProfileDTO;
import com.itschool.job_seeker.repository.UsersRepository;
import com.itschool.job_seeker.services.JobSeekerProfileService;
import com.itschool.job_seeker.util.FileDownloadUtil;
import com.itschool.job_seeker.util.FileUploadUtil;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;


@Controller
@RequestMapping("/job-seeker-profile")
public class JobSeekerProfileController {

    private final JobSeekerProfileService jobSeekerProfileService; // Service to handle job seeker profiles
    private final UsersRepository usersRepository; // Repository for user data

    // Constructor for dependency injection of services and repositories
    public JobSeekerProfileController(JobSeekerProfileService jobSeekerProfileService, UsersRepository usersRepository) {
        this.jobSeekerProfileService = jobSeekerProfileService;
        this.usersRepository = usersRepository;
    }

    /**
     * Displays the job seeker profile for the authenticated user.
     *
     * This method handles GET requests to the "/job-seeker-profile/" URL.
     *
     * @param model the Model object used to pass data to the view
     * @return String representing the view name (job-seeker-profile)
     */
    @GetMapping("/")
    public String JobSeekerProfile(Model model) {
        // Retrieve authentication details to identify the current user
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        // Ensure that the user is authenticated (not anonymous)
        if (!(authentication instanceof AnonymousAuthenticationToken)) {
            String currentUsername = authentication.getName(); // Get the authenticated user's username

            // Fetch user by email, throwing an exception if the user is not found
            Users user = usersRepository.findByEmail(currentUsername)
                    .orElseThrow(() -> new UsernameNotFoundException("User not found"));

            // Retrieve the job-seeker profile associated with the user
            Optional<JobSeekerProfile> jobSeekerProfileOpt = jobSeekerProfileService.getOne(user.getUserId());

            // Check if the profile exists
            if (jobSeekerProfileOpt.isPresent()) {
                JobSeekerProfile jobSeekerProfile = jobSeekerProfileOpt.get(); // Get the profile object
                model.addAttribute("profile", jobSeekerProfile); // Add the profile to the model

                // Initialize skills if necessary
                if (jobSeekerProfile.getSkills() == null || jobSeekerProfile.getSkills().isEmpty()) {
                    List<Skills> skills = new ArrayList<>();
                    skills.add(new Skills()); // Add a default skill entry
                    jobSeekerProfile.setSkills(skills); // Set the skills in the profile
                }

                model.addAttribute("skills", jobSeekerProfile.getSkills()); // Add skills to the model
            }
        }

        return "job-seeker-profile"; // Return the job seeker profile view
    }

    /**
     * Handles the addition or updating of a job seeker profile.
     *
     * This method processes POST requests to the "/job-seeker-profile/addNew" URL.
     *
     * @param jobSeekerProfileDTO the data transfer object with profile details
     * @param image the profile photo uploaded by the user
     * @param pdf the resume uploaded by the user
     * @param model the Model object used to pass data to the view
     * @return String redirecting to the dashboard after processing
     */
    @PostMapping("/addNew")
    public String addNew(JobSeekerProfileDTO jobSeekerProfileDTO,
                         @RequestParam("image") MultipartFile image,
                         @RequestParam("pdf") MultipartFile pdf,
                         Model model) {
        // Retrieve authentication details to identify the current user
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication(); // Get authentication details

        // Check if the user is authenticated
        if (!(authentication instanceof AnonymousAuthenticationToken)) {
            String currentUsername = authentication.getName(); // Get the authenticated user's username
            Users user = usersRepository.findByEmail(currentUsername)
                    .orElseThrow(() -> new UsernameNotFoundException("User not found")); // Handle user not found

            // Set the user ID and user account ID in the DTO for profile saving
            jobSeekerProfileDTO.setUserId(user);
            jobSeekerProfileDTO.setUserAccountId(user.getUserId());
        }

        List<Skills> skillsList = new ArrayList<>(); // Prepare to hold skills
        model.addAttribute("profile", jobSeekerProfileDTO); // Add the profile DTO to the model
        model.addAttribute("skills", skillsList); // Add the empty skills list to the model

        // Initialize skills if necessary
        if (jobSeekerProfileDTO.getSkills() == null || jobSeekerProfileDTO.getSkills().isEmpty()) {
            List<Skills> skills = new ArrayList<>();
            for (Skills skill : jobSeekerProfileDTO.getSkills()) {
                skills.add(skill); // Add each provided skill
            }
            jobSeekerProfileDTO.setSkills(skills); // Set the updated skills in the DTO
        }

        String imageName = ""; // Initialize filename for the profile photo
        String resumeName = ""; // Initialize filename for the resume

        // Check if a profile photo file was uploaded
        if (!image.getOriginalFilename().equals("")) {
            imageName = StringUtils.cleanPath(Objects.requireNonNull(image.getOriginalFilename())); // Clean the file name
            jobSeekerProfileDTO.setProfilePhoto(imageName); // Set the profile photo in the DTO
        }

        // Check if a resume file was uploaded
        if (!pdf.getOriginalFilename().equals("")) {
            resumeName = StringUtils.cleanPath(Objects.requireNonNull(pdf.getOriginalFilename())); // Clean the file name
            jobSeekerProfileDTO.setResume(resumeName); // Set the resume filename in the DTO
        }

        // Save the job-seeker profile using the service
        JobSeekerProfileDTO savedProfile = jobSeekerProfileService.addNew(jobSeekerProfileDTO);

        try { // Attempt to save the uploaded files
            String uploadDir = "photos/candidate/" + savedProfile.getUserAccountId(); // Create upload directory path

            // Save the profile photo if provided
            if (!Objects.equals(image.getOriginalFilename(), "")) {
                FileUploadUtil.saveFile(uploadDir, imageName, image); // Save the file using utility method
            }

            // Save the resume if provided
            if (!Objects.equals(pdf.getOriginalFilename(), "")) {
                FileUploadUtil.saveFile(uploadDir, resumeName, pdf); // Save the file using utility method
            }

        } catch (Exception e) {
            e.printStackTrace(); // Print the stack trace if an exception occurs during file saving
        }

        return "redirect:/dashboard/"; // Redirect to the dashboard after saving
    }

    /**
     * Displays the profile of a candidate.
     *
     * This method retrieves a specific job seeker profile based on the ID.
     *
     * @param id the ID of the job seeker profile
     * @param model the Model object used to pass data to the view
     * @return String representing the view name (job-seeker-profile)
     */
    @GetMapping("/{id}")
    public String candidateProfile(@PathVariable("id") Long id, Model model) {
        // Get the job seeker profile by ID
        Optional<JobSeekerProfile> seekerProfile = jobSeekerProfileService.getOne(id);

        // Add the candidate profile to the model
        seekerProfile.ifPresent(profile -> model.addAttribute("profile", profile));

        return "job-seeker-profile"; // Return the job-seeker profile view
    }

    /**
     * Handles the download of a resume file.
     *
     * This method serves the resume file for download when a GET request is made to "/downloadResume".
     *
     * @param filename the name of the file to download
     * @param userId the ID of the user associated with the resume
     * @return ResponseEntity containing the file resource or an error message
     */
    @GetMapping("/downloadResume")
    public ResponseEntity<?> downloadResume(@RequestParam(value = "fileName") String filename,
                                            @RequestParam("userId") String userId) {
        FileDownloadUtil fileDownloadUtil = new FileDownloadUtil(); // Initialize the file download utility

        Resource resource = null; // Resource to hold the file resource

        // Attempt to get the file as a resource
        try {
            resource = fileDownloadUtil.getFileAsResource("/photos/candidate/" + userId, filename); // Load the file resource
        } catch (IOException e) {
            return ResponseEntity.notFound().build(); // Return not found response if an error occurs
        }

        // Check if the resource is null and return an error response if so
        if (resource == null) {
            return new ResponseEntity<>("File not found", HttpStatus.NOT_FOUND);
        }

        String contentType = "application/octet-stream"; // Set content type for the download
        String headerValue = "attachment; filename=\"" + resource.getFilename() + "\""; // Set the header to trigger download

        // Return the file resource as a downloadable response
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, headerValue)
                .body(resource);
    }
}