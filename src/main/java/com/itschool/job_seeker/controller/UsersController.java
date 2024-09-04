package com.itschool.job_seeker.controller;

import com.itschool.job_seeker.entity.Users;
import com.itschool.job_seeker.model.UsersDTO;
import com.itschool.job_seeker.model.UsersTypeDTO;
import com.itschool.job_seeker.services.UsersService;
import com.itschool.job_seeker.services.UsersTypeService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;
import java.util.Optional;

@Controller // Indicates that this class serves as a controller in the MVC pattern
public class UsersController {

    private final UsersTypeService usersTypeService; // Service for user types
    private final UsersService usersService; // Service for user-related operations

    // Constructor for dependency injection of services
    public UsersController(UsersTypeService usersTypeService, UsersService usersService) {
        this.usersTypeService = usersTypeService;
        this.usersService = usersService;
    }

    /**
     * Renders the registration form for new users.
     *
     * This method handles GET requests to the "/register" URL.
     *
     * @param model the model object used to pass attributes to the view
     * @return the name of the registration view
     */
    @GetMapping("/register") // Endpoint for rendering registration form
    public String register(Model model) {
        // Fetch all user types to be displayed in the registration form
        List<UsersTypeDTO> usersTypes = usersTypeService.getAll();
        model.addAttribute("getAllTypes", usersTypes); // Add user types to model
        model.addAttribute("user", new Users()); // Provide an empty Users object for binding
        return "register"; // Return the view name for registration
    }

    /**
     * Processes the registration of a new user.
     *
     * This method handles POST requests to the "/register/new" URL.
     *
     * @param model   the model object used to carry data to the view
     * @param userDTO the data transfer object containing user details
     * @return redirect URL to the dashboard if successful, or the registration view with an error message
     */
    @PostMapping("/register/new") // Endpoint for processing registration form submission
    public String userRegister(Model model, @Valid UsersDTO userDTO) {
        // Check if a user with the given email already exists
        Optional<UsersDTO> optionalUsers = usersService.findUserByEmail(userDTO.getEmail());

        if (optionalUsers.isPresent()) {
            model.addAttribute("error", "This email already exists"); // Set error message in model
            List<UsersTypeDTO> usersTypes = usersTypeService.getAll(); // Retrieve user types again for the form
            model.addAttribute("getAllTypes", usersTypes); // Add user types to model
            model.addAttribute("user", new Users()); // Provide an empty object for form binding
            return "register"; // Return to registration view
        }

        // If the email is unique, create a new user
        usersService.addUser(userDTO);
        return "redirect:/dashboard/"; // Redirect to the dashboard on successful registration
    }

    /**
     * Renders the login page.
     *
     * This method handles GET requests to the "/login" URL.
     *
     * @return the name of the login view
     */
    @GetMapping("/login") // Endpoint for rendering the login page
    public String login() {
        return "login"; // Return the login view
    }

    /**
     * Handles user logout.
     *
     * This method handles GET requests to the "/logout" URL.
     *
     * @param request  the HTTP request object
     * @param response the HTTP response object
     * @return redirect URL to the home page after logging out
     */
    @GetMapping("/logout") // Endpoint for logging out a user
    public String logout(HttpServletRequest request, HttpServletResponse response) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication(); // Get current authentication
        if (authentication != null) {
            // Log out the user if authenticated
            new SecurityContextLogoutHandler().logout(request, response, authentication);
        }
        return "redirect:/"; // Redirect to the home page post logout
    }
}