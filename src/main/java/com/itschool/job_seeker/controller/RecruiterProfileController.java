package com.itschool.job_seeker.controller;

import com.itschool.job_seeker.entity.Users;
import com.itschool.job_seeker.model.RecruiterProfileDTO;
import com.itschool.job_seeker.repository.UsersRepository;
import com.itschool.job_seeker.services.RecruiterProfileService;
import com.itschool.job_seeker.util.FileUploadUtil;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.util.Objects;
import java.util.Optional;

@Controller
@RequestMapping("/recruiter-profile") // Base URL for all recruiter profile-related endpoints
public class RecruiterProfileController {

    private final UsersRepository usersRepository; // Repository for user-related database operations
    private final RecruiterProfileService recruiterProfileService; // Service for handling recruiter profile operations

    // Constructor to inject the necessary dependencies
    public RecruiterProfileController(UsersRepository usersRepository, RecruiterProfileService recruiterProfileService) {
        this.usersRepository = usersRepository;
        this.recruiterProfileService = recruiterProfileService;
    }

    /**
     * Displays the recruiter's profile page.
     *
     * This method handles GET requests to the "/recruiter-profile/" URL.
     *
     * @param model the model object to be used in the view
     * @return the name of the view to be rendered
     */
    @GetMapping("/") // Represents the view for the recruiter's profile
    public String recruiterProfile(Model model) {
        // Retrieve authentication details for the current user
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        // Ensure the user is authenticated (not anonymous)
        if (!(authentication instanceof AnonymousAuthenticationToken)) {
            String currentUsername = authentication.getName(); // Get the authenticated user's username
            Users user = usersRepository.findByEmail(currentUsername) // Fetch user by email
                    .orElseThrow(() -> new UsernameNotFoundException("User not found")); // Handle case where user is not found

            // Retrieve the recruiter profile associated with the user
            Optional<RecruiterProfileDTO> recruiterProfile = recruiterProfileService.getOne(user.getUserId());

            // Add the recruiter profile DTO to the model for the view
            if (recruiterProfile.isPresent()) {
                model.addAttribute("recruiterProfile", recruiterProfile.get());
            } else {
                model.addAttribute("recruiterProfile", new RecruiterProfileDTO()); // Provide an empty DTO for the form
            }
        }
        return "recruiter_profile"; // Return the view for the recruiter's profile
    }

    /**
     * Handles the creation of a new recruiter profile.
     *
     * This method handles POST requests to the "/recruiter-profile/addNew" URL,
     * which allows the recruiter to submit their profile details.
     *
     * @param recruiterProfileDTO the data transfer object containing recruiter profile details
     * @param multipartFile the profile photo file uploaded by the recruiter
     * @param model the model object to be used in the view
     * @return redirect URL to the dashboard view after saving the profile
     */
    @PostMapping("/addNew") // Endpoint to add a new recruiter profile
    public String addNew(RecruiterProfileDTO recruiterProfileDTO,
                         @RequestParam("image") MultipartFile multipartFile,
                         Model model) {
        // Retrieve authentication details of the current user
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        // Ensure the user is authenticated (not anonymous)
        if (!(authentication instanceof AnonymousAuthenticationToken)) {
            String currentUsername = authentication.getName(); // Get the authenticated user's username
            Users user = usersRepository.findByEmail(currentUsername) // Fetch user by email
                    .orElseThrow(() -> new UsernameNotFoundException("User not found")); // Handle case where user is not found

            // Set user ID and account ID in the DTO for saving
            recruiterProfileDTO.setUserId(user);
            recruiterProfileDTO.setUserAccountId(user.getUserId());
        }

        // Add the profile DTO to the model for the view
        model.addAttribute("profile", recruiterProfileDTO);

        // Initialize variable for the uploaded file name
        String fileName = "";
        // Check if a file was uploaded
        if (!multipartFile.getOriginalFilename().equals("")) {
            fileName = StringUtils.cleanPath(Objects.requireNonNull(multipartFile.getOriginalFilename())); // Clean the file name to avoid issues
            recruiterProfileDTO.setProfilePhoto(fileName); // Set the profile photo in the DTO
        }

        // Save the recruiter profile using the service
        RecruiterProfileDTO savedProfile = recruiterProfileService.addNew(recruiterProfileDTO);

        // Define the directory for saving the uploaded photo
        String uploadDir = "photos/recruiter/" + savedProfile.getUserAccountId();

        try { // Attempt to save the uploaded file
            FileUploadUtil.saveFile(uploadDir, fileName, multipartFile); // Save the file using a utility method
        } catch (Exception e) {
            e.printStackTrace(); // Print stack trace if an exception occurs during file saving
        }

        return "redirect:/dashboard/";  // Redirect to the dashboard after saving the profile
    }
}