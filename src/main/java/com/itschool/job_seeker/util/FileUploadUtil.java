package com.itschool.job_seeker.util;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

/**
 * Utility class for handling file uploads.
 * Provides functionality to save uploaded files to a specified directory.
 */
public class FileUploadUtil {

    /**
     * Saves a file to the specified upload directory.
     *
     * @param uploadDir   the directory where the file should be uploaded
     * @param fileName    the name to give the saved file
     * @param multipartFile the MultipartFile containing the file data
     * @throws IOException if an I/O error occurs while saving the file
     */
    public static void saveFile(String uploadDir,String fileName, MultipartFile multipartFile) throws IOException {

        Path uploadPath = Paths.get(uploadDir);
        if(!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        try(InputStream inputStream = multipartFile.getInputStream()) {
            Path path = uploadPath.resolve(fileName);
            System.out.println("filePath " + path);
            System.out.println("fileName " + fileName);
            Files.copy(inputStream, path, StandardCopyOption.REPLACE_EXISTING);
        }catch (IOException ioe){
            throw new IOException("Could not save image file" + fileName, ioe);
        }
    }
}
