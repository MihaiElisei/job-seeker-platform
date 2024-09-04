package com.itschool.job_seeker.util;

import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;



public class FileDownloadUtil {

    // Holds the reference to the found file path
    private Path foundPath;

    /**
     * Fetches the file from the specified directory based on the given file name.
     *
     * @param downloadDir The directory where the file is located.
     * @param fileName The name or the prefix of the file to be searched.
     * @return A Resource representing the file if found; otherwise, null.
     * @throws IOException If an I/O error occurs while reading the directory.
     */
    public Resource getFileAsResource(String downloadDir, String fileName) throws IOException {

        // Convert the download directory string to a Path object
        Path path = Paths.get(downloadDir);

        // List all files in the specified directory
        Files.list(path).forEach(file -> {
            // Check if the file starts with the given fileName
            if(file.getFileName().toString().startsWith(fileName)) {
                // If a matching file is found, store its path in foundPath
                foundPath = file;
            }
        });

        // If a file was found, create a UrlResource from the found path
        if (foundPath == null) {
            return new UrlResource(foundPath.toUri());
        }

        // Return null if no matching file was found
        return null;
    }
}
