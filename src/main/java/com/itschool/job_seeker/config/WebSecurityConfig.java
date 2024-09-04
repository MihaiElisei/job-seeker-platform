package com.itschool.job_seeker.config;

import com.itschool.job_seeker.services.CustomUserDetailsService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration // Indicates that this class can be used by the Spring IoC container as a source of bean definitions
@EnableWebSecurity // Enables Spring Security’s web security support and provides the Spring MVC integration
public class WebSecurityConfig {

    private final CustomUserDetailsService customUserDetailsService; // Service for loading user-specific data
    private final CustomAuthenticationSuccessHandler customAuthenticationSuccessHandler; // Custom handler for successful authentication

    // Constructor for injecting dependencies
    public WebSecurityConfig(CustomUserDetailsService customUserDetailsService, CustomAuthenticationSuccessHandler customAuthenticationSuccessHandler) {
        this.customUserDetailsService = customUserDetailsService;
        this.customAuthenticationSuccessHandler = customAuthenticationSuccessHandler;
    }

    // List of public URLs that Spring Security will allow access without authentication
    private final String[] publicURL = {
            "/",
            "/global-search/**",
            "/register",
            "/register/**",
            "/webjars/**",
            "/resources/**",
            "/assets/**",
            "/css/**",
            "/summernote/**",
            "/js/**",
            "/*.css",
            "/*.js",
            "/*.js.map",
            "/fonts**",
            "/favicon.ico",
            "/resources/**",
            "/error"
    };

    /**
     * Configures the security filter chain for handling HTTP security.
     *
     * @param http the HttpSecurity object to configure
     * @return the configured SecurityFilterChain
     * @throws Exception if there is an error in configuring the security
     */
    @Bean
    protected SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        // Specify the authentication provider to use
        http.authenticationProvider(authenticationProvider());

        // Set up authorization rules
        http.authorizeHttpRequests(auth -> {
            auth.requestMatchers(publicURL).permitAll(); // Allow public access to specified URLs
            auth.anyRequest().authenticated(); // All other requests require authentication
        });

        // Set up the form login configuration
        http.formLogin(form -> form.loginPage("/login").permitAll() // Allow anyone to access the login page
                        .successHandler(customAuthenticationSuccessHandler)) // Use custom success handler
                .logout(logout -> {
                    logout.logoutUrl("/logout"); // Logout URL
                    logout.logoutSuccessUrl("/"); // Redirect to home after logout
                })
                .cors(Customizer.withDefaults()) // Enable CORS with default configurations
                .csrf(csrf -> csrf.disable()); // Disable CSRF protection for simplicity (consider for production)

        return http.build(); // Build and return the security filter chain
    }

    /**
     * Configures the authentication provider that will load user-specific data and authenticate users.
     *
     * @return the configured AuthenticationProvider
     */
    @Bean
    public AuthenticationProvider authenticationProvider() {

        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider(); // Create a new authentication provider
        authProvider.setPasswordEncoder(passwordEncoder()); // Set the password encoder for hashing
        authProvider.setUserDetailsService(customUserDetailsService); // Set the custom user details service
        return authProvider; // Return the configured authentication provider
    }

    /**
     * Configures the password encoder for encoding and verifying passwords.
     *
     * @return the configured PasswordEncoder
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(); // Use BCrypt for password encoding
    }
}