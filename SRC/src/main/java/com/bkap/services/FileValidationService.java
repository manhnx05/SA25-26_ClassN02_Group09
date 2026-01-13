package com.bkap.services;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;
import java.util.List;

@Service
public class FileValidationService {
    
    private static final List<String> ALLOWED_EXTENSIONS = Arrays.asList("jpg", "jpeg", "png", "gif");
    private static final long MAX_FILE_SIZE = 5 * 1024 * 1024; // 5MB
    
    public boolean isValidImageFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            return false;
        }
        
        // Check file size
        if (file.getSize() > MAX_FILE_SIZE) {
            return false;
        }
        
        // Check file extension
        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null) {
            return false;
        }
        
        String extension = getFileExtension(originalFilename).toLowerCase();
        return ALLOWED_EXTENSIONS.contains(extension);
    }
    
    public boolean isValidImageFileOptional(MultipartFile file) {
        // Cho phép file rỗng (không upload ảnh mới)
        if (file == null || file.isEmpty()) {
            return true;
        }
        
        return isValidImageFile(file);
    }
    
    private String getFileExtension(String filename) {
        int lastDotIndex = filename.lastIndexOf('.');
        if (lastDotIndex == -1) {
            return "";
        }
        return filename.substring(lastDotIndex + 1);
    }
    
    public String getValidationMessage(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            return "Vui lòng chọn file ảnh";
        }
        
        if (file.getSize() > MAX_FILE_SIZE) {
            return "File ảnh không được vượt quá 5MB";
        }
        
        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null) {
            return "Tên file không hợp lệ";
        }
        
        String extension = getFileExtension(originalFilename).toLowerCase();
        if (!ALLOWED_EXTENSIONS.contains(extension)) {
            return "Chỉ chấp nhận file ảnh định dạng: " + String.join(", ", ALLOWED_EXTENSIONS);
        }
        
        return null;
    }
}