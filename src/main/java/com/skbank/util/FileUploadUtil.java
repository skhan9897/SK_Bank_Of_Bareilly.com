package com.skbank.util;

import javax.servlet.http.Part;
import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

public class FileUploadUtil {

    private static final long MAX_FILE_SIZE = 5 * 1024 * 1024; // 5 MB

    public static String processProfilePhoto(Part filePart, String uploadDir) {
        if (filePart == null || filePart.getSize() <= 0) {
            return "assets/images/default-avatar.png";
        }

        if (filePart.getSize() > MAX_FILE_SIZE) {
            throw new IllegalArgumentException("Profile photo size must be less than or equal to 5 MB.");
        }

        String contentType = filePart.getContentType();
        if (contentType == null || (!contentType.equalsIgnoreCase("image/jpeg") &&
                                    !contentType.equalsIgnoreCase("image/jpg") &&
                                    !contentType.equalsIgnoreCase("image/png"))) {
            throw new IllegalArgumentException("Invalid file format. Only JPG, JPEG, and PNG images are allowed.");
        }

        String ext = contentType.contains("png") ? ".png" : ".jpg";
        String uniqueFileName = "customer_" + System.currentTimeMillis() + "_" + UUID.randomUUID().toString().substring(0, 8) + ext;

        try {
            File uploadFolder = new File(uploadDir);
            if (!uploadFolder.exists()) {
                uploadFolder.mkdirs();
            }

            File destFile = new File(uploadFolder, uniqueFileName);
            try (InputStream input = filePart.getInputStream()) {
                Files.copy(input, destFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
            }

            return "uploads/profile/" + uniqueFileName;

        } catch (Exception e) {
            System.err.println("Error saving profile photo: " + e.getMessage());
            return "assets/images/default-avatar.png";
        }
    }
}
