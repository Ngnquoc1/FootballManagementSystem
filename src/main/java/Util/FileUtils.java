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

    private static final String LOGO_STORAGE_PATH = "src/main/resources/image/ClubLogo/";

    /**
     * Sao chép file logo vào thư mục lưu trữ
     *
     * @param logoFile File logo cần sao chép
     * @param clubName Tên CLB để đặt tên file
     * @return Đường dẫn đến file logo đã sao chép
     * @throws IOException Nếu có lỗi khi sao chép file
     */
    public static String copyLogoToStorage(File logoFile, String clubName) throws IOException {
        // Tạo thư mục lưu trữ nếu chưa tồn tại
        File storageDir = new File(LOGO_STORAGE_PATH);
        if (!storageDir.exists()) {
            storageDir.mkdirs();
        }

        // Tạo tên file mới
        String fileExtension = getFileExtension(logoFile.getName());
        String sanitizedClubName = sanitizeFileName(clubName);
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        String newFileName = sanitizedClubName + "_" + timestamp + fileExtension;

        // Đường dẫn đến file mới
        Path targetPath = Paths.get(LOGO_STORAGE_PATH + newFileName);

        // Sao chép file
        Files.copy(logoFile.toPath(), targetPath, StandardCopyOption.REPLACE_EXISTING);

        return targetPath.toString();
    }

    /**
     * Lấy phần mở rộng của file
     *
     * @param fileName Tên file
     * @return Phần mở rộng của file (bao gồm dấu chấm)
     */
    private static String getFileExtension(String fileName) {
        int lastDotIndex = fileName.lastIndexOf('.');
        if (lastDotIndex > 0) {
            return fileName.substring(lastDotIndex);
        }
        return "";
    }

    /**
     * Làm sạch tên file để tránh các ký tự không hợp lệ
     *
     * @param fileName Tên file cần làm sạch
     * @return Tên file đã được làm sạch
     */
    private static String sanitizeFileName(String fileName) {
        // Thay thế các ký tự không hợp lệ bằng dấu gạch dưới
        return fileName.replaceAll("[^a-zA-Z0-9.-]", "_");
    }

    public static String copyLogoToDirectory(File logoFile, String targetDirectory, String clubName) throws IOException {
        // Ensure the target directory exists
        File directory = new File(targetDirectory);
        if (!directory.exists()) {
            directory.mkdirs();
        }

        // Generate a unique file name
        String fileExtension = logoFile.getName().substring(logoFile.getName().lastIndexOf("."));
        String uniqueFileName = clubName.replaceAll("\\s+", "_") + fileExtension;

        // Copy the file to the target directory
        Path targetPath = Paths.get(targetDirectory, uniqueFileName);
        Files.copy(logoFile.toPath(), targetPath, StandardCopyOption.REPLACE_EXISTING);

        return uniqueFileName;
    }
}
