package Util;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class FileUtils {

    private static String getFileExtension(String fileName) {
        int lastDotIndex = fileName.lastIndexOf('.');
        if (lastDotIndex > 0) {
            return fileName.substring(lastDotIndex);
        }
        return "";
    }

    private static String sanitizeFileName(String fileName) {
        return fileName.replaceAll("[^a-zA-Z0-9]", "").toLowerCase();
    }
    private static String getUniqueFileName(String fileName, String name) {
        String sanitizedFileName = sanitizeFileName(name);
        String fileExtension = getFileExtension(fileName);

        // Generate a unique timestamp
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));

        return sanitizedFileName + "_" + timestamp + fileExtension;
    }

    public static String copyLogoToDirectory(File logoFile, String targetDirectory, String name) throws IOException {
        File directory = new File(targetDirectory);
        if (!directory.exists()) {
            directory.mkdirs();
        }

        // Generate a unique file name
        String uniqueFileName = getUniqueFileName(logoFile.getName(),name);

        // Copy the file to the target directory
        Path targetPath = Paths.get(targetDirectory, uniqueFileName);
        Files.copy(logoFile.toPath(), targetPath, StandardCopyOption.REPLACE_EXISTING);

        return uniqueFileName;
    }
}
