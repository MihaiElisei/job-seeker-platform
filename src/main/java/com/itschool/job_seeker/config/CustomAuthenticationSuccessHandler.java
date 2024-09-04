package com.itschool.job_seeker.config;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component // Indicates that this class is a Spring-managed component
public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    /**
     * This method is called when user authentication is successful.
     *
     * It handles the redirection based on the user's role(s).
     *
     * @param request the HttpServletRequest object that contains the request the client made to the servlet
     * @param response the HttpServletResponse object that contains the response the servlet returns to the client
     * @param authentication the Authentication object that contains the user's authentication data
     * @throws IOException if an input or output error is detected
     * @throws ServletException if the request could not be handled
     */
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal(); // Get the principal (user details)
        String username = userDetails.getUsername(); // Retrieve the username of the authenticated user
        System.out.println("The username is " + username + " is logged in"); // Log the successful login

        // Check if the user has the "Job Seeker" role
        boolean hasJobSeekerRole = authentication.getAuthorities().stream()
                .anyMatch(r -> r.getAuthority().equals("Job Seeker"));

        // Check if the user has the "Recruiter" role
        boolean hasRecruiterRole = authentication.getAuthorities().stream()
                .anyMatch(r -> r.getAuthority().equals("Recruiter"));

        // Redirect to the dashboard page if the user is a Job Seeker or a Recruiter
        if (hasJobSeekerRole || hasRecruiterRole) {
            response.sendRedirect("/dashboard/"); // Redirect to the dashboard
        }
    }
}
