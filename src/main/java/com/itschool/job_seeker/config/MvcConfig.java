package com.itschool.job_seeker.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Path;
import java.nio.file.Paths;

@Configuration // This annotation indicates that the class can be used by the Spring IoC container as a source of bean definitions.
public class MvcConfig implements WebMvcConfigurer {

    // Constant that defines the directory where uploaded files are stored
    private static final String UPLOAD_DIR = "photos";

    /**
     * Adds resource handlers for serving static resources.
     *
     * This method is called by the Spring framework to allow custom configuration of resource handling.
     *
     * @param registry the registry to add resource handler to
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Call the method to expose the upload directory for serving static files
        exposeDirectory(UPLOAD_DIR, registry);
    }

    /**
     * Exposes a directory to serve static resources located in the specified upload directory.
     *
     * This method registers a resource handler that allows serving files from a directory on the server
     * via the web application.
     *
     * @param uploadDir the directory to expose (e.g., "photos")
     * @param registry the resource handler registry to which the resource handler will be added
     */
    private void exposeDirectory(String uploadDir, ResourceHandlerRegistry registry) {
        // Convert the uploadDir string to a Path object for better path handling
        Path path = Paths.get(uploadDir);

        // Register the resource handler to serve files from the specified directory.
        // This configuration matches requests made to URLs starting with "/photos/**" and serves
        // the corresponding files located in the upload directory on the server.
        registry.addResourceHandler("/" + uploadDir + "/**") // URL pattern to match requests
                .addResourceLocations("file:" + path.toAbsolutePath() + "/"); // Actual location of the files on the server
    }
}
